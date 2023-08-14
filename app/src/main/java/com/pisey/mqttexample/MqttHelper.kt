package com.pisey.mqttexample

import android.content.Context
import android.util.Log
import org.eclipse.paho.mqttv5.client.IMqttToken
import org.eclipse.paho.mqttv5.client.MqttActionListener
import org.eclipse.paho.mqttv5.client.MqttAsyncClient
import org.eclipse.paho.mqttv5.client.MqttCallback
import org.eclipse.paho.mqttv5.client.MqttClientPersistence
import org.eclipse.paho.mqttv5.client.MqttConnectionOptionsBuilder
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence
import org.eclipse.paho.mqttv5.common.MqttException
import org.eclipse.paho.mqttv5.common.MqttMessage
import org.eclipse.paho.mqttv5.common.packet.MqttProperties
import java.nio.charset.Charset

private const val TAG = "checkStatus"
class MqttHelper(private val context:Context) {
    private lateinit var mqttClient: MqttAsyncClient
    private var messageArrived:((String)-> Unit)? = null

    fun connect(callback:()->Unit) {
        try {
            val persistence: MqttClientPersistence = MemoryPersistence()
            val serverURI = "tcp://159.138.93.19:1883"
            val clientId = "Android"
            mqttClient = MqttAsyncClient(serverURI, clientId, persistence)
            val options = MqttConnectionOptionsBuilder()
                .automaticReconnect(true)
                .username("admin@jalatlogistics.com")
                .password("Jalat@12345!".toByteArray())
                .automaticReconnect(true)
                .build()
            mqttClient.connect(options,context,object :MqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    mqttClient.setCallback(object:MqttCallback{
                        override fun disconnected(disconnectResponse: MqttDisconnectResponse?) {}

                        override fun mqttErrorOccurred(exception: MqttException?) {}

                        override fun messageArrived(topic: String?, message: MqttMessage?) {
                            messageArrived?.invoke(String(message?.payload!!, Charset.defaultCharset()))
                        }

                        override fun deliveryComplete(token: IMqttToken?) {
                        }

                        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                            reconnect
                        }

                        override fun authPacketArrived(reasonCode: Int, properties: MqttProperties?) {
                            properties
                        }

                    })
                    callback.invoke()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {}

            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            mqttClient.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, payload: ByteArray, qos: Int = 0, retained: Boolean = false) {
        try {
            val message = MqttMessage(payload)
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, context, object : MqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    // Handle successful publish
                    Log.i(TAG, "onSuccess: Send Success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    // Handle failed publish
                    Log.e(TAG, "onFailure: $exception", )
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String, qos: Int = 0,messageArrived:(String)->Unit) {
       this.messageArrived = messageArrived
        try {
            mqttClient.subscribe(topic, qos, context, object : MqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    // Handle successful subscription
                    Log.i(TAG, "onSuccess: subscript success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    // Handle failed subscription
                    Log.e(TAG, "onFailure: $exception" )
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}

