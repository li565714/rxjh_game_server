package tsssdk.jni;

public class TssSdkAntiRecvDataInfoEx2 {
    /*
    * 用户OpenID
    */
    public String openid;

    /*
    * 用户设备类型, 0: IOS, 1: Android
    */
    public int platid;

    /*
    * 反外挂数据
    */
    public byte []anti_data;

    /*
    * 用户扩展数据
    */
    public byte []user_ext_data;
}
