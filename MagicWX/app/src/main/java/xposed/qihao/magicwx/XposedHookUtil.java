package xposed.qihao.magicwx;

import android.content.Context;
import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedHookUtil implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {

    private Context context;
    private String pkgName;
    private String processName;
    private String versionName;
    private static String MODULE_PATH = null;

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        // 包名，进程名
        pkgName = loadPackageParam.packageName;
        processName = loadPackageParam.processName;
        // 获取当前上下文
        context = (Context) XposedHelpers.callMethod(
                XposedHelpers.callStaticMethod(
                        XposedHelpers.findClass("android.app.ActivityThread", null),
                        "currentActivityThread",
                        new Object[0]),
                "getSystemContext",
                new Object[0]);
        // 获取版本号
        versionName = context.getPackageManager().getPackageInfo(pkgName, 0).versionName;


        if (loadPackageParam.packageName.equals("xposed.qihao.magicwx")) {
            Class clazz = loadPackageParam.classLoader.loadClass("xposed.qihao.magicwx.MainActivity");

            XposedHelpers.findAndHookMethod(clazz, "toastMessage", new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    param.setResult("你已被劫持");
                }
            });
            // hook具体方法
            XposedHelpers.findAndHookMethod(clazz, "toastMessage", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    return "哈哈哈哈，返回值被替换咯……";
                }
            });
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {

        resparam.res.hookLayout(resparam.packageName, "layout", "activity_main", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                XposedBridge.log("修改资源");
                LogUtil.d("resparam.packageName=" + resparam.packageName);
//                Button button_hook =
//                        (Button) liparam.view.findViewById(liparam.res.getIdentifier("Button", "id", "com.android.systemui"));
//                button_hook.setTextColor(Color.RED);

                //获取图片资源的ID
                int drawableId = liparam.res.getIdentifier("ic_launcher", "drawable", "com.android.systemui");
//                mImageView.setImageResource(drawableId);
                //获取字符串资源
                int stringId = liparam.res.getIdentifier("hello", "string", "com.android.systemui");

                LogUtil.d(drawableId + "==" + stringId);

            }
        });

        // replacements only for SystemUI
        if (!resparam.packageName.equals("com.android.systemui"))
            return;

        // 这就是“简单”的替换，通过这种方式你可以直接替换值。这种方式可以用于：Boolean, Color, Integer, int[], String and String[]。
        // WLAN切换文本。您不应该这样做，因为id不是固定的。仅对于框架资源，您可以使用android.R.string.something
        resparam.res.setReplacement(0x7f080083, "YEAH!");
        resparam.res.setReplacement("com.android.systemui:string/quickpanel_bluetooth_text", "WOO!");
        resparam.res.setReplacement("com.android.systemui", "string", "quickpanel_gps_text", "HOO!");
        resparam.res.setReplacement("com.android.systemui", "integer", "config_maxLevelOfSignalStrengthIndicator", 6);

        // 替换Drawable也采用相似的办法。然而你不能只使用Drawable作为替换物，因为这可能导致同一个Drawable实例被不同的ImageViews引用。因此，你需要使用包装器
        resparam.res.setReplacement("com.android.systemui", "drawable", "status_bar_background", new XResources.DrawableLoader() {
            @Override
            public Drawable newDrawable(XResources res, int id) throws Throwable {
                return new ColorDrawable(Color.WHITE);
            }
        });

        // 你可以随意命名你的替换资源。我选择 ic_launcher 替代 stat_sys_battery 让它们在本文中更好区分。
        XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
        resparam.res.setReplacement("com.android.systemui", "drawable", "stat_sys_battery", modRes.fwd(R.mipmap.ic_launcher));
        resparam.res.setReplacement("com.android.systemui", "drawable", "stat_sys_battery_charge", modRes.fwd(R.mipmap.ic_launcher_round));


    }


}