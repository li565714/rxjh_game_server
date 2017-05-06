
package i3k.auth;

public class ServiceTest
{
	
	public static void main(String[] args)
	{
		Service service = ServiceManager.createService();
		
		try
		{
			service.start("127.0.0.1", 8106);
			for(int i =0; i<2; ++i) Thread.sleep(500);
			int ret = service.setPayResult(1001, "2071", "m52g4g", 111, "com.longtugame.rxjh.d1", 1, "1", "1_f31508f1-cc6d-42cd-9b1c-3151022da092");
			System.out.println("set pay result res is " + ret);
			while( true ) Thread.sleep(500);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		service.destroy();
	}
}
