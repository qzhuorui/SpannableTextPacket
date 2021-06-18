package com.choryan.spannabletextpacket.base

import android.app.Application
import com.choryan.spannabletextpacket.util.FileUtil
import com.choryan.spannabletextpacket.util.ZipUtil
import java.io.File
import java.io.IOException

/**
 * @author: ChoRyan Quan
 * @date: 6/15/21
 */
class BaseApplication : Application() {

    companion object {
        lateinit var instance: BaseApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (!File(FileUtil.getRootPath().toString() + "e.e").exists()) {
            try {
                ZipUtil.decompress(assets.open("res.zip"), FileUtil.getRootPath(), false)
                File(FileUtil.getRootPath().toString() + "e.e").createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}