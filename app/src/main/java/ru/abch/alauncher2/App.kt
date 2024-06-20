package ru.abch.alauncher2

import android.app.Application
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import com.fazecast.jSerialComm.SerialPort
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

class App : Application() {
    var TAG = "App"
    var speed = 0
    var channel = 0
    var volume = 0
    private var mute = false
    val defaultFreqs = ArrayList(
        mutableListOf(
            9910,
            9950,
            10000,
            10040,
            10080,
            10200,
            10290,
            10350,
            10450,
            10590,
            10630
        )
    )
    var freqsList: ArrayList<Int> = ArrayList()
    private var sp: SharedPreferences? = null
    public var mcuSerialPort : SerialPort? = null
    lateinit var mcuDevice : String
    lateinit var mcuSpeed : String
    lateinit var mcuDelay : String
    override fun onCreate() {
        super.onCreate()
        instance = this
        Companion.packageName = packageName
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
//        mcuDevice = Utilities.getprop("ro.mcu.device")
        mcuDevice = "/dev/ttyACM1"
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
        for( serialPort in SerialPort.getCommPorts()) {
            Log.d(TAG, "Serial port " + serialPort.portDescription + " " + serialPort.systemPortPath)
            if(serialPort.systemPortPath.equals(mcuDevice)) mcuSerialPort = serialPort
        }
        Log.d(TAG, "MCU = " + Config.getMCUDevice() + " Speed = " + Config.getMCUSpeed())
        sp = sharedPreferences
        volume = sharedPreferences.getInt("volume", 6)
        channel = sharedPreferences.getInt("channel", 10000)
        val freqsJSONArray = sharedPreferences.getString("freqs",null)
        if(freqsJSONArray != null) {
            val gson = Gson()
            val type = object : TypeToken<java.util.ArrayList<Int?>?>() {}.type
            freqsList = gson.fromJson(freqsJSONArray, type)
            Log.d(TAG, "freqs " + freqsJSONArray + "\n size " + freqsList.size)
        }
        val script = File(Config.script)
        if (script.exists()) {
            Log.d(TAG, "Execute " + Config.script)
            try {
                Runtime.getRuntime().exec(arrayOf(Config.script))
            } catch (i: IOException) {
                Log.e(TAG, i.message.orEmpty())
            }
        }

        val mcuFile = File(mcuDevice)
        if(!mcuFile.canWrite()) {
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
                        Log.e(TAG, i.message.orEmpty())
                    }
                }
            }, (delay * 1000).toLong())
        } else {
            Log.d(TAG, "MCU device writeable " + mcuFile.absolutePath)
        }
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

    fun saveFreqsList(list: java.util.ArrayList<Int>) {
        val editor = sp!!.edit()
        val key = "freqs"
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(key, json)
        editor.commit()
        freqsList = list
        Log.d(TAG, "Saved freqs list size " + list.size)
    }
    fun getMute(): Boolean {
        return mute
    }
    fun setMute(state: Boolean) {
        mute = state
        sp!!.edit().putBoolean("mute", mute).apply()
    }
    fun storeVolume(vol : Int) {
        volume = vol
        sharedPreferences.edit().putInt("volume", vol).commit()
    }
    fun storeChannel(ch : Int) {
        channel = ch
        sharedPreferences.edit().putInt("channel", ch).commit()
    }
    fun getFreqs() = if(freqsList.isEmpty()) {
        defaultFreqs
    } else {
        freqsList
    }
}