// modified by ket.kio.RPCGen at Wed May 03 13:22:31 CST 2017.

package i3k.gmap;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.ObjectName;

import ket.kio.NetManager;
import ket.kio.NetAddress;
import ket.util.SStream;
import ket.kio.Statistic;
import i3k.alarm.TCPAlarmServer;
import i3k.gs.GameData;
import i3k.rpc.Packet;
import i3k.util.GameRandom;
import i3k.util.GameTime;
import i3k.util.OpenConnectFailCount;
import i3k.util.GameServerTable;
import i3k.LuaPacket;
import i3k.SBean;

public class RPCManager
{
	// mbean
	public interface MapIOStatMBean
	{
		int getSamplinginterval();
		int getSendClientStrMsgCountPerSecond();
		int getSendClientNormalMsgCountPerSecond();
		int getSendServerMsgCountPerSecond();
		
		int getSessionSendPacketCountPerSecond();
		int getSessionSendTimesCountPerSecond();
		int getSessionRecvTimesCountPerSecond();
		int getSessionPacketTaskAddedPerSecond();
		int getSessionSendKBytesPerSecond();
		
		int getSessionSendPacketTotal();
		int getSessionSendTimesTotal();
		int getSessionRecvTimesTotal();
		int getSessionPacketTaskQueue();
		int getSessionSendBytes();
	}
		
	public RPCManager(MapServer ms)
	{
		this.keepaliveRandomTime = GameRandom.getRandom().nextInt(15);
		this.ms = ms;
		strChannelHandler = new StringChannelHandler(ms);
	}

	public void onTimer(int timeTick)
	{
		managerNet.checkIdleConnections();
		if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
		{
			if (!tfmc.isOpen())
				tfmc.open();
		}
		else
		{
			if (!tmc.isOpen())
				tmc.open();
		}
		if (talarms != null)
			talarms.onTimer();
		keepAlive(timeTick);
	}
	
	public void setAllCounter(boolean reportPerTimes)
	{
		mapcount.setReportPerTimes(reportPerTimes);
	}

	public void start()
	{
		setAllCounter(ms.getConfig().pIOFailedPerTimes == 1);
		stat.start();
		taskExecutor = Executors.newSingleThreadExecutor();
		if( ms.getConfig().nIOThread == 1 )
			managerNet = new NetManager(NetManager.NetManagerType.eSelectNetManager, ms.getConfig().nIOThread);
		else
			managerNet = new NetManager(NetManager.NetManagerType.eMTSelectNetManager, ms.getConfig().nIOThread);
		managerNet.start();
		if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
		{
			tgms = new TCPGlobalMapServer(this);
			tfmc = new TCPFightMapClient(this);
			
			tgms.setListenAddr(ms.getConfig().addrGlobalMapListen, ket.kio.BindPolicy.eReuseTimewait);
			tgms.setListenBacklog(128);
			tgms.open();
			
			tfmc.setServerAddr(ms.getConfig().addrFightMap);
			tfmc.open();
		}
		else
		{
			tmc = new TCPMapClient(this);
			
			tmc.setServerAddr(ms.getConfig().addrMap);
			tmc.open();	
		}
		talarms = new TCPAlarmServer(this.getNetManager(), ms.getConfig().addrAlarmListen, ms.getLogger());
		talarms.start();
	}

	public void destroy()
	{
		managerNet.destroy();
		try
		{
			taskExecutor.shutdown();
			while (!taskExecutor.awaitTermination(1, TimeUnit.SECONDS));
		}
		catch (Exception e)
		{
			ms.getLogger().warn("shutdown rpc executor cause exception", e);
		}
	}
	
	void addSendEvent(Runnable runnable)
	{
		if(this.taskExecutor.isShutdown())
			return;
		
		this.taskExecutor.execute(runnable);
	}
	
	public NetManager getNetManager()
	{
		return managerNet;
	}

	//// begin handlers.
	public void onTCPMapClientOpen(TCPMapClient peer)
	{
		ms.getLogger().info("tcpmapclient open connect to " + peer.getServerAddr());
		stat.sendServerMsgCounter.incrementAndGet();
		mapcount.resetCount();
		this.addSendEvent(() -> 
		{
			peer.sendPacket(new Packet.M2S.WhoAmI(ms.getConfig().group, ms.getConfig().id, ms.getDeployConf().getMapDelpoy().getDeployMaps()));
		});
	}

	public void onTCPMapClientOpenFailed(TCPMapClient peer, ket.kio.ErrorCode errcode)
	{
		if (mapcount.increaseCount())
			ms.getLogger().warn("tcpmapclient open connect to " + peer.getServerAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPMapClientClose(TCPMapClient peer, ket.kio.ErrorCode errcode)
	{
		ms.getLogger().warn("tcpmapclient close on " + peer.getServerAddr() + " , errcode=" + errcode);
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().resetMap();	
		});
	}

	public void onTCPMapClientRecvKeepAlive(TCPMapClient peer, Packet.S2M.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncTimeOffset(TCPMapClient peer, Packet.S2M.SyncTimeOffset packet)
	{
		ms.getLogger().info("receive gs sync server time offset : " + packet.getTimeOffset() + ", current time offset " + GameTime.getServerTimeOffset());
		GameTime.setServerTimeOffset(packet.getTimeOffset());
	}

	public void onTCPMapClientRecvLuaChannel(TCPMapClient peer, Packet.S2M.LuaChannel packet)
	{
		ms.getLogger().warn("role " + packet.getRoleID() + " lua channel packet : " + packet.getData());
		try
		{
			int roleID = packet.getRoleID();
			String data = packet.getData();
			MapRole role = ms.getMapManager().getMapRole(roleID);
			if (role == null || !role.active)
				return;

			String[] msg = LuaPacket.decode(data);
			ms.getMapManager().addClientEvent(() ->
			{
				try
				{
					onLuaChannelList(role, msg);
				}
				catch (Exception ex)
				{
					ms.getLogger().warn(ex.getMessage(), ex);
				}
			});
		}
		catch (Exception ex)
		{
			ms.getLogger().warn(ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("unchecked")
	public void onTCPMapClientRecvStrChannel(TCPMapClient peer, Packet.S2M.StrChannel packet)
	{
		try
		{
			int roleID = packet.getRoleID();
			String data = packet.getData();

//			ms.getLogger().trace("receive role " + roleID + " lua str packet : " + data);
//			if (data.startsWith("1role_move") || data.startsWith("1role_stopmove") || data.startsWith("1pet_move") || data.startsWith("1pet_stopmove") || data.startsWith("1client_ping_start"))
//				ms.getLogger().trace("receive role " + roleID + " lua str packet : " + data);
//			else
//				ms.getLogger().debug("receive role " + roleID + " lua str packet : " + data + ", at tick [" + ms.getMapManager().getTimeTick().tickLine + " , " + ms.getMapManager().getTimeTick().outTick + "]" + " at " + GameTime.getTimeMillis() + " , " + ms.getMapManager().getMapLogicTime());

			MapRole role = ms.getMapManager().getMapRole(roleID);
			if (role == null)
				return;

			String packetName = SStream.detectPacketName(data);
			if (packetName == null)
				return;
			
			if(GameData.getInstance().containsMapRecvChars(packetName))
				ms.getLogger().debug("receive role " + roleID + " lua str packet : " + data);
			else
				ms.getLogger().trace("receive role " + roleID + " lua str packet : " + data);
			
			@SuppressWarnings("rawtypes")
			Class cls = SBean.getStrPacketClass(packetName);
			if (cls == null)
				return;

			SStream.IStreamable stream = SStream.decode(data, cls);
			ms.getMapManager().addClientEvent(() ->
			{
				try
				{
					if (!role.active || role.curMap == null || role.curMap.isTimeOut())
						return;

					Method mtd = StringChannelHandler.class.getMethod("onRecv_" + packetName, MapRole.class, SStream.IStreamable.class);
					mtd.invoke(strChannelHandler, role, stream);
					//ms.getLogger().debug("@@@@ " + ms.getMapManager().getMapLogicTime() + " handle role " + roleID + " end : " + data + " dTime " + (ms.getMapManager().getMapLogicTime() - receiveTick));
				}
				catch (Exception ex)
				{
					ms.getLogger().warn(ex.getMessage() + " lua str packet : " + data, ex);
				}
			});
		}
		catch (Exception ex)
		{
			ms.getLogger().warn(ex.getMessage() + " lua str packet : " + packet.getData(), ex);
		}
	}

	public void onTCPMapClientRecvSyncDoubleDropCfg(TCPMapClient peer, Packet.S2M.SyncDoubleDropCfg packet)
	{
		ms.getLogger().info("receive gs sync double drop cfgs");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().syncDoubleDropCfgs(packet.getCfgs());
		});
	}

	public void onTCPMapClientRecvSyncExtraDropCfg(TCPMapClient peer, Packet.S2M.SyncExtraDropCfg packet)
	{
		ms.getLogger().info("receive gs sync extra drop cfgs");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().syncExtraDropCfgs(packet.getCfgs());	
		});
	}

