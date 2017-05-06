
package i3k.gs.test;


import i3k.SBean;
import i3k.SBean.DBRegisterID;
import i3k.SBean.UserClientinfo;
import i3k.SBean.UserLoginInfo;
import i3k.SBean.UserLoginParam;
import i3k.SBean.UserSysteminfo;
import i3k.gs.GameData;
import i3k.gs.Role;
import i3k.rpc.Packet;
import i3k.util.GameRandom;
import i3k.util.GameTime;
import i3k.util.GVector3;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import ket.util.SStream;

public class Robot extends TCPGameClient
{
	
	public static final int STATE_NULL = -1;
	public static final int STATE_COMBAT_START = 0;
	public static final int STATE_COMBAT = 1;
	public static final int STATE_COMBAT_FINISH = 2;
	
	public static final int TASK_NULL = 0;
	public static final int TASK_COMBAT = 1;
	public static final int TASK_COUNT = 2;
	
	public static final int ACTION_COOL_BASE = 10;
	public static final int ACTION_COOL_RANDOM = 5;
	
	public Robot(GameClient gc, int id)
	{
		super(gc.getRPCManager(id));
		this.gc = gc;
		this.idSeed = id;
		this.loginInfo = new SBean.UserLoginInfo(getLoginParam(), getClientInfo(), getSystemInfo());
		this.classType = GameRandom.getRandInt(1, 6);
		this.setServerAddr(gc.getConfig().addrServer);
	}
	
	
	public int getIDSeed()	
	{
		return this.idSeed;
	}
	
	public String getRoleName()
	{
		return gc.getConfig().randomRoleNamePrefix + Integer.toString(idSeed);
	}
	
	public String getUID()
	{
		return gc.getConfig().randomUserNamePrefix + Integer.toString(idSeed + 100000000);
	}
	
	public String getChannel()
	{
		return gc.getConfig().channel;
	}
	
	private SBean.UserLoginParam getLoginParam()
	{
		return new SBean.UserLoginParam(SBean.UserLoginParam.eLoginNormal, "", "");
	}
	
	private SBean.UserClientinfo getClientInfo()
	{
		return new SBean.UserClientinfo(gc.getConfig().gameAppId, gc.getConfig().verPacket, gc.getConfig().verResource, new HashSet<>(GameData.getInstance().getFullPackets()));
	}
	
	private SBean.UserSysteminfo getSystemInfo()
	{
		return new SBean.UserSysteminfo("oo", "", "", "", 1080, 720, 1.0f, 0, "", "127.0.0.1");
	}
	
	private SBean.CreateRoleParam getCreateRoleParam()
	{
		int gender = GameRandom.getRandInt(1, 3);
		int face = gender == 1 ?  GameRandom.getRandInt(1, 4) : GameRandom.getRandInt(4, 7);
		int hair = gender == 1 ?  GameRandom.getRandInt(31, 40) : GameRandom.getRandInt(40, 49);
		return new SBean.CreateRoleParam(getRoleName(), (byte)gender, (byte)face, (byte)hair, (byte)classType);
	}
	
	
	private GVector3 getRandomDirUnitVector()
	{
		double theta = GameRandom.getRandom().nextFloat()*2*Math.PI;
		return new GVector3((float)Math.cos(theta), 0, (float)Math.sin(theta));
	}
	
	private int getBaseSkillId()
	{
		SBean.ClassRoleCFGS crCfg = GameData.getInstance().getClassRoleCFG(classType);
		return crCfg.attacks.get(GameRandom.getRandom().nextInt(crCfg.attacks.size()));
	}
	
	void start()
	{
		gc.getPerformanceStats().logStatDoConnect(false, false);
		active = true;
		this.open();
	}
	
	void destroy()
	{
		active = false;
		this.close();
	}
	
	
	synchronized void reconnect()
	{
		gc.getLogger().debug("robot " + this.idSeed + " will be reconnect.");
		if(this.active && gc.getConfig().reconnectInterval > 0)
			gc.getExecutor().schedule(this::doReconect, GameRandom.getRandom().nextInt(gc.getConfig().reconnectInterval * 1000), TimeUnit.MILLISECONDS);
	}
	
	synchronized void doReconect()
	{
		gc.getLogger().debug("robot " + this.idSeed + " doReconnect.");
		int times = this.reconnectTimes.incrementAndGet();
		gc.getPerformanceStats().logStatDoConnect(true, times == 1);
		this.open();
	}
	
	synchronized void onConnectSuccess()
	{
		gc.getLogger().debug("robot " + this.idSeed + " connect success.");
		gc.getPerformanceStats().logStatConnecteSuccess();
		this.openTime = GameTime.getTime();
	}
	
	synchronized void onConnectFailed()
	{
		gc.getLogger().debug("robot " + this.idSeed + " connect failed.");
		gc.getPerformanceStats().logStatConnectFailed();
		this.reconnect();
	}
	
