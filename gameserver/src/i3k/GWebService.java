package i3k;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

	/**
	 * game web service interface
	 */
	@WebService
	@SOAPBinding(style = Style.RPC)
	public interface GWebService 
	{
	
		/**
		 * use cdkey
		 * @param cdkey cdkey code
		 * @param bid batch id
		 * @param gid gen id
		 * @param maxusecnt batch max use count 
		 * @param gsid game server id
		 * @param roleid role id
		 * @param rolename role name
		 * @param channel String
		 * @param uid  user id
		 * @return  0 : success   -1| :failed
		 */
	    @WebMethod
	    int useCDKey(String cdkey, int bid, int gid, int maxusecnt, int gsid, int roleid, String rolename, String channel, String uid);
	    
		/**
		 * verify register
		 * @param key key code
		 * @param gsid game server id
		 * @param username user name 
		 * @return  0 : success   -1| :failed
		 */
	    @WebMethod
	    int verifyRegister(String key, int gsid, String username);
	    
	    
	    /**
		 * query cdkey  longtu use interface
		 * @param bid batch id
		 * @param cdkey cdkey code
		 * @param gsid game server id
		 * @param roleid role id
		 * @param channel String
		 * @param gameapp  gameapp id
		 * @param level role level
		 * @param viplvl role vip level
		 * @return  string : result|reward|title
		 */
	    @WebMethod
	    String queryCDKey(int bid, String cdkey, int gsid, int roleid, String channel, String gameapp, int level, int viplvl);
	    
	    /**
		 * exchange cdkey  longtu use interface
		 * @param bid batch id
		 * @param cdkey cdkey code
		 * @param gsid game server id
		 * @param roleid role id
		 * @param rolename role name
		 * @param channel String
		 * @param uid  user id
		 * @param gameapp  gameapp id
		 * @param level role level
		 * @param viplvl role vip level
		 * @return  0 : success   -1| :failed
		 */
	    @WebMethod
	    int exchangeCDKey(int bid, String cdkey, int gsid, int roleid, String rolename, String channel, String uid, String gameapp, int level, int viplvl, int paypoint);
	    
	    /**
	     * @param bid test batch id
	     * @param gsid gameserver id
	     * @param channel 
	     * @param uid username
	     * @param roleid the id of role who the new role
	     * @param roleName
	     * @return
	     */
	    @WebMethod 
	    int queryCashBack(int bid, int gsid, String channel, String uid, int roleid, String roleName); 
	    /** exchangeCashBack used to take the public beta test reward, if success, only once
	     * @param bid
	     * @param gsid
	     * @param channel
	     * @param uid
	     * @param roleid
	     * @param roleName
	     * @return
	     */
	    @WebMethod
	    int exchangeCashBack(int bid, int gsid, String channel, String uid, int roleid, String roleName);
	}

	