	public void onTCPMapClientRecvSyncWorldNum(TCPMapClient peer, Packet.S2M.SyncWorldNum packet)
	{
		ms.getLogger().info("receive gs sync world num " + packet.getWorldNum() + " extral world num " + packet.getExtraWorldNum());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().syncWorldNum(packet.getWorldNum(), packet.getExtraWorldNum());	
		});
	}

	public void onTCPMapClientRecvStartMapCopy(TCPMapClient peer, Packet.S2M.StartMapCopy packet)
	{
		ms.getLogger().info("receive gs start mapcopy id " + packet.getMapID() + " instanceID " + packet.getInstanceID());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().createMapCopy(packet.getMapID(), packet.getInstanceID());
		});
	}

	public void onTCPMapClientRecvEndMapCopy(TCPMapClient peer, Packet.S2M.EndMapCopy packet)
	{
		ms.getLogger().info("receive gs end mapcopy id " + packet.getMapID() + " instanceID " + packet.getInstanceID());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().destoryMap(packet.getMapID(), packet.getInstanceID(), false);
		});
	}

	public void onTCPMapClientRecvMapCopyReady(TCPMapClient peer, Packet.S2M.MapCopyReady packet)
	{
		ms.getLogger().info("receive gs mapcopy " + packet.getMapID() + " , " + packet.getInstanceID() + " ready");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().mapcopyReady(packet.getMapID(), packet.getInstanceID());
		});
	}

	public void onTCPMapClientRecvResetSectMap(TCPMapClient peer, Packet.S2M.ResetSectMap packet)
	{
		ms.getLogger().info("receive gs reset sect map id " + packet.getMapID() + " instanceID " + packet.getInstanceID());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().resetSectMap(packet.getMapID(), packet.getInstanceID(), packet.getProgress());
		});
	}

	public void onTCPMapClientRecvResetArenaMap(TCPMapClient peer, Packet.S2M.ResetArenaMap packet)
	{
		ms.getLogger().debug("receive gs reset arena map id " + packet.getMapID() + " instanceID " + packet.getInstanceID() + " enemy " + packet.getEnemy().fightRole.base.roleID);
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().resetArenaMap(packet.getMapID(), packet.getInstanceID(), packet.getEnemy());
		});
	}

	public void onTCPMapClientRecvResetBWArenaMap(TCPMapClient peer, Packet.S2M.ResetBWArenaMap packet)
	{
		ms.getLogger().debug("receive gs reset bw arena map" + packet.getMapID() + " , " + packet.getInstanceID());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().resetBWArenaMap(packet.getMapID(), packet.getInstanceID(), packet.getEnemy(), packet.getPetLack() == 1);
		});
	}

	public void onTCPMapClientRecvEnterMap(TCPMapClient peer, Packet.S2M.EnterMap packet)
	{
		ms.getLogger().debug("receive role " + packet.getRole().base.roleID + " enter map[" + packet.getMapId() + ", " + packet.getMapInstance() + "], location=" + GameData.toString(packet.getLocation().position) + " pets " + packet.getPets().keySet()
				+ " curRideHorse " + packet.getCurRideHorse());
		ms.getMapManager().addServerEvent(() ->
		{
				ms.getMapManager().roleEnterMap(packet.getRole(), packet.getMapId(), packet.getMapInstance(), packet.getLocation(), packet.getHp(), packet.getSp(), packet.getArmorVal(),
												packet.getBuffs(), packet.getPets(), packet.getPetSeq(), packet.getPethost(), packet.getPkInfo(), packet.getTeam(), 
												packet.getCurRideHorse(), packet.getAlterState(), packet.getMainSpawnPos() == 1, packet.getMulRoleInfo(), packet.getDayFailedStreak(), packet.getVipLevel(), packet.getCurWizardPet(), packet.getCanTakeDrop());
		});
	}

	public void onTCPMapClientRecvLeaveMap(TCPMapClient peer, Packet.S2M.LeaveMap packet)
	{
		ms.getLogger().info("receive role " + packet.getRoleID() + " leave map...");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleLeaveMap(packet.getRoleID());
		});
	}

	public void onTCPMapClientRecvResetLocation(TCPMapClient peer, Packet.S2M.ResetLocation packet)
	{
		ms.getLogger().debug("receive role " + packet.getRoleID() + " reset location " + GameData.toString(packet.getLocation().position));
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleResetLocation(packet.getRoleID(), packet.getLocation());
		});
	}

	public void onTCPMapClientRecvUpdateActive(TCPMapClient peer, Packet.S2M.UpdateActive packet)
	{
		ms.getLogger().info("receive role " + packet.getRoleID() + " update active ... " + packet.getActive());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateActive(packet.getRoleID(), packet.getActive() > 0);
		});
	}

	public void onTCPMapClientRecvUpdateEquip(TCPMapClient peer, Packet.S2M.UpdateEquip packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " update equip wid " + packet.getWid());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateEquip(packet.getRoleID(), packet.getWid(), packet.getEquip());
		});
	}

	public void onTCPMapClientRecvUpdateEquipPart(TCPMapClient peer, Packet.S2M.UpdateEquipPart packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " update equip part ...");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateEquipPart(packet.getRoleID(), packet.getEquipPart());
		});
	}

	public void onTCPMapClientRecvUpdateSkill(TCPMapClient peer, Packet.S2M.UpdateSkill packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " update skill...");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateSkill(packet.getRoleID(), packet.getSkill());
		});
	}

	public void onTCPMapClientRecvUpdateCurSkills(TCPMapClient peer, Packet.S2M.UpdateCurSkills packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " update current skills...");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateCurSkills(packet.getRoleID(), packet.getSkills());
		});
	}

	public void onTCPMapClientRecvUpdateBuff(TCPMapClient peer, Packet.S2M.UpdateBuff packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " update buff...");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateBuff(packet.getRoleID(), packet.getBuff());
		});
	}

	public void onTCPMapClientRecvUpdateLevel(TCPMapClient peer, Packet.S2M.UpdateLevel packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " update level " + packet.getLevel());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateLevel(packet.getRoleID(), packet.getLevel());
		});
	}

	public void onTCPMapClientRecvAddHp(TCPMapClient peer, Packet.S2M.AddHp packet)
	{
		ms.getLogger().debug("receive role " + packet.getRoleID() + " add hp " + packet.getHp());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleAddHp(packet.getRoleID(), packet.getHp());
		});
	}

	public void onTCPMapClientRecvUpdateWeapon(TCPMapClient peer, Packet.S2M.UpdateWeapon packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " updateWeapon");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateWeapon(packet.getRoleID(), packet.getWeapon());
		});
	}

	public void onTCPMapClientRecvUpdateCurWeapon(TCPMapClient peer, Packet.S2M.UpdateCurWeapon packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " updateCurWeapon");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleChangeCurWeapon(packet.getRoleID(), packet.getCurWeapon());
		});
	}

	public void onTCPMapClientRecvUpdateSpirit(TCPMapClient peer, Packet.S2M.UpdateSpirit packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " updateSpirit ... ");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateSpirit(packet.getRoleID(), packet.getSpirit());
		});
	}

	public void onTCPMapClientRecvUpdateCurSpirit(TCPMapClient peer, Packet.S2M.UpdateCurSpirit packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " updateCurSpirit");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateCurSpirits(packet.getRoleID(), packet.getCurSpirit());
		});
	}

	public void onTCPMapClientRecvStartMine(TCPMapClient peer, Packet.S2M.StartMine packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " start mine " + packet.getMineID() + ", " + packet.getMineInstance());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleStartMine(packet.getRoleID(), packet.getMineID(), packet.getMineInstance());
		});
	}

	public void onTCPMapClientRecvRoleRevive(TCPMapClient peer, Packet.S2M.RoleRevive packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " revive fullhp: " + packet.getFullHp());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleRevive(packet.getRoleID(), packet.getFullHp() == 1);
		});
	}

	public void onTCPMapClientRecvUpdatePet(TCPMapClient peer, Packet.S2M.UpdatePet packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " update Pet ...");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdatePet(packet.getRoleID(), packet.getPet());
		});
	}

	public void onTCPMapClientRecvUpdateTeam(TCPMapClient peer, Packet.S2M.UpdateTeam packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " update team info : " + packet.getTeam().id);
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateTeam(packet.getRoleID(), packet.getTeam());
		});
	}

	public void onTCPMapClientRecvChangeCurPets(TCPMapClient peer, Packet.S2M.ChangeCurPets packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " change curpets ...");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleChangeCurPets(packet.getRoleID(), packet.getPets());
		});
	}

	public void onTCPMapClientRecvUpdateSectAura(TCPMapClient peer, Packet.S2M.UpdateSectAura packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " update aura " + packet.getAuraID() + " , " + packet.getAuraLvl());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateSectAura(packet.getRoleID(), packet.getAuraID(), packet.getAuraLvl());
		});
	}

	public void onTCPMapClientRecvResetSectAuras(TCPMapClient peer, Packet.S2M.ResetSectAuras packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " reset sectauras ... ");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleResetSectAuras(packet.getRoleID(), packet.getAuras());
		});
	}

	public void onTCPMapClientRecvUpdatePKInfo(TCPMapClient peer, Packet.S2M.UpdatePKInfo packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " sync pkinfo " + packet.getPKMode() + ", " + packet.getPKValue());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdatePKInfo(packet.getRoleID(), packet.getPKMode(), packet.getPKValue());
		});
	}

	public void onTCPMapClientRecvUpdateCurDIYSkill(TCPMapClient peer, Packet.S2M.UpdateCurDIYSkill packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " update curDIYSkill " + packet.getCurDIYSkill());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateCurDIYSkill(packet.getRoleID(), packet.getCurDIYSkill());
		});
	}

	public void onTCPMapClientRecvUpdateTransformInfo(TCPMapClient peer, Packet.S2M.UpdateTransformInfo packet)
	{
		ms.getLogger().trace("receive role " + packet.getRoleID() + " UpdateTransformInfo " + packet.getTransformLevel() + " , " + packet.getBWType());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateTransformInfo(packet.getRoleID(), packet.getTransformLevel(), packet.getBWType());
		});
	}

	public void onTCPMapClientRecvCreateWorldMapBoss(TCPMapClient peer, Packet.S2M.CreateWorldMapBoss packet)
	{
		ms.getLogger().debug("receive gs world map [" + packet.getMapID() + " , " + packet.getMapInstanceID() + "] create boss " + packet.getBossID() + " seq " + packet.getSeq());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().createWorldMapBoss(packet.getMapID(), packet.getMapInstanceID(), packet.getBossID(), packet.getSeq(), packet.getCurHP());
		});
	}

	public void onTCPMapClientRecvDestroyWorldMapBoss(TCPMapClient peer, Packet.S2M.DestroyWorldMapBoss packet)
	{
		ms.getLogger().debug("receive gs world map [" + packet.getMapID() + " , " + packet.getMapInstanceID() + "] destroy boss " + packet.getBossID());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().destroyWorldMapBoss(packet.getMapID(), packet.getMapInstanceID(), packet.getBossID());
		});
	}

	public void onTCPMapClientRecvInitWorldBoss(TCPMapClient peer, Packet.S2M.InitWorldBoss packet)
	{
		ms.getLogger().debug("receive gs init map world boss ");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().initWorldBoss(packet.getDbBoss());
		});
	}

	public void onTCPMapClientRecvCreateWorldMapSuperMonster(TCPMapClient peer, Packet.S2M.CreateWorldMapSuperMonster packet)
	{
		ms.getLogger().debug("receive gs map " + packet.getMapID() + " create super monster " + packet.getSuperMonsterID() + " standTime " + packet.getStandTime());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().createWorldMapSuperMonster(packet.getMapID(), packet.getSuperMonsterID(), packet.getStandTime());
		});
	}

	public void onTCPMapClientRecvCreateWorldMapMineral(TCPMapClient peer, Packet.S2M.CreateWorldMapMineral packet)
	{
		ms.getLogger().debug("receive gs map " + packet.getMapID() + " create world mineral " + packet.getWorldMineral() + " standTime " + packet.getStandTime());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().createWorldMineral(packet.getMapID(), packet.getWorldMineral(), packet.getStandTime());
		});
	}

	public void onTCPMapClientRecvGainNewSuite(TCPMapClient peer, Packet.S2M.GainNewSuite packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " gain new suite " + packet.getSuiteID());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleGainNewSuite(packet.getRoleID(), packet.getSuiteID());
		});
	}

	public void onTCPMapClientRecvUpdateSectBrief(TCPMapClient peer, Packet.S2M.UpdateSectBrief packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update sect brief " + packet.getSectBrief().sectName + " , " + packet.getSectBrief().sectPosition + " , " + packet.getSectBrief().sectID);
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateSectBrief(packet.getRoleID(), packet.getSectBrief());
		});
	}

	public void onTCPMapClientRecvUpdateHorseInfo(TCPMapClient peer, Packet.S2M.UpdateHorseInfo packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update horse " + packet.getInfo().id + " info");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateHorseInfo(packet.getRoleID(), packet.getInfo());
		});
	}

	public void onTCPMapClientRecvUpdateCurUseHorse(TCPMapClient peer, Packet.S2M.UpdateCurUseHorse packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update cur use horse " + packet.getHid());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateCurUseHorse(packet.getRoleID(), packet.getHid());
		});
	}

	public void onTCPMapClientRecvUpdateMedal(TCPMapClient peer, Packet.S2M.UpdateMedal packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update medal " + packet.getMedal() + " , " + packet.getState());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateMedal(packet.getRoleID(), packet.getMedal(), packet.getState());
		});
	}

	//
	public void onTCPMapClientRecvUpWearFashion(TCPMapClient peer, Packet.S2M.UpWearFashion packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " upwear fashion " + packet.getFashionType() + " , " + packet.getFashionID() + " isShow " + packet.getIsShow());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpWearFashion(packet.getRoleID(), packet.getFashionType(), packet.getFashionID(), packet.getIsShow());
		});
	}

	public void onTCPMapClientRecvUpdateAlterState(TCPMapClient peer, Packet.S2M.UpdateAlterState packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update alter state [" + packet.getAlterState().alterID + " , " + packet.getAlterState().attrEndTime + "]");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateAlterState(packet.getRoleID(), packet.getAlterState());
		});
	}

	public void onTCPMapClientRecvChangeHorseShow(TCPMapClient peer, Packet.S2M.ChangeHorseShow packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " change horse show " + packet.getShowID());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleChangeHorseShow(packet.getRoleID(), packet.getHid(), packet.getShowID());
		});
	}

	public void onTCPMapClientRecvAddBuff(TCPMapClient peer, Packet.S2M.AddBuff packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " add buff " + packet.getBuffID());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleAddBuff(packet.getRoleID(), packet.getBuffID());
		});
	}

	public void onTCPMapClientRecvUpdateSealGrade(TCPMapClient peer, Packet.S2M.UpdateSealGrade packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update seal grade " + packet.getSealGrade());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateSealGrade(packet.getRoleID(), packet.getSealGrade());
		});
	}

	public void onTCPMapClientRecvUpdateSealSkills(TCPMapClient peer, Packet.S2M.UpdateSealSkills packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update seal skills " + packet.getSkills());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateSealSkills(packet.getRoleID(), packet.getSkills());
		});
	}

	public void onTCPMapClientRecvSyncRolePetLack(TCPMapClient peer, Packet.S2M.SyncRolePetLack packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " sync bw arena map pet lack " + packet.getPetLack());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleSyncPetLack(packet.getRoleID(), packet.getPetLack() == 1);
		});
	}

	public void onTCPMapClientRecvUpdateRoleGrasp(TCPMapClient peer, Packet.S2M.UpdateRoleGrasp packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update grasp " + packet.getGraspID() + " , " + packet.getLevel());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateGrasp(packet.getRoleID(), packet.getGraspID(), packet.getLevel());
		});
	}

	public void onTCPMapClientRecvUpdateRareBook(TCPMapClient peer, Packet.S2M.UpdateRareBook packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update rare book " + packet.getBookID() + " , " + packet.getLevel());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateRareBook(packet.getRoleID(), packet.getBookID(), packet.getLevel());
		});
	}

	public void onTCPMapClientRecvUpdateRoleTitle(TCPMapClient peer, Packet.S2M.UpdateRoleTitle packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update title " + packet.getRoleID() + " add " + (packet.getAdd() == 1));
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateTitle(packet.getRoleID(), packet.getTitleID(), packet.getAdd() == 1);
		});
	}

	public void onTCPMapClientRecvUpdateRoleCurTitle(TCPMapClient peer, Packet.S2M.UpdateRoleCurTitle packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update cur title " + packet.getTitleID() + " titleType " + packet.getTitleType());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateCurTitle(packet.getRoleID(), packet.getTitleID(), packet.getTitleType());
		});
	}
	public void onTCPMapClientRecvUpdatePetAchieve(TCPMapClient peer, Packet.S2M.UpdatePetAchieve packet)
	{
		ms.getLogger().debug("receive gs role " + packet.getRoleID() + " update pet achieves");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdatePetAchieves(packet.getRoleID(), packet.getAchieves());
		});
	}
	public void onTCPMapClientRecvUpdateCurUniqueSkill(TCPMapClient peer, Packet.S2M.UpdateCurUniqueSkill packet)
	{
		ms.getLogger().debug("receive gs role " + packet.getRoleID() + " update curuniqueskill " + packet.getCurUniqueSkill());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateCurUniqueSkill(packet.getRoleID(), packet.getCurUniqueSkill());
		});
	}

	public void onTCPMapClientRecvSetPetAlter(TCPMapClient peer, Packet.S2M.SetPetAlter packet)
	{
		ms.getLogger().debug("receive gs role " + packet.getRoleID() + " enter pet lift map set pet alter !");
		ms.getMapManager().addServerEvent(() -> 
		{
			ms.getMapManager().roleSetPetAlter(packet.getRoleID(), packet.getPet(), packet.getHost());
		});
	}

	public void onTCPMapClientRecvCarEnterMap(TCPMapClient peer, Packet.S2M.CarEnterMap packet)
	{
		ms.getLogger().info("receive gs car " + packet.getOwnerID() + " enter map[" + packet.getCarInfo().mapID + ", " + packet.getCarInfo().mapInstance + "], location=" + GameData.toString(packet.getCarInfo().location.position));
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().escortCarEnterMap(packet.getCarInfo(), packet.getOwnerID(), packet.getOwnerName(), packet.getTeamCarCnt(), packet.getTeam(), packet.getSectID());
		});
	}

	public void onTCPMapClientRecvCarLeaveMap(TCPMapClient peer, Packet.S2M.CarLeaveMap packet)
	{
		ms.getLogger().info("receive gs car " + packet.getCarID() + " leave map ...");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().escortCarLeaveMap(packet.getCarID());
		});
	}

	public void onTCPMapClientRecvCarUpdateTeamCarCnt(TCPMapClient peer, Packet.S2M.CarUpdateTeamCarCnt packet)
	{
		ms.getLogger().debug("receive gs car " + packet.getCarID() + " update teamcar cnt " + packet.getCarCnt());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().escortCarUpdateTeamCarCnt(packet.getCarID(), packet.getCarCnt());
		});
	}
	
	public void onTCPMapClientRecvUpdateRoleCarBehavior(TCPMapClient peer, Packet.S2M.UpdateRoleCarBehavior packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() +  " update car behavior " + packet.getCarOwner() + " , " + packet.getCarRobber());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().updateRoleCarBehavior(packet.getRoleID(), packet.getCarOwner(), packet.getCarRobber());
		});
	}

	public void onTCPMapClientRecvRoleUseItemSkill(TCPMapClient peer, Packet.S2M.RoleUseItemSkill packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " use item " + packet.getItemID() + " skill");
		ms.getMapManager().addServerEvent(() ->
		{
			boolean ok = ms.getMapManager().roleUseItemSkill(packet.getRoleID(), packet.getItemID(), packet.getPos(), packet.getRotation(), packet.getTargetID(), packet.getTargetType(), packet.getOwnerID(), packet.getTimeTick());
			this.notifyGSSyncRoleUseItemSkillSuc(packet.getRoleID(), packet.getItemID(), ok ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED);
		});
	}

	public void onTCPMapClientRecvRoleRename(TCPMapClient peer, Packet.S2M.RoleRename packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " rename " + packet.getNewName());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleRename(packet.getRoleID(), packet.getNewName());
		});
	}

	public void onTCPMapClientRecvAddPetHp(TCPMapClient peer, Packet.S2M.AddPetHp packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " pet " + packet.getPetID() + " add hp " + packet.getHp());
		ms.getMapManager().addServerEvent(() -> {
			ms.getMapManager().petAddHp(packet.getRoleID(), packet.getPetID(), packet.getHp());
		});
	}

	public void onTCPMapClientRecvSyncCurRideHorse(TCPMapClient peer, Packet.S2M.SyncCurRideHorse packet)
	{
		ms.getLogger().trace("receive gs sync role " + packet.getRoleID() + " cur ride horse " + packet.getHorseID());
		ms.getMapManager().addServerEvent(() -> {
			ms.getMapManager().syncRoleCurRideHorse(packet.getRoleID(), packet.getHorseID());
		});
	}

	public void onTCPMapClientRecvUpdateMulHorse(TCPMapClient peer, Packet.S2M.UpdateMulHorse packet)
	{
		ms.getLogger().trace("receive gs sync role " + packet.getLeaderID() + " update mulhorse ...");
		ms.getMapManager().addServerEvent(() -> {
			ms.getMapManager().roleUpdateMulHorse(packet.getLeaderID(), packet.getPos(), packet.getMemberID());
		});
	}

	public void onTCPMapClientRecvChangeArmor(TCPMapClient peer, Packet.S2M.ChangeArmor packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " change cur armor");
		ms.getMapManager().addServerEvent(() -> {
			ms.getMapManager().roleChangeCurArmor(packet.getRoleID(), packet.getArmor());
		});
	}

	public void onTCPMapClientRecvUpdateArmorLevel(TCPMapClient peer, Packet.S2M.UpdateArmorLevel packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update armor level " + packet.getArmorLevel());
		ms.getMapManager().addServerEvent(() -> {
			ms.getMapManager().roleUpdateArmorLevel(packet.getRoleID(), packet.getArmorLevel());
		});
	}

	public void onTCPMapClientRecvUpdateArmorRank(TCPMapClient peer, Packet.S2M.UpdateArmorRank packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update armor rank " + packet.getArmorRank());
		ms.getMapManager().addServerEvent(() -> {
			ms.getMapManager().roleUpdateArmorRank(packet.getRoleID(), packet.getArmorRank());
		});
	}

	public void onTCPMapClientRecvUpdateArmorRune(TCPMapClient peer, Packet.S2M.UpdateArmorRune packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update armor runes " + packet.getSoltGroupIndex() + " , " + packet.getArmorRune());
		ms.getMapManager().addServerEvent(() -> {
			ms.getMapManager().roleUpdateArmorRunes(packet.getRoleID(), packet.getSoltGroupIndex(), packet.getArmorRune());
		});
	}

	public void onTCPMapClientRecvUpdateTalentPoint(TCPMapClient peer, Packet.S2M.UpdateTalentPoint packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update armor talent point " + packet.getArmorTalentPoint());
		ms.getMapManager().addServerEvent(() -> {
			ms.getMapManager().roleUpdateArmorTalent(packet.getRoleID(), packet.getArmorTalentPoint());
		});
	}

	public void onTCPMapClientRecvSpawnSceneMonster(TCPMapClient peer, Packet.S2M.SpawnSceneMonster packet)
	{
		ms.getLogger().trace("receive gs sync role " + packet.getRoleID() + " spawn monster pointID " + packet.getPointID());
		ms.getMapManager().addServerEvent(() -> {
			ms.getMapManager().roleSpawnSceneMonster(packet.getMapID(), packet.getMapInstance(), packet.getRoleID(), packet.getPointID());
		});
	}

	public void onTCPMapClientRecvClearSceneMonster(TCPMapClient peer, Packet.S2M.ClearSceneMonster packet)
	{
		ms.getLogger().trace("receive gs clear role " + packet.getRoleID() + " scene monster " + packet.getMonsterID());
		ms.getMapManager().addServerEvent(() -> {
			ms.getMapManager().roleClearSceneMonster(packet.getRoleID(), packet.getMonsterID());
		});
	}

	public void onTCPMapClientRecvResetSectGroupMap(TCPMapClient peer, Packet.S2M.ResetSectGroupMap packet)
	{
		ms.getLogger().debug("receive gs reset sect group map id " + packet.getMapID() + " instanceID " + packet.getInstanceID());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().resetSectGroupMap(packet.getMapID(), packet.getInstanceID(), packet.getProgress(), packet.getKillNum(), packet.getDamageRank());
		});
	}

	public void onTCPMapClientRecvUpdateHorseSkill(TCPMapClient peer, Packet.S2M.UpdateHorseSkill packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update horse skill " + packet.getSkillID() + " , " + packet.getSkillLvl());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateHorseSkill(packet.getRoleID(), packet.getSkillID(), packet.getSkillLvl());
		});
	}

	public void onTCPMapClientRecvUpdateStayWith(TCPMapClient peer, Packet.S2M.UpdateStayWith packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getLeaderID() + " update stay with state leader " + packet.getMulRoleInfo().leader);
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateStayWith(packet.getLeaderID(), packet.getMulRoleInfo());
		});
	}

	public void onTCPMapClientRecvUpdateRoleWeaponSkill(TCPMapClient peer, Packet.S2M.UpdateRoleWeaponSkill packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update weapon skill " + packet.getWeaponID());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateWeaponSkill(packet.getRoleID(), packet.getWeaponID(), packet.getSkills());
		});
	}

	public void onTCPMapClientRecvUpdateRoleWeaponTalent(TCPMapClient peer, Packet.S2M.UpdateRoleWeaponTalent packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update weapon talent " + packet.getWeaponID());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateWeaponTalent(packet.getRoleID(), packet.getWeaponID(), packet.getTalents());
		});
	}

	public void onTCPMapClientRecvCreateWorldMapFlag(TCPMapClient peer, Packet.S2M.CreateWorldMapFlag packet)
	{
		ms.getLogger().debug("receive gs map " + packet.getMapID() + " create flag");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().createWorldMapFlag(packet.getMapID(), packet.getFlagPoint(), packet.getFlagId(), packet.getMonsterPointId(), packet.getSect());
		});
	}

	public void onTCPMapClientRecvInitWorldMapFlag(TCPMapClient peer, Packet.S2M.InitWorldMapFlag packet)
	{
		ms.getLogger().debug("receive gs init map world map flag");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().initWorldMapFlag(packet.getMapflags());
		});
	}

	public void onTCPMapClientRecvSyncMapFlagInfo(TCPMapClient peer, Packet.S2M.SyncMapFlagInfo packet)
	{
		ms.getLogger().debug("receive gs sync map world map flag info");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().updateMapFlagInfo(packet.getMapID(), packet.getSect());
		});
	}

	public void onTCPMapClientRecvSyncRoleItemProps(TCPMapClient peer, Packet.S2M.SyncRoleItemProps packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update item props");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateItemProps(packet.getRoleID(), packet.getProps());
		});
	}

	public void onTCPMapClientRecvSyncTaskDrop(TCPMapClient peer, Packet.S2M.SyncTaskDrop packet)
	{
		ms.getLogger().debug("receive gs role " + packet.getRoleID() + " sync task drop " + packet.getTaskDrop());
		ms.getMapManager().addServerEvent(() -> 
		{
			ms.getMapManager().syncRoleTaskDrop(packet.getRoleID(), packet.getTaskDrop());
		});
	}

	public void onTCPMapClientRecvUpdateRolePetSkill(TCPMapClient peer, Packet.S2M.UpdateRolePetSkill packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update pet skill " + packet.getPetId());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdatePetSkill(packet.getRoleID(), packet.getPetId(), packet.getSkills());
		});
	}

	public void onTCPMapClientRecvSyncWeaponOpen(TCPMapClient peer, Packet.S2M.SyncWeaponOpen packet)
	{
		ms.getLogger().info("receive gs role " + packet.getRoleID() + " weapon " + packet.getWeaponID() + " open");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleWeaponOpen(packet.getRoleID(), packet.getWeaponID());
		});
	}

	public void onTCPMapClientRecvWorldBossPop(TCPMapClient peer, Packet.S2M.WorldBossPop packet)
	{
		ms.getLogger().trace("receive gs world map [" + packet.getMapID() + " , " + packet.getMapInstance() + "] boss " + packet.getBossID() + " pop " + packet.getIndex());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().worldBossPop(packet.getMapID(), packet.getMapInstance(), packet.getBossID(), packet.getIndex());
		});
	}

	public void onTCPMapClientRecvPickUpResult(TCPMapClient peer, Packet.S2M.PickUpResult packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " pick up drop result " + packet.getSuccess());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().rolePickUpResult(packet.getRoleID(), packet.getDropIDs(), packet.getSuccess() == 1);
		});
	}

	public void onTCPMapClientRecvUnSummonCurPets(TCPMapClient peer, Packet.S2M.UnSummonCurPets packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " unsummon pets");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUnSummonFightPets(packet.getRoleID());
		});
	}

	public void onTCPMapClientRecvUpdateRolePerfectDegree(TCPMapClient peer, Packet.S2M.UpdateRolePerfectDegree packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update perfect degree " + packet.getPerfectDegree());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdatePerfectDegree(packet.getRoleID(), packet.getPerfectDegree());
		});
	}

	public void onTCPMapClientRecvUpdateCurPetSpirit(TCPMapClient peer, Packet.S2M.UpdateCurPetSpirit packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update pet " + packet.getPetID() + " cur spirit");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateCurPetSpirit(packet.getRoleID(), packet.getPetID(), packet.getIndex(), packet.getSpirit());
		});
	}

	public void onTCPMapClientRecvStartMarriageParade(TCPMapClient peer, Packet.S2M.StartMarriageParade packet)
	{
		ms.getLogger().info("receive gs man " + packet.getMan().id + " woman " + packet.getWoman().id + " start marriage parade in map " + packet.getMapID() + " , " + packet.getMapInstance());
		ms.getMapManager().addServerEvent(() -> 
		{
			ms.getMapManager().roleStartMarriageParade(packet.getMapID(), packet.getMapInstance(), packet.getCarID(), packet.getMan(), packet.getWoman());
		});
	}

	public void onTCPMapClientRecvUpdateRoleHeirloomDisplay(TCPMapClient peer, Packet.S2M.UpdateRoleHeirloomDisplay packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " update heirloom display " + packet.getDisplay());
		ms.getMapManager().addServerEvent(() -> 
		{
			ms.getMapManager().roleUpdateHeirloomDisplay(packet.getRoleID(), packet.getDisplay() == 1);
		});
	}

	public void onTCPMapClientRecvSetWeaponForm(TCPMapClient peer, Packet.S2M.SetWeaponForm packet)
	{
		ms.getLogger().trace("receive gs role " + packet.getRoleID() + " set weapon " + packet.getWeaponID() + " form " + packet.getForm());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleSetWeaponForm(packet.getRoleID(), packet.getWeaponID(), packet.getForm());
		});
	}

	public void onTCPMapClientRecvStartMarriageBanquet(TCPMapClient peer, Packet.S2M.StartMarriageBanquet packet)
	{
		ms.getLogger().info("receive gs role " + packet.getRoleID() + " start marriage banquet " + packet.getBanquet());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleStartMarriageBanquet(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getBanquet());
		});
	}

	public void onTCPMapClientRecvUpdateRoleMarriageSkillInfo(TCPMapClient peer, Packet.S2M.UpdateRoleMarriageSkillInfo packet)
	{
		ms.getLogger().info("receive gs role " + packet.getRoleID() + " update marriage skills");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateMarriageSkillInfo(packet.getRoleID(), packet.getMarriageSkills(), packet.getMarriagePartnerId());
		});
	}

	public void onTCPMapClientRecvUpdateRoleMarriageSkillLevel(TCPMapClient peer, Packet.S2M.UpdateRoleMarriageSkillLevel packet)
	{
		ms.getLogger().info("receive gs role " + packet.getRoleID() + " update marriage skill " + packet.getSkillId() + " level " + packet.getSkillLevel());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateMarriageSkillLevel(packet.getRoleID(), packet.getSkillId(), packet.getSkillLevel());
		});
	}

	public void onTCPMapClientRecvMarriageLevelChange(TCPMapClient peer, Packet.S2M.MarriageLevelChange packet)
	{
		ms.getLogger().info("receive gs role " + packet.getRoleID() + " marriage level change " + packet.getNewLevel());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleMarriageLevelChange(packet.getRoleID(), packet.getNewLevel());
		});
	}

	public void onTCPMapClientRecvRoleDMGTransferUpdate(TCPMapClient peer, Packet.S2M.RoleDMGTransferUpdate packet)
	{
		ms.getLogger().debug("receive gs role " + packet.getRoleID() + " dmgTransfer point lvls update ");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleDMGTransferPointLvlsUpdate(packet.getRoleID(), packet.getPointLvls());
		});
	}

	public void onTCPMapClientRecvCreateRobotHero(TCPMapClient peer, Packet.S2M.CreateRobotHero packet)
	{
		ms.getLogger().debug("receive gs create robot hero " + packet.getRole().base.roleID + " in map [" + packet.getMapID() + " , " + packet.getMapInstance() + "] spawn point " + packet.getSpawnPoint());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().createRobotHero(packet.getMapID(), packet.getMapInstance(), packet.getRole(), packet.getPets(), packet.getSpawnPoint());
		});
	}

	public void onTCPMapClientRecvDestroyRobotHero(TCPMapClient peer, Packet.S2M.DestroyRobotHero packet)
	{
		ms.getLogger().debug("receive gs destroy robot hero " + packet.getRoleID() + " in map [" + packet.getMapID() + " , " + packet.getMapInstance() + "]");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().destroyRobotHero(packet.getMapID(), packet.getMapInstance(), packet.getRoleID());
		});
	}

	public void onTCPMapClientRecvSyncCreateStele(TCPMapClient peer, Packet.S2M.SyncCreateStele packet)
	{
		ms.getLogger().info("receive gs create stele [" + packet.getSteleType() + " , " + packet.getIndex() +"]" + " remainTimes " + packet.getRemainTimes());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().createStele(packet.getSteleType(), packet.getIndex(), packet.getRemainTimes());
		});
	}

	public void onTCPMapClientRecvSyncDestroyStele(TCPMapClient peer, Packet.S2M.SyncDestroyStele packet)
	{
		ms.getLogger().info("receive gs destroy stele [" + packet.getSteleType() + " , " + packet.getIndex() +"]");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().destroyStele(packet.getSteleType(), packet.getIndex());
		});
	}

	public void onTCPMapClientRecvSyncJusticeNpcShow(TCPMapClient peer, Packet.S2M.SyncJusticeNpcShow packet)
	{
		ms.getLogger().info("receive gs show justice npc [" + packet.getPosIndex() +"]");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().addJusticeNpc(packet.getPosIndex());
		});
	}

	public void onTCPMapClientRecvSyncJusticeNpcLeave(TCPMapClient peer, Packet.S2M.SyncJusticeNpcLeave packet)
	{
		ms.getLogger().info("receive gs delete justice npc [" + packet.getPosIndex() +"]");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().delJusticeNpc(packet.getPosIndex());
		});
	}

	public void onTCPMapClientRecvSyncEmergencyLastTime(TCPMapClient peer, Packet.S2M.SyncEmergencyLastTime packet)
	{
		ms.getLogger().info("receive gs sync emergency map [" + packet.getMapID() + ", " + packet.getMapInstanceId() + "] last time " + packet.getLastTime());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().syncEmergencyLastTime(packet.getMapID(), packet.getMapInstanceId(), packet.getLastTime());
		});
	}

	public void onTCPMapClientRecvSyncRoleVipLevel(TCPMapClient peer, Packet.S2M.SyncRoleVipLevel packet)
	{
		ms.getLogger().info("receive gs role " + packet.getRoleID() + " sync vip level " + packet.getVipLevel());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateVipLevel(packet.getRoleID(), packet.getVipLevel());
		});
	}

	public void onTCPMapClientRecvSyncRoleCurWizardPet(TCPMapClient peer, Packet.S2M.SyncRoleCurWizardPet packet)
	{
		ms.getLogger().info("receive gs role " + packet.getRoleID() + " sync cur wizard pet " + packet.getWizardPet());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateWizardPet(packet.getRoleID(), packet.getWizardPet());
		});
	}

	public void onTCPMapClientRecvUpdateRoleSpecialCardAttr(TCPMapClient peer, Packet.S2M.UpdateRoleSpecialCardAttr packet)
	{
		ms.getLogger().debug("receive gs update role " + packet.getRoleID() + " sepcail card ");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateSpecialCardAttr(packet.getRoleID(), packet.getAttrs());
		});
	}

	public void onTCPMapClientRecvRoleShowProps(TCPMapClient peer, Packet.S2M.RoleShowProps packet)
	{
		ms.getLogger().debug("receive gs role " + packet.getRoleID() + " show props " + packet.getPropID());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleShowProps(packet.getRoleID(), packet.getPropID());
		});
	}

	public void onTCPMapClientRecvRoleRedNamePunish(TCPMapClient peer, Packet.S2M.RoleRedNamePunish packet)
	{
		ms.getLogger().info("receive gs role " + packet.getRoleID() + " red name punish");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleRedNamePunish(packet.getRoleID());
		});
	}

	public void onTCPMapClientRecvGMCommand(TCPMapClient peer, Packet.S2M.GMCommand packet)
	{
		ms.getLogger().debug("receive gs role " + packet.getRoleID() + " gm command");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleGMCommend(packet.getRoleID(), packet.getIType(), packet.getIArg1(), packet.getIArg2(), packet.getIArg3(), packet.getSArg());
		});
	}

	public int getTCPGlobalMapServerMaxConnectionIdleTime()
	{
		return 30 * 1000;
	}

	public void onTCPGlobalMapServerOpen(TCPGlobalMapServer peer)
	{
		ms.getLogger().info("TCPGlobalMAPServer open on " + peer.getListenAddr());
	}

	public void onTCPGlobalMapServerOpenFailed(TCPGlobalMapServer peer, ket.kio.ErrorCode errcode)
	{
		ms.getLogger().warn("TCPGlobalMAPServer open on " + peer.getListenAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPGlobalMapServerClose(TCPGlobalMapServer peer, ket.kio.ErrorCode errcode)
	{
		ms.getLogger().info("TCPGlobalMAPServer close on " + peer.getListenAddr() + ", errcode=" + errcode);
	}

	public void onTCPGlobalMapServerSessionOpen(TCPGlobalMapServer peer, int sessionid, NetAddress addrClient)
	{
		ms.getLogger().info("TCPGlobalMAPServer on session " + sessionid  + " open, client " + addrClient);
	}

	public void onTCPGlobalMapServerSessionClose(TCPGlobalMapServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		ms.getLogger().info("TCPGlobalMAPServer on session " + sessionid  + " close, errcode=" + errcode);
		ms.getMapManager().addServerEvent(() ->
		{
			Set<Integer> zones = table.getZonesBySessionID(sessionid);
			if (zones != null)
				ms.getMapManager().clearGlobalMapZoneRolesOnSessionClose(sessionid, zones);
			table.onSessionClose(sessionid);
		});
	}

	public void onTCPGlobalMapServerRecvKeepAlive(TCPGlobalMapServer peer, Packet.S2GM.KeepAlive packet, int sessionid)
	{
		ms.getLogger().debug("receive gs session " + sessionid + " gameserver " + packet.getHello() + " keepalive packet");
	}

	public void onTCPGlobalMapServerRecvWhoAmI(TCPGlobalMapServer peer, Packet.S2GM.WhoAmI packet, int sessionid)
	{
		ms.getLogger().info("receive gs session " + sessionid + " gameserver " + packet.getServerId() + " whoami packet");
		if (GameData.getAreaIdFromGSId(packet.getServerId()) != ms.getConfig().group)
		{
			ms.getLogger().warn("close gs session " + sessionid + " for gameserver " + packet.getServerId() + " not match area id " + ms.getConfig().group);
			tgms.closeSession(sessionid);
			return;
		}
		
		Integer oldSession = table.tryCloseSessionByServerID(packet.getServerId());
		if(oldSession != null)
		{
			ms.getLogger().warn("close gs session " + oldSession + " on gameserver [" + packet.getServerId() + " " + sessionid+ "] announce");
			tgms.closeSession(oldSession);
		}
		
		if (!table.onSessionAnnounce(sessionid, packet.getServerId(), packet.getZones()))
		{
			ms.getLogger().warn("close gs session " + sessionid + " for gameserver " + packet.getServerId() + " or zones " + packet.getZones() + " clash");
			tgms.closeSession(sessionid);
			return;
		}
		ms.getLogger().info("global map sync gs " + sessionid + " maps");
		tgms.sendPacket(sessionid, new Packet.GM2S.SyncGlobalMaps(ms.getConfig().id, ms.getDeployConf().getMapDelpoy().getDeployMaps()));
	}

	public void onTCPGlobalMapServerRecvReportTimeOffset(TCPGlobalMapServer peer, Packet.S2GM.ReportTimeOffset packet, int sessionid)
	{
		ms.getLogger().info("receive gs session " + sessionid + " report gs time offset " + packet.getTimeOffset() + ", current time offset " + GameTime.getServerTimeOffset());
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if (serverID == null)
			return;
		GameTime.setServerTimeOffset(packet.getTimeOffset());
	}

	public void onTCPGlobalMapServerRecvLuaChannel(TCPGlobalMapServer peer, Packet.S2GM.LuaChannel packet, int sessionid)
	{
	}

	public void onTCPGlobalMapServerRecvStrChannel(TCPGlobalMapServer peer, Packet.S2GM.StrChannel packet, int sessionid)
	{
		try
		{
			Integer serverID = table.getServerIDBySessionID(sessionid);
			if (serverID == null)
				return;
			int roleID = packet.getRoleID();
			String data = packet.getData();
			
//			ms.getLogger().trace("receive gs " + serverID + " role " + roleID + " lua str packet : " + data);
//			if (data.startsWith("1role_move") || data.startsWith("1role_stopmove") || data.startsWith("1pet_move") || data.startsWith("1pet_stopmove") || data.startsWith("1client_ping_start"))
//				ms.getLogger().trace("receive role " + roleID + " lua str packet : " + data);
//			else
//				ms.getLogger().debug("receive role " + roleID + " lua str packet : " + data + ", at tick [" + ms.getMapManager().getTimeTick().tickLine + " , " + ms.getMapManager().getTimeTick().outTick + "]" + " at " + GameTime.getTimeMillis() + " , " + ms.getMapManager().getMapLogicTime());

			MapRole role = ms.getMapManager().getMapRole(roleID);
			if (role == null)
				return;

			String packetName = SStream.detectPacketName(data);
			if (packetName == null)
				return;
			
			if(GameData.getInstance().containsMapRecvChars(packetName))
				ms.getLogger().debug("receive gs " + serverID + " role " + roleID + " lua str packet : " + data);
			else
				ms.getLogger().trace("receive gs " + serverID + " role " + roleID + " lua str packet : " + data);
			
			@SuppressWarnings("rawtypes")
			Class cls = SBean.getStrPacketClass(packetName);
			if (cls == null)
				return;

			SStream.IStreamable stream = SStream.decode(data, cls);
			ms.getMapManager().addClientEvent(() ->
			{
				try
				{
					if (!role.active || role.curMap == null || role.curMap.isTimeOut())
						return;

					Method mtd = StringChannelHandler.class.getMethod("onRecv_" + packetName, MapRole.class, SStream.IStreamable.class);
					mtd.invoke(strChannelHandler, role, stream);
					//ms.getLogger().debug("@@@@ " + ms.getMapManager().getMapLogicTime() + " handle role " + roleID + " end : " + data + " dTime " + (ms.getMapManager().getMapLogicTime() - receiveTick));
				}
				catch (Exception ex)
				{
					ms.getLogger().warn(ex.getMessage() + " lua str packet : " + data, ex);
				}
			});
		}
		catch (Exception ex)
		{
			ms.getLogger().warn(ex.getMessage() + " lua str packet : " + packet.getData(), ex);
		}
	}

	public void onTCPGlobalMapServerRecvEnterMap(TCPGlobalMapServer peer, Packet.S2GM.EnterMap packet, int sessionid)
	{
		ms.getLogger().info("global map receive gs session " + sessionid + " role " + packet.getRole().base.roleID + " enter map[" + packet.getMapId() + ", " + packet.getMapInstance() + "], location=" + GameData.toString(packet.getLocation().position) + " pets " + packet.getPets().keySet()+ " curRideHorse " + packet.getCurRideHorse());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleEnterMap(packet.getRole(), packet.getMapId(), packet.getMapInstance(), packet.getLocation(), packet.getHp(), packet.getSp(), packet.getArmorVal(),
											packet.getBuffs(), packet.getPets(), packet.getPetSeq(), packet.getPethost(), packet.getPkInfo(), packet.getTeam(), 
											packet.getCurRideHorse(), packet.getAlterState(), packet.getMainSpawnPos() == 1, packet.getMulRoleInfo(), packet.getDayFailedStreak(), packet.getVipLevel(), packet.getCurWizardPet(), packet.getCanTakeDrop());
		});
	}

	public void onTCPGlobalMapServerRecvLeaveMap(TCPGlobalMapServer peer, Packet.S2GM.LeaveMap packet, int sessionid)
	{
		ms.getLogger().info("global map receive gs session " + sessionid + " role " + packet.getRoleID() + " leave global map...");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleLeaveMap(packet.getRoleID());
		});
	}

	public void onTCPGlobalMapServerRecvUpdateActive(TCPGlobalMapServer peer, Packet.S2GM.UpdateActive packet, int sessionid)
	{
		ms.getLogger().info("global map receive gs session " +  sessionid + " role " + packet.getRoleID() + " update active ... " + packet.getActive());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateActive(packet.getRoleID(), packet.getActive() > 0);
		});
	}

	public void onTCPGlobalMapServerRecvAddHp(TCPGlobalMapServer peer, Packet.S2GM.AddHp packet, int sessionid)
	{
		ms.getLogger().trace("global map receive gs session " + sessionid + " role " + packet.getRoleID() + " add hp " + packet.getHp());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleAddHp(packet.getRoleID(), packet.getHp());
		});
	}

	public void onTCPGlobalMapServerRecvRoleUseItemSkill(TCPGlobalMapServer peer, Packet.S2GM.RoleUseItemSkill packet, int sessionid)
	{
		ms.getLogger().trace("global map receive gs session " + sessionid + packet.getRoleID() + " use item " + packet.getItemID() + " skill");
		ms.getMapManager().addServerEvent(() ->
		{
			boolean ok = ms.getMapManager().roleUseItemSkill(packet.getRoleID(), packet.getItemID(), packet.getPos(), packet.getRotation(), packet.getTargetID(), packet.getTargetType(), packet.getOwnerID(), packet.getTimeTick());
			this.notifyGSSyncRoleUseItemSkillSuc(packet.getRoleID(), packet.getItemID(), ok ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED);
		});
	}

	public void onTCPGlobalMapServerRecvAddPetHp(TCPGlobalMapServer peer, Packet.S2GM.AddPetHp packet, int sessionid)
	{
		ms.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleID() + " pet " + packet.getPetID() + " add hp " + packet.getHp());
		ms.getMapManager().addServerEvent(() -> {
			ms.getMapManager().petAddHp(packet.getRoleID(), packet.getPetID(), packet.getHp());
		});
	}

	public void onTCPGlobalMapServerRecvStartMine(TCPGlobalMapServer peer, Packet.S2GM.StartMine packet, int sessionid)
	{
		ms.getLogger().trace("receive gs session + " + sessionid + " role " + packet.getRoleID() + " start mine " + packet.getMineID() + ", " + packet.getMineInstance());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleStartMine(packet.getRoleID(), packet.getMineID(), packet.getMineInstance());
		});
	}

	public void onTCPGlobalMapServerRecvResetLocation(TCPGlobalMapServer peer, Packet.S2GM.ResetLocation packet, int sessionid)
	{
		ms.getLogger().info("receive gs session " + sessionid + " role " + packet.getRoleID() + " reset location ... ");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleResetLocation(packet.getRoleID(), packet.getLocation());
		});
	}

	public void onTCPGlobalMapServerRecvUpdateCurSkills(TCPGlobalMapServer peer, Packet.S2GM.UpdateCurSkills packet, int sessionid)
	{
		ms.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleID() + " update current skills...");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateCurSkills(packet.getRoleID(), packet.getSkills());
		});
	}

	public void onTCPGlobalMapServerRecvUpdateCurSpirit(TCPGlobalMapServer peer, Packet.S2GM.UpdateCurSpirit packet, int sessionid)
	{
		ms.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleID() + " updateCurSpirit");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateCurSpirits(packet.getRoleID(), packet.getCurSpirit());
		});
	}

	public void onTCPGlobalMapServerRecvPickUpResult(TCPGlobalMapServer peer, Packet.S2GM.PickUpResult packet, int sessionid)
	{
		ms.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleID() + " pick up drop result " + packet.getSuccess());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().rolePickUpResult(packet.getRoleID(), packet.getDropIDs(), packet.getSuccess() == 1);
		});
	}

	public void onTCPGlobalMapServerRecvUpdateRoleMarriageSkillInfo(TCPGlobalMapServer peer, Packet.S2GM.UpdateRoleMarriageSkillInfo packet, int sessionid)
	{
		ms.getLogger().info("receive gs session " + sessionid + " role " + packet.getRoleID() + " update marriage skills");
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateMarriageSkillInfo(packet.getRoleID(), packet.getMarriageSkills(), packet.getMarriagePartnerId());
		});
	}

	public void onTCPGlobalMapServerRecvUpdateRoleMarriageSkillLevel(TCPGlobalMapServer peer, Packet.S2GM.UpdateRoleMarriageSkillLevel packet, int sessionid)
	{
		ms.getLogger().info("receive gs session " + sessionid + " role " + packet.getRoleID() + " update marriage skill " + packet.getSkillId() + " level " + packet.getSkillLevel());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleUpdateMarriageSkillLevel(packet.getRoleID(), packet.getSkillId(), packet.getSkillLevel());
		});
	}

	public void onTCPGlobalMapServerRecvRoleRevive(TCPGlobalMapServer peer, Packet.S2GM.RoleRevive packet, int sessionid)
	{
		ms.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleID() + " revive fullhp: " + packet.getFullHp());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().roleRevive(packet.getRoleID(), packet.getFullHp() == 1);
		});
	}

	public void onTCPFightMapClientOpen(TCPFightMapClient peer)
	{
		ms.getLogger().info("tcpfightmapclient open connect to " + peer.getServerAddr() + " regist fs maps");
		mapcount.resetCount();
		tfmc.sendPacket(new Packet.GM2F.WhoAmI(ms.getConfig().group, ms.getDeployConf().getMapDelpoy().getDeployMaps()));
	}

	public void onTCPFightMapClientOpenFailed(TCPFightMapClient peer, ket.kio.ErrorCode errcode)
	{
		if (mapcount.increaseCount())
			ms.getLogger().warn("tcpfightmapclient open connect to " + peer.getServerAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPFightMapClientClose(TCPFightMapClient peer, ket.kio.ErrorCode errcode)
	{
		ms.getLogger().warn("tcpfightmapclient close on " + peer.getServerAddr() + " , errcode=" + errcode);
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().resetMap();	
		});
	}

	public void onTCPFightMapClientRecvKeepAlive(TCPFightMapClient peer, Packet.F2GM.KeepAlive packet)
	{
		
	}

	public void onTCPFightMapClientRecvCreateMapCopyReq(TCPFightMapClient peer, Packet.F2GM.CreateMapCopyReq packet)
	{
		ms.getLogger().info("receive fs create map copy[" + packet.getMapID() + " , " + packet.getMapInstance() + "]");
		ms.getMapManager().addServerEvent(() ->
		{
			boolean ok = ms.getMapManager().createForceWarMap(packet.getMapID(), packet.getMapInstance());
			this.notifyFightCreateMapCopyRes(packet.getMapType(), ok ? packet.getMapInstance() : 0);
		});
	}

	public void onTCPFightMapClientRecvEndMapCopy(TCPFightMapClient peer, Packet.F2GM.EndMapCopy packet)
	{
		ms.getLogger().info("receive fs end map copy id " + packet.getMapID() + " instanceID " + packet.getMapInstance());
		ms.getMapManager().addServerEvent(() ->
		{
			ms.getMapManager().destoryMap(packet.getMapID(), packet.getMapInstance(), true);
		});
	}

