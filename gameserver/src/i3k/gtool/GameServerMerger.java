package i3k.gtool;

import i3k.*;
import i3k.SBean.BWArenaRankRole;
import i3k.SBean.BWArenaRewardRole;
import i3k.SBean.DBArena;
import i3k.SBean.DBArenaReward;
import i3k.SBean.DBBWArena;
import i3k.SBean.DBBWArenaLvlCache;
import i3k.SBean.DBClimbTowerServerRecordDataCfg;
import i3k.SBean.DBRanks;
import i3k.SBean.DBRoleRanks;
import i3k.SBean.DBSectRanks;
import i3k.SBean.RankRole;
import i3k.SBean.RankSect;
import ket.kdb.DB;
import ket.kdb.Table;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;
import ket.util.ArgsMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import ket.util.Stream;
import ket.util.Stream.DecodeException;
import ket.util.Stream.EOFException;

import org.apache.log4j.Logger;

public class GameServerMerger
{
    public GameServerMerger() {}
    
    static class ByteArray {
    	public ByteArray (byte[] array)
    	{
    		this.array = Arrays.copyOf(array, array.length) ;
    	}
    	
    	@Override
    	public int hashCode()
    	{
    		int code = 0;
    		for (byte b : array)
    		{
    			code = (code << 8) | b;
    		}
    		return code>=0?code:-code;
    	}
    	
    	@Override 
    	public boolean equals(Object other)
    	{
    		if (this==other)
    			return true;
    		
    		if (other==null || getClass()!=other.getClass())
    			return false;
    		
    		return Arrays.equals(array, ((ByteArray)other).get());
    	}
    	
    	public byte[] get()
    	{
    		return array;
    	}
    	
    	public byte[] getCopy()
    	{
    		return Arrays.copyOf(array, array.length);
    	}
    	
    	private byte[] array;
    }

    static <T1, T2> Map<T1, T2> deepCopy(TableReadonly<T1, T2> source)
    {
        Map<T1, T2> dataMap = new HashMap<>();
        source.forEach(tableEntry -> {
            T1 key = tableEntry.getKey();
            T2 value = tableEntry.getValue();
            if (value==null)
            {
                System.out.println(key + "is NULL");
            }
            dataMap.put(key, value);
        });
        
        dataMap.forEach((k, v)->{
            if (v==null)
            {
                System.out.println(k);
            }
        });
        
        dataMap.entrySet().removeIf( e->e.getValue()==null);

        return dataMap;
    }
    
    static Map<ByteArray, byte[]> arrayKeyCopy(TableReadonly<byte[], byte[]> source)
    {
        Map<ByteArray, byte[]> dataMap = new HashMap<>();
        source.forEach(tableEntry -> {
            byte[] key = (byte[])tableEntry.getKey();
            byte[] value = tableEntry.getValue();
            if (value==null)
            {
                System.out.println(key + "is NULL");
            }
            dataMap.put(new ByteArray(key), value);
        });
        
        dataMap.forEach((k, v)->{
            if (v==null)
            {
                System.out.println(k);
            }
        });
        
        dataMap.entrySet().removeIf( e->e.getValue()==null);

        return dataMap;
    }

    public interface MergeDatabaseTransCallBack
    {
        void callBack(DBData dbdata);
    }

    public interface MergeDataTask
    {
        boolean doTask(DBMergeTrans trans) throws Stream.EOFException, Stream.DecodeException;
    }
    
    public static class MergeRole implements MergeDataTask
    {
        @Override
        public boolean doTask(DBMergeTrans trans) throws EOFException, DecodeException
        {
            System.out.println("Merge Role begin ... ");
            Map<Integer, DBRole> slaverRole = trans.slaverData.role;
            Map<Integer, DBRole> masterRole = trans.masterData.role;
            
            final Table<Integer, DBRole> role = trans.role;

            slaverRole.forEach((k, v)->
            {
                role.put(k, v);
            });
            slaverRole.clear();
            
            masterRole.forEach((k, v)->
            {
                role.put(k, v);
            });
            masterRole.clear();
            
            final Map<String, Integer> slaverRoleNames = trans.slaverData.rolename;
            final Map<String, Integer> masterRoleNames = trans.masterData.rolename;
            
            final Table<String, Integer> rolename = trans.rolename;
            masterRoleNames.forEach((k, v)->
            {
                rolename.put(k, v);
            });
            masterRoleNames.clear();
            
            slaverRoleNames.forEach((k, v)->
            {
                Integer oldTimes = rolename.get(k);
                if (oldTimes!=null)
                {
                    rolename.put(k, v+oldTimes);
                }
                else
                {
                    rolename.put(k, v);
                }
            });
            slaverRoleNames.clear();
            
            Map<String, DBRoleShare> slaverRoleShare = trans.slaverData.roleshare;
            Map<String, DBRoleShare> masterRoleShare = trans.masterData.roleshare;
            final Table<String, DBRoleShare> disRoleShare = trans.roleshare;
            
            slaverRoleShare.forEach((k, v)->{
                disRoleShare.put(k, v);
            });
            slaverRoleShare.clear();
            
            masterRoleShare.forEach((k, v)->{
                disRoleShare.put(k, v);
            });
            masterRoleShare.clear();
            
            
            System.out.println("Merge Role process Name Conflict begin ... ");
            processNameConflict();
            System.out.println("Merge Role process Name Conflict over ... ");

            System.out.println("Merge Role over ... ");
            return true;
        }
        
