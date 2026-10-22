# XCSABER TV OS ProGuard rules

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application

-keepattributes SourceFile,LineNumberTable

-keep class kotlin.Metadata { *; }

-keep class com.xcsaber.tv.MainActivity { *; }
