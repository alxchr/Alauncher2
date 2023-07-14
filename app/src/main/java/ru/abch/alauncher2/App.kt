package ru.abch.alauncher2

import android.app.Application
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import com.fazecast.jSerialComm.SerialPort

import java.io.File
import java.io.IOException

class App : Application() {
    val TAG = this.javaClass.simpleName
    private var sp: SharedPreferences? = null

    lateinit var mcuDevice : String
    lateinit var mcuSpeed : String
    lateinit var mcuDelay : String
    override fun onCreate() {
        super.onCreate()
        instance = this
        Companion.packageName = packageName

        for( serialPort in SerialPort.getCommPorts()) {
            Log.d(TAG, "Serial port " + serialPort.portDescription)
        }


        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        mcuDevice = Utilities.getprop("ro.mcu.device")
        if (!mcuDevice.isEmpty()) {
            if (!mcuDevice.startsWith("/dev/")) {
                mcuDevice = "/dev/" + mcuDevice
            }
            Config.setMCUDevice(mcuDevice)
            mcuSpeed = Utilities.getprop("ro.mcu.speed")
            if (!mcuSpeed.isEmpty()) {
                Config.setMCUSpeed(mcuSpeed.toInt())
            }
            mcuDelay = Utilities.getprop("ro.mcu.delay")
            if (!mcuDelay.isEmpty()) {
                delay = mcuDelay.toInt()
            }
        }
        Log.d(TAG, "MCU = " + Config.getMCUDevice() + " Speed = " + Config.getMCUSpeed())
        sp = sharedPreferences
        Config.radioType = sp!!.getString("tuner", "0")!!.toInt()
        val script = File(Config.script)
        if (script.exists()) {
            Log.d(TAG, "Execute " + Config.script)
            try {
                Runtime.getRuntime().exec(arrayOf(Config.script))
            } catch (i: IOException) {
                Log.e(TAG, i.message.orEmpty())
            }
        }
        val handler = Handler()
        handler.postDelayed({
            val cmd = "chmod 666 " + Config.getMCUDevice()
            if (!mcuDevice.isEmpty()) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Runtime.getRuntime().exec(arrayOf(cmd))
                    } else {
                        Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
                    }
                    //                    Runtime.getRuntime().exec(new String[]{"chmod","666",Config.getMCUDevice()});
                } catch (i: IOException) {
                    Log.e(TAG,  i.message.orEmpty())
                }
            }
        }, (delay * 1000).toLong())

    }

    companion object {
        @JvmField
        var instance: App? = null
        var packageName: String? = null
        var delay = 5
        var audioManager: AudioManager? = null
        @JvmStatic
        fun get(): App? {
            return instance
        }
        val sharedPreferences: SharedPreferences
            get() = PreferenceManager.getDefaultSharedPreferences(get())
    }
}