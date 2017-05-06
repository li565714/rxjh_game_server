
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

public class ServerTable
{
	public synchronized boolean onSessionAnnounce(int sessionid, int serverID)
	{
		if (mapgs2s.containsKey(serverID))
			return false;
		if (maps2gs.containsKey(sessionid))
			return false;
		mapgs2s.put(serverID, sessionid);
		maps2gs.put(sessionid, serverID);
		return true;
	}
	
	public synchronized Integer tryCloseSessionByServerID(int serverID)
	{
		Integer sessionid = mapgs2s.remove(serverID);
		if(sessionid != null)
		{
			maps2gs.remove(sessionid, serverID);
		}
		
		return sessionid;
	}
	
	public synchronized void onSessionClose(int sessionid)
	{
		Integer serverID = maps2gs.remove(sessionid);
		if(serverID != null)
		{
			mapgs2s.remove(serverID, sessionid);
		}
	}
	
	public synchronized Integer getSessionIDByServerID(int serverID)
	{
		return mapgs2s.get(serverID);
	}
	
	public synchronized Integer getServerIDBySessionID(int sessionid)
	{
		return maps2gs.get(sessionid);
	}
	
	public synchronized Set<Integer> getAllServers()
	{
		return new HashSet<>(mapgs2s.keySet());
	}
	
	public synchronized Set<Integer> getAllServerSessions()
	{
		return new HashSet<>(maps2gs.keySet());
	}
	
	protected Map<Integer, Integer> mapgs2s = new HashMap<>();		// server id 2 sessionid
	protected Map<Integer, Integer> maps2gs = new HashMap<>();		// sessionid id 2 server id
}