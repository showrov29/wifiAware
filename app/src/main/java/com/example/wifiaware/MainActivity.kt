package com.example.wifiaware
import android.Manifest
import android.net.wifi.aware.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.wifiaware.ui.theme.WifiAwareTheme
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.net.wifi.aware.PeerHandle
import android.content.pm.PackageManager
import android.net.wifi.aware.DiscoverySessionCallback
import android.net.wifi.aware.PublishConfig
import android.net.wifi.aware.PublishDiscoverySession
import android.net.wifi.aware.SubscribeDiscoverySession
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.aware.WifiAwareSession
import android.net.wifi.aware.AttachCallback
import android.util.Log
import android.widget.Toast
import  androidx.compose.material3.TextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat



class MainActivity : ComponentActivity() {
    //private lateinit var wifiAwareSession: WifiAwareSession
    private lateinit var wifiAwareSession: WifiAwareSession
    private lateinit var discoverySession: PublishDiscoverySession
    private lateinit var subscribeSession: SubscribeDiscoverySession

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private const val TAG = "WifiAware"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            WifiAwareTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Greeting("Android")
                    var userInput by remember { mutableStateOf("") }
                    var broadcastMessage by remember { mutableStateOf("") }


//                    Publish()
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(text = broadcastMessage)


                        TextField(
                            value = userInput,
                            onValueChange = { userInput = it }, // Update user input
                            label = { Text("Enter value") },   // Optional label
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                publishService(userInput) { message ->
                                    broadcastMessage = message
                                }

                            },
                            modifier = Modifier
                                .height(100.dp)
                                .width(200.dp) // Set width here
                        ) {
                            Text("Publish")
                        }

                        Spacer(modifier = Modifier.height(16.dp)) // Adds space between buttons

                        Button(
                            onClick = {
                                subscribeService(userInput) { message ->
                                    broadcastMessage = message
                                }
                            },
                            modifier = Modifier
                                .height(100.dp)
                                .width(200.dp) // Set width here
                        ) {
                            Text("Subscribe")
                        }
                    }


                }
            }
        }


        // Check if Wi-Fi Aware is supported
        val wifiAwareSupported = packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE);
        Log.d("FeatureCheck", "Wi-Fi Aware supported: $wifiAwareSupported")
        if (wifiAwareSupported) {
            // Proceed with Wi-Fi Aware functionality
//            setupWifiAware()
            val wifiAwareManager = getSystemService(WIFI_AWARE_SERVICE) as WifiAwareManager
            if (wifiAwareManager.isAvailable) {
//
                attachWifiAware(wifiAwareManager)
                Toast.makeText(this, "Wifi Aware is available this device.", Toast.LENGTH_LONG)
                    .show()
            } else {
                // Handle the case where Wi-Fi Aware is not supported
                Toast.makeText(
                    this,
                    "Wi-Fi Aware is not supported on this device.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(this, "Wi-Fi Aware is not supported on this device.", Toast.LENGTH_LONG)
                .show()
        }

    }


//    private fun setupWifiAware(wifiAwareManager: WifiAwareManager) {
//        var attachCallback=object: WifiAwareSession.Callback(){
//            fun  oncu
//        }
//    }

    private fun attachWifiAware(wifiAwareManager: WifiAwareManager) {
        wifiAwareManager.attach(object : AttachCallback() {
            override fun onAttached(session: WifiAwareSession) {
                super.onAttached(session)
                wifiAwareSession = session

//                Log.d("WifiAware", "Session attached successfully")
//                publishService()
//                subscribeService()
            }

            override fun onAttachFailed() {
                super.onAttachFailed()
                Log.e("WifiAware", "Failed to attach Wi-Fi Aware session")

                Toast.makeText(this@MainActivity, "Wi-Fi Aware attach failed", Toast.LENGTH_LONG)
                    .show()
            }

        }, null)
    }

    private fun publishService(userInput: String, onMessageReceived: (String) -> Unit) {
        val publishConfig = PublishConfig.Builder().setServiceName(userInput)
//        .setServiceSpecificInfo("Name: Showrov, Age: 23".toByteArray(Charsets.UTF_8))
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
//        || ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.NEARBY_WIFI_DEVICES
//        ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ),
                1
            )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }





        wifiAwareSession.publish(publishConfig, object : DiscoverySessionCallback() {

            override fun onPublishStarted(session: PublishDiscoverySession) {
                super.onPublishStarted(session)
                discoverySession = session
//            val message = "Name: Atif, Age: 25".toByteArray(Charsets.UTF_8)
                Log.d("WifiAware", "Service published successfully")
                Toast.makeText(
                    this@MainActivity,
                    "Publishing ${userInput} Channel",
                    Toast.LENGTH_LONG
                ).show()
                onMessageReceived("Published ${discoverySession.toString()} Channel")

            }


//            override fun onMessageSendFailed(messageId: Int) {
//                super.onMessageSendFailed(messageId)
//                Log.e("WifiAware", "Failed to send message with ID: $messageId")
//                onMessageReceived("Failed to send message with ID: $messageId")
//                Toast.makeText(this@MainActivity, "Failed to send message", Toast.LENGTH_SHORT)
//                    .show()
//            }
//
//            override fun onMessageSendSucceeded(messageId: Int) {
//                super.onMessageSendSucceeded(messageId)
//                Log.d("WifiAware", "Message sent successfully")
//                onMessageReceived("Message sent successfully from publisher.")
//            }

            override fun onSessionTerminated() {
                super.onSessionTerminated()
                onMessageReceived("session is terminated")
            }


            override fun onSessionConfigFailed() {
                super.onSessionConfigFailed()

                onMessageReceived("Service publishing failed")
//            Log.e("WifiAware", "Service publishing failed")
            }


            override fun onMessageReceived(peerHandle: PeerHandle?, message: ByteArray?) {
                super.onMessageReceived(peerHandle, message)
                val messageString = message?.toString(Charsets.UTF_8)
                Log.d("WifiAware", "Message received: $messageString from peer: $peerHandle")
                onMessageReceived("Message received: $messageString")
                // Handle the message (e.g., display a toast or log it)
                Toast.makeText(
                    this@MainActivity,
                    "Message received: $messageString",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }, null)

    }


    private fun subscribeService(userInput: String, onMessageReceived: (String) -> Unit) {
        val subscribeConfig = SubscribeConfig.Builder()
            .setServiceName(userInput) // Same service name as the publisher
            .build()


        // Subscribe to the service
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
//
//            || ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.NEARBY_WIFI_DEVICES
//            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }


        wifiAwareSession.subscribe(subscribeConfig, object : DiscoverySessionCallback() {
            override fun onSubscribeStarted(session: SubscribeDiscoverySession) {
                super.onSubscribeStarted(session)
                subscribeSession = session
                onMessageReceived("On Subscribe started")
            }

            override fun onServiceDiscovered(
                peerHandle: PeerHandle,
                serviceSpecificInfo: ByteArray?,
                matchFilter: MutableList<ByteArray>?
            ) {
                super.onServiceDiscovered(peerHandle, serviceSpecificInfo, matchFilter)
                onMessageReceived("Service discovered with peerhandle ${peerHandle}")
                var message= "location:23.43.23.45,".toByteArray()
                subscribeSession.sendMessage(peerHandle,101,message)
            }

            override fun onMessageSendFailed(messageId: Int) {
                super.onMessageSendFailed(messageId)
                Toast.makeText(this@MainActivity, "Failed to send message", Toast.LENGTH_LONG).show()
            }

            override fun onMessageSendSucceeded(messageId: Int) {
                super.onMessageSendSucceeded(messageId)
                Toast.makeText(this@MainActivity, "Successfully sent message", Toast.LENGTH_LONG).show()
            }
        }, null)
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WifiAwareTheme {
        Greeting("Android")
    }
}