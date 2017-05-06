package i3k.util;

import i3k.SBean.sync_special_card_res;
import i3k.gs.GameData;

import java.util.List;
import java.util.ArrayList;

public class CyclicQueue<T>
{
	private static final int QUEUE_DEFAULT_SIZE = 16;
	public static final int MAX_PAGE_LENGTH	 = 20;
	
	final int buffSize;
	List<T> values = new ArrayList<>();
	
	int head;
	int tail;
	
	public CyclicQueue(int cap, int head, int tail, List<T> lst)
	{
		this.buffSize = (cap > 0 ? cap : QUEUE_DEFAULT_SIZE) + 1;
		this.head = head;
		this.tail = tail;
		
		for(int i = 0; i < buffSize; i++)
			values.add(null);
		
		for(int i = 0; i < lst.size(); i++)
			values.set((head + i) % buffSize, lst.get(i));
	}
	
	public CyclicQueue(int cap)
	{
		this.buffSize = (cap > 0 ? cap : QUEUE_DEFAULT_SIZE) + 1;
		this.head = 0;
		this.tail = 0;
		for(int i = 0; i < buffSize; i++)
			values.add(null);
	}
	
	public CyclicQueue()
	{
		this.buffSize = QUEUE_DEFAULT_SIZE + 1;
		this.head = 0;
		this.tail = 0;
		for(int i = 0; i < buffSize; i++)
			values.add(null);
	}
	
	public boolean isFull()
	{
		return head == ( (tail + 1) % buffSize);
	}
	
	public boolean isEmpty()
	{
		return head == tail;
	}
	
	public int getHead()
	{
		return head;
	}
	
	public int getTail()
	{
		return tail;
	}
	
	public int getSize()
	{
		return head > tail ? (tail + buffSize - head) : (tail - head);
	}
	
	public boolean push(T val)
	{
		if(isFull())
			return false;

		values.set(tail, val);
		tail = (tail + 1) % buffSize;
		return true;
	}
	
	public boolean forcePush(T val)
	{
		if(isFull())
			pop();
		
		return push(val);
	}
	
	public T pop()
	{
		if(isEmpty())
			return null;
		
		T val = values.set(head, null);
		head = (head + 1) % buffSize;
		return val;
	}
	
	public List<T> subList(int fromIndex, int toIndex)
	{
		List<T> lst = new ArrayList<>();
		fromIndex = fromIndex % buffSize;
		toIndex = toIndex % buffSize;
		if(fromIndex > toIndex)
		{
			lst.addAll(values.subList(fromIndex, buffSize));
			lst.addAll(values.subList(0, toIndex));
		}
		else
		{
			lst.addAll(values.subList(fromIndex, toIndex));
		}
		return lst;
	}
	
	public List<T> getReversePageItems(int page, int len)
	{
		if(page <= 0 || len <= 0)
			return GameData.emptyList();
		
		len = len > MAX_PAGE_LENGTH ? MAX_PAGE_LENGTH : len;
		if(isEmpty())
			return GameData.emptyList();
		
		List<T> all = getAll();
		int fromIndex = (page - 1) * len;
		if(fromIndex > all.size())
			return GameData.emptyList();
		
		int toIndex = fromIndex + len;
		if(toIndex > all.size())
			toIndex = all.size();
		
		fromIndex = all.size() - fromIndex;
		toIndex = all.size() - toIndex;
		
		List<T> lst = new ArrayList<>();
		for (int i = fromIndex; i > toIndex; i--)
		{
			T e = all.get(i - 1);
			if(e == null)
			{
				System.err.println("index " + i + " null, all.size() " + all.size() + " fromIndex " + fromIndex + " toIndex " + toIndex);
				continue;
			}
			lst.add(e);
		}
		
		return lst;
	}
	
	public List<T> getPageItems(int page, int len)
	{
		if(page <= 0 || len <= 0)
			return GameData.emptyList();
		
		len = len > MAX_PAGE_LENGTH ? MAX_PAGE_LENGTH : len;
		if(isEmpty())
			return GameData.emptyList();
		
		int maxIndex = head + getSize();
		int fromIndex = head + (page - 1) * len;
		if(fromIndex > maxIndex)
			return GameData.emptyList();
		
		int toIndex = fromIndex + len;
		if(toIndex > maxIndex)
			toIndex = maxIndex;
			
		return subList(fromIndex, toIndex);
	}
	
	public List<T> getAll()
	{
		return subList(head, tail);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("head: ").append(head).append(" tail: ").append(tail).append("-->");
		for(T val: values)
		{
			sb.append("|");
			sb.append(val == null ? "?" : val);
			sb.append("|");
		}
		
		return sb.toString();
	}
	
	public static void main(String args[])
	{
		CyclicQueue<Integer> cq = new CyclicQueue<>(20);
		for(int i = 1; i <= 115; i++)
			cq.forcePush(i);
		
		System.out.println(cq);
		final int len = 15;
		System.out.println(cq.getPageItems(2, len));
		System.out.println(cq.getAll());
		System.out.println(cq);
	}
}
