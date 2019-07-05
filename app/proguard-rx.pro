-dontwarn sun.misc.**
-dontwarn rx.**
-dontwarn io.reactivex.**
-keep class io.reactivex.** {*;}

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keep class rx.internal.util.unsafe { *;}
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}

-keep class org.apache.commons.**

-keep class com.google.android.gms.analytics.**
-keep class com.google.analytics.tracking.**
-keep class com.google.android.gms.**
-keep class com.google.firebase.**
-dontwarn com.google.android.gms.analytics.**
-dontwarn com.google.analytics.tracking.**
-dontwarn com.google.android.gms.**
-dontwarn com.google.firebase.**

-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider

-dontwarn com.appsflyer.**

# Added for guava 23.5-android
-dontwarn com.google.common.collect.MinMaxPriorityQueue
-dontwarn com.google.common.util.concurrent.FuturesGetChecked**
-dontwarn javax.lang.model.element.Modifier
-dontwarn afu.org.checkerframework.**
-dontwarn org.checkerframework.**

# keep youtube / google
-keep class com.google.api.services.** { *; }
-keep class com.google.android.youtube.player.** { *; }
# Needed by google-api-client to keep generic types and @Key annotations accessed via reflection
-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

-keep class com.tencent.** { *; }
-dontwarn com.google.android.exoplayer2.**
-dontwarn com.tencent.**
# video recorder
-keep class com.media.mediasdk.common.SimpleFile {*;}
-keep class com.media.mediasdk.common.TimeUtil {*;}
-keep class com.media.mediasdk.common.OSUtil {*;}
-keep class com.media.mediasdk.common.log.* {*;}

-keep public class com.media.mediasdk.codec.common.MediaObject {*;}
-keep public class * extends com.media.mediasdk.codec.common.MediaObject {*;}

-keep class com.media.mediasdk.common.Size {*;}
-keep class com.media.mediasdk.common.SizeMap {*;}
-keep class com.media.mediasdk.codec.common.VideoSize {*;}
-keep class com.media.mediasdk.codec.common.VideoInfo {*;}
-keep class com.media.mediasdk.codec.common.AVCommon {*;}
#-keepclassmembers class com.media.mediasdk.common.AspectRatio {*;}

-keep class com.media.mediasdk.codec.MP4HeadInfo {*;}
-keep class com.media.mediasdk.codec.FileUtil {*;}
-keep class com.media.mediasdk.codec.JNIVideoEncoder {*;}
-keep class com.media.mediasdk.codec.VideoEncoderUI {*;}
-keep class com.media.mediasdk.codec.VideoEncoderEventListener {*;}

-keep class com.media.mediasdk.codec.JNIFileEncoder {*;}
-keep class com.media.mediasdk.codec.FileEncoderUI {*;}
-keep class com.media.mediasdk.codec.FileEncoderEventListener {*;}
-keep class com.media.mediasdk.codec.FileEncoderEventListener {*;}

-keep class com.media.mediasdk.codec.GIF.JNIGIFEncoder {*;}
-keep class com.media.mediasdk.codec.GIF.GIFEncoder {*;}
-keep class com.media.mediasdk.codec.GIF.GIFEncoder$GIFEncoderListener {*;}

#-keep class com.media.mediasdk.OSCore.* {*;}

#com.media.mediasdk.codec.WAV.AudioEdit
#com.media.mediasdk.codec.WAV.SamplePlayer$OnCompletionListener

-keep class com.media.mediasdk.UI.* {*;}

#-keep class com.media.mediasdk.media.core.media.RenderBean {*;}
#-keep class com.media.mediasdk.media.core.media.SurfaceShower$OnDrawEndListener {*;}

#-keep class com.media.mediasdk.core.record.* {*;}
-keep class com.media.mediasdk.core.RDCamera.**{*;}
-keep class com.media.mediasdk.core.player.* {*;}
#apollo
-keep class com.UCMobile.** { *; }
-keep class io.vov.** { *; }

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# ------- end okhttp proguard rules ----

# ------- because of we using com.liulishuo.okdownload:okhttp on sample ----
-keepnames class com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection
# ------- end com.liulishuo.okdownload:okhttp proguard rules ----

# ------- because of we using com.liulishuo.okdownload:sqlite on sample ----
-keep class com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnSQLite {
        public com.liulishuo.okdownload.core.breakpoint.DownloadStore createRemitSelf();
        public com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnSQLite(android.content.Context);
}
# ------- end com.liulishuo.okdownload:sqlite proguard rules ----

-keep class com.redefine.welike.business.publisher.management.bean.** {*;}

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Gson specific classes
-dontwarn sun.misc.**
-dontwarn retrofit2.Platform$Java8
##---------------End: proguard configuration for Gson  ----------

-keep class com.alibaba.sdk.android.oss.** { *; }
-dontwarn okio.**
-dontwarn org.apache.commons.codec.binary.**