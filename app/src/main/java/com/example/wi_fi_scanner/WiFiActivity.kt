package com.example.wi_fi_scanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PatternMatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.wi_fi_scanner.databinding.ActivityWiFiBinding

class WiFiActivity : AppCompatActivity(R.layout.activity_wi_fi) {

    private val viewBinding by viewBinding(ActivityWiFiBinding::bind)

    var networkSSID = "MGTS_GPON_1767"
    var networkBBID = "3c:98:72:37:84:8b"
    var networkPass = "ABFXKVEZ"

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

        val wifiAndroid10 = WifiAndroid10(this@WiFiActivity)
        wifiAndroid10.connectingNetworkYes(ssid = networkSSID, password = networkPass, resultLauncher = null) // ок
       // wifiAndroid10.connectingNetworkNot(ssid = networkSSID, bssi = networkBBID)
        //connectToYourWifi(ssid = networkSSID, password = networkPass)

        viewBinding.btnConnect.setOnClickListener {
            connectToWiFi(pin = networkPass, ssid = networkSSID)
        }

        viewBinding.btnConnect.setOnClickListener {
            connectToWiFiReturn(pin = networkPass, ssid = networkSSID)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToYourWifi(ssid: String, password:String) {

        (this@WiFiActivity.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager)?.apply { isWifiEnabled = true /*or false*/ }

        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .build()

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiNetworkSpecifier)
            .build()

        val connectivityManager =
            applicationContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, ConnectivityManager.NetworkCallback())
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectToWiFi(pin: String, ssid:String) {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val specifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(pin)
            .setSsidPattern(PatternMatcher(ssid, PatternMatcher.PATTERN_PREFIX))
            .build()
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .setNetworkSpecifier(specifier)
            .build()
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                showToast(this@WiFiActivity,"onAvailable")
                connectivityManager.bindProcessToNetwork(network)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                showToast(this@WiFiActivity,"onUnavailable")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                showToast(this@WiFiActivity,"onLost")
            }
        }
        connectivityManager.requestNetwork(request, networkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectToWiFiReturn(pin: String, ssid:String) {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val specifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(pin)
            .setSsidPattern(PatternMatcher(ssid, PatternMatcher.PATTERN_PREFIX))
            .build()
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .setNetworkSpecifier(specifier)
            .build()
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                showToast(this@WiFiActivity,"onAvailable")
                connectivityManager.bindProcessToNetwork(network)
                (this@WiFiActivity.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager)?.apply { isWifiEnabled = true /*or false*/ }
            }

            override fun onUnavailable() {
                super.onUnavailable()
                showToast(this@WiFiActivity,"onUnavailable")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                showToast(this@WiFiActivity,"onLost")
            }
        }
        connectivityManager.requestNetwork(request, networkCallback)
    }

    private fun showToast(context: Context,string: String){
        Toast.makeText(context,string,Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if(permissions.entries.toList().all { it.value }){
                Toast.makeText(this@WiFiActivity,"Connect",Toast.LENGTH_SHORT).show()
            }else{
                requestPermissions()
            }
        }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            Toast.makeText(this@WiFiActivity,"Connect Start",Toast.LENGTH_SHORT).show()
        }else{
            requestCameraPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}