package tsssdk.jni;

public class TssSdkAntiAddUserInfoEx2 {
    /*
    * 用户OpenID
    */
    public String openid;

    /*
    * 用户设备类型, 0: IOS, 1: Android
    */
    public int platid;

    /*
    * 客户端版本
    */
    public int client_ver;

    /*
    * 客户端ip
    */
    public long client_ip;

    /*
    * 用户角色名
    */
    public String role_name;

    /*
    * 用户扩展数据
    */
    public byte []user_ext_data;
}
