# ProGuard / R8 rules for StepTracker
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* *;
}
