# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Develop\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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
#代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5
#混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
#指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
#这句话能够使我们的项目混淆后产生映射文件
#包含有类名->混淆后类名的映射关系
-verbose
# 关闭优化功能，因为优化可能会造成一些潜在的风险，无法保证在所有版本的 Dalvik 都正常运行
#-dontoptimize
#指定不去忽略非公共库的类
-dontskipnonpubliclibraryclassmembers
#不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify
-useuniqueclassmembernames
-mergeinterfacesaggressively
#混淆包名
-repackageclasses ''
-allowaccessmodification
#保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses,Exceptions,Signature
#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
#指定混淆是采用的算法，后面的参数是一个过滤器
#这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
-dontwarn android.webkit.**
-dontwarn javax.**
-dontnote android.webkit.**

-keep public class javax.**
-keep public class android.webkit.**
#保留java与js通信的类不被混淆
-keep public class com.redefine.commonui.h5.WeLikeJsBridge
#保留我们使用的四大组件，自定义的Application等等这些类不被混淆
#因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#保留support下的所有类及其内部类
-keep class android.support.** {*;}

#保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
     public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
     public boolean *(android.webkit.WebView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
     public void *(android.webkit.WebView,java.lang.String);
     public void openFileChooser(...);
     public boolean onShowFileChooser(...);
}

#保留在Activity中的方法参数是view的方法，
#这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

#保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保留我们自定义控件（继承自View）不被混淆
# 保留继承自 View 的 setXx() 和 getXx() 方法，因为属性动画会用到相应的 setter 和 getter
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int d(...);
    public static int w(...);
    public static int v(...);
    public static int i(...);
}

-dontwarn com.android.installreferrer
-keep class com.appsflyer.** { *; }