	synchronized void onConnectClose()
	{
		gc.getLogger().debug("robot " + this.idSeed + " connect close.");
		openTime = GameTime.getTime();
		readyTime = 0;
		if (loginTime > 0)
			gc.getPerformanceStats().logStatLogout();
		loginTime = 0;
		enterMapTime = 0;
		pingValue = 0;
		if (pingMode)
		{
			pingMode = false;
			gc.getPerformanceStats().logStatPingMode(false);
		}
		gc.getPerformanceStats().logStatClose();
		this.reconnect();
	}
	
	synchronized void onChallenged()
	{
		gc.getLogger().debug("robot " + this.idSeed + " on challenged.");
		readyTime = GameTime.getTime();
		gc.getPerformanceStats().logStatConnecteReady();
	}
	
	synchronized void doUserLogin()
	{
		gc.getLogger().debug("robot " + this.idSeed + " doUserLogin.");
		SBean.user_login_req packet = new SBean.user_login_req(gc.getConfig().gsid, getUID(), getChannel(), loginInfo, 0, null);
		sendStrPacket(packet);
	}
	
	synchronized void doRoleLogout()
	{
		gc.getLogger().debug("robot " + this.idSeed + " doRoleLogout.");
		SBean.role_logout_req packet = new SBean.role_logout_req();
		sendStrPacket(packet);
	}
	
	synchronized void doCreateRole()
	{
		gc.getLogger().debug("robot " + this.idSeed + " doCreateRole.");
		SBean.user_login_req packet = new SBean.user_login_req(gc.getConfig().gsid, getUID(), getChannel(), loginInfo, 0, getCreateRoleParam());
		sendStrPacket(packet);
	}
	
	synchronized void doRoleLogin(int roleId)
	{
		gc.getLogger().debug("robot " + this.idSeed + " doRoleLogin.");
		SBean.user_login_req packet = new SBean.user_login_req(gc.getConfig().gsid, getUID(), getChannel(), loginInfo, roleId, null);
		sendStrPacket(packet);
	}
	
	synchronized void setMoveable(boolean moveable)
	{
		this.moveable = moveable;
	}
	
	synchronized void onTimer(int timeTick, long timeMillis)
	{
		if (this.isOpen() && readyTime > 0)
		{
			 if ((timeTick - openTime) % 60 == 0)
			 {
				 this.sendStrPacket(new SBean.keep_alive());
//				 if (loginTime <= 0)
//					 gc.getLogger().info("robot " + this.idSeed + " loginTime=" + loginTime + ", enterMapTime=" + enterMapTime);
			 }
			 if (loginTime == 0)
			 {
				 loginTime = -1;
				 this.doUserLogin();
				 roleLoginCostTime = timeTick;
			 }
			 if (enterMapTime > 0)
			 {
				 updatePing(timeTick, timeMillis);
			 }
			 int move = gc.getConfig().move;
			 if (move != 0 && moveable && pingValue > 0 && !this.pingMode && posSyncTime > 0)
			 {
				 switch (move)
				 {
				 case 1:
					 this.updateMovement(timeMillis);
					 break;
				 case 2:
					 this.updateCastSkill(timeMillis);
					 break;
				default:
					break;
				 }
			 }
		}
//		if (gc.getRobotsManager().isLongTimeNotLogined() && loginTime <= 0)
//			gc.getLogger().info("robot " + this.idSeed + " isOpen=" + this.isOpen() + ", readyTime=" + readyTime + ", openTime=" + openTime + ", loginTime=" + loginTime + ", enterMapTime=" + enterMapTime);
	}
	
	private void updatePing(int timeTick, long timeMillis)
	{
		if (timeTick >= lastPingTime + 1000)
		{
			int deltaTime = (int)(timeMillis - this.timeTickUpdateTime);
			this.timeTickUpdateTime = timeMillis;
			this.timeTick += deltaTime;
			SBean.TimeTick tt = new SBean.TimeTick((int)(this.timeTick/GameData.getInstance().getCommonCFG().engine.interval), (int)(this.timeTick%GameData.getInstance().getCommonCFG().engine.interval));
			this.sendStrPacket(new SBean.client_ping_start(tt, pingValue));
			lastPingTime = timeTick;
		}
	}
	
