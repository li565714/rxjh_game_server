
package i3k.auth;

public class ServiceManager
{
	public synchronized static Service createService()
	{
		if( imp == null )
			imp = new ServiceImp();
		return imp;
	}
	
	private static ServiceImp imp = null;
}