//	public void onTCPMapClientRecvSyncRoleCanNotTakeDrop(TCPMapClient peer, Packet.S2M.SyncRoleCanNotTakeDrop packet)
//	{
//		ms.getLogger().debug("receive gs role " + packet.getRoleID() + " can not take drop");
//		ms.getMapManager().addServerEvent(() ->
//		{
//			ms.getMapManager().roleChangeCanNotTakeDrop(packet.getRoleID());
//		});
//	}
//
	//// end handlers.	

	void keepAlive(int timeTick)
	{
		if (timeTick % 15 == this.keepaliveRandomTime)
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				tfmc.sendPacket(new Packet.GM2F.KeepAlive());
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.KeepAlive());
			}
		}
	}

	void sendLuaPacket(int roleID, String data)
	{
//		if (data.startsWith("|map|nearby|move") || data.startsWith("|map|nearby|stopmove"))
//			ms.getLogger().trace("send role " + roleID + " lua channel packet:[" + data + "]");
//		else
//			ms.getLogger().debug("send role " + roleID + " lua channel packet:[" + data + "]");
		ms.getLogger().trace("send role " + roleID + " lua channel packet:[" + data + "]");
		stat.sendClientStrMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
					tgms.sendPacket(sessionid, new Packet.GM2S.LuaChannel(roleID, data));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.LuaChannel(roleID, data));
			}
		});
	}

	void sendStrPacket(int roleID, SStream.IStrPacket packet)
	{
		stat.sendClientStrMsgCounter.incrementAndGet();
//		stat.strPackets.compute(packet.getPacketName(), (k, v) -> v == null ? 1 : v + 1);
		this.addSendEvent(() -> 
		{
			String data = SStream.encode(packet);
			if(GameData.getInstance().containsMapSendChars(packet.getPacketName()))
				ms.getLogger().debug("send role " + roleID + " lua channel packet:[" + data + "]");
			else
				ms.getLogger().trace("send role " + roleID + " lua channel packet:[" + data + "]");
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
					tgms.sendPacket(sessionid, new Packet.GM2S.StrChannel(roleID, data));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.StrChannel(roleID, data));	
			}
		});
	}
	
	void broadcastStrPacket(Set<Integer> rolesID, SStream.IStrPacket packet)
	{
		stat.sendClientStrMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			String data = SStream.encode(packet);
			try
			{
				if(GameData.getInstance().containsMapSendChars(packet.getPacketName()))
					ms.getLogger().debug("broadcast roles " + rolesID + " lua channel packet:[" + data + "]");
				else
					ms.getLogger().trace("broadcast roles " + rolesID + " lua channel packet:[" + data + "]");
				if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
				{
					for(int roleID: rolesID)
					{
						Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
						if(sessionid != null)
							tgms.sendPacket(sessionid, new Packet.GM2S.StrChannel(roleID, data));
					}
				}
				else
				{
					tmc.sendPacket(new Packet.M2S.StrChannelBroadcast(rolesID, data));	
				}
			} 
			catch (Exception e)
			{
				ms.getLogger().warn(e.getMessage() + " broadcast lua channel packet:[" + data + "]");
			}
		});
	}
	
	void mapRoleReady(int roleId)
	{
		stat.sendClientNormalMsgCounter.incrementAndGet();
		this.addSendEvent(() ->
		{
			ms.getLogger().debug("notify role " + roleId + " map ready !");
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleId));
				if(sessionid != null)
					tgms.sendPacket(sessionid, new Packet.GM2S.MapRoleReady(roleId));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.MapRoleReady(roleId));
			}
		});
	}

	void nearbyRoleMove(Set<Integer> rids, int moveRoleID, SBean.Vector3 pos, int speed, SBean.Vector3F rotation, SBean.Vector3 target, SBean.TimeTick timeTick)
	{
		stat.sendClientNormalMsgCounter.incrementAndGet();
		this.addSendEvent(() ->
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				this.broadcastStrPacket(rids, new SBean.nearby_move_role(moveRoleID, pos, speed, rotation, target, timeTick));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.NearByRoleMove(rids, moveRoleID, pos, speed, rotation, target, timeTick));
			}
		});
	}
	
	//reduce map serialize packet
	void nearbyRoleStopMove(Set<Integer> rids, int stopRoleID, SBean.Vector3 pos, int speed, SBean.TimeTick timeTick)
	{
		stat.sendClientNormalMsgCounter.incrementAndGet();
		this.addSendEvent(() ->
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				this.broadcastStrPacket(rids, new SBean.nearby_stopmove_role(stopRoleID, pos, speed, timeTick));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.NearByRoleStopMove(rids, stopRoleID, pos, speed, timeTick));
			}
		});
	}
	
