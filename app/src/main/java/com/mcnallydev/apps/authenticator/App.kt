package com.mcnallydev.apps.authenticator

import android.app.Application
import com.tumblr.remember.Remember
import io.realm.Realm
import io.realm.Realm.setDefaultConfiguration
import io.realm.RealmConfiguration



class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Remember.init(applicationContext, packageName)
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder().build()
        Realm.setDefaultConfiguration(realmConfiguration)
    }

}


