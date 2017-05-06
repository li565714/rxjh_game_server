
package tsssdk.test;

import tsssdk.jni.*;

/* 继承TssSdk并实现自己的AntiSendDataToClientEx2函数 */
class MyTssSdk extends TssSdk {
    public int AntiSendDataToClientEx2(TssSdkAntiSendDataInfoEx2 send_pkg_info)
    {
        System.out.println("In MyTssSdk");
        return super.AntiSendDataToClientEx2(send_pkg_info);
    }
}

public class SDKTest {
    public static void main(String[] args) {
        TssSdkInitInfo init_info = new TssSdkInitInfo();

        init_info.unique_instance_id = 1;
        init_info.tss_sdk_conf = "./conf";

        TssSdk tss_sdk = new MyTssSdk();

        int ret = tss_sdk.Init(init_info);

        if(ret != 0) {
            System.out.println("tss_sdk init failed, ret: " + ret);
            return;
        }

        System.out.println("tss_sdk init succ");

        ret = tss_sdk.AntiInterfInitEx2();
        if(ret != 0) {
            System.out.println("anti interf init failed, ret: " + ret);
        }

        System.out.println("anti intef init succ");

        byte []user_ext_data = { 0x59, 0x2d };

        TssSdkAntiAddUserInfoEx2 add_user_info = new TssSdkAntiAddUserInfoEx2();

        add_user_info.openid = new String("testid");
        add_user_info.platid = 1;
        add_user_info.client_ver = 4782;
        add_user_info.client_ip = 0x100007f; /* 127.0.0.1 */
        add_user_info.role_name = new String("testrole");
        add_user_info.user_ext_data = user_ext_data;

        ret = tss_sdk.AntiAddUserEx2(add_user_info);
        if(ret != 0) {
            System.out.println("anti add user failed, ret: " + ret);
        }

        byte []anti_data = { 0x1, 0x2, 0x3, 0x4 };

        TssSdkAntiRecvDataInfoEx2 recv_pkg_info = new TssSdkAntiRecvDataInfoEx2();

        recv_pkg_info.openid = new String("testid");
        recv_pkg_info.platid = 1;
        recv_pkg_info.anti_data = anti_data;
        recv_pkg_info.user_ext_data = user_ext_data;

        ret = tss_sdk.AntiRecvDataFromClientEx2(recv_pkg_info);
        if(ret != 0) {
            System.out.println("anti recv data from client failed, ret: " + ret);
        }

        TssSdkAntiDelUserInfoEx2 del_user_info = new TssSdkAntiDelUserInfoEx2();

        del_user_info.openid = new String("testid");
        del_user_info.platid = 1;
        del_user_info.user_ext_data = user_ext_data;

        ret = tss_sdk.AntiDelUserEx2(del_user_info);
        if(ret != 0) {
            System.out.println("anti del user failed, ret: " + ret);
        }

        tss_sdk.Proc();

        tss_sdk.UnLoad();
    }
}