//	void nearbyRoleEnter(int rid, List<SBean.EnterDetail> roles)
//	{
//		stat.sendClientNormalMsgCounter.incrementAndGet();
//		this.addSendEvent(() ->
//		{
//			if (ms.getConfig().group < GameData.MAX_AREA_GS_INNER_ID)
//			{
//				
//			}
//			else
//			{
//				tmc.sendPacket(new Packet.M2S.NearByRoleEnter(rid, roles));
//			}
//		});
//	}
//	
//	void nearbyRoleLeave(int rid, Set<Integer> roles, int destory)
//	{
//		stat.sendClientNormalMsgCounter.incrementAndGet();
//		this.addSendEvent(() ->
//		{
//			if (ms.getConfig().group < GameData.MAX_AREA_GS_INNER_ID)
//			{
//				
//			}
//			else
//			{
//				tmc.sendPacket(new Packet.M2S.NearByRoleLeave(rid, roles, destory));
//			}
//		});
//	}
	
	void syncRoleLocation(int roleID, int mapID, int mapInstance, SBean.Location location)
	{
		ms.getLogger().trace("sync gs role " + roleID + " location");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
					tgms.sendPacket(sessionid, new Packet.GM2S.SyncLocation(roleID, mapID, mapInstance, location));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.SyncLocation(roleID, mapID, mapInstance, location));		
			}
		});
	}

	void syncRoleHP(int roleID, int mapID, int mapInstance, int hp, int hpMax)
	{
		ms.getLogger().trace("sync gs role " + roleID + " hp = " + hp + " hpMax " + hpMax);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
				{
					tgms.sendPacket(sessionid, new Packet.GM2S.SyncHp(roleID, mapID, mapInstance, hp, hpMax));
					tfmc.sendPacket(new Packet.GM2F.SyncHp(roleID, hp, hpMax));
				}
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.SyncHp(roleID, mapID, mapInstance, hp, hpMax));
			}
		});
	}
	
	void syncPetHp(int roleID, int pid, int mapID, int mapInstance, int hp, int hpMax)
	{
		ms.getLogger().trace("sync gs role " + roleID + " pet " + pid + " hp " + hp + " hpMax " + hpMax);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
					tgms.sendPacket(sessionid, new Packet.GM2S.SyncRolePetHp(roleID, pid, mapID, mapInstance, new SBean.Hp(hp, hpMax)));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.SyncRolePetHp(roleID, pid, mapID, mapInstance, new SBean.Hp(hp, hpMax)));
			}
		});
	}
	
	void syncRoleSp(int roleID, int mapID, int mapInstance, int sp)
	{
		ms.getLogger().trace("sync gs role " + roleID + " sp ");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> {
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.SyncRoleSp(roleID, mapID, mapInstance, sp));
			}
		});
	}
	
	void syncRoleArmorVal(int roleID, int mapID, int mapInstance, int armorVal, int armorValMax)
	{
		ms.getLogger().trace("sync gs role " + roleID + " armorVal " + armorVal + " , " + armorValMax);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> {
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
					tgms.sendPacket(sessionid, new Packet.GM2S.SyncArmorVal(roleID, mapID, mapInstance, armorVal, armorValMax));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.SyncArmorVal(roleID, mapID, mapInstance, armorVal, armorValMax));
			}
		});
	}
