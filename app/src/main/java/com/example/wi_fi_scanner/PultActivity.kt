package com.example.wi_fi_scanner

import android.graphics.Color.red
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.wi_fi_scanner.databinding.ActivityPultBinding
import com.example.wi_fi_scanner.databinding.ActivityWiFiBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PultActivity : AppCompatActivity(R.layout.activity_pult) {

    private val viewBinding by viewBinding(ActivityPultBinding::bind)

    private val listColor = listOf(
        R.color.green,
        R.color.red,
        R.color.white
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            while (true)
                listColor.forEach {
                    delay(1000)
                    viewBinding.ivCoolingMode.setColorFilter(ContextCompat.getColor(this@PultActivity, it), android.graphics.PorterDuff.Mode.MULTIPLY)
                    viewBinding.ivHeatingMode.setColorFilter(ContextCompat.getColor(this@PultActivity, it), android.graphics.PorterDuff.Mode.MULTIPLY)
                    viewBinding.ivHumidifierOperation.setColorFilter(ContextCompat.getColor(this@PultActivity, it), android.graphics.PorterDuff.Mode.MULTIPLY)
                    viewBinding.ivDehumidificationMode.setColorFilter(ContextCompat.getColor(this@PultActivity, it), android.graphics.PorterDuff.Mode.MULTIPLY)
                    viewBinding.ivTypeTemperature.setColorFilter(ContextCompat.getColor(this@PultActivity, it), android.graphics.PorterDuff.Mode.MULTIPLY)
                    viewBinding.ivOnOff.setColorFilter(ContextCompat.getColor(this@PultActivity, it), android.graphics.PorterDuff.Mode.MULTIPLY)
                    viewBinding.ivScheduledWork.setColorFilter(ContextCompat.getColor(this@PultActivity, it), android.graphics.PorterDuff.Mode.MULTIPLY)
                    viewBinding.ivRefusal.setColorFilter(ContextCompat.getColor(this@PultActivity, it), android.graphics.PorterDuff.Mode.MULTIPLY)

                    viewBinding.ivTemperatureExt.setColorFilter(ContextCompat.getColor(this@PultActivity, it), android.graphics.PorterDuff.Mode.MULTIPLY)
                    viewBinding.ivTemperatureRwt.setColorFilter(ContextCompat.getColor(this@PultActivity, it), android.graphics.PorterDuff.Mode.MULTIPLY)
                    viewBinding.ivTemperatureAirRet.setColorFilter(ContextCompat.getColor(this@PultActivity, it), android.graphics.PorterDuff.Mode.MULTIPLY)
                    viewBinding.ivTemperatureAirEat.setColorFilter(ContextCompat.getColor(this@PultActivity, it), android.graphics.PorterDuff.Mode.MULTIPLY)
                    delay(1000)
                }
        }


    }
}