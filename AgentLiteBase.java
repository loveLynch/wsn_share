package main;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import com.huawei.iota.base.BaseService;
import com.huawei.iota.bind.BindService;
import com.huawei.iota.iodev.IotaDeviceInfo;
import com.huawei.iota.login.LoginService;
import test.PublishTest;

public class AgentLiteBase {

    public static IotaDeviceInfo deviceInfo = null;
    public static int count = 0;
    public static String COM = "COM4";
    public static String PLATFORM_IP = "119.3.47.54";
    public static String MQTTS_PORT = "8883";
    public static String HTTPS_PORT = "8943";
    public static String nodeId = "28-6E-D4-88-FC-59";
    public static String verifyCode = "28-6E-D4-88-FC-59";
    public static String manufactrueId = "1ee38c1f7ece416e8761492942c03bee";
    public static String deviceType = "GateWay";
    public static String model = "AgentLite01";
    public static String protocolType = "MQTT";
    public static String receiverId = "1";
    public static boolean loginSuccess = false;

    public static void main(String[] args) {
        System.out.println(" =============   demo  begin ============== ");

        boolean res = false;
        //初始化AgentLite资源
        res = BaseService.init("./workdir", null);
        if (res == false) {
            System.out.println(" ============ init failed ============ ");
        }

        //加载配置文件
        Properties prop = new Properties();
        String path = "./workdir/conf/conf.properties";
        try {
            FileInputStream file = new FileInputStream(path);
            try {
                prop.load(file);
                COM = prop.getProperty("COM");
                PLATFORM_IP = prop.getProperty("PLATFORM_IP");
                MQTTS_PORT = prop.getProperty("MQTTS_PORT");
                HTTPS_PORT = prop.getProperty("HTTPS_PORT");
                nodeId = prop.getProperty("nodeId");
                verifyCode = prop.getProperty("verifyCode");
                manufactrueId = prop.getProperty("manufactrueId");
                deviceType = prop.getProperty("deviceType");
                model = prop.getProperty("model");
                protocolType = prop.getProperty("protocolType");
                receiverId = prop.getProperty("receiverId");
                System.out.println(" ============ 加载配置文件成功  ============ ");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(" ============ 读取配置文件异常！ ============ ");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(" ============ 获取文件异常！ ============ ");
        }

        File file = new File("./workdir/gwbindinfo.json");
        AgentLiteLogin agentLiteLogin = AgentLiteLogin.getInstance();
        LoginService loginService = LoginService.getInstance();
        loginService.registerObserver(agentLiteLogin);

        if (!file.exists()) {
            //网关绑定
            AgentLiteBind agentLiteBind = AgentLiteBind.getInstance();
            BindService bindService = BindService.getInstance();
            bindService.registerObserver(agentLiteBind);
            agentLiteBind.bindAction();
        } else {
            //网关登陆
            agentLiteLogin.loginAction();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (loginSuccess)
            PublishTest.start();
//		//初始化串口
//		ContinueRead cRead = new ContinueRead();
//		int ret = cRead.startComPort();
//		byte[] writeCommand;
//		writeCommand = new byte[]{102, 102, 4, -109};//{0x66, 0x66, 0x04, 0x93}开始盘点指令
//		if(ret == 1) {
//			System.out.println(" ============ 串口打开成功============");
//			try {
//			    cRead.outputStream.write(writeCommand);
//			}catch (IOException e) {
//				e.printStackTrace();
//			}
//		}else {
//			System.out.println(" ============ 串口打开失败 ============");
//		}
//
    }
}
