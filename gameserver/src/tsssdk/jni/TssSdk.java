package tsssdk.jni;

/*

反外挂接口版本

    因为历史原因,反外挂接口有几个版本.这里只提供了V2版本的
    接口(前面还有V0/V1版本),该版本的函数名都带有Ex2后缀.
    旧版本的接口不推荐使用,所以这里没有提供.
    后面提到的函数如果在定义中找不到,可以查找对应后缀的同名函数.

使用方法

    1. 首先继承TssSdk类并实现自己的数据发送函数(AntiSendDataToClient):
        class MyTssSdk extends TssSdk {
            public int AntiSendDataToClientEx2(TssSdkAntiSendDataInfoEx2 send_pkg_info) {
                //your code
                return 0;
            }
        }

    2. 初始化sdk
        MyTssSdk tss_sdk = new MyTssSdk();
        TssSdkInitInfo init_info = new TssSdkInitInfo();
        //填充init_info
        //.........
        tss_sdk.Init(init_info);

    3. 初始化反外挂接口
        tss_sdk.AntiInterfInitEx2();

    4. 正常游戏逻辑
        4.1 用户进入svr时
            TssSdkAntiAddUserInfoEx2 add_user_info = new TssSdkAntiAddUserInfoEx2();
            //填充add_user_info
            //........
            tss_sdk.AntiAddUserEx2(add_user_info);

        4.2 用户离开svr时
            TssSdkAntiDelUserInfoEx2 del_user_info = new TssSdkAntiDelUserInfoEx2();
            //填充del_user_info
            //..............
            tss_sdk.AntiDelUserEx2(del_user_info);

        4.3 收到客户端上报的安全数据时
            TssSdkAntiRecvDataInfoEx2 recv_pkg_info = new TssSdkAntiRecvDataInfoEx2();
            //填充recv_pkg_info
            //客户端上报的安全数据放在recv_pkg_info.anti_data
            //..............
            tss_sdk.AntiRecvDataFromClientEx2(recv_pkg_info);

        4.4 定时调用Proc,这个函数会驱动sdk内部的处理逻辑,要求至少每秒调用100次
            tss_sdk.Proc();

        4.5 向客户端发送安全数据(AntiSendDataToClientEx2)
            这个函数是由sdk主动调用的,gamesvr要重新实现该函数,但不要调用它.
            发给客户端的安全数据放在参数的anti_data字段里.

    5. 卸载sdk
        tss_sdk.UnLoad();

    更具体的例子可以参考TssSdkDemo.java

返回值约定

    除了返回值为void的函数,其它函数的返回值皆为int,
    具体可以分为以下三类:

    1. 等于 0
        代表函数执行成功

    2. 小于 0
        代表java和c的适配层发生了错误,
        请确保没有对本sdk做过修改,并且传递的参数都是合法的,
        如果仍然无法解决,请联系开发人员处理

    3. 大于 0
        代表sdk本身处理时发生了错误
        请检查日志(linux: /tmp/mbgame_tss*.log),一般会有线索.
        不同函数返回的错误需要区别处理,具体请参考 "错误处理" 部分

错误处理

    不同函数返回返回的错误需要区别对待,具体如下:

    1. 不可忽略
        Init/AntiInterfInit返回的错误不可忽略.
        这两个函数失败一般是由配置或者环境问题导致的,
        请查看日志获取错误的具体原因.

    2. 忽略
        AntiAddUser/AntiDelUser/AntiRecvDataFromClient,
        这几个函数在测试环境中容易发生错误,请检查日志,
        排除参数错误外,其它都是正常情况.如果不确定,可以
        联系开发人员确认.
     注意: 这几个函数的返回值始终不应该影响游戏的执行逻辑,
        即执行流程始终应该忽略这些函数的返回值.

*/

public class TssSdk {
    public TssSdk() {
    }

    /*
    * 加载安全sdk
    */
    static {
        System.loadLibrary("tss_sdk");
    }

    /* 安全SDK公共接口开始 */

    /*
    * 初始化sdk,返回0表示成功,其它失败
    */
    public native int Init(TssSdkInitInfo init_info);

    /*
    * sdk内部事件的处理函数,要求至少每秒钟调用100次
    */
    public native void Proc();

    /*
    * 卸载sdk,在svr退出的时候尽量调用一次这个函数
    */
    public native void UnLoad();

    /* 安全SDK公共接口结束 */

    /* 反外挂接口开始 */

    /* 这里不提供V0和V1版本的接口 */

    /* V2版本反外挂接口开始 */

    /*
    * 反外挂接口初始化,必须在调用其它反外挂接口函数前调用
    */
    public native int AntiInterfInitEx2();

    /*
    * 用户登入
    */
    public native int AntiAddUserEx2(TssSdkAntiAddUserInfoEx2 add_user_info);

    /*
    * 用户登出
    */
    public native int AntiDelUserEx2(TssSdkAntiDelUserInfoEx2 del_user_info);

    /*
    * 收到客户端上报数据
    */
    public native int AntiRecvDataFromClientEx2(TssSdkAntiRecvDataInfoEx2 recv_pkg_info);

    /*
    * 向客户端发送数据
    */
    public int AntiSendDataToClientEx2(TssSdkAntiSendDataInfoEx2 send_pkg_info)
    {
        //这个函数需要业务自己继承并重新实现
        //下面的代码仅供调试
        System.out.println("AntiSendDataToClientEx2");
        System.out.println("openid " + send_pkg_info.openid);
        System.out.println("platid " + send_pkg_info.platid);
        System.out.println("anti_data_len " + send_pkg_info.anti_data.length);
        for(int i = 0; i < send_pkg_info.anti_data.length; i++)
        {
            System.out.println("anti_data[" + i + "] " + send_pkg_info.anti_data[i]);
        }
        System.out.println("user_ext_data_len " + send_pkg_info.user_ext_data.length);
        for(int i = 0; i < send_pkg_info.user_ext_data.length; i++)
        {
            System.out.println("user_ext_data[" + i + "] " + send_pkg_info.user_ext_data[i]);
        }
        System.out.println("-------------------------");
        return 0;
    }

    /* V2版本反外挂接口开始 */

    /* 反外挂接口结束 */
}
