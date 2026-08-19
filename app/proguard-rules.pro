# Keep model classes used with Firestore's reflective (de)serialization.
-keepclassmembers class com.goldmine.uncc.data.model.** {
  <init>();
  <fields>;
  <methods>;
}
-keep class com.goldmine.uncc.data.model.** { *; }

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.goldmine.uncc.**$$serializer { *; }
-keepclassmembers class com.goldmine.uncc.** {
    *** Companion;
}
-keepclasseswithmembers class com.goldmine.uncc.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit / OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keepattributes Signature, Exceptions

# Firebase
-keepattributes RuntimeVisibleAnnotations
-dontwarn com.google.firebase.**
