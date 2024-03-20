package com.example.wi_fi_scanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.net.wifi.hotspot2.PasspointConfiguration
import android.os.Build
import android.os.PatternMatcher
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult


class WifiAndroid10(private val context: Context) {

    /*
        Без подключения к интеренту
    */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun connecting(ssid:String, password:String,resultLauncher:ActivityResultLauncher<Intent>){
        val builder = WifiNetworkSpecifier.Builder()
        builder.setSsid(ssid)
        builder.setWpa2Passphrase(password)

        val wifiNetworkSpecifier = builder.build()

        val networkRequestBuilder1 = NetworkRequest.Builder()
        networkRequestBuilder1.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        networkRequestBuilder1.setNetworkSpecifier(wifiNetworkSpecifier)

        val nr = networkRequestBuilder1.build()
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object :ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                Toast.makeText(context,"Подключенно 1",Toast.LENGTH_SHORT).show()
            }

            override fun onUnavailable() {
                Toast.makeText(context,"Отказ 1",Toast.LENGTH_SHORT).show()
            }
        }
        cm.requestNetwork(nr, networkCallback);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resultLauncher.launch(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        } else {
            (this.context.getSystemService(Context.WIFI_SERVICE) as? WifiManager)?.apply { isWifiEnabled = true /*or false*/ }
        }*/

        (this.context.getSystemService(Context.WIFI_SERVICE) as? WifiManager)?.apply { isWifiEnabled = true /*or false*/ }
        //cm.unregisterNetworkCallback(networkCallback) Отключитесь от привязанной сети:
    }

    /*var resultLauncher =  context.applicationContext.registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_CANCELED) {

            }
        }*/

    /*
        с подключением к интернету
    */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectingNetworkYes(ssid:String, password:String,resultLauncher:ActivityResultLauncher<Intent>){
        val suggestion1 = WifiNetworkSuggestion.Builder()
            .setSsid(ssid)
            .setIsAppInteractionRequired(true) // Optional (Needs location permission)
            .build();

        val suggestion2 = WifiNetworkSuggestion.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .setIsAppInteractionRequired(true) // Optional (Needs location permission)
            .build();

        val suggestion3 = WifiNetworkSuggestion.Builder()
            .setSsid(ssid)
            .setWpa3Passphrase(password)
            .setIsAppInteractionRequired(true) // Optional (Needs location permission)
            .build();

        val suggestionsList = mutableListOf(suggestion1, suggestion2, suggestion3)

        val passpointConfig = PasspointConfiguration(); // configure passpointConfig to include a valid Passpoint configuration

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val suggestion4 = WifiNetworkSuggestion.Builder()
                .setPasspointConfig(passpointConfig)
                .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                .build()

            suggestionsList.add(suggestion4)
        }*/

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager;

        val status = wifiManager.addNetworkSuggestions(suggestionsList);
        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            Toast.makeText(context,"Операция Not",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context,"Операция Yes",Toast.LENGTH_SHORT).show()
        }

        val intentFilter = IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (!intent.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                    Toast.makeText(context,"Отказ 2",Toast.LENGTH_SHORT).show()
                    return;
                }else if (intent.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)){
                    Toast.makeText(context,"Подключенно 2",Toast.LENGTH_SHORT).show()
                }
                // do post connect processing here
            }
        };
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resultLauncher.launch(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        } else {
            (this.context.getSystemService(Context.WIFI_SERVICE) as? WifiManager)?.apply { isWifiEnabled = true /*or false*/ }
        }*/

        (this.context.getSystemService(Context.WIFI_SERVICE) as? WifiManager)?.apply { isWifiEnabled = true /*or false*/ }
        context.applicationContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    /*
       без подключения к интернету к интернету
   */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectingNetworkNot(ssid:String, bssi:String){
        val specifier = WifiNetworkSpecifier.Builder()
            .setSsidPattern(PatternMatcher(ssid, PatternMatcher.PATTERN_PREFIX))
            .setBssidPattern(MacAddress.fromString(bssi), MacAddress.fromString("ff:ff:ff:00:00:00"))
            .build()

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .setNetworkSpecifier(specifier)
            .build()

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Toast.makeText(context,"Подключенно 3",Toast.LENGTH_SHORT).show()
            }

            override fun onUnavailable() {
                Toast.makeText(context,"Отказ 3",Toast.LENGTH_SHORT).show()
            }
        }
        connectivityManager.requestNetwork(request, networkCallback)

        //connectivityManager.unregisterNetworkCallback(networkCallback) Отключитесь от привязанной сети:
    }
}