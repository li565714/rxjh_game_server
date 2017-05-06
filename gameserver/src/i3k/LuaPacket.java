
package i3k;


import i3k.gmap.MapRole;
import i3k.gmap.BaseRole.Buff;
import i3k.gmap.DropGoods.DropItem;
import i3k.gs.Role;
import i3k.util.GVector3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class LuaPacket
{
	public static String[] decode(String data)
	{
		return data.split("\\|");
	}

}