        static void processNameConflict()
        {
        	//TODO this method will process same name on different role 
        }
        
    }

    public static class MergeWorld implements MergeDataTask
    {
        Map<ByteArray, byte[]> mWorld;
        Map<ByteArray, byte[]> sWorld;
        Table<byte[], byte[]> disWorld;
        
        private void mergeOpenday()
        {
            byte[] key = Stream.encodeStringLE("openDay");
            byte[] data = mWorld.get(new ByteArray(key));
            if (data != null)
            {
                disWorld.put(key, data);
            }
        }
        
        private void mergeWorldMails() throws DecodeException, EOFException
        {
            byte[] key = Stream.encodeStringLE("worldMails");
            byte[] sData = sWorld.get(new ByteArray(key));
            byte[] mData = mWorld.get(new ByteArray(key));
            List<SBean.DBWorldMail> sMails = new ArrayList<>();
            List<SBean.DBWorldMail> mMails = new ArrayList<>();
            if (sData != null)
            {
                sMails = Stream.decodeListLE(SBean.DBWorldMail.class, sData);
            }

            if (mData != null)
            {
                mMails = Stream.decodeListLE(SBean.DBWorldMail.class, mData);
            }

            mMails.addAll(sMails);
            disWorld.put(key, Stream.encodeListLE(mMails));
        }
        
        private void mergeArena()
        {
            byte[] key = Stream.encodeStringLE("arena");
            byte[] sData = sWorld.get(new ByteArray(key));
            byte[] mData = mWorld.get(new ByteArray(key));
                
                
            if (mData != null && sData != null)
            {
                SBean.DBArena mArena = getArenaFromByteArray(mData);
                SBean.DBArena sArena = getArenaFromByteArray(sData);
                    
                SBean.DBArena disArena = new SBean.DBArena(mergeRank(mArena, sArena), mergeReward(mArena, sArena), mArena.padding);
                disWorld.put(key, transArenaToByteArray(disArena));
            }
        }
        
        private void mergeBWArena()
        {
			byte[] key = Stream.encodeStringLE("bwarena");
			byte[] sData = sWorld.get(new ByteArray(key));
			byte[] mData = mWorld.get(new ByteArray(key));
			
			if (sData!=null && mData!=null)
			{
			    DBBWArena mBWArena = getBWArenaFromByteArray(mData);
			    DBBWArena sBWArena = getBWArenaFromByteArray(sData);
			    
			    List<SBean.BWArenaRankRole> whiteRank = mergeBWArenaWhiteRankRole(mBWArena, sBWArena);
			    List<SBean.BWArenaRankRole> blackRank = mergeBWArenaBlackRankRole(mBWArena, sBWArena);
			    List<SBean.BWArenaRankRole> whiteRankSnap = mergeBWArenaSnapWhiteRankRole(mBWArena, sBWArena);
			    List<SBean.BWArenaRankRole> blackRankSnap = mergeBWArenaSnapBlackRankRole(mBWArena, sBWArena);
			    Map<Integer, SBean.BWArenaRewardRole> whiteRankRewards = mergeBWArenaWhiteRankReward(mBWArena, sBWArena);
			    Map<Integer, SBean.BWArenaRewardRole> blackRankRewards = mergeBWArenaBlackRankReward(mBWArena, sBWArena);
			    int rankRefreshStamp = mergeBWArenaRankRefreshStamp(mBWArena, sBWArena);
			    int rankRewardStamp = mergeBWArenaRankRewardStamp(mBWArena, sBWArena);
			    Map<Integer, SBean.DBBWArenaLvlCache> lvlCache = mergeBWArenaLvlCacheStamp(mBWArena, sBWArena);
			    int padding = mergeBWArenaPadding(mBWArena, sBWArena);
			    DBBWArena disBWArena = new DBBWArena(whiteRank, whiteRankSnap, blackRank, blackRankSnap, whiteRankRewards, blackRankRewards,
			            rankRefreshStamp, rankRewardStamp, lvlCache, padding);
			    disWorld.put(key, transBWArenaFromByteArray(disBWArena));
			}
        }
        
        private void mergeClimbTower()
        {
			byte[] key = Stream.encodeStringLE("climbTower");
			byte[] sData = sWorld.get(new ByteArray(key));
			byte[] mData = mWorld.get(new ByteArray(key));
			if (mData!=null && sData!=null)
			{
			    SBean.DBClimbTowerServerRecordDataCfg sClimbTowerCfg = getClimbTowerFromByteArray(sData);
			    SBean.DBClimbTowerServerRecordDataCfg mClimbTowerCfg = getClimbTowerFromByteArray(mData);
			    SBean.DBClimbTowerServerRecordDataCfg disClimbTowerCfg = mergeClimbTower(mClimbTowerCfg, sClimbTowerCfg);
			    disWorld.put(key, transClimbTowerToByteArray(disClimbTowerCfg));
			}
        }
        
