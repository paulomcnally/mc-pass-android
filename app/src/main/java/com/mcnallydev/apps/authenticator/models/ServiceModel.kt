package com.mcnallydev.apps.authenticator.models

import io.realm.RealmObject

open class ServiceModel : RealmObject() {
    var name: String? = null
    var username: String? = null
}