package com.pisey.mqttexample

data class PayloadLocationControl(val onlineStatus:String)
enum class PayloadLocationControlStatus(val key:String){
    ONLINE("ONLINE"),
    OFFLINE("OFFLINE"),
}