        private void mergeRanks()
        {
			byte[] key = Stream.encodeStringLE("ranks");
			byte[] sData = sWorld.get(new ByteArray(key));
			byte[] mData = mWorld.get(new ByteArray(key));
			if (sData!=null && mData!=null)
			{
				DBRanks sRanks = getRanksFromByteArray(sData);
				DBRanks mRanks = getRanksFromByteArray(mData);
				DBRanks disRanks = mergeRanks(mRanks, sRanks);
				disWorld.put(key, transRanksToByteArray(disRanks));
			}
        }
        
        private void mergeGameConf()
        {
			byte[] key = Stream.encodeStringLE("gameconf");
			byte[] sData = sWorld.get(new ByteArray(key));
			byte[] mData = mWorld.get(new ByteArray(key));
			
			SBean.DBGameConfData sGameConf = null;
			SBean.DBGameConfData mGameConf = null;
			if(sData != null && mData != null)
			{
				sGameConf = new SBean.DBGameConfData();
				mGameConf = new SBean.DBGameConfData();
				try
                {
                    Stream.decodeLE(sGameConf, sData);
                    Stream.decodeLE(mGameConf, mData);
                }
                catch (DecodeException | EOFException e)
                {
                    e.printStackTrace();
                    return;
                }
				
				SBean.DBGameConfData gameConf = mergeGameConf(mGameConf, sGameConf);
				disWorld.put(key, Stream.encodeLE(gameConf));
			}
        }
        
        private static SBean.DBGameConfData mergeGameConf(SBean.DBGameConfData master, SBean.DBGameConfData slaver)
        {
			SBean.DBGameConfData disGameConf = new SBean.DBGameConfData();
			Map<Integer, SBean.DBGroupBuy> mergedGroupBuy = mergeGroupBuy(master.groupBuy, slaver.groupBuy);
            Map<Integer, SBean.DBGiftPack> mergedGiftPack = mergeGiftPack(master.giftPack, slaver.giftPack);
			disGameConf.giftPack = mergedGiftPack;
			disGameConf.groupBuy = mergedGroupBuy;
            return disGameConf;
        }
        
        private static Map<Integer, SBean.DBGroupBuy> mergeGroupBuy(Map<Integer, SBean.DBGroupBuy> master, Map<Integer, SBean.DBGroupBuy> slaver)
        {
            final Map<Integer, SBean.DBGroupBuy> disGroupBuy = new HashMap<>();
            master.forEach((k, v) -> {
                disGroupBuy.put(k, v);
            });
            
            slaver.forEach((k, v) -> {
                SBean.DBGroupBuy masterGroupBuy = disGroupBuy.get(k);
                if (masterGroupBuy!=null)
                {
                    SBean.DBGroupBuy gb = new SBean.DBGroupBuy();
                    Set<Integer> buyedRoleSet = new TreeSet<>();
                    buyedRoleSet.addAll(masterGroupBuy.buyRoles);
                    buyedRoleSet.addAll(v.buyRoles);
                    gb.buyRoles = buyedRoleSet;
                    
                    final Map<Integer, Integer> buyLogs = new HashMap<>();
                    final Map<Integer, Integer> masterBuyLogs = v.buyLogs;
                    final Map<Integer, Integer> slaverBuyLogs = masterGroupBuy.buyLogs;
                    masterBuyLogs.forEach((goodId, buyTimes)->{
                        int slaverTimes = slaverBuyLogs.getOrDefault(goodId, 0);
                        buyLogs.put(goodId, buyTimes + slaverTimes);
                    });
                    gb.buyLogs = buyLogs;
                    gb.id = v.id;
                    disGroupBuy.put(k, gb);
                }
                else
                {
                    disGroupBuy.put(k, v);
                }
            });
            
            return disGroupBuy;
        }
        
        private static Map<Integer, SBean.DBGiftPack> mergeGiftPack(Map<Integer, SBean.DBGiftPack> master, Map<Integer, SBean.DBGiftPack> slaver)
        {
            final Map<Integer, SBean.DBGiftPack> disGiftPack = new HashMap<>();
            master.forEach((k, v)->{
                disGiftPack.put(k, v);
            });
            
            slaver.forEach((k, v)->{
                final SBean.DBGiftPack masterGiftPack = disGiftPack.get(k);
                if (masterGiftPack!=null)
                {
                    SBean.DBGiftPack giftPack = new SBean.DBGiftPack();
                    giftPack.id = v.id;
                    giftPack.useCount = new HashMap<>();
                    
                    final Map<String, Integer> masterUseInfo = masterGiftPack.useCount;
                    final Map<String, Integer> slaverUseInfo = v.useCount;
                    
                    masterUseInfo.forEach((shortCode, times)->{
                        Integer slaverTimes = slaverUseInfo.get(shortCode);
                        if (slaverTimes != null)
                        {
                            giftPack.useCount.put(shortCode, Math.max(times, slaverTimes));
                        }
                        else
                        {
                            giftPack.useCount.put(shortCode, times);
                        }
                    });
                    
                    disGiftPack.put(k, giftPack);
                }
                else 
                {
                    disGiftPack.put(k, v);
                }
            });
            
            return disGiftPack;
        }
        
        @Override
        public boolean doTask (DBMergeTrans trans) throws Stream.EOFException, Stream.DecodeException
        {
            System.out.println(" Merge World begin ... ");
            
	        mWorld = trans.masterData.world;
	        sWorld = trans.slaverData.world;

            disWorld = trans.world;
            
            mergeOpenday();
            mergeWorldMails();
            // Attention ignore roll notice
            mergeArena();
            mergeBWArena();
            // Attention ignore boss
            mergeClimbTower();
            mergeRanks();
            mergeGameConf();

            System.out.println(" Merge World over ... ");
            return true;
        }
        
