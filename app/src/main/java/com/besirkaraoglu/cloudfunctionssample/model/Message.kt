package com.besirkaraoglu.cloudfunctionssample.model

data class Message (
    val uid: String? = null,
    var senderId:String? = null,
    var receiverId:String? = null,
    var content:String? = null
)