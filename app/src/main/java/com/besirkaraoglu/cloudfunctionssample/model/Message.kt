package com.besirkaraoglu.cloudfunctionssample.model

data class Message (
    var senderId:String? = null,
    var receiverId:String? = null,
    var content:String? = null
)