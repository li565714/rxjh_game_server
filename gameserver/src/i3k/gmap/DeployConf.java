package i3k.gmap;

import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

import i3k.gs.GameData;
import i3k.util.XmlElement;

public class DeployConf
{
	public static class MapDeploy
	{
		int mapServerId;
		Set<Integer> maps = new TreeSet<>();

		public MapDeploy(int mapServerId)
		{
			this.mapServerId = mapServerId;
		}

		public int getMapServerId()
		{
			return this.mapServerId;
		}

		public Set<Integer> getDeployMaps()
		{
			return this.maps;
		}

		public void addDeployMap(int mapId)
		{
			this.maps.add(mapId);
		}

	}

	public DeployConf(MapServer ms)
	{
		this.ms = ms;
	}

	public MapDeploy getMapDelpoy()
	{
		return this.config;
	}

	public void setConfigs(Map<Integer, Integer> cfgs)
	{
		this.config = this.checkConfigs(cfgs);
		ms.getLogger().info("set map server " + ms.getConfig().id + " 's map deploy config .");
		for (int mapId : this.config.getDeployMaps())
		{
			ms.getLogger().info("map server deploy map " + mapId + " .");
		}
		ms.getLogger().info("map deploy will be changed. " + (this.config.getDeployMaps().isEmpty() ? "no valid map is deploy on this map server " + ms.getConfig().id + " !!!" : "") + "\n");
	}

	public Map<Integer, Integer> loadConfigs(String filePath)
	{
		ms.getLogger().info("try load map deploy config file ...");
		try
		{
			XmlElement root = XmlElement.parseXml(filePath);
			Map<Integer, Integer> cfgs = parseConfigs(root);
			ms.getLogger().info("load map deploy config file success.");
			return cfgs;
		}
		catch (Throwable t)
		{
			ms.getLogger().warn("load map deploy config file failed !!!", t);
		}
		return null;
	}

	private MapDeploy checkConfigs(Map<Integer, Integer> cfgs)
	{
		MapDeploy deploy = new MapDeploy(ms.getConfig().id);
		if (cfgs != null)
		{
			for (Map.Entry<Integer, Integer> cfg : cfgs.entrySet())
			{
				int mapId = cfg.getKey();
				int serverId = cfg.getValue();
				try
				{
					GameData.getInstance().checkMapValid(mapId);
					if (deploy.getMapServerId() == serverId)
					{
						deploy.addDeployMap(mapId);
					}
				}
				catch (GameData.MapException e)
				{
					ms.getLogger().warn("mapserver " + serverId + " config deploy map " + mapId + " is invalid ==> " + e.getMessage());
				}
			}
		}
		return deploy;
	}

	private Map<Integer, Integer> parseConfigs(XmlElement root) throws Exception
	{
		Map<Integer, Integer> mapdeploy = new TreeMap<>();
		if (root.getName().equals("mapdeploy"))
		{
			for (XmlElement e : root.getChildrenByName("map"))
			{
				int mapserverId = e.getIntegerAttribute("mapserver");
				int mapId = e.getIntegerAttribute("mapid");
				if (mapdeploy.put(mapId, mapserverId) != null)
					throw new XmlElement.XmlReadException("map " + mapId + " deploy multiple servers !!!");
			}
		}
		return mapdeploy;
	}

	MapServer ms;

	MapDeploy config;
}