        private DBRanks mergeRanks(DBRanks mRanks, DBRanks sRanks)
        {
            List<DBRoleRanks> mRoleRanks = mRanks.roleRanks;
            List<DBRoleRanks> sRoleRanks = sRanks.roleRanks;
            List<DBRoleRanks> disRoleRanks = mergeRoleRanks(mRoleRanks, sRoleRanks);
            
            List<DBSectRanks> mSectRanks = mRanks.sectRanks;
            List<DBSectRanks> sSectRanks = sRanks.sectRanks;
            List<DBSectRanks> disSectRanks = mergeSectRanks(mSectRanks, sSectRanks);
            
            return new DBRanks(disRoleRanks, disSectRanks, mRanks.padding1 + sRanks.padding1,
                    mRanks.padding2+sRanks.padding2);
        }

        private List<DBSectRanks> mergeSectRanks(List<DBSectRanks> mSectRanks, List<DBSectRanks> sSectRanks)
        {
            List<DBSectRanks> disSectRanks = new ArrayList<>(mSectRanks.size()+sSectRanks.size());
            
            for (DBSectRanks mSectRank : mSectRanks)
            {
                DBSectRanks sSectRank = sSectRanks.stream().filter(x->x.id==mSectRank.id).findFirst().orElse(null);
                if (sSectRank!=null)
                {
                    List<RankSect> rankedSect = mergeRankedSect(mSectRank.ranks, sSectRank.ranks);
                    List<RankSect> snapRankSect = mergeRankedSect(mSectRank.snapshot, sSectRank.snapshot);
                    DBSectRanks disSectRank = new DBSectRanks(mSectRank.id, rankedSect, snapRankSect, 
                            Math.max(mSectRank.snapshotCreateTime, sSectRank.snapshotCreateTime), 
                            Math.max(mSectRank.lastRewardTime, sSectRank.lastRewardTime));
                    disSectRanks.add(disSectRank);
                }
                else
                {
                    disSectRanks.add(mSectRank);
                }
            }
            sSectRanks.stream().filter( y->!mSectRanks.stream().anyMatch(z->z.id==y.id)).forEachOrdered(x->
            {
                disSectRanks.add(x);
            });
            
            disSectRanks.sort((a,b)->a.id-b.id);
            
            return disSectRanks;
        }

        private List<RankSect> mergeRankedSect(List<RankSect> mRanks, List<RankSect> sRanks)
        {
            List<RankSect> rankedSects = new ArrayList<RankSect>(mRanks.size()+sRanks.size());
            rankedSects.addAll(mRanks);
            rankedSects.addAll(sRanks);
            return rankedSects.stream().sorted((a,b)->b.rankKey-a.rankKey).collect(Collectors.toList());
        }

        private List<DBRoleRanks> mergeRoleRanks(List<DBRoleRanks> mRoleRanks, List<DBRoleRanks> sRoleRanks)
        {
            List<DBRoleRanks> disRoleRanks = new ArrayList<>(mRoleRanks.size()+sRoleRanks.size());
            for (DBRoleRanks mRoleRank : mRoleRanks)
            {
                DBRoleRanks sRoleRank = sRoleRanks.stream().filter(x->x.id==mRoleRank.id).findFirst().orElse(null);
                if (sRoleRank!=null)
                {
                    List<RankRole> rankedRole = mergeRankedRole(mRoleRank.ranks, sRoleRank.ranks);
                    List<RankRole> snapRankRole = mergeRankedRole(mRoleRank.snapshot, sRoleRank.snapshot);
                    DBRoleRanks disRoleRank = new DBRoleRanks(mRoleRank.id, rankedRole, snapRankRole,
                            Math.max(mRoleRank.snapshotCreateTime, sRoleRank.snapshotCreateTime), 
                            Math.max(mRoleRank.lastRewardTime, sRoleRank.lastRewardTime));
                    disRoleRanks.add(disRoleRank);
                }
                else
                {
                    disRoleRanks.add(mRoleRank);
                }
            }
            return disRoleRanks;
        }
        
        private List<RankRole> mergeRankedRole(List<RankRole> mRanks, List<RankRole> sRanks)
        {
            List<RankRole> rankRoles = new ArrayList<>(mRanks.size()+sRanks.size());
            rankRoles.addAll(mRanks);
            rankRoles.addAll(sRanks);
            return rankRoles.stream().sorted((a,b)-> b.rankKey-a.rankKey).collect(Collectors.toList());
        }

        private byte[] transRanksToByteArray(DBRanks ranks)
        {
            if (ranks==null)
                return new byte[]{};
            return Stream.encodeLE(ranks);
        }

        private DBRanks getRanksFromByteArray(byte[] data)
        {
			SBean.DBRanks ranks = new SBean.DBRanks();
			try
            {
                Stream.decodeLE(ranks, data);
            }
            catch (DecodeException | EOFException e)
            {
                e.printStackTrace();
                return null;
            }
			return ranks;
        }
        
