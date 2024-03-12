package com.example.wi_fi_scanner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.wi_fi_scanner.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var nets: MutableList<Element>
    private var wifiManager: WifiManager? = null
    private var wifiList: List<ScanResult>? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startActivity(Intent(this@MainActivity,MainActivity2::class.java))

        binding.fab.setOnClickListener {
            detectWifi()
            /*Snackbar.make(view, "Сканирование...", Snackbar.LENGTH_SHORT).setAction("Action", null).show()*/
        }
    }

    private fun detectWifi() {
        wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager!!.startScan()
        wifiList = wifiManager!!.scanResults
        Log.d("TAG", wifiList.toString())
        nets = mutableListOf()
        for (i in wifiList!!.indices) {
            val item = wifiList!![i].toString()
            val vector_item = item.split(",").toTypedArray()
            val item_essid = vector_item[0]
            val item_capabilities = vector_item[2]
            val item_level = vector_item[3]
            val ssid = item_essid.split(": ").toTypedArray()[1]
            val security = item_capabilities.split(": ").toTypedArray()[1]
            val level = item_level.split(":").toTypedArray()[1]
            nets.add(Element(ssid, security, level))
        }
        val adapterElements = AdapterElements(this)
        val netList: ListView = findViewById<View>(R.id.listItem) as ListView
        netList.adapter = adapterElements
    }

    inner class AdapterElements(var context: Activity) : ArrayAdapter<Any?>(context, R.layout.items, nets.toList()) {

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = context.layoutInflater
            val item: View = inflater.inflate(R.layout.items, null)
            val tvSsid = item.findViewById<View>(R.id.tvSSID) as TextView
            tvSsid.text = nets[position].title
            val tvSecurity = item.findViewById<View>(R.id.tvSecurity) as TextView
            tvSecurity.text = nets[position].security
            val tvLevel = item.findViewById<View>(R.id.tvLevel) as TextView
            val level: String = nets[position].level
            try {
                val i = level.toInt()
                if (i > -50) {
                    tvLevel.text = "Высокий"
                } else if (i <= -50 && i > -80) {
                    tvLevel.text = "Средний"
                } else if (i <= -80) {
                    tvLevel.text = "Низкий"
                }
            } catch (e: NumberFormatException) {
                Log.d("TAG", "Неверный формат строки")
            }
            return item
        }
    }
}