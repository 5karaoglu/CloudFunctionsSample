package com.besirkaraoglu.cloudfunctionssample

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.besirkaraoglu.cloudfunctionssample.core.SharedPreferences
import com.besirkaraoglu.cloudfunctionssample.core.util.Resource
import com.besirkaraoglu.cloudfunctionssample.data.FirestoreRepository
import com.besirkaraoglu.cloudfunctionssample.model.Message
import com.besirkaraoglu.cloudfunctionssample.model.User
import com.google.firebase.messaging.ktx.FirebaseMessagingKtxRegistrar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val sharedPreferences: SharedPreferences
): ViewModel(){
    val TAG = "MainViewModel"


    private val _user = MutableLiveData<User>()
    val user get() = _user

    private val _currentUser = MutableLiveData<User>()
    val currentUser get() = _currentUser

    private val _users = MutableLiveData<Resource<List<User>>>()
    val users get() = _users

    fun setCurrentUserId(user: User){
        _user.value = user
    }

    private val _chatUsers = MutableLiveData<Resource<Pair<User,User>>>()
    val chatUsers get() = _chatUsers

    private val _addUserResult = MutableLiveData<Boolean>()
    val addUserResult get() = _addUserResult

    private val _messages = MutableLiveData<Resource<List<Message>>>()
    val messages get() = _messages

    private val _sendMessageResult = MutableLiveData<Resource<Any?>>()
    val sendMessageResult get() = _sendMessageResult

    fun addUser(name: String, photoUrl: String?) = viewModelScope.launch {
        val uid = UUID.randomUUID().toString()
        val user = User(uid, name, photoUrl)
        firestoreRepository.addOrUpdateUser(user).collect {
            when(it){
                is Resource.Empty -> {}
                is Resource.Error -> {
                    Log.e(TAG, "addUser: ${it.message}")
                    _addUserResult.value = false
                }
                is Resource.Loading -> {
                    Log.d(TAG, "addUser: Loading")
                }
                is Resource.Success -> {
                    Log.d(TAG, "addUser: Success")
                    sharedPreferences.setUid(uid)
                    _addUserResult.value = true
                }
            }
        }
    }

    fun isUserRegistered(): Boolean =
        sharedPreferences.getUid() != null

    fun getUsers() = viewModelScope.launch(Dispatchers.IO) {
        firestoreRepository.getUsers().collect{
            _users.postValue(it)
            if (it is Resource.Success){
                for (i in it.data!!){
                    if (i.uid!! == sharedPreferences.getUid()!!)
                        _currentUser.postValue(i)
                }
            }
        }
    }

    fun getChatUsers(userId: String, receiverId: String) = viewModelScope.launch(Dispatchers.IO) {
        firestoreRepository.getChatUsers(userId, receiverId).collect{
            _chatUsers.postValue(it)
        }
    }

    fun getMessages() = viewModelScope.launch(Dispatchers.IO) {
        firestoreRepository.getMessages(user.value!!.uid!!).collect{
            _messages.postValue(it)
        }
    }

    fun sendMessage(receiverId: String, content: String) = viewModelScope.launch {
        firestoreRepository.sendMessage(receiverId, content).collect{
            _sendMessageResult.postValue(it)
        }
    }
}