        private DBClimbTowerServerRecordDataCfg mergeClimbTower(DBClimbTowerServerRecordDataCfg mData, DBClimbTowerServerRecordDataCfg sData)
        {
            Map<Integer, SBean.DBClimbTowerRecordDataCfg> mClimbTowerData = mData.datas;
            final Map<Integer, SBean.DBClimbTowerRecordDataCfg> sClimbTowerData = sData.datas;
            final Map<Integer, SBean.DBClimbTowerRecordDataCfg> disClimbTowerData = new HashMap<>();
            
            mClimbTowerData.forEach((k, v)->
            {
                SBean.DBClimbTowerRecordDataCfg clibDataCfg = sClimbTowerData.get(k);
                if (clibDataCfg!=null && v.floor<clibDataCfg.floor)
                {
                    disClimbTowerData.put(k, clibDataCfg);
                }
                else
                {
                    disClimbTowerData.put(k, v);
                }
            });
            
            return new DBClimbTowerServerRecordDataCfg(disClimbTowerData);
        }
        
        private byte[] transClimbTowerToByteArray(DBClimbTowerServerRecordDataCfg tower)
        {
            if (tower==null)
                return new byte[]{};
            return Stream.encodeLE(tower);
        }

        private SBean.DBClimbTowerServerRecordDataCfg getClimbTowerFromByteArray(byte[] data)
        {
			SBean.DBClimbTowerServerRecordDataCfg climbTower = new SBean.DBClimbTowerServerRecordDataCfg();
			try
            {
                Stream.decodeLE(climbTower, data);
            }
            catch (DecodeException | EOFException e)
            {
                e.printStackTrace();
                return null;
            }
			return climbTower;
        }
        
        private int mergeBWArenaPadding(DBBWArena mBWArena, DBBWArena sBWArena)
        {
            return Math.max(mBWArena.padding, sBWArena.padding);
        }

        private Map<Integer, DBBWArenaLvlCache> mergeBWArenaLvlCacheStamp(DBBWArena mBWArena, DBBWArena sBWArena)
        {
            final Map<Integer, DBBWArenaLvlCache> disLvlCache = new HashMap<>();
            Map<Integer, DBBWArenaLvlCache> mLvlCache = mBWArena.lvlCaches;
            Map<Integer, DBBWArenaLvlCache> sLvlCache = mBWArena.lvlCaches;
            
            mLvlCache.forEach((k, v) ->
            {
            	if (v==null)
            		return;
                DBBWArenaLvlCache disLevelCache = new DBBWArenaLvlCache(new HashMap<>());
                if (v.cache != null)
                	disLevelCache.cache.putAll(v.cache);
                DBBWArenaLvlCache sLevlCache = sLvlCache.get(k);
                if (sLevlCache!=null && sLevlCache.cache!=null)
                {
                    disLevelCache.cache.putAll(sLevlCache.cache);
                }
                disLvlCache.put(k, disLevelCache);
            });
            return disLvlCache;
        }

        private int mergeBWArenaRankRewardStamp(DBBWArena mBWArena, DBBWArena sBWArena)
        {
            return Math.max(mBWArena.rankRewardStamp, sBWArena.rankRewardStamp);
        }

        private int mergeBWArenaRankRefreshStamp(DBBWArena mBWArena, DBBWArena sBWArena)
        {
            return Math.max(mBWArena.rankRefreshStamp, sBWArena.rankRefreshStamp);
        }

        private Map<Integer, BWArenaRewardRole> mergeBWArenaBlackRankReward(DBBWArena mBWArena, DBBWArena sBWArena)
        {
            Map<Integer, BWArenaRewardRole> blackRankRewards = new HashMap<Integer, BWArenaRewardRole>();
            blackRankRewards.putAll(mBWArena.blackRankRewards);
            blackRankRewards.putAll(sBWArena.blackRankRewards);
            return blackRankRewards;
        }

        private Map<Integer, BWArenaRewardRole> mergeBWArenaWhiteRankReward(DBBWArena mBWArena, DBBWArena sBWArena)
        {
            Map<Integer, BWArenaRewardRole> whiteRankRewards = new HashMap<Integer, BWArenaRewardRole>();
            whiteRankRewards.putAll(mBWArena.whiteRankRewards);
            whiteRankRewards.putAll(sBWArena.whiteRankRewards);
            return whiteRankRewards;
        }

        private List<BWArenaRankRole> mergeBWArenaSnapBlackRankRole(DBBWArena mBWArena, DBBWArena sBWArena)
        {
            return mergeBWArenaRankRole(mBWArena.blackSnapshot, sBWArena.blackSnapshot);
        }

        private List<BWArenaRankRole> mergeBWArenaSnapWhiteRankRole(DBBWArena mBWArena, DBBWArena sBWArena)
        {
            return mergeBWArenaRankRole(mBWArena.whiteSnapshot, sBWArena.whiteSnapshot);
        }

        private List<BWArenaRankRole> mergeBWArenaBlackRankRole(DBBWArena mBWArena, DBBWArena sBWArena)
        {
            return mergeBWArenaRankRole(mBWArena.blackRanks, sBWArena.blackRanks);
        }
        
        private List<BWArenaRankRole> mergeBWArenaWhiteRankRole(DBBWArena mBWArena, DBBWArena sBWArena)
        {
            return mergeBWArenaRankRole(mBWArena.whiteRanks, sBWArena.whiteRanks);
        }
        
