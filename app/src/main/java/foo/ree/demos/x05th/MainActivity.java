package foo.ree.demos.x05th;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.NetworkOnMainThreadException;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import foo.ree.demos.x05th.util.Mnt;
import foo.ree.demos.x05th.util.NetWorkUtil;
import foo.ree.demos.x05th.util.SharedPref;


public class MainActivity extends AppCompatActivity {
    private DeviceInfoListView mDeivceInfoView;
    private static final String XPOSED_HELPERS = "de.robv.android.xposed.XposedHelpers";
    private static final String XPOSED_BRIDGE = "de.robv.android.xposed.XposedBridge";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDeivceInfoView = (DeviceInfoListView) findViewById(R.id.rv_recycle);

        Save();
        Cpu();
        final ArrayList<String> deviceInfo = getBuildInfo();
        NetWorkUtil.getRealIpAdress(new NetWorkUtil.GetRealIpCallback() {
            @Override
            public void onSucess(String ip) {
                deviceInfo.add("realIP:"+ip);
                mDeivceInfoView.setMoreTags(deviceInfo);
            }
        });

//        Log.d("chao","onCreate:"+SharedPref.getXValue("serial"));
//         Log.d("chao","onCreate:"+SharedPref.getXValue("getBaseband"));
        Log.d("chao", "onCreate:" + Process.myPid());
//        isXposedExistByThrow();
//
//
//        try {
//            ClassLoader.getSystemClassLoader().loadClass("de.robv.android.xposed.XposedBridge");
//        } catch (ClassNotFoundException e) {
//            Log.d("chao", "getSystemClassLoader:" + e);
//            e.printStackTrace();
//        }
//
//        try {
//            Class.forName("de.robv.android.xposed.XposedBridge");
//        } catch (ClassNotFoundException e) {
//            Log.d("chao", "froname:" + e);
//            e.printStackTrace();
//        }
//        tryShutdownXposed();
    }

    public boolean tryShutdownXposed() {
        Field xpdisabledHooks = null;
        try {
            xpdisabledHooks = ClassLoader.getSystemClassLoader()
                    .loadClass(XPOSED_BRIDGE)
                    .getDeclaredField("disableHooks");
            xpdisabledHooks.setAccessible(true);
            xpdisabledHooks.set(null, Boolean.TRUE);
            Log.d("chao","tryShutdownXposed0:");
            return true;
        } catch (NoSuchFieldException e) {
            Log.d("chao","tryShutdownXposed1:"+e);
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            Log.d("chao","tryShutdownXposed2:"+e);
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            Log.d("chao","tryShutdownXposed3:"+e);
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 通过主动抛出异常，检查堆栈信息来判断是否存在XP框架
     *
     * @return
     */
    public boolean isXposedExistByThrow() {
        try {
            throw new Exception("gg");
        } catch (Exception e) {
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                Log.d("chao","isXposedExistByThrow:"+stackTraceElement.getClassName());
                if (stackTraceElement.getClassName().contains(XPOSED_BRIDGE)) return true;
            }
            return false;
        }
    }

    @NonNull
    private ArrayList<String> getBuildInfo() {
        ArrayList<String> deviceInfo = new ArrayList<>();
        deviceInfo.add("-----------Build基本信息-------------"); //
        deviceInfo.add("版本名display:"+Build.DISPLAY); //设备显示的版本包 固件版本
        deviceInfo.add("指令集cpu_abi:"+Build.CPU_ABI); //CPU指令集
        deviceInfo.add("指令集cpu_abi2:"+Build.CPU_ABI2); //CPU指令集
        deviceInfo.add("手机制造商manufacturer:"+Build.MANUFACTURER); //手机制造商，例如：HUAWEI
        deviceInfo.add("品牌brand:"+Build.BRAND); //手机品牌，例如：HONOR
        deviceInfo.add("CPU型号hardware:"+Build.HARDWARE); //CPU型号
        deviceInfo.add("手机型号product:"+Build.PRODUCT); //手机型号，设置-关于手机-型号
        deviceInfo.add("指纹信息fingerprint:"+Build.FINGERPRINT); //build的指纹信息
        deviceInfo.add("基带radioversion1:"+Build.getRadioVersion()); //基带版本
        deviceInfo.add("基带radioversion2:"+Build.RADIO); //基带版本
        deviceInfo.add("主板board:"+Build.BOARD); //主版
        deviceInfo.add("设备驱动名称device:"+Build.DEVICE); //设备驱动名称
        deviceInfo.add("设备版本号id:"+Build.ID); //设备版本号
        deviceInfo.add("手机型号mddel:"+Build.MODEL); //手机型号
        deviceInfo.add("设备引导程序booltloader:"+Build.BOOTLOADER); //主板引导程序
        deviceInfo.add("设备主机地址host:"+Build.HOST); //设备主机地址
        deviceInfo.add("设备版本标签build_tags:"+Build.TAGS); //描述标签
        deviceInfo.add("设备版本类型serial:"+Build.TYPE); //设备版本类型
        deviceInfo.add("源码控制版本号incremental:"+Build.VERSION.INCREMENTAL); //源码控制版本号
        deviceInfo.add("Andorid系统版本:"+Build.VERSION.RELEASE); //
        deviceInfo.add("Android系统api版本:"+Build.VERSION.SDK_INT);
        deviceInfo.add("固定build时间:"+Build.TIME);
        deviceInfo.add("AndroidID:"+Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID)); //设备版本类型



        deviceInfo.add("---------android.os.SystemProperties---------------"); //
        try {
            Class<?> classSysProp = Class
                    .forName("android.os.SystemProperties");
            Method method2 = classSysProp.getDeclaredMethod("get",String.class,String.class);
            Method method1 = classSysProp.getDeclaredMethod("get",String.class);
            Object obj = classSysProp.getConstructor().newInstance();
            Object one = method1.invoke(obj,"gsm.version.baseband"); //基带版本
            Object two = method1.invoke(obj,"gsm.version.baseband"); //基带版本
            Object description = method1.invoke(obj,"ro.build.description"); //基带版本

            deviceInfo.add("基带radioversion3:"+one); //gsm.version.baseband
            deviceInfo.add("基带radioversion4:"+two); //gsm.version.baseband
            deviceInfo.add("描述信息:"+description);
        } catch (Exception e) {
            e.printStackTrace();
        }
        deviceInfo.add("---------TelephoneManager相关---------------"); //
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, "android.permission.READ_PHONE_NUMBERS") != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS,"android.permission.READ_PHONE_NUMBERS",Manifest.permission.READ_PHONE_STATE},1);
        }else {
            deviceInfo.add("IMEI:"+telephonyManager.getDeviceId());
            deviceInfo.add("蓝牙地址:"+BluetoothAdapter.getDefaultAdapter().getAddress());
            try {
                Class<?> bluetooth = Class.forName("android.bluetooth.BluetoothDevice");
                Field field = bluetooth.getDeclaredField("mAddress");
                Object obj = bluetooth.getConstructor().newInstance();
                field.setAccessible(true);
                deviceInfo.add("蓝牙地址:"+field.get(obj));
            } catch (Exception e) {
                e.printStackTrace();
            }
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();

            deviceInfo.add("WiFiMac地址:"+info.getMacAddress());
            deviceInfo.add("WiFi名称:"+info.getSSID());
            deviceInfo.add("接入点的识别地址:"+info.getBSSID());
            deviceInfo.add("电话号码:"+telephonyManager.getLine1Number());
            deviceInfo.add("手机卡序列号:"+telephonyManager.getSimSerialNumber());
            deviceInfo.add("网络运营商类型:"+telephonyManager.getNetworkOperator());
            deviceInfo.add("网络类型名称:"+telephonyManager.getNetworkOperatorName());
            deviceInfo.add("sim卡运营商类型:"+telephonyManager.getSimOperator());
            deviceInfo.add("sim卡运营商名称:"+telephonyManager.getSimOperatorName());
            deviceInfo.add("网络ISO代码:"+telephonyManager.getNetworkCountryIso());
            deviceInfo.add("sim卡ISO代码:"+telephonyManager.getSimCountryIso());

            deviceInfo.add("系统版本:"+telephonyManager.getDeviceSoftwareVersion());
            deviceInfo.add("网络链接类型:"+telephonyManager.getNetworkType());
            deviceInfo.add("手机类型:"+telephonyManager.getPhoneType());
            deviceInfo.add("sim卡状态:"+telephonyManager.getSimState());
            Display display = getWindowManager().getDefaultDisplay();
            deviceInfo.add("手机宽高:" + display.getWidth()+"："+display.getHeight());
            deviceInfo.add("手机内网ip地址:"+NetWorkUtil.getLocalIpAddress(this));
            deviceInfo.add("---------显示相关---------------"); //
            Resources resources=getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            deviceInfo.add("屏幕densityDpi:"+displayMetrics.densityDpi);
            deviceInfo.add("屏幕density:"+displayMetrics.density);
            deviceInfo.add("屏幕xdpi:"+displayMetrics.xdpi);
            deviceInfo.add("屏幕ydpi:"+displayMetrics.ydpi);
            deviceInfo.add("屏幕scalDensity:"+displayMetrics.scaledDensity);


        }













        return deviceInfo;
    }

    private  void Save(){

        SharedPref mySP = new SharedPref(getApplicationContext());
    /*
      build 系列
     */


        mySP.setSharedPref("serial","aee5060e"); // 串口序列号
        mySP.setSharedPref("getBaseband","SCL23KDU1BNG3"); // get 参数
        mySP.setSharedPref("BaseBand", "REL" ); // 固件版本
        mySP.setSharedPref("board", "msm8916" ); //主板
        mySP.setSharedPref("brand", "Huawei-Z" ); //设备品牌
        mySP.setSharedPref("ABI", "armeabi-v7a" ); //  设备指令集名称 1
        mySP.setSharedPref("ABI2", "armeabi" ); //   设备指令集名称 2
        mySP.setSharedPref("device", "hwG750-T01" ); //设备驱动名称
        mySP.setSharedPref("display", "R7c_11_151207" ); //设备显示的版本包 固件版本
        //  指纹 设备的唯一标识。由设备的多个信息拼接合成。
        mySP.setSharedPref("fingerprint", "Huawei/G750-T01/hwG750-T01:4.2.2/HuaweiG750-T01/C00B152:user/ota-rel-keys,release-keys" );
        mySP.setSharedPref("NAME", "mt6592" ); //设备硬件名称
        mySP.setSharedPref("ID", "KTU84P" ); //设备版本号
        mySP.setSharedPref("Manufacture", "HUAWEI" ); //设备制造商
        mySP.setSharedPref("model", "HUAWEI G750-T01" ); //手机的型号 设备名称
        mySP.setSharedPref("product", "hwG750-T01" ); //设备驱动名称
        mySP.setSharedPref("booltloader", "unknown" ); //设备引导程序版本号
        mySP.setSharedPref("host", "ubuntu-121-114" ); //设备主机地址
        mySP.setSharedPref("build_tags", "release-keys" ); //设备标签
        mySP.setSharedPref("shenbei_type", "user" ); //设备版本类型
        mySP.setSharedPref("incrementalincremental", "eng.root.20151207" ); //源码控制版本号
        mySP.setSharedPref("AndroidVer", "5.1" ); //系统版本
        mySP.setSharedPref("API", "19" ); //系统的API级别 SDK

        mySP.setintSharedPref("time",123456789);// 固件时间
        mySP.setSharedPref("AndroidID", "fc4ad25f66d554a8" ); //  android id
        mySP.setSharedPref("DESCRIPTION", "jfltexx-user 4.3 JSS15J I9505XXUEML1 release-keys" ); //用户的KEY





 /*
     TelephonyManager相关
     */
        mySP.setSharedPref("IMEI","506066104722640"); // 序列号IMEI
        mySP.setSharedPref("LYMAC","BC:1A:EA:D9:8D:98");//蓝牙 MAC
        mySP.setSharedPref("WifiMAC","a8:a6:68:a3:d9:ef"); // WIF mac地址
        mySP.setSharedPref("WifiName","免费WIFI"); // 无线路由器名
        mySP.setSharedPref("BSSID", "ce:ea:8c:1a:5c:b2"); // 无线路由器地址
        mySP.setSharedPref("IMSI,subscriberid","460017932859596");
        mySP.setSharedPref("PhoneNumber","13117511178"); // 手机号码
        mySP.setSharedPref("SimSerial", "89860179328595969501"); // 手机卡序列号
        mySP.setSharedPref("networktor","46001" ); // 网络运营商类型
        mySP.setSharedPref("Carrier","中国联通" );// 网络类型名
        mySP.setSharedPref("CarrierCode","46001" ); // 运营商
        mySP.setSharedPref("simopename","中国联通" );// 运营商名字
        mySP.setSharedPref("gjISO", "cn");// 国家iso代码
        mySP.setSharedPref("CountryCode","cn" );// 手机卡国家
        mySP.setSharedPref("deviceversion", "100"); // 返回系统版本

        mySP.setintSharedPref("getType",1); // 联网方式 1为WIFI 2为流量
        mySP.setintSharedPref("networkType", 6);//      具体网络类型
        mySP.setintSharedPref("phonetype",5 ); // 手机类型
        mySP.setintSharedPref("SimState", 10); // 手机卡状态
        mySP.setintSharedPref("width", 720); // 宽
        mySP.setintSharedPref("height", 1280); // 高
        mySP.setintSharedPref("getIP", -123456789); // 内网ip(wifl可用)
    /*
     屏幕相关
     */

        mySP.setintSharedPref("DPI",320); // dpi
        mySP.setfloatharedPref("density", (float) 2.0); // density
        mySP.setfloatharedPref("xdpi", (float) 200.123);
        mySP.setfloatharedPref("ydpi", (float) 211.123);
        mySP.setfloatharedPref("scaledDensity", (float) 2.0); // 字体缩放比例



 /*
    显卡信息
     */

        mySP.setSharedPref("GLRenderer", "Adreno (TM) 111"); // GPU
        mySP.setSharedPref("GLVendor", "UFU");// GPU厂商


            /*
            位置信息
        30.2425140000,120.1404220000 杭州
     */

        mySP.setfloatharedPref("lat", (float) 30.2425140000); // 纬度
        mySP.setfloatharedPref("log", (float) 120.1404220000); // 经度


        Toast.makeText(this,"保存成功",Toast.LENGTH_LONG).show();



    }

    /*
  创建 cpuinfo 文件 等待HOOK 重定向
 */

    private  void Cpu() {

        String filePath = "/sdcard/Test/";
        String fileName = "cpuinfo";

        String hardware="GT1000";

        //生成文件夹之后，再生成文件，不然会出错
        Mnt.makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = "Processor	: ARMv7 Processor rev 0 (v7l)" + "\r\n";
        String strContent2 = "processor	: 0" + "\r\n";
        String strContent3 = "BogoMIPS	: 38.40";
        String strContent4 = "" + "\r\n";
        String strContent5 = "" + "\r\n";
        String strContent6 = "processor	: 1"+ "\r\n";
        String strContent7 = "BogoMIPS	: 38.40"+ "\r\n";
        String strContent8 = ""+ "\r\n";
        String strContent9 = "Features	: swp half thumb fastmult vfp edsp neon vfpv3 tls vfpv4 idiva idivt"+ "\r\n";
        String strContent10 = "CPU implementer	: 0x51"+ "\r\n";
        String strContent11 = "CPU architecture: 7"+ "\r\n";
        String strContent12 = "CPU variant	: 0x2"+ "\r\n";
        String strContent13 = "CPU part	: 0x06f"+ "\r\n";
        String strContent14 = "CPU revision	: 0"+ "\r\n";
        String strContent15 = ""+ "\r\n";
        String strContent16 = "Hardware	: "+hardware+ "\r\n";
        String strContent17 = "Revision	: 000d"+ "\r\n";
        String strContent18 = "Serial		: 0000088900004e4b"+ "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }


            //要先将已有文件删除、避免干扰。
            if(file.exists()){
                file.delete();
            }

            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.write(strContent2.getBytes());
            raf.write(strContent3.getBytes());
            raf.write(strContent4.getBytes());
            raf.write(strContent5.getBytes());
            raf.write(strContent6.getBytes());
            raf.write(strContent7.getBytes());
            raf.write(strContent8.getBytes());
            raf.write(strContent9.getBytes());
            raf.write(strContent10.getBytes());
            raf.write(strContent11.getBytes());
            raf.write(strContent12.getBytes());
            raf.write(strContent13.getBytes());
            raf.write(strContent14.getBytes());
            raf.write(strContent15.getBytes());
            raf.write(strContent16.getBytes());
            raf.write(strContent17.getBytes());
            raf.write(strContent18.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }



    }

}
