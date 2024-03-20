package com.example.wi_fi_scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.wi_fi_scanner.databinding.ActivityMain3Binding

class MainActivity3 : AppCompatActivity(R.layout.activity_main3) {

    private val viewBinding by viewBinding(ActivityMain3Binding::bind)
    val mainWifiObj: WifiManager by lazy(LazyThreadSafetyMode.NONE) {
        this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
    var networkSSID = "MGTS_GPON_1767"
    var networkBBID = "3c:98:72:37:84:8b"
    var networkPass = "ABFXKVEZ"

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

        viewBinding.buttonConnect.setOnClickListener {
            Toast.makeText(this@MainActivity3,"Connect1 ...",Toast.LENGTH_SHORT).show()

            val conf = WifiConfiguration()
            conf.SSID = "\"" + networkSSID + "\""
            conf.wepKeys[0] = "\"" + networkPass + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            conf.preSharedKey = "\""+ networkPass +"\"";

            val netId: Int = mainWifiObj.addNetwork(conf)
            mainWifiObj.disconnect()
            mainWifiObj.enableNetwork(netId, true)
            mainWifiObj.reconnect()
        }

    }

    inner class WifiReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1 != null) {
                if (p1.action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    val wifiList: List<ScanResult> = mainWifiObj.scanResults
                    viewBinding.tv.text = wifiList.toString()
                }
            }
        }

    }

    /*private fun connect(){
        Toast.makeText(this@MainActivity3,"Connect2 ...",Toast.LENGTH_SHORT).show()
        val wifiReciever = WifiScanReceiver()

        registerReceiver(wifiReciever, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        val wifiScanList: List<ScanResult> = mainWifiObj.scanResults
        viewBinding.tv.text = wifiScanList.toString()
        Toast.makeText(this@MainActivity3,wifiScanList.toString(),Toast.LENGTH_SHORT).show()
    }*/

    /*SSID: MGTS_GPON5_1767, BSSID: 3c:98:72:37:84:8b, capabilities: [WPA2-PSK-CCMP][ESS][WPS], level: -56, frequency: 5180, timestamp: 62374022313, distance: ?(cm), distanceSd: ?(cm), passpoint: no, ChannelBandwidth: 2, centerFreq0: 5210, centerFreq1: 0, 80211mcResponder: is not supported, Carrier AP: no, Carrier AP EAP Type: -1, Carrier name: null, Radio Chain Infos: null*/

    @RequiresApi(Build.VERSION_CODES.M)
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if(permissions.entries.toList().all { it.value }){
                Toast.makeText(this@MainActivity3,"Connect Activity 3",Toast.LENGTH_SHORT).show()
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
            && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
        ){
            Toast.makeText(this@MainActivity3,"Connect Start",Toast.LENGTH_SHORT).show()
        }else{
            requestCameraPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.CHANGE_NETWORK_STATE
                )
            )
        }
    }
}