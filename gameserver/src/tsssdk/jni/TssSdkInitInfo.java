package tsssdk.jni;

public class TssSdkInitInfo {
    /*
    * 非重实例id,要求在一台机器上非重且固定
    */
    public int unique_instance_id;

    /*
    * 配置文件路径
    */
    public String tss_sdk_conf;
}
