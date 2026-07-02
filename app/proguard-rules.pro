# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.serialization.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.aichat.app.data.remote.model.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Compose
-dontwarn androidx.compose.**

# Markwon
-keep class io.noties.markwon.** { *; }

# App models
-keep class com.aichat.app.domain.model.** { *; }
