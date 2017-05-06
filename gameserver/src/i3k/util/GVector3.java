
package i3k.util;

import i3k.SBean;

import org.apache.log4j.Logger;



public final class GVector3
{
	private static final float SMALL_NUMBER = 1.e-8f;
	private static final float KINDA_SMALL_NUMBER = 1.e-4f; 
	public static final GVector3 ZERO = new GVector3();
	public static final GVector3 UNIT_X = new GVector3(1.0f, 0.0f, 0.0f);
	public static final GVector3 UNIT_Y = new GVector3(0.0f, 1.0f, 0.0f);
	public static final GVector3 UNIT_Z = new GVector3(0.0f, 0.0f, 1.0f);
	
	public float x;
	public float y;
	public float z;
	
	public GVector3()
	{
		
	}
	
	public GVector3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static GVector3 randomRotation()
	{
		float angel = GameRandom.getRandFloat(0, (float)(2 * Math.PI));
		return new GVector3((float)Math.cos(angel), 0, (float)Math.sin(angel));
	}
	
	public GVector3(SBean.Vector3 vector3)
	{
		fromVector3(vector3);
	}
	
	public GVector3(SBean.Vector3F vector3f)
	{
		fromVector3(vector3f);
	}

	public SBean.Vector3 toVector3()
	{
		return new SBean.Vector3((int)x, (int)y, (int)z);
	}
	
	public SBean.Vector3F toVector3F()
	{
		return new SBean.Vector3F(x, y, z);
	}
	
	public GVector3 fromVector3(SBean.Vector3 vector3)
	{
		this.x = vector3.x;
		this.y = vector3.y;
		this.z = vector3.z;
		return this;
	}
	
	public GVector3 fromVector3(SBean.Vector3F vector3f)
	{
		
		this.x = vector3f.x;
		this.y = vector3f.y;
		this.z = vector3f.z;
		return this;
	}
	
	public GVector3 reset(GVector3 v)
	{
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		return this;
	}
	
	public boolean equals(GVector3 v)
	{
//		return v != null && this.x == v.x && this.y == v.y && this.z == v.z;
		return v != null && Math.abs(this.x - v.x) < 1.f/1000.f && Math.abs(this.y - v.y) < 1.f/1000.f && Math.abs(this.z - v.z) < 1.f/1000.f;
	}
	
	public boolean nearlyZero()
	{
		return Math.abs(x) < KINDA_SMALL_NUMBER && Math.abs(y) < KINDA_SMALL_NUMBER && Math.abs(z) < KINDA_SMALL_NUMBER;
	}
	
	public boolean nearly2DZero()
	{
		return Math.abs(x) < KINDA_SMALL_NUMBER && Math.abs(z) < KINDA_SMALL_NUMBER;
	}
	
	public float size()
	{
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	public float size2D()
	{
		return (float)Math.sqrt(x*x + z*z);
	}
	
	public GVector3 selfSacle(float scalar)
	{
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
		return this;
	}
	
	public GVector3 selfSum(GVector3 v)
	{
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}
	
	public GVector3 selfDiffence(GVector3 v)
	{
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}
	
	public GVector3 selfNormalize()
	{
		float squareSum = x*x + y*y + z*z;
		if (squareSum >= SMALL_NUMBER)
		{
			float scalar = (float)(1/Math.sqrt(squareSum));
			selfSacle(scalar);
		}
		return this;
	}
	
	public GVector3 scale(float scalar)
	{
		return new GVector3(x*scalar, y*scalar, z*scalar);
	}
	
	public GVector3 sum(GVector3 v)
	{
		return new GVector3(this.x + v.x, this.y + v.y, this.z + v.z);
	}
	
	public GVector3 diffence(GVector3 v)
	{
		return new GVector3(this.x - v.x, this.y - v.y, this.z - v.z);
	}
	
	public GVector3 diffence2D(GVector3 v)
	{
		return new GVector3(this.x - v.x, 0, this.z - v.z);
	}
	
	public GVector3 crossProduct(GVector3 v)
	{
		return new GVector3(this.y*v.z-this.z*v.y, this.z*v.x-this.x*v.z, this.x*v.y-this.y*v.x);
	}
	
	public float dotProduct(GVector3 v)
	{
		return this.x*v.x + this.y*v.y + this.z*v.z;
	}
	
	public GVector3 normalize()
	{
		float squareSum = x*x + y*y + z*z;
		if (squareSum >= SMALL_NUMBER)
		{
			float scale = (float)(1/Math.sqrt(squareSum));
			return scale(scale);
		}
		return new GVector3(x, y, z);
	}
	
	public float angle(GVector3 v)
	{
		float s = this.size()*v.size();
		return s >= SMALL_NUMBER ? (float)Math.acos(this.dotProduct(v)/s) : 0;
	}
	
	public float projectXZAngleX()
	{
		return x == 0.0f ? (z == 0.0f ? 0 : z > 0.0f ? (float)(Math.PI/2) : (float)(-Math.PI/2)) 
				         : (z == 0.0f ? (x > 0.0f ? 0 : (float)(Math.PI)) : x > 0.0f ? (float)Math.atan(z/x) : (float)(Math.PI + Math.atan(z/x)));
	}
	
	public float distance(GVector3 v)
	{
		return this.diffence2D(v).size2D();
	}
	
	public String toString()
	{
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	public static void main(String[] args)
	{
		//org.apache.log4j.helpers.LogLog.setInternalDebugging(true);
		org.apache.log4j.ConsoleAppender ca = new org.apache.log4j.ConsoleAppender();
		ca.setName("AA");
		ca.setWriter(new java.io.PrintWriter(System.out));
		ca.setLayout(new org.apache.log4j.PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %m%n"));
		Logger.getRootLogger().addAppender(ca);
		try
		{
			Logger.getRootLogger().info("");
		}
		catch (Exception e)
		{
			Logger.getRootLogger().warn("exception :", e);
		}
		
	}
}
