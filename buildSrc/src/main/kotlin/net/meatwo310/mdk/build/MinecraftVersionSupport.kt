package net.meatwo310.mdk.build

fun String.supportsGameTestServer(): Boolean {
    val parts = split(".").mapNotNull(String::toIntOrNull)
    val major = parts.getOrElse(0) { 0 }
    val minor = parts.getOrElse(1) { 0 }
    val patch = parts.getOrElse(2) { 0 }
    return major > 26 || (major == 26 && minor >= 1) || (major == 1 && minor == 21 && patch >= 11)
}
