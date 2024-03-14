package com.example.wi_fi_scanner

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.os.PatternMatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wi_fi_scanner.databinding.ActivityMain2Binding


class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding

    var lv: ListView? = null
    var wifi: WifiManager? = null
    lateinit var wifis: MutableList<String>
    var wifiReciever: WifiScanReceiver? = null

    var networkSSID = "MGTS_GPON5_1767"
    var networkPass = "ABFXKVEZ"

    var TAG = "TAG_WI_FI_CONNECT"


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()

        lv=binding.listView
        wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        enableWifi()
        wifiReciever = WifiScanReceiver()
        wifi!!.startScan()
        val wifiList = wifi!!.scanResults

        wifiList.filter {
            it.SSID == networkSSID
        }.map {
            connect5(it,wifi!!)
            Toast.makeText(this@MainActivity2,"start fun connect",Toast.LENGTH_SHORT).show()
        }

        /*for (item in wifiList){
            if (item.SSID == networkSSID){
                //connect5(item,wifi!!)
                connect4(item)
                break
            }
        }*/

        //connect2()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectToWiFi(pin: String, ssid:String) {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as
                    ConnectivityManager
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
                //showToast(context,context.getString(R.string.connection_success))
            }

            override fun onUnavailable() {
                super.onUnavailable()
                //showToast(context,context.getString(R.string.connection_fail))
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                //showToast(context,context.getString(R.string.out_of_range))
            }
        }
        connectivityManager.requestNetwork(request, networkCallback)
    }

    private fun connect5(result:ScanResult,wifiManager:WifiManager){
        val securityMode: String = getScanResultSecurity(result)

        val wifiConfiguration = createAPConfiguration(networkSSID, networkPass, securityMode)

        val res: Int = wifiManager.addNetwork(wifiConfiguration)
        Log.d(TAG, "# addNetwork returned $res")

        val b: Boolean = wifiManager.enableNetwork(res, true)
        Log.d(TAG, "# enableNetwork returned $b")

        wifiManager.isWifiEnabled = true

        val changeHappen: Boolean = wifiManager.saveConfiguration()

        if (res != -1 && changeHappen) {
            Log.d(TAG, "# Change happen")
        } else {
            Log.d(TAG, "# Change NOT happen")
        }
    }

    private fun createAPConfiguration(
        networkSSID: String,
        networkPasskey: String,
        securityMode: String
    ): WifiConfiguration? {
        val wifiConfiguration = WifiConfiguration()
        wifiConfiguration.SSID = "\"" + networkSSID + "\""
        if (securityMode.equals("OPEN", ignoreCase = true)) {
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        } else if (securityMode.equals("WEP", ignoreCase = true)) {
            /*wifiConfiguration.wepKeys[0] = "\"" + networkPasskey + "\""
            wifiConfiguration.wepTxKeyIndex = 0
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)*/

            wifiConfiguration.apply {
                wepKeys[0] = "\"" + networkPasskey + "\""
                wepTxKeyIndex = 0
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            }

        } else if (securityMode.equals("PSK", ignoreCase = true)) {
            /*wifiConfiguration.preSharedKey = "\"" + networkPasskey + "\""
            wifiConfiguration.hiddenSSID = true
            wifiConfiguration.status = WifiConfiguration.Status.ENABLED
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA)*/

            wifiConfiguration.apply {
                preSharedKey = "\"" + networkPasskey + "\""
                hiddenSSID = true
                status = WifiConfiguration.Status.ENABLED
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            }
        }else if (securityMode.equals("WPA", ignoreCase = true)) {
            /*wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            wifiConfiguration.preSharedKey = "\"" + networkPass + "\""*/

            wifiConfiguration.apply {
                allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                preSharedKey = "\"" + networkPass + "\""
            }
        }
        else {
            Log.i(TAG, "# Unsupported security mode: $securityMode")
            return null
        }
        return wifiConfiguration
    }

    private fun getScanResultSecurity(scanResult: ScanResult): String {
        val cap = scanResult.capabilities
        val securityModes = arrayOf("WEP", "PSK", "EAP","WPA")
        //val securityModes = arrayOf("WEP", "PSK", "EAP")
        for (i in securityModes.indices.reversed()) {
            if (cap.contains(securityModes[i])) {
                Log.i(TAG, "${securityModes[i]}")
                return securityModes[i]
            }
        }
        return "OPEN"
    }

    @SuppressLint("MissingPermission")
    private fun connect4(scanResult:ScanResult){
        try {
            Log.v(
                "rht",
                "Item clicked, SSID " + scanResult.SSID.toString() + " Security : " + scanResult.capabilities
            )
            val networkSSID: String = scanResult.SSID
            val networkPass = networkPass
            val conf = WifiConfiguration()
            conf.SSID =
                "\"" + networkSSID + "\"" // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED
            conf.priority = 40
            if (scanResult.capabilities.toUpperCase().contains("WEP")) {
                Log.v("rht", "Configuring WEP")
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                if (networkPass.matches(Regex("^[0-9a-fA-F]+$"))) {
                    conf.wepKeys[0] = networkPass
                } else {
                    conf.wepKeys[0] = "\"" + networkPass + "\""
                }
                conf.wepTxKeyIndex = 0
            } else if (scanResult.capabilities.toUpperCase().contains("WPA")) {
                Log.v("rht", "Configuring WPA")
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                conf.preSharedKey = "\"" + networkPass + "\""
            } else {
                Log.v("rht", "Configuring OPEN network")
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                conf.allowedAuthAlgorithms.clear()
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            }
            val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val networkId = wifiManager.addNetwork(conf)
            Log.v("rht", "Add result $networkId")
            val list = wifiManager.configuredNetworks
            for (i in list) {
                if (i.SSID != null && i.SSID == "\"" + networkSSID + "\"") {
                    Log.d(TAG, "WifiConfiguration SSID " + i.SSID)
                    val isDisconnected = wifiManager.disconnect()
                    Log.d(TAG, "isDisconnected : $isDisconnected")
                    val isEnabled = wifiManager.enableNetwork(i.networkId, true)
                    Log.d(TAG, "isEnabled : $isEnabled")
                    val isReconnected = wifiManager.reconnect()
                    Log.d(TAG, "isReconnected : $isReconnected")
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun connect3(){
        val conf = WifiConfiguration()
        conf.SSID = "\"" + networkSSID + "\""

        conf.wepKeys[0] = "\"" + networkPass + "\"";
        conf.wepTxKeyIndex = 0;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        conf.preSharedKey = "\""+ networkPass +"\"";
        conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        wifiManager.addNetwork(conf)

        val list: List<WifiConfiguration> = wifiManager.configuredNetworks
        for (i in list) {
            if (i.SSID != null && i.SSID == "\"" + networkSSID + "\"") {
                wifiManager.disconnect()
                wifiManager.enableNetwork(i.networkId, true)
                val isE = wifiManager.reconnect().toString()
                Log.d("TAG_WI_FI_CONNECT",isE)
                break
            }
        }
    }

    private fun connect2(){
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = java.lang.String.format("\"%s\"", networkSSID)
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass)

        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        //remember id
        //remember id
        val netId = wifiManager.addNetwork(wifiConfig)
        wifiManager.disconnect()
        wifiManager.enableNetwork(netId, true)
        val isE = wifiManager.reconnect().toString()
        Log.d("TAG_WI_FI_CONNECT",isE)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToYourWifi(ssid: String, password:String) {

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

    @RequiresApi(Build.VERSION_CODES.M)
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if(permissions.entries.toList().all { it.value }){
                Toast.makeText(this@MainActivity2,"Connect",Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this@MainActivity2,"Connect Start",Toast.LENGTH_SHORT).show()
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

    override fun onPause() {
        unregisterReceiver(wifiReciever)
        super.onPause()
    }

    override fun onResume() {
        registerReceiver(wifiReciever, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        super.onResume()
    }


    inner class WifiScanReceiver : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            val wifiScanList: List<ScanResult> = wifi!!.scanResults
            wifis = mutableListOf()
            for (i in wifiScanList.indices) {
                wifis.add(wifiScanList[i].toString())
            }
            lv?.adapter = ArrayAdapter<String>(
                applicationContext,
                R.layout.simple_list_item_1,
                wifis
            )
        }
    }


    private fun enableWifi() {
        if (!wifi!!.isWifiEnabled) {
            wifi!!.isWifiEnabled = true
            val toast = Toast.makeText(applicationContext, "Wifi Turned On", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    private fun myConnect() {
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = String.format("\"%s\"", networkSSID)
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass)
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        //remember id
        val netId = wifiManager.addNetwork(wifiConfig)
        wifiManager.disconnect()
        wifiManager.enableNetwork(netId, true)
        wifiManager.reconnect()
    }

}