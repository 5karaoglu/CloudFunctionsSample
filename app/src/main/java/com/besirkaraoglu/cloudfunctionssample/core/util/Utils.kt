package com.besirkaraoglu.cloudfunctionssample.core.util

fun getMessageId(uid1: String, uid2: String):String{
    return if (uid1 > uid2) "$uid1$uid2" else "$uid2$uid1"
}

const val image1 = "https://www.clipartmax.com/png/middle/277-2772086_user-female-skin-type-4-icon-icon.png"
const val image2 = "https://www.clipartmax.com/png/middle/258-2582267_circled-user-male-skin-type-1-2-icon-male-user-icon.png"