	private void updateMovement(long timeMillis)
	{
		 int deltaTime = (int)(timeMillis - this.timeTickUpdateTime);
		 this.timeTickUpdateTime = timeMillis;
		 this.timeTick += deltaTime;
		 SBean.TimeTick tt = new SBean.TimeTick((int)(this.timeTick/GameData.getInstance().getCommonCFG().engine.interval), (int)(this.timeTick%GameData.getInstance().getCommonCFG().engine.interval));
		 if (this.startMoveTime == 0)
		 {
			 if (this.position.equals(this.randomPosition))
			 {
				 this.moveDir = getRandomDirUnitVector();
				 this.moveDistance = moveRadius;
			 }
			 else
			 {
				 GVector3 dir = this.randomPosition.diffence2D(this.position);
				 this.moveDir = dir.normalize();
				 this.moveDistance = dir.size();
			 }
			 this.moveStartPos = this.position;
			 this.startMoveTime = timeMillis;
			 this.sendStrPacket(new SBean.role_move(this.position.toVector3(), this.moveStartPos.sum(this.moveDir.scale(this.moveDistance)).toVector3(), moveSpeed, this.moveDir.toVector3F(), tt));
		 }
		 else
		 {
			 float distance = (moveSpeed/1000.0f)*(timeMillis - this.startMoveTime);
			 GVector3 targetPos = this.moveStartPos.sum(this.moveDir.scale(this.moveDistance));
			 if (distance >= this.moveDistance)
			 {
				 this.position = targetPos;
				 this.startMoveTime = 0;
				 this.sendStrPacket(new SBean.role_stopmove(this.position.toVector3(), tt));
			 }
			 else
			 {
				 this.position = this.moveStartPos.sum(this.moveDir.scale(distance));
				 this.sendStrPacket(new SBean.role_move(this.position.toVector3(), targetPos.toVector3(), moveSpeed, this.moveDir.toVector3F(), tt));
			 }
		 }
	}
	
	private void updateCastSkill(long timeMillis)
	{
		 if (++this.castSkillTicks >= 10)
		 {
			 int deltaTime = (int)(timeMillis - this.timeTickUpdateTime);
			 this.timeTickUpdateTime = timeMillis;
			 this.timeTick += deltaTime;
			 SBean.TimeTick tt = new SBean.TimeTick((int)(this.timeTick/GameData.getInstance().getCommonCFG().engine.interval), (int)(this.timeTick%GameData.getInstance().getCommonCFG().engine.interval));
			 int skillId = this.getBaseSkillId();
			 this.sendStrPacket(new SBean.role_useskill(skillId, this.position.toVector3(), this.moveDir.toVector3F(), 0, 0, 0, tt));
			 this.castSkillTicks = 0;
		 }
	}
	
	public void sendStrPacket(SStream.IStrPacket packet)
	{
		String data = SStream.encode(packet);
		this.sendPacket(new Packet.C2S.StrChannel(data));
		gc.getLogger().debug("robot " + this.idSeed + " send str packet " + data);
	}
	
	synchronized public void onReceiveStrPacket(Packet.S2C.StrChannel packet)
	{
		try
		{
			String data = packet.getData();
			String packetName = SStream.detectPacketName(data);
			if( packetName == null )
				return;
		
			@SuppressWarnings("rawtypes")
			Class cls = SBean.getStrPacketClass(packetName);
			if( cls == null )
				return;
			
			Method mtd = Robot.class.getMethod("onRecv_" + packetName, SStream.IStreamable.class);
			mtd.invoke(this, SStream.decode(data, cls));
		}
		catch (java.lang.NoSuchMethodException ex)
		{
			//gc.getLogger().debug("not found robot handler : " + ex.getMessage());
		}
		catch(Exception ex)
		{			
			gc.getLogger().warn(ex.getMessage(), ex);
		}
	}
	
	public void onRecv_user_login_res(SStream.IStreamable ipacket)
	{
		SBean.user_login_res packet = (SBean.user_login_res) ipacket;
		if (packet.errCode == GameData.USERLOGIN_ROLE_OK)
		{
			this.loginTime = GameTime.getTime();
			this.roleLoginCostTime = this.loginTime - this.roleLoginCostTime;
			gc.getPerformanceStats().logStatLoginOk();
			if (this.roleLoginCostTime > 5)
				gc.getLogger().info("robot " + this.idSeed + " login success, login cost " + this.roleLoginCostTime);
		}
		else if (packet.errCode != GameData.USERLOGIN_OK)
		{
			this.loginTime = 0;
			gc.getPerformanceStats().logStatLoginError();
		}
	}
	
	public void onRecv_user_role_list(SStream.IStreamable ipacket)
	{
		SBean.user_role_list packet = (SBean.user_role_list) ipacket;
		if (packet.roles.isEmpty())
			doCreateRole();
		else
			doRoleLogin(packet.roles.get(0).overview.id);
	}
	
	public void onRecv_role_logout_res(SStream.IStreamable ipacket)
	{
		SBean.role_logout_res packet = (SBean.role_logout_res) ipacket;
		if (packet.ok > 0)
		{
			this.pingValue = 0;
			this.enterMapTime = 0;
			this.loginTime = 0;
			this.logoutTime = GameTime.getTime();
			gc.getPerformanceStats().logStatLogout();
		}
	}
	
