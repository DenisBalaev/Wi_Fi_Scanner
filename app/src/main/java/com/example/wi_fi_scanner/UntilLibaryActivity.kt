package com.example.wi_fi_scanner

import android.annotation.SuppressLint
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration.Protocol.WPA
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.wi_fi_scanner.databinding.ActivityUntilLibaryBinding
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener


class UntilLibaryActivity : AppCompatActivity(R.layout.activity_until_libary) {

    private val viewBinding by viewBinding(ActivityUntilLibaryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WifiUtils.withContext(applicationContext).enableWifi()
        WifiUtils.withContext(applicationContext).scanWifi(this::getScanResults).start();

        WifiUtils.withContext(applicationContext)
            .connectWith(networkSSID, networkPass)
            .setTimeout(40000)
            .onConnectionResult(object : ConnectionSuccessListener {
                override fun success() {
                    Toast.makeText(this@UntilLibaryActivity, "SUCCESS!", Toast.LENGTH_SHORT).show()
                }

                override fun failed(errorCode: ConnectionErrorCode) {
                    Toast.makeText(this@UntilLibaryActivity, "EPIC FAIL!$errorCode", Toast.LENGTH_SHORT)
                        .show()
                }
            })
            .start()
    }

    @SuppressLint("LongLogTag")
    private fun getScanResults(results: List<ScanResult>) {
        if (results.isEmpty()) {
            Log.i(TAG, "SCAN RESULTS IT'S EMPTY")
            return
        }
        Log.i(TAG, "GOT SCAN RESULTS $results")
    }

    fun findAllDevicesInNetwork() {
        val jmdns = JmDNS.create()

        jmdns.addServiceListener("_services._dns-sd._udp.local.", object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent) {
                val serviceInfo = jmdns.getServiceInfo(event.type, event.name)
                println("Found device: ${serviceInfo.name} - ${serviceInfo.inet4Addresses.joinToString(", ")}")
            }

            override fun serviceRemoved(event: ServiceEvent) {
            }

            override fun serviceResolved(event: ServiceEvent) {
            }
        })
    }

    private val TAG = "UntilLibaryActivity_WIFI"
    private var networkSSID = "MGTS_GPON_1767"
    private var networkBBID = "3c:98:72:37:84:8b"
    private var networkPass = "ABFXKVEZ"
}