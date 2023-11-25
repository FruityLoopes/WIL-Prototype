package uviwe.app.uviweappv1

data class AttendanceData(
    val Student: String,
    var attended: Boolean = false,
    var Days: String = "0"
)
