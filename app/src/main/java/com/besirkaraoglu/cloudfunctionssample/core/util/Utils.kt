package com.besirkaraoglu.cloudfunctionssample.core.util

fun getMessageId(uid1: String, uid2: String):String{
    return if (uid1 > uid2) "$uid1$uid2" else "$uid2$uid1"
}