//	void addRoleAddExp(int roleID, int mapID, int mapInstance, int exp)
//	{
//		ms.getLogger().debug("add gs role " + roleID + " exp = " + exp);
//		stat.sendServerMsgCounter.incrementAndGet();
//		this.addSendEvent(() -> 
//		{
//			tmc.sendPacket(new Packet.M2S.AddExp(roleID, mapID, mapInstance, exp));
//		});
//	}

	void addRoleDrop(int roleID, int mapID, int mapInstance, Map<Integer, Integer> drops)
	{
		ms.getLogger().trace("sync gs role " + roleID + " add drops = " + drops);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() ->
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
					tgms.sendPacket(sessionid, new Packet.GM2S.AddDrops(roleID, mapID, mapInstance, drops));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.AddDrops(roleID, mapID, mapInstance, drops));
			}
		}); 
	}

	void rolePickUpRareDrops(int roleID, int mapID, int mapInstance, int dropId, SBean.DummyGoods drop, int monsterId)
	{
		ms.getLogger().trace("sync gs role " + roleID + " pick up drops ");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() ->
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
					tgms.sendPacket(sessionid, new Packet.GM2S.RolePickUpRareDrops(roleID, mapID, mapInstance, dropId, drop, monsterId));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.RolePickUpRareDrops(roleID, mapID, mapInstance, dropId, drop, monsterId));
			}
		});
	}

	void rolePickUpDrops(int roleID, int mapID, int mapInstance, Map<Integer, SBean.DummyGoods> drops)
	{
		ms.getLogger().trace("sync gs role " + roleID + " pick up drops ");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() ->
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
					tgms.sendPacket(sessionid, new Packet.GM2S.RolePickUpDrops(roleID, mapID, mapInstance, drops));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.RolePickUpDrops(roleID, mapID, mapInstance, drops));
			}
		});
	}
	
	void addRoleKill(int roleID, int mapID, int mapInstance, int targetType, int targetID, float weaponAdd, int killRole)
	{
		ms.getLogger().trace("sync gs role " + roleID + " add kill = " + targetType + ", " + targetID);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
					tgms.sendPacket(sessionid, new Packet.GM2S.AddKill(roleID, mapID, mapInstance, targetType, targetID, weaponAdd, killRole));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.AddKill(roleID, mapID, mapInstance, targetType, targetID, weaponAdd, killRole));
			}
		});
	}

	void syncDurability(int roleID, int mapID, int mapInstance, int wid, int durability)
	{
		ms.getLogger().trace("sync gs role " + roleID + " wid = " + wid + " durability = " + durability);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
					tgms.sendPacket(sessionid, new Packet.GM2S.SyncDurability(roleID, mapID, mapInstance, wid, durability));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.SyncDurability(roleID, mapID, mapInstance, wid, durability));
			}
		});
	}

	void syncEndMine(int roleID, int mapID, int mapInstance, int mineId, int mineInstance, boolean success)
	{
		ms.getLogger().trace("sync gs role " + roleID + " end mine " + success + " mineId = " + mineId + " mineInstance = " + mineInstance);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if (ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
					tgms.sendPacket(sessionid, new Packet.GM2S.SyncEndMine(roleID, mapID, mapInstance, mineId, mineInstance, success ? 1 : 0));
			}
			else
			{
				tmc.sendPacket(new Packet.M2S.SyncEndMine(roleID, mapID, mapInstance, mineId, mineInstance, success ? 1 : 0));
			}
		});
	}

	void syncCommonMapCopyStart(int mapID, int mapInstance)
	{
		ms.getLogger().info("sync gs common mapCopy " + mapID + " " + mapInstance + " start ");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if (ms.getConfig().group >= GameData.MAX_AREA_GS_COUNT)
			{
				tmc.sendPacket(new Packet.M2S.SyncCommonMapCopyStart(mapID, mapInstance));
			}
		});
	}

	void syncCommonMapCopyEnd(int mapID, int mapInstance, int score)
	{
		ms.getLogger().info("sync gs common mapCopy " + mapID + " " + mapInstance + " end " + score);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncCommonMapCopyEnd(mapID, mapInstance, score));
		});
	}

	void syncSectMapCopyStart(int mapID, int mapInstance)
	{
		ms.getLogger().trace("sync gs sect mapCopy " + mapID + " " + mapInstance + " start ");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncSectMapCopyStart(mapID, mapInstance));
		});
	}

	public void syncSectGroupMapStart(int mapID, int mapInstance)
	{
		ms.getLogger().trace("sync gs sect group mapCopy " + mapID + " " + mapInstance + " start ");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncSectGroupMapCopyStart(mapID, mapInstance));
		});
	}
	
	public void syncEmergencyMapStart(int mapID, int mapInstance)
	{
		ms.getLogger().trace("sync gs emergency mapCopy " + mapID + " " + mapInstance + " start ");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncEmergencyMapStart(mapID, mapInstance));
		});
	}
	
	public void syncEmergencyMapEnd(int mapID, int mapInstance)
	{
		ms.getLogger().trace("sync gs emergency mapCopy " + mapID + " " + mapInstance + " end ");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncEmergencyMapEnd(mapID, mapInstance));
		});
	}
	
	public void syncEmergencyMapKillMonster(Set<Integer> roles, int monsterId)
	{
		ms.getLogger().trace("sync gs emergency mapCopy " + roles + " kill " + monsterId);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncEmergencyMapKillMonster(roles, monsterId));
		});
	}

	public void syncSectGroupMapStatus(int mapID, int mapInstanceID, int progress)
	{
		ms.getLogger().trace("sync gs sect group mapCopy " + mapID + " " + mapInstanceID + " progress : " + progress);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncSectGroupMapCopyStatus(mapID, mapInstanceID, progress));
		});
	}

	public void syncSectGroupMapResult(int mapID, int mapInstanceID, int progress)
	{
		ms.getLogger().trace("sync gs sect group mapCopy " + mapID + " " + mapInstanceID + " end, progress : " + progress);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncSectGroupMapCopyResult(mapID, mapInstanceID, progress));
		});
	}
	
	public void syncSectGroupMapCopyAddKill(int mapID, int mapInstanceID, int monsterId, int spawnPointId)
	{
		ms.getLogger().trace("sync gs sect group mapCopy " + mapID + " " + mapInstanceID + " monster kill, monsterId : " + monsterId);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncSectGroupMapCopyAddKill(mapID, mapInstanceID, monsterId, spawnPointId));
		});
	}

	void syncSectMapProgress(int mapID, int mapInstance, int spawnpointID, int damage, int hpLostBP)
	{
		ms.getLogger().trace("sync gs sect mapCopy " + mapID + " , " + mapInstance + " ,spawnpointID  " + spawnpointID + " damage " + damage + " , hpLostBP " + hpLostBP + " progress");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncSectMapCopyProgress(mapID, mapInstance, spawnpointID, damage, hpLostBP));
		});
	}

	void syncSectGroupMapProgress(int mapID, int mapInstance, int spawnpointID, int roleID, int monsterID, int damage, int hpLostBP)
	{
		ms.getLogger().trace("sync gs sect mapCopy " + mapID + " , " + mapInstance + " ,spawnpointID  " + spawnpointID + " damage " + damage + " , hpLostBP " + hpLostBP + " progress");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() ->
		{
			tmc.sendPacket(new Packet.M2S.SyncSectGroupMapCopyProgress(mapID, mapInstance, spawnpointID, roleID, monsterID, damage, hpLostBP));
		});
	}

	public void syncFlagCanTake(int mapID)
	{
		ms.getLogger().trace("sync gs map " + mapID + " ,flag can take");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() ->
		{
			tmc.sendPacket(new Packet.M2S.SyncMapFlagCanTake(mapID));
		});
	}

	void syncArenaMapStart(int mapID, int mapInstance)
	{
		ms.getLogger().info("sync gs arena mapCopy [" + mapID + " " + mapInstance + "] start ");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncArenaMapCopyStart(mapID, mapInstance));
		});
	}

	void syncArenaMapEnd(int mapID, int mapInstance, int win, SBean.BattleArrayHp attackingSide, SBean.BattleArrayHp defendingSide)
	{
		ms.getLogger().info("sync gs arena mapCopy [" + mapID + " " + mapInstance + "] end reslut: " + win);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncArenaMapCopyEnd(mapID, mapInstance, win, attackingSide, defendingSide));
		});
	}
	
	void syncBWArenaMapStart(int mapID, int mapInstance)
	{
		ms.getLogger().info("sync gs bw arena mapCopy [" + mapID + " " + mapInstance + "] start ");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncBWArenaMapCopyStart(mapID, mapInstance));
		});
	}

	void syncBWArenaMapEnd(int mapID, int mapInstance, int win, SBean.BattleArrayHp attackingSide, SBean.BattleArrayHp defendingSide)
	{
		ms.getLogger().info("sync gs bw arena mapCopy [" + mapID + " " + mapInstance + "] end reslut: " + win);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncBWArenaMapCopyEnd(mapID, mapInstance, win, attackingSide, defendingSide));
		});
	}
	
	void syncFightNpcMapCopyStart(int mapID, int mapInstance)
	{
		ms.getLogger().info("sync gs fight npc mapCopy [" + mapID + " " + mapInstance + "] start");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncFightNpcMapStart(mapID, mapInstance));
		});
	}
	
	void syncFightNpcMapCopyEnd(int mapID, int mapInstance, boolean win)
	{
		ms.getLogger().info("sync gs fight npc mapCopy [" + mapID + " " + mapInstance + "] end reslut: " + win);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncFightNpcMapEnd(mapID, mapInstance, win ? (byte) 1 : (byte) 0));
		});
	}

	void syncTowerDefenceMapCopyStart(int mapID, int mapInstance)
	{
		ms.getLogger().info("sync gs tower defence mapcopy [" + mapID + " " + mapInstance + "] start");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncTowerDefenceMapStart(mapID, mapInstance));
		});
	}
	
	void syncTowerDefenceMapCopyEnd(int mapID, int mapInstance, int count)
	{
		ms.getLogger().info("sync gs tower defence mapcopy [" + mapID + " " + mapInstance + "] end result count " + count);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncTowerDefenceMapEnd(mapID, mapInstance, count));
		});
	}
	
	void syncTowerDefenceMapCopySpawnCount(int mapID, int mapInstance, int count)
	{
		ms.getLogger().info("sync gs tower defence mapcopy [" + mapID + " " + mapInstance + "] cur spawn count " + count);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncTowerDefenceSpawnCount(mapID, mapInstance, count));
		});
	}
	
	void syncTowerDefenceMapCopyScore(int mapID, int mapInstance, int roleID, int monsterID)
	{
		ms.getLogger().info("sync gs tower defence mapcopy [" + mapID + " " + mapInstance + "] score monster " + monsterID);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncTowerDefenceScore(mapID, mapInstance, roleID, monsterID));
		});
	}
	
	void syncPetLifeMapStart(int mapID, int mapInstance)
	{
		ms.getLogger().debug("sync gs pet life map " + mapID + " , " + mapInstance + " start");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncPetLifeMapCopyStart(mapID, mapInstance));
		});
	}
	
	void syncAddPKValue(int roleID, int mapID, int mapInstance, int value)
	{
		ms.getLogger().trace("sync gs role " + roleID + " add pk value" + value);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.AddPKValue(roleID, mapID, mapInstance, value));
		});
	}

	void syncWorldBossProgress(int bossID, int hp, String killerName, int killerId)
	{
		ms.getLogger().trace("sync gs boss " + bossID + " progress " + hp + " , " + killerName + " , " + killerId);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncWorldMapBossProgress(bossID, hp, killerName, killerId));
		});
	}

	void syncWorldBossRecord(int bossID, SBean.BossRecord record)
	{
		ms.getLogger().trace("sync gs boss " + bossID + " record ");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncWorldMapBossRecord(bossID, record));
		});
	}
	
	void syncWorldBossDamageRoles(int bossID, int killer, Map<Integer, Integer> damageRoles)
	{
		ms.getLogger().trace("sync gs boss " + bossID + " damage roles ");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncWorldBossDamageRoles(bossID, killer, damageRoles));
		});
	}
	
	void syncSuperMonster(SBean.ActivityEntity monster, boolean add)
	{
		ms.getLogger().trace("sync gs world [" + monster.mapID + ", " + monster.mapLine + "]super monster " + monster.id + " state " + add);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncSuperMonster(monster,  (byte)(add ? 1 : 0)));
		});
	}
	
	void syncWorldMineral(SBean.ActivityEntity mineral, boolean add)
	{
		ms.getLogger().trace("sync gs world [" + mineral.mapID + ", " + mineral.mapLine + "]mineral " + mineral.id + " state " + add);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncWorldMineral(mineral,  (byte)(add ? 1 : 0)));
		});
	}
	
	void syncRoleCurRideHorse(int roleID, int mapID, int mapInstance, int hid)
	{
//		ms.getLogger().trace("sync gs role " + roleID + " cur ride horse " + hid);
//		stat.sendServerMsgCounter.incrementAndGet();
//		this.addSendEvent(() -> 
//		{
//			tmc.sendPacket(new Packet.M2S.SyncCurRideHorse(roleID, mapID, mapInstance, hid));
//		});
	}

	void syncCarLocation(int roleID, int mapID, int mapInstance, SBean.Location location)
	{
		ms.getLogger().trace("sync gs role " + roleID + " car location " + GameData.toString(location.position));
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncCarLocation(roleID, mapID, mapInstance, location));
		});
	}
	
	void syncCarCurHP(int roleID, int mapID, int mapInstance, int hp)
	{
		ms.getLogger().trace("sync gs role " + roleID + " car hp " + hp);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncCarHp(roleID, mapID, mapInstance, hp));
		});
	}
	
	void updateCarDamage(int roleID, int mapID, int mapInstance, int damageRole, int damage)
	{
		ms.getLogger().trace("update gs role " + roleID + " car damageRole " + damageRole + " damage " + damage);
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.UpdateCarDamage(roleID, mapID, mapInstance, damageRole, damage));
		});
	}
	
	void syncRoleRobSuccess(int roleID, int carID)
	{
		ms.getLogger().debug("sync gs role " + roleID + " rob car " + carID + " success!");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.SyncRoleRobSuccess(roleID, carID));
		});
	}
	
	void updateRoleCarRobber(int roleID)
	{
		ms.getLogger().trace("update gs role " + roleID + " update car robber");
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			tmc.sendPacket(new Packet.M2S.UpdateRoleCarRobber(roleID));
		});
	}
	
	void notifyGSKickRoleFromMap(int roleID)
	{
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if(ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
				{
					ms.getLogger().info("notify gs " + sessionid + " kick role " + roleID + " from map");
					tgms.sendPacket(sessionid, new Packet.GM2S.KickRoleFromMap(roleID));
				}
			}
			else
			{
				ms.getLogger().info("notify gs kick role " + roleID + " from map");
				tmc.sendPacket(new Packet.M2S.KickRoleFromMap(roleID));
			}
		});
	}
	
	void notifyGSSyncRoleUseItemSkillSuc(int roleID, int itemID, int ok)
	{
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if(ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
				{
					ms.getLogger().info("notify gs " + sessionid + " role " + roleID + " use item " + itemID + " skill suc:" + ok);
					tgms.sendPacket(sessionid, new Packet.GM2S.RoleUseItemSkillSuc(roleID, itemID, ok));
				}
			}
			else
			{
				ms.getLogger().info("notify gs role " + roleID + " use item " + itemID + " skill suc:" + ok);
				tmc.sendPacket(new Packet.M2S.RoleUseItemSkillSuc(roleID, itemID, ok));
			}
		});
	}
	
	void notifyGSUpdateRoleFightState(int roleID, int mapID, int mapInstance, boolean fightState)
	{
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if(ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
				{
					ms.getLogger().trace("notify gs " + sessionid + " role " + roleID + " update fight state " + fightState);
					tgms.sendPacket(sessionid, new Packet.GM2S.UpdateRoleFightState(roleID, mapID, mapInstance, fightState ? (byte)1 : (byte)0));
				}
			}
			else
			{
				ms.getLogger().trace("notify gs role " + roleID + " update fight state " + fightState);
				tmc.sendPacket(new Packet.M2S.UpdateRoleFightState(roleID, mapID, mapInstance, fightState ? (byte)1 : (byte)0));
			}
		});
	}
	
	void notifyGSSyncRoleWeaponMaster(int roleID, int mapID, int mapInstance, int weaponID)
	{
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if(ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(sessionid != null)
				{
					ms.getLogger().trace("notify gs " + sessionid + " sync role " + roleID + " weapon " + weaponID + " master");
					tgms.sendPacket(sessionid, new Packet.GM2S.SyncWeaponMaster(roleID, mapID, mapInstance, weaponID));
				}
			}
			else
			{
				ms.getLogger().trace("notify gs sync role " + roleID + " weapon " + weaponID + " master");
				tmc.sendPacket(new Packet.M2S.SyncWeaponMaster(roleID, mapID, mapInstance, weaponID));
			}
		});
	}
	
	void notifyGSRoleMarriageParadeEnd(int manID, int womanID)
	{
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if(ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				
			}
			else
			{
				ms.getLogger().info("notify gs sync man " + manID + " woman " + womanID + " marriage parade end");
				tmc.sendPacket(new Packet.M2S.SyncMarriageParadeEnd(manID, womanID));
			}
		});
	}
	
	void notifyGSSyncSteleRemainTimes(int steleType, int index, int remainTimes)
	{
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if(ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				
			}
			else
			{
				ms.getLogger().info("notify gs sync stele [" + steleType + ", " + index + "] remainTimes " + remainTimes);
				tmc.sendPacket(new Packet.M2S.SyncSteleRemainTimes(steleType, index, remainTimes));
			}
		});
	}
	
	void notifyGSSyncRoleAddSteleCard(int roleID, int addCards, int addType)
	{
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if(ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				
			}
			else
			{
				ms.getLogger().info("notify gs sync role " + roleID + " add stele card " + addCards + " add type " + addType);
				tmc.sendPacket(new Packet.M2S.SyncRoleAddSteleCard(roleID, addCards, addType));
			}
		});
	}
	
	void notifyGSSyncRefreshSteleMonster(int roleID, String roleName, int mapID, int mapLine, int steleType, int index, int monsterID)
	{
		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
			if(ms.getConfig().group < GameData.MAX_AREA_GS_COUNT)
			{
				
			}
			else
			{
				ms.getLogger().debug("notify gs sync role " + roleID + " refresh stele monster");
				tmc.sendPacket(new Packet.M2S.SyncRefreshSteleMonster(roleID, roleName, mapID, mapLine, steleType, index, monsterID));
			}
		});
	}
	
	void onLuaChannelList(MapRole role, String[] msg)
	{
		
	}
	
	//gm2f------------------------------------------------------------------------------------------------------
	void notifyFightCreateMapCopyRes(int mapType, int mapInstance)
	{
		ms.getLogger().info("notify fs create map copy responce " + mapInstance);
		this.addSendEvent(() -> 
		{
			tfmc.sendPacket(new Packet.GM2F.CreateMapCopyRes(mapType, mapInstance));
		});
	}
	
	void notifyFightForceWarMapEnd(int mapID, int mapInstance, int winSide, int killedBoss, int whiteScore, int blackScore, Map<Integer, SBean.ForceWarOverview> whiteSide, Map<Integer, SBean.ForceWarOverview> blackSide)
	{
		ms.getLogger().info("notify fs force war end winSide " + winSide + " whiteScore " + whiteScore + " blackScore " + blackScore);
		this.addSendEvent(() -> 
		{
			tfmc.sendPacket(new Packet.GM2F.SyncForceWarMapEnd(mapID, mapInstance, winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide));
		});
	}
	
	void notifyFightSuperArenaMapEnd(int mapID, int mapInstance, SBean.SuperArenaBattleResult result)
	{
		ms.getLogger().info("sync fight super arena mapCopy [" + mapID + " " + mapInstance + "] end loseGroup: " + result.loseTeam);
//		stat.sendServerMsgCounter.incrementAndGet();
		this.addSendEvent(() -> 
		{
//			tmc.sendPacket(new Packet.M2S.SyncSuperArenaMapCopyEnd(mapID, mapInstance, result));
			tfmc.sendPacket(new Packet.GM2F.SyncSuperArenaMapEnd(mapID, mapInstance, result));
		});
	}
	
	void notifyFightSuperArenaRaceEnd(int mapID, int mapInstance)
	{
		ms.getLogger().info("sync fight super arena race [" + mapID + " , " + mapInstance + "] end");
		this.addSendEvent(() ->
		{
			tfmc.sendPacket(new Packet.GM2F.SyncSuperArenaRaceEnd(mapID, mapInstance));
		});
	}
	
	void notifyFightSyncDemonHoleKill(int mapID, int mapInstance, int killerID, int deaderID)
	{
		ms.getLogger().trace("sync fight demon hole map copy[" + mapID + " , " + mapInstance + " killer " + killerID + " deader " + deaderID);
		this.addSendEvent(() ->
		{
			tfmc.sendPacket(new Packet.GM2F.SyncDemonHoleKill(mapID, mapInstance, killerID, deaderID));
		});
	}
	
	
	class MapIOStat implements MapIOStatMBean
	{
		AtomicLong sendClientStrMsgCounter = new AtomicLong(0);
		AtomicLong sendClientNormalMsgCounter = new AtomicLong(0);
		AtomicLong sendServerMsgCounter = new AtomicLong(0);
		Statistic sessionStat = new Statistic();
		
		long profileTime;
		long profileInterval;
		
		long lastProfileSendClientStrMsgTotalCount;
		long lastProfileSendClientNormalMsgTotalCount;
		long lastProfileSendServerMsgTotalCount;
		int lastProfileSessionSendPacketTotalCount;
		int lastProfileSessionSendTimesTotalCount;
		int lastProfileSessionRecvTimesTotalCount;
		int lastProfileSessionSendPacketTaskCount;
		int lastProfileSessionSendBytes;
		
		int curProfileSendClientStrMsgCount;
		int curProfileSendClientNormalMsgCount;
		int curProfileSendServerMsgCount;
		int curProfileSessionSendPacketCount;
		int curProfileSessionSendTimesCount;
		int curProfileSessionRecvTimesCount;
		int curProfileSessionSendPacketTaskAdded;
		int curProfileSessionSendBytes;
		
//		int logInterval;
//		Map<String, Integer> strPackets = new HashMap<String, Integer>();
		
		public MapIOStat()
		{
			profileTime = GameTime.getTimeMillis();
		}
		
		public void start()
		{
			try
			{
				ManagementFactory.getPlatformMBeanServer().registerMBean(this, new ObjectName("i3k.gmap:type=MapIOStat"));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		public void resetSession()
		{
			sessionStat = new Statistic();
		}
		
		@Override
		public int getSamplinginterval()
		{
			return (int)profileInterval;
		}
		
		@Override
		public int getSendClientStrMsgCountPerSecond()
		{
			return (int)(curProfileSendClientStrMsgCount*1000/profileInterval);
		}
		
		@Override
		public int getSendClientNormalMsgCountPerSecond()
		{
			return (int)(curProfileSendClientNormalMsgCount*1000/profileInterval);
		}
		
		@Override
		public int getSendServerMsgCountPerSecond()
		{
			return (int)(curProfileSendServerMsgCount*1000/profileInterval);
		}
		
		@Override
		public int getSessionSendPacketCountPerSecond()
		{
			return (int)(curProfileSessionSendPacketCount*1000/profileInterval);
		}
		
		@Override
		public int getSessionSendTimesCountPerSecond()
		{
			return (int)(curProfileSessionSendTimesCount*1000/profileInterval);
		}
		
		@Override
		public int getSessionRecvTimesCountPerSecond()
		{
			return (int)(curProfileSessionRecvTimesCount*1000/profileInterval);
		}
		
		@Override
		public int getSessionPacketTaskAddedPerSecond()
		{
			return (int)(curProfileSessionSendPacketTaskAdded*1000/profileInterval);
		}
		
		@Override
		public int getSessionSendKBytesPerSecond()
		{
			return (int)(curProfileSessionSendBytes/profileInterval);
		}
		
		@Override
		public int getSessionSendPacketTotal()
		{
			return lastProfileSessionSendPacketTotalCount;
		}
		
		@Override
		public int getSessionSendTimesTotal()
		{
			return lastProfileSessionSendTimesTotalCount;
		}
		
		@Override
		public int getSessionRecvTimesTotal()
		{
			return lastProfileSessionRecvTimesTotalCount;
		}
		
		@Override
		public int getSessionPacketTaskQueue()
		{
			return lastProfileSessionSendPacketTaskCount;
		}
		
		@Override
		public int getSessionSendBytes()
		{
			return lastProfileSessionSendBytes;
		}
		
		public void profile()
		{
			long now = GameTime.getTimeMillis();
			profileInterval = now - profileTime;
			profileTime = now;
			if (profileInterval < 100)
				ms.getLogger().warn("profile timer tick is less than 100ms");
			if (profileInterval == 0)
				profileInterval = 1;
			
			long curProfileSendClientStrMsgTotalCount = sendClientStrMsgCounter.get();
			long curProfileSendClientNormalMsgTotalCount = sendClientNormalMsgCounter.get();
			long curProfileSendServerMsgTotalCount = sendServerMsgCounter.get();
			if (ms.getConfig().group >= GameData.MAX_AREA_GS_COUNT)
				tmc.getStatistic(sessionStat);
			
			
			curProfileSendClientStrMsgCount = (int)(curProfileSendClientStrMsgTotalCount - lastProfileSendClientStrMsgTotalCount);
			curProfileSendClientNormalMsgCount = (int)(curProfileSendClientNormalMsgTotalCount - lastProfileSendClientNormalMsgTotalCount);
			curProfileSendServerMsgCount = (int)(curProfileSendServerMsgTotalCount - lastProfileSendServerMsgTotalCount);
			curProfileSessionSendPacketCount = (int)(sessionStat.nPacketsSend - lastProfileSessionSendPacketTotalCount);
			curProfileSessionSendTimesCount = (int)(sessionStat.nSendTimes - lastProfileSessionSendTimesTotalCount);
			curProfileSessionRecvTimesCount = (int)(sessionStat.nRecvTimes - lastProfileSessionRecvTimesTotalCount);
			curProfileSessionSendPacketTaskAdded = (int)(sessionStat.nSendPacketAccumlate - lastProfileSessionSendPacketTaskCount);
			curProfileSessionSendBytes = (int)(sessionStat.nBytesSend - lastProfileSessionSendBytes);
			
			lastProfileSendClientStrMsgTotalCount = curProfileSendClientStrMsgTotalCount;
			lastProfileSendClientNormalMsgTotalCount = curProfileSendClientNormalMsgTotalCount;
			lastProfileSendServerMsgTotalCount = curProfileSendServerMsgTotalCount;
			lastProfileSessionSendPacketTotalCount = sessionStat.nPacketsSend;
			lastProfileSessionSendTimesTotalCount = sessionStat.nSendTimes;
			lastProfileSessionRecvTimesTotalCount = sessionStat.nRecvTimes;
			lastProfileSessionSendPacketTaskCount = sessionStat.nSendPacketAccumlate;
			lastProfileSessionSendBytes = sessionStat.nBytesSend;
			
//			logInterval += profileInterval;
//			if (logInterval > 10000)
//			{
//				for (Map.Entry<String, Integer> e : strPackets.entrySet())
//				{
//					ms.getLogger().info(e.getKey() + " " + e.getValue());
//				}
//			}
			
			if (ms.getConfig().pInfo != 0)
			{
				ms.getLogger().info(String.format("SI=%d, strmsg=%d, othmsg=%d, svrmsg=%d, sc=%d, st=%d, rt=%d, qa=%d, q=%d, skbps=%d, sm=%d, sct=%d, stt=%d, rtt=%d",
						getSamplinginterval(), getSendClientStrMsgCountPerSecond(), getSendClientNormalMsgCountPerSecond(), getSendServerMsgCountPerSecond(),
						getSessionSendPacketCountPerSecond(), getSessionSendTimesCountPerSecond(), getSessionRecvTimesCountPerSecond(),
						getSessionPacketTaskAddedPerSecond(), getSessionPacketTaskQueue(), 
						getSessionSendKBytesPerSecond(), getSessionSendBytes()/1000000, 
						getSessionSendPacketTotal(), getSessionSendTimesTotal(), getSessionRecvTimesTotal()));	
			}
		}
		
	}
	
	public MapIOStat getStats()
	{
		return stat;
	}

	MapIOStat stat = new MapIOStat();
	
	int keepaliveRandomTime;
	NetManager managerNet;
	TCPMapClient tmc;
	TCPGlobalMapServer tgms;
	TCPFightMapClient tfmc;
	TCPAlarmServer talarms;
	MapServer ms;
	StringChannelHandler strChannelHandler;
	ExecutorService taskExecutor;
	GameServerTable table = new GameServerTable();
	OpenConnectFailCount mapcount = new OpenConnectFailCount();
	
}
