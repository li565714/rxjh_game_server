
package i3k.auth;

import java.util.List;

public interface Service
{
	@SuppressWarnings("serial")
	public static class ServiceException extends Exception
	{
		public ServiceException(String msg)
		{
			super(msg);
		}
	}
	
	/**
	 * 
	 * @param host 监听地址
	 * @param port
	 */
	public void start(String host, int port);
	
	/**
	 * 退出清理
	 */
	public void destroy();
	
	/**
	 * 设置充值结果
	 * @param gsid 游戏服务器id 
	 * @param roleid 角色id
	 * @param paylvl 充值等级
	 * @param orderid 订单号
	 */
	public int setPayResult(int gsid, String channel, String uid, int roleid, String goodsid, int payLevel, String orderid, String payext) throws ServiceException;
	
	
//	/**
//	 * 补充值结果
//	 * @param gsid 补充值游戏服务器 
//	 * @param roleid 角色id
//	 * @param payLvl 补充值等级
//	 */
//	public int patchPay(int gsid, int roleid, byte payLvl) throws ServiceException;
//	
//	
//	/**
//	 * 查询玩家信息
//	 * @param gsid 补充值游戏服务器 
//	 * @param openID 用户openID
//	 */
//	public List<i3k.SBean.RoleInfo> queryRoleInfo(int gsid, String openID) throws ServiceException;
//	
//
//	/**
//	 * 发放角色道具
//	 * @param gsid 补充值游戏服务器 
//	 * @param openID 用户openID
//	 */
//	public int updateRoleItem(int gsid, int roleid, short type, short id, int count, String order) throws ServiceException;
}
