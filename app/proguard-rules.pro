# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-ignorewarnings                     # 忽略警告，避免打包时某些警告出现
-optimizationpasses 5               # 指定代码的压缩级别
-dontusemixedcaseclassnames         # 是否使用大小写混合
-dontskipnonpubliclibraryclasses    # 是否混淆第三方jar
-dontskipnonpubliclibraryclassmembers
-dontpreverify                      # 混淆时是否做预校验
-verbose                            # 混淆时是否记录日志
-dontoptimize
-keepattributes Signature			#使用到了泛型

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*        # 混淆时所采用的算法


-dontwarn android.support.v4.**
-dontwarn android.os.**

-keep class android.support.v4.** { *; }        # 保持哪些类不被混淆
-keep interface android.support.v8.** { *; }
-keep class android.support.v8.** { *; }
-keep public class * extends android.support.v8.**

-keep class com.baidu.** { *; }
-keep class com.alibaba.** { *; }
-keep class com.openim.** { *; }
-keep class com.taobao.**{ *; }
-keep class com.ta.**{ *; }
-keep class com.ut.**{ *; }
-keep class com.alipay.**{ *; }
-keep class vi.com.gdi.bgl.android.**{*;}
-keep class android.os.**{*;}

# for skyworth fastjson
-keep class com.alibaba.fastjson.** { *; }

-keep class com.tencent.** {*;}
-keep class com.tencent.mm.sdk.** {*;}
-keep class u.upd.** {*;}
-keep class com.nineoldandroids.** {*;}
-keep class org.apache.** {*;}
-keep class u.aly.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.marsor.common.** {*;}
-keep class com.sina.** {*;}

-keep interface android.support.v4.app.** { *; }
-keep interface android.support.v8.** { *; }
-keep class android.support.v8.** { *; }
-keep class android.support.v7.** { *; }
-keep public class * extends android.support.v8.**

-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.view.View
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.support.v4.widget

#WebViewClient 重写onReceivedSslError方法 一定要保持 （混淆后部分手机会有问题）
-keep public class android.net.http.SslError
-keep public class android.webkit.SslErrorHandler
# 沉浸式状态栏适配
 -keep class com.gyf.barlibrary.* {*;}
 -dontwarn com.gyf.barlibrary.**


-keepclasseswithmembernames class * {       # 保持 native 方法不被混淆
    native <methods>;
}

-keepclasseswithmembers class * {            # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {            # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity { #保持类成员
   public void *(android.view.View);
}

-keepclassmembers enum * {                  # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {*;}    # 保持 Parcelable 不被混淆
-keep class * implements java.io.Serializable {*;}    # 保持 Serializable 不被混淆

#阿里云 httpdns
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep interface com.androidex.zbuild.IKeepSource {*;}
-keep class * implements com.androidex.zbuild.IKeepSource {*;} #自定义的

#fresco start
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.facebook.infer.**
#fresco end

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

#webview
-keep class com.sjteam.weiguan.page.web.** {*;}
-keep class com.sjteam.weiguan.page.web.activity.BrowserActivity$* {*;}

#微信回调
-keep class com.sjteam.weiguan.page.sns.**{*;}
-keep class com.sjteam.weiguan.wxapi.**{*;}

# butterknife start
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }
# butterknife end


# event bus start -------------------------
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
# event bus end -------------------------

# bugly start -----------
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#bugly end ---------------

-keep public class android.webkit.**

-keepattributes SetJavaScriptEnabled
-keepattributes JavascriptInterface

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# okhttp

-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# okio

-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

#头条广告sdk
-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep class com.androidquery.callback.** {*;}
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
#头条广告sdk

#umeng analysis
-keep class com.umeng.** {*;}
#新版友盟
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
#umeng analysis

#GreenDao
-keep class org.greenrobot.greendao.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
#GreenDao
