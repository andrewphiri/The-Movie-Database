package com.drew.themoviedatabase

fun formatDuration(minutes: Int?): String {
    if (minutes == null) return "N/A"
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return "${hours}h ${remainingMinutes}m"
}