        private List<BWArenaRankRole> mergeBWArenaRankRole(List<BWArenaRankRole> mRankRoles, List<BWArenaRankRole> sRankRoles)
        {
            List<BWArenaRankRole> arenaRankRoles = new ArrayList<>(mRankRoles.size()+sRankRoles.size());
            arenaRankRoles.addAll(mRankRoles);
            arenaRankRoles.addAll(sRankRoles);
            arenaRankRoles.sort( (x, y)-> ((y.lvl<<16)|(y.score&0xFFFF) - (x.lvl<<16)|(x.score&0xFFFF)));
            return arenaRankRoles;
        }
        
        private byte[] transBWArenaFromByteArray(DBBWArena bwarena)
        {
            if (bwarena==null)
                return new byte[]{};
            return Stream.encodeLE(bwarena);
        }

        private DBBWArena getBWArenaFromByteArray(byte[] data)
        {
			SBean.DBBWArena dbBWArena = new SBean.DBBWArena();
			try
            {
                Stream.decodeLE(dbBWArena, data);
            }
            catch (DecodeException | EOFException e)
            {
                e.printStackTrace();
                return null;
            }
			return dbBWArena;
        }
        
        private byte[] transArenaToByteArray(DBArena arena)
        {
            return Stream.encodeLE(arena);
        }
        
        private DBArena getArenaFromByteArray(byte[] data)
        {
            SBean.DBArena arena = new SBean.DBArena();
            try
            {
                Stream.decodeLE(arena, data);
            }
            catch (DecodeException | EOFException e)
            {
                e.printStackTrace();
                return null;
            }
            return arena;
        }
        
        private DBArenaReward mergeReward(DBArena mArena, DBArena sArena)
        {
            SBean.DBArenaReward mReward = mArena.reward;
            SBean.DBArenaReward sReward = sArena.reward;
            Map<Integer, Integer> role2rankMap = new HashMap<>();
            role2rankMap.putAll(mReward.rewardRoles);
            role2rankMap.putAll(sReward.rewardRoles);
            int rewardTime = Math.max(mReward.rewardTime, sReward.rewardTime);
            return new SBean.DBArenaReward(rewardTime, role2rankMap);
        }
        
        private Map<Integer, Integer> mergeRank(SBean.DBArena mArena, SBean.DBArena sArena)
        {
            List<Map.Entry<Integer, Integer>> list = new ArrayList<>();
            list.addAll(mArena.normal.entrySet());
            list.addAll(sArena.normal.entrySet());
            list.sort( (x,y)->x.getKey()-y.getKey());
                    
            int rank = 1;
            Map<Integer, Integer> rank2role = new HashMap<>();
            for (Map.Entry<Integer, Integer> entry : list)
            {
                rank2role.put(rank++, entry.getValue());
            }
            return rank2role;
        }
    }
    
    public static class MergeMail implements MergeDataTask
    {
        @Override
        public boolean doTask(DBMergeTrans trans) throws EOFException, DecodeException
        {
            System.out.println("Merge Mail Begin...");
            Map<Integer, DBMailBox> slaverMails = trans.slaverData.mail;
            Map<Integer, DBMailBox> masterMails = trans.masterData.mail;
            final Table<Integer, DBMailBox> disMails = trans.mail;
            
            slaverMails.forEach((k, v)->
            {
                disMails.put(k, v);
            });
            
            masterMails.forEach((k, v)->
            {
                disMails.put(k, v);
            });

            System.out.println("Merge Mail Over...");
            
            return true;
        }
    }

    public static class MergeMarriage implements MergeDataTask
    {
        @Override
        public boolean doTask(DBMergeTrans trans) throws EOFException, DecodeException
        {
            System.out.println("Merge Marriage Begin ...");
            
            Map<Integer, DBMarriageShare> slaverMarriage = trans.slaverData.marriageshare;
            Map<Integer, DBMarriageShare> masterMarriage = trans.masterData.marriageshare;
            final Table<Integer, DBMarriageShare> disMarriage = trans.marriageshare;
            slaverMarriage.forEach((k, v) ->
            {
                disMarriage.put(k, v);
            });
            
            masterMarriage.forEach((k, v) ->
            {
                disMarriage.put(k, v);
            });

            System.out.println("Merge Marriage Over ...");
            
            return true;
        }
        
    }
    
    public static class MergeSect implements MergeDataTask
    {
        @Override
        public boolean doTask(DBMergeTrans trans) throws EOFException, DecodeException
        {
            System.out.println("Merge Sect Begin ...");
            
            Map<Integer, DBSect> slaverSects = trans.slaverData.sect;
            Map<Integer, DBSect> masterSects = trans.masterData.sect;
            
            Map<String, Integer> slaverSectNames = trans.slaverData.sectnames;
            Map<String, Integer> masterSectNames = trans.masterData.sectnames;
            
            final Table<Integer, DBSect> disSects = trans.sect;
            final Table<String, Integer> disSectNames = trans.sectname;
            
            slaverSects.forEach((k, v)->
            {
                disSects.put(k, v);
            });
            
            masterSects.forEach((k, v)->
            {
                disSects.put(k, v);
            });
            
            slaverSectNames.forEach((k, v)->
            {
                disSectNames.put(k, v);
            });
            
            masterSectNames.forEach((k, v)->
            {
                disSectNames.put(k, v);
            });

            System.out.println("Merge Sect Over ...");

            return true;
        }
    }
    
