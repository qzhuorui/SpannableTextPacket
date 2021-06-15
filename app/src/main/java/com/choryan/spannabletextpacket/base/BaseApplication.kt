package com.choryan.spannabletextpacket.base

import android.app.Application

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
    }

}