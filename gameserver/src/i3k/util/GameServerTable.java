
package i3k.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import ket.kio.NetAddress;
import ket.kio.SimplePacket;
import ket.kio.NetManager;
import i3k.gs.GameData;
import i3k.rpc.Packet;
import i3k.SBean;

public class GameServerTable extends ServerTable
{
	public synchronized boolean onSessionAnnounce(int sessionid, int serverID, Set<Integer> zones)
	{
		if (mapgs2s.containsKey(serverID))
			return false;
		if (maps2gs.containsKey(sessionid))
			return false;
		for (int zone : zones)
		{
			if (mapz2gs.containsKey(zone))
				return false;
		}
		mapgs2s.put(serverID, sessionid);
		maps2gs.put(sessionid, serverID);
		for (int zone : zones)
		{
			mapz2gs.put(zone, serverID);
		}
		mapgs2z.put(serverID, zones);
		return true;
	}
	
	public synchronized Integer tryCloseSessionByServerID(int serverID)
	{
		Integer sessionid = mapgs2s.remove(serverID);
		if(sessionid != null)
		{
			maps2gs.remove(sessionid, serverID);
		}
		
		Set<Integer> zones = mapgs2z.remove(serverID);
		if(zones != null)
		{
			for (int zone : zones)
			{
				mapz2gs.remove(zone, serverID);
			}
		}
		
		return sessionid;
	}
	
	public synchronized void onSessionClose(int sessionid)
	{
		Integer serverID = maps2gs.remove(sessionid);
		if(serverID != null)
		{
			mapgs2s.remove(serverID, sessionid);
			Set<Integer> zones = mapgs2z.remove(serverID);
			if (zones != null)
			{
				for (int zone : zones)
				{
					mapz2gs.remove(zone, serverID);
				}
			}
		}
	}
	
	
	public synchronized Integer getServerIDByZoneID(int zoneID)
	{
		return mapz2gs.get(zoneID);
	}
	
	public synchronized Integer getSessionIDByZoneID(int zoneID)
	{
		Integer serverID = mapz2gs.get(zoneID);
		return serverID == null ? null : mapgs2s.get(serverID);
	}
	
	
	public synchronized Set<Integer> getZonesBySessionID(int sessionid)
	{
		Integer serverID = maps2gs.get(sessionid);
		return serverID == null ? null : mapgs2z.get(serverID);
	}
	
	Map<Integer, Integer> mapz2gs = new HashMap<>();		// zone id 2 server id
	Map<Integer, Set<Integer>> mapgs2z = new HashMap<>();		// serever id 2 zones
}