    public static class MergeUser implements MergeDataTask
    {
        @Override
        public boolean doTask(DBMergeTrans trans) throws EOFException, DecodeException
        {
            System.out.println("Merge User Begin ...");
            
            Map<String, DBUser> slaverUser = trans.slaverData.user;
            Map<String, DBUser> masterUser = trans.masterData.user;
            final Table<String, DBUser> disUser = trans.user;
            
            slaverUser.forEach((k, v)->
            {
                disUser.put(k, v);
            });
            
            masterUser.forEach((k, v)->
            {
                disUser.put(k, v);
            });

            System.out.println("Merge User Over ...");
            
            return true;
        }
    }
    
    public static class MergeMaxIds implements MergeDataTask
    {
        @Override
        public boolean doTask(DBMergeTrans trans) throws EOFException, DecodeException
        {
            System.out.println("Merge MaxIds Begin...");
            
            Map<String, Integer> slaverMaxIds = trans.slaverData.maxids;
            Map<String, Integer> masterMaxIds = trans.masterData.maxids;
            final Table<String, Integer> disMaxIds = trans.maxids;
            
            slaverMaxIds.forEach((k, v)->
            {
                disMaxIds.put(k, v);
            });
            
            masterMaxIds.forEach((k, v)->
            {
                disMaxIds.put(k, v);
            });

            System.out.println("Merge MaxIds Over...");
            return true;
        }
        
    }
    
    public class DBData
    {
        public DBData copyTo(DBData target)
        {
            if (target==null)
                return null;

            target.world = world;
            target.maxids = maxids;
            target.user = user;
            target.roleshare = roleshare;
            target.role = role;
            target.rolename = rolename;
            target.role = role;
            target.rolename = rolename;
            target.mail = mail;
            target.sect = sect;
            target.sectnames = sectnames;
            target.marriageshare = marriageshare;

            return target;
        }

        public Map<ByteArray, byte[]> world;

        public Map<String, Integer> maxids;

        public Map<String, DBUser> user;

        public Map<String, DBRoleShare> roleshare;

        public Map<Integer, DBRole> role;

        public Map<String, Integer> rolename;

        public Map<Integer, DBMailBox> mail;

        public Map<Integer, DBSect> sect;
        
        public Map<String, Integer> sectnames;

        public Map<Integer, DBMarriageShare> marriageshare;
    }
    
    public class GetDBDataTrans implements Transaction
    {
        public GetDBDataTrans (MergeDatabaseTransCallBack callback)
        {
            this.callBack = callback;
        }

        @Override
        public boolean doTransaction()
        {
            initDBData();
            
            dbData.world = GameServerMerger.arrayKeyCopy(world);
            //dbData.world = GameServerMerger.deepCopy(world);
            System.out.println("world read over ...");
            dbData.maxids = GameServerMerger.deepCopy(maxids);
            System.out.println("maxids read over ... size " + dbData.maxids.size());
            dbData.user = GameServerMerger.deepCopy(user);
            System.out.println("user read over ... size " + dbData.user.size());
            dbData.roleshare = GameServerMerger.deepCopy(roleshare);
            System.out.println("roleshare read over ... size " + dbData.roleshare.size());
            dbData.role = GameServerMerger.deepCopy(role);
            System.out.println("role read over ... size " + dbData.role.size());
            dbData.rolename = GameServerMerger.deepCopy(rolename);
            System.out.println("rolename read over ... size " + dbData.rolename.size());
            dbData.mail = GameServerMerger.deepCopy(mail);
            System.out.println("mail read over ... size " + dbData.mail.size());
            dbData.sect = GameServerMerger.deepCopy(sect);
            System.out.println("sect read over ... size " + dbData.sect.size());
            dbData.sectnames = GameServerMerger.deepCopy(sectname);
            System.out.println("sectnames read over ... size " + dbData.sect.size());
            dbData.marriageshare = GameServerMerger.deepCopy(marriageshare);
            System.out.println("marriageshare read over ... size " + dbData.marriageshare.size());
            
            return true;
        }

        public void initDBData ()
        {
            dbData = new DBData();
        }

        @Override
        public void onCallback(ErrorCode errCode)
        {
            callBack.callBack(dbData);
        }

		@AutoInit
		public TableReadonly<byte[], byte[]> world;

		@AutoInit
		public TableReadonly<String, Integer> maxids;

        @AutoInit
        public TableReadonly<String, DBUser> user;

        @AutoInit
        public TableReadonly<String, DBRoleShare> roleshare;

        @AutoInit
        public TableReadonly<Integer, DBRole> role;

        @AutoInit
        public TableReadonly<String, Integer> rolename;

        @AutoInit
        public TableReadonly<Integer, DBMailBox> mail;

		@AutoInit
		public TableReadonly<Integer, DBSect> sect;
		
		@AutoInit
		public TableReadonly<String, Integer> sectname;

        @AutoInit
        public TableReadonly<Integer, DBMarriageShare> marriageshare;

        private DBData dbData ;
        private MergeDatabaseTransCallBack callBack;
    }

