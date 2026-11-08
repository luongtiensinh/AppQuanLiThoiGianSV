# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# --- BẢO VỆ DỮ LIỆU ĐỂ FIRESTORE VÀ GSON KHÔNG BỊ CRASH ---

# 1. Giữ nguyên tên biến cho các Entity (Models)
-keep class com.example.apphotrohoctap.data.** { *; }
-keepclassmembers class com.example.apphotrohoctap.data.** { *; }
-keep class com.example.apphotrohoctap.ui.tasks.TaskItem { *; }
-keepclassmembers class com.example.apphotrohoctap.ui.tasks.TaskItem { *; }
-keep class com.example.apphotrohoctap.ui.tasks.Subtask { *; }
-keepclassmembers class com.example.apphotrohoctap.ui.tasks.Subtask { *; }
-keep class com.example.apphotrohoctap.ui.grades.*UiModel { *; }
-keepclassmembers class com.example.apphotrohoctap.ui.grades.*UiModel { *; }

# 2. Hỗ trợ Gson và Firebase
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.Unsafe
-keep class com.google.gson.stream.** { *; }

# Hỗ trợ Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler { *; }