	public void onRecv_role_base(SStream.IStreamable ipacket)
	{
		SBean.role_base packet = (SBean.role_base) ipacket;
		this.classType = packet.classType;
	}
	
	public void onRecv_sync_guide_mapcopy_step(SStream.IStreamable ipacket)
	{
		SBean.sync_guide_mapcopy_step packet = (SBean.sync_guide_mapcopy_step) ipacket;
		if (packet.step != 0)
		{
			sendStrPacket(new SBean.save_guide_mapcopy_req(0));
		}
	}
	
	public void onRecv_role_change_map(SStream.IStreamable ipacket)
	{
		SBean.role_change_map packet = (SBean.role_change_map) ipacket;
		this.enterMapTime = 0;
		this.pingValue = 0;
		this.position = new GVector3(packet.location.location.position);
		this.moveDir = new GVector3(packet.location.location.rotation);
		if (this.randomPosition == null)
			this.randomPosition = this.position.sum(getRandomDirUnitVector().scale(initRandomRadius));
		sendStrPacket(new SBean.role_enter_map());
		this.roleEnterMapCostTime = GameTime.getTime();
		this.posSyncTime = GameTime.getTime();
	}
	
	public void onRecv_role_map_welcome(SStream.IStreamable ipacket)
	{
		SBean.role_map_welcome packet = (SBean.role_map_welcome) ipacket;
		this.timeTick = packet.timeTick.tickLine * GameData.getInstance().getCommonCFG().engine.interval + packet.timeTick.outTick;
		this.timeTickUpdateTime = GameTime.getTimeMillis();
		this.enterMapTime = GameTime.getTime(); 
		this.roleEnterMapCostTime = this.enterMapTime - this.roleEnterMapCostTime;
		if (this.roleEnterMapCostTime > 5)
			gc.getLogger().info("robot " + this.idSeed + " receive role_map_welcome, enter cost " + this.roleEnterMapCostTime);
	}
	
	public void onRecv_client_ping_end(SStream.IStreamable ipacket)
	{
		SBean.client_ping_end packet = (SBean.client_ping_end) ipacket;
		this.timeTick = packet.recvTimeTick.tickLine * GameData.getInstance().getCommonCFG().engine.interval + packet.recvTimeTick.outTick;
		this.timeTickUpdateTime = GameTime.getTimeMillis();
		this.pingValue = (packet.recvTimeTick.tickLine - packet.sendTimeTick.tickLine)*GameData.getInstance().getCommonCFG().engine.interval + (packet.recvTimeTick.outTick - packet.sendTimeTick.outTick);
		if (this.pingValue > 1000)
		{
			if (++this.highPingTimes >= pingModeHighPingTimes)
			{
				if (!this.pingMode)
				{
					this.pingMode = true;
					gc.getPerformanceStats().logStatPingMode(true);
					this.sendStrPacket(new SBean.role_sync_map());
					this.posSyncTime = 0;
					gc.getLogger().debug("robot " + this.idSeed + " receive client_ping_end, ping value " + this.pingValue + ", highPingTimes " + this.highPingTimes);	
				}
			}
		}
		else
		{
			this.highPingTimes = 0;
			if (this.pingMode)
			{
				this.pingMode = false;
				gc.getPerformanceStats().logStatPingMode(false);
			}
		}
		gc.getLogger().debug("robot " + this.idSeed + " receive client_ping_end, ping value " + this.pingValue);
	}
	
	public void onRecv_role_adjust_pos(SStream.IStreamable ipacket)
	{
		SBean.role_adjust_pos packet = (SBean.role_adjust_pos) ipacket;
		this.position = new GVector3(packet.pos);
	}
	

	
	private GameClient gc;
	AtomicInteger reconnectTimes = new AtomicInteger(0);
	final int idSeed;
	int classType;
	boolean active;
	int openTime;
	int readyTime;
	
	SBean.UserLoginInfo loginInfo;
	int loginTime;
	int logoutTime;
	int enterMapTime;
	GVector3 position;
	long timeTick;
	long timeTickUpdateTime;

	GVector3 randomPosition;
	GVector3 moveStartPos;
	GVector3 moveDir;
	float moveDistance;
	long startMoveTime;
	boolean moveable;
	
	long castSkillTicks;
	
	int roleEnterMapCostTime;
	int roleLoginCostTime;
	int posSyncTime;
	
	int lastPingTime;
	int pingValue;
	int highPingTimes;
	boolean pingMode;
	
	final static int initRandomRadius = 1000;
	final static int moveRadius = 1500;
	final static int moveSpeed = 700;
	final static int pingModeHighPingTimes = 3;
	
	public int rid;
	public boolean bLogin = false;
	public int state = STATE_NULL;
	public int lastActTime = 0;
}
