package xposed.qihao.magicwx;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * @author qihao
 * @Description (此类核心功能): 重新封装了日志工具类
 * 此类为单例，仅初始化一次。
 * @date on 2019/1/4 12:11
 */
public class LogUtil {

    /**
     * 此类为单例.
     * 已经标记 @Singleton
     */
    public LogUtil() {


        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(0)
                .methodOffset(7)
                .tag("HOOK")
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
//                return BuildConfig.DEBUG;
                return true;
            }
        });
        Logger.d("激活日志配置…");
    }

    private static StringBuilder getlocal() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 4;
        String className = stackTrace[index].getFileName();
        int lineNumber = stackTrace[index].getLineNumber();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Log位置(").append(className).append(":").append(lineNumber).append(")");
        return stringBuilder;
    }

    public static void v(String tag, String msg) {

        Logger.v(getlocal().append(msg).toString());
    }

    public static void v(String msg) {
        Logger.v(getlocal().append(msg).toString());
    }

    /**
     * 用Log.d()能输出Debug、Info、Warning、Error级别的Log信息。
     */
    public static void d(String msg) {
        Logger.d(getlocal().append(msg).toString());
    }

    public static void d(String tag, String msg) {
        Logger.d(getlocal().append(msg).toString());
    }

    public static void d(Object object) {
        Logger.d(object);
    }

    /**
     * 用Log.i()能输出Info、Warning、Error级别的Log信息。
     */
    public static void i(String msg) {
        Logger.d(getlocal().append(msg).toString());
    }

    public static void i(String tag, String msg) {
        Logger.d(getlocal().append(msg).toString());
    }

    /**
     * Warning表示警告：开发时有时用来表示特别注意的地方。用Log.w()能输出Warning、Error级别的Log信息。
     */
    public static void w(String tag, String msg) {
        Logger.d(getlocal().append(msg).toString());
    }

    public static void w(String msg) {
        Logger.d(getlocal().append(msg).toString());
    }

    /**
     * Error表示出现错误：是最需要关注解决的。用Log.e()输出，能输出Error级别的Log信息。
     */
    public static void e(String tag, String msg) {
        Logger.e(getlocal().append(msg).toString());
    }

    public static void e(@NonNull String tag, @Nullable Object... args) {
        Logger.e(tag, args);
    }

    public static void e(@NonNull String message) {
        Logger.e(getlocal().append(message).toString());
    }

    public static void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        Logger.e(null, message, args);
    }

    public static void json(@Nullable String json) {
        Logger.d(getlocal().append("如下·格式化json日志").toString());
        try {
            Logger.json(json + "");
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    public static void xml(@Nullable String xml) {
        Logger.xml(xml + "");
    }

    public static void wtf(@NonNull String message, @Nullable Object... args) {
        Logger.wtf(message, args);
    }
}

