package foo.ree.demos.x05th;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import foo.ree.demos.x05th.hook.Cpuinfo;
import foo.ree.demos.x05th.hook.GPShook;
import foo.ree.demos.x05th.hook.Hook;
import foo.ree.demos.x05th.hook.OpenGL;
import foo.ree.demos.x05th.hook.Phone;
import foo.ree.demos.x05th.hook.Resolution;
import foo.ree.demos.x05th.hook.XBuild;
import foo.ree.demos.x05th.util.RootCloak;
import foo.ree.demos.x05th.util.SharedPref;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by fooree on 2018/3/4.
 */

public class FooxMain  implements IXposedHookLoadPackage{
    @Override
    public void handleLoadPackage(LoadPackageParam loadPackageParam) throws Throwable {
        //     XposedBridge.log("HOOK  作用于全局" );


        new Hook().HookTest(loadPackageParam); // 动态生效 不用重启
        Log.d("chao","handleLoadPackage2");
        new RootCloak().handleLoadPackage(loadPackageParam);
//        Log.d("chao","handleLoadPackage3");
        new XBuild(loadPackageParam);  //build
//        Log.d("chao","handleLoadPackage4");
        new Phone(loadPackageParam);  // TelephonyManager
//        Log.d("chao","handleLoadPackage5");
        new Resolution().Display(loadPackageParam);  // 屏幕
//        Log.d("chao","handleLoadPackage6");
        new OpenGL().OpenGLTest(loadPackageParam);   // 显卡
//        Log.d("chao","handleLoadPackage7");
        new Cpuinfo(loadPackageParam);         // CPU*/
//        Log.d("chao","handleLoadPackage8");


        /*
            GPS位置 只对百度高德生效 有需要的朋友可以添加
            要更改位置应用的包名  不要作用于全局  某些机型可能不太好使 请自行适配 不难的
         */
        if (loadPackageParam.packageName.equals("com.baidu.BaiduMap")||
                loadPackageParam.packageName.equals("com.autonavi.minimap")) {

            GPShook.HookAndChange(loadPackageParam.classLoader,
                    SharedPref.getfloatXValue("lat"),SharedPref.getfloatXValue("log"));



        }
    }
}
