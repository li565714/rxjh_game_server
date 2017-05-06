package i3k.exchange;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import ket.kdb.Table;
import ket.kdb.TableEntry;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;

public class ExchangeManager 
{
	ExchangeManager(ExchangeServer es)
	{
		this.es = es;
	}
	
	void start()
	{	
		
	}
	
	void destroy()
	{
	}
	
	void onTimer(int timeTick)
	{
	}
	
	
	
	ExchangeServer es;
}
