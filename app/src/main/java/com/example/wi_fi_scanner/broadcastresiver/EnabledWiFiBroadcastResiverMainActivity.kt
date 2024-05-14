package com.example.wi_fi_scanner.broadcastresiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.wi_fi_scanner.R
import com.example.wi_fi_scanner.databinding.ActivityEnabledWiFiBroadcastResiverMainBinding

class EnabledWiFiBroadcastResiverMainActivity :
    AppCompatActivity(R.layout.activity_enabled_wi_fi_broadcast_resiver_main) {

    private val viewBinding by viewBinding(ActivityEnabledWiFiBroadcastResiverMainBinding::bind)
    private val wifiManager: WifiManager by lazy(LazyThreadSafetyMode.NONE) {
        this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
    private val connectivityManager: ConnectivityManager by lazy(LazyThreadSafetyMode.NONE) {
        this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val wifiStateReceiver = WifiStateReceiver()
        val intentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        this.applicationContext.registerReceiver(wifiStateReceiver, intentFilter)

        // В вашей активности или сервисе
        /*val wifiConnectionReceiver = WifiConnectionReceiver()
        val intentFilter2 = IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        applicationContext.registerReceiver(wifiConnectionReceiver, intentFilter2)*/

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        val wifiNetworkCallback = WifiNetworkCallback(applicationContext)
        connectivityManager.registerNetworkCallback(networkRequest, wifiNetworkCallback)
    }

}

class WifiStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                when (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)) {
                    WifiManager.WIFI_STATE_ENABLED -> {
                        Toast.makeText(context, "Wi-Fi включен", Toast.LENGTH_SHORT).show()
                    }
                    WifiManager.WIFI_STATE_DISABLED -> {
                        Toast.makeText(context, "Wi-Fi выключен", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

/*class WifiConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
            val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
            if (networkInfo?.isConnected == true) {
                // Wi-Fi подключен
                Toast.makeText(context, "Подключено к Wi-Fi сети", Toast.LENGTH_LONG).show()
            } else {
                // Wi-Fi не подключен
                Toast.makeText(context, "Wi-Fi сеть отключена", Toast.LENGTH_LONG).show()
            }
        }
    }
}*/

class WifiNetworkCallback(private val context: Context) : ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val isWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        if (isWifi) {
            Toast.makeText(context, "Подключено к Wi-Fi сети", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLost(network: Network) {
        Toast.makeText(context, "Wi-Fi сеть отключена", Toast.LENGTH_SHORT).show()
    }

    override fun onUnavailable() {
        Toast.makeText(context, "Wi-Fi сеть не подключена не куда", Toast.LENGTH_SHORT).show()
    }
}