    public class DBMergeTrans implements Transaction
    {
        public DBMergeTrans (DBData s, DBData m, MergeDataTask task, MergeDatabaseTransCallBack callback)
        {
            slaverData = s;
            masterData = m;
            this.exeTask = task;
            this.callBack = callback;
        }

        @Override
        public boolean doTransaction()
        {
            try
            {
                return exeTask.doTask(this);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        }

        public void initDBData ()
        {
            //dbData = new DBData();
        }

        @Override
        public void onCallback(ErrorCode errCode)
        {
            callBack.callBack(masterData);
        }

        @AutoInit
        public Table<byte[], byte[]> world;

        @AutoInit
        public Table<String, Integer> maxids;

        @AutoInit
        public Table<String, DBUser> user;

        @AutoInit
        public Table<String, DBRoleShare> roleshare;

        @AutoInit
        public Table<Integer, DBRole> role;

        @AutoInit
        public Table<String, Integer> rolename;

        @AutoInit
        public Table<Integer, DBMailBox> mail;

        @AutoInit
        public Table<Integer, DBSect> sect;
        
        @AutoInit
        public Table<String, Integer> sectname;

        @AutoInit
        public Table<Integer, DBMarriageShare> marriageshare;

        private DBData slaverData;
        private DBData masterData;
        private MergeDataTask exeTask;
        private MergeDatabaseTransCallBack callBack;
    }

    public void init(String[] args) throws Exception
    {
        ArgsMap am = new ArgsMap(args);
        masterCfg = am.get("mdb", "gs.cfg");
        slaverCfg = am.get("sdb", "gs.cfg");
        distCfg = am.get("ddb", "gs.cfg");

        masterDB = ket.kdb.Factory.newDB();
        slaverDB = ket.kdb.Factory.newDB();
        distDB = ket.kdb.Factory.newDB();

        logger = Logger.getLogger("mergeLogger");
        masterDB.setLogger(logger);
        slaverDB.setLogger(logger);
        distDB.setLogger(logger);

        Path mPath = Paths.get(masterCfg);
        Path sPath = Paths.get(slaverCfg);
        Path dPath = Paths.get(distCfg);

        System.out.println(mPath.getFileName());
        System.out.println(sPath.getFileName());
        System.out.println(dPath.getFileName());

        masterDB.open(mPath.getParent(), mPath);
        slaverDB.open(sPath.getParent(), sPath);
        distDB.open(dPath.getParent(), dPath);
    }

    public void cleanUp()
    {
        slaverDB.close();
        masterDB.close();
        distDB.close();
    }
    
    public void doMerge()
    {
        DBData slaverData = getDBData(false);
        DBData masterData = getDBData(true);
        mergeDB(slaverData, masterData);
    }

    private void mergeDB (DBData slaverData, DBData masterData)
    {
        final CountDownLatch countDownLatch = new CountDownLatch(7);
        distDB.execute(new DBMergeTrans(slaverData, masterData, new MergeWorld(), (newData)->{System.out.println(">>>merge world over ..."); countDownLatch.countDown();}));
        distDB.execute(new DBMergeTrans(slaverData, masterData, new MergeRole(), (newData)->{System.out.println(">>>merge role over ..."); countDownLatch.countDown();}));
        distDB.execute(new DBMergeTrans(slaverData, masterData, new MergeMarriage(), (newData)->{System.out.println(">>>merge marriage over ..."); countDownLatch.countDown();}));
        distDB.execute(new DBMergeTrans(slaverData, masterData, new MergeUser(), (newData)->{System.out.println(">>>merge user over ..."); countDownLatch.countDown();}));
        distDB.execute(new DBMergeTrans(slaverData, masterData, new MergeMail(), (newData)->{System.out.println(">>>merge mail over ..."); countDownLatch.countDown();}));
        distDB.execute(new DBMergeTrans(slaverData, masterData, new MergeSect(), (newData)->{System.out.println(">>>merge sect over ..."); countDownLatch.countDown();}));
        distDB.execute(new DBMergeTrans(slaverData, masterData, new MergeMaxIds(), (newData)->{System.out.println(">>>merge maxids over ..."); countDownLatch.countDown();}));
        
        try
        {
            System.out.println(" wait over");
            countDownLatch.await();
            System.out.println(" execute over");
        }
        catch (InterruptedException e)
        {
            System.out.println(" wait was interrupted");
            e.printStackTrace();
        }
    }

    private DBData getDBData (boolean master)
    {
        final CountDownLatch latch = new CountDownLatch(1);
        DB tarDB = slaverDB;
        if(master)
            tarDB = masterDB;
        final DBData data = new DBData();
        tarDB.execute(new GetDBDataTrans((dbdata ->
        {
            synchronized (latch)
            {
	            dbdata.copyTo(data);
	            latch.countDown();
            }
        })));

        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return data;
    }

    public static void main(String[] args)
    {
        GameServerMerger gsm = new GameServerMerger();
        try
        {
            gsm.init(args);
        }
        catch (Exception e)
        {
            System.out.println(" before begin");
            return;
        }
        gsm.doMerge();
        System.out.println("Merge task over ...");
        gsm.cleanUp();
        System.out.println("Clean task over ...");
        System.out.println("end....");
    }

    DB slaverDB;
    DB masterDB;
    DB distDB;

    String slaverCfg;
    String masterCfg;
    String distCfg;

    Logger logger;
}

