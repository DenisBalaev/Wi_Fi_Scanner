package com.example.wi_fi_scanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager

/*class WifiScanReceiver(
    private val wifi:WifiManager,
    private val list:MutableList<String> = mutableListOf()
    ) : BroadcastReceiver() {
    override fun onReceive(c: Context?, intent: Intent?) {
        val wifiScanList: List<ScanResult> = wifi.scanResults
        for (i in wifiScanList.indices) {
            list.add(wifiScanList[i].toString())
        }
    }
}*/