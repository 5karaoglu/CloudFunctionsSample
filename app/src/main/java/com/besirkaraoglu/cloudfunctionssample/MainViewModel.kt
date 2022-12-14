package com.besirkaraoglu.cloudfunctionssample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.besirkaraoglu.cloudfunctionssample.core.SharedPreferences
import com.besirkaraoglu.cloudfunctionssample.core.util.Resource
import com.besirkaraoglu.cloudfunctionssample.data.FirestoreRepository
import com.besirkaraoglu.cloudfunctionssample.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val sharedPreferences: SharedPreferences
): ViewModel(){

    private val _userId = MutableLiveData<String>()
    val userId get() = _userId

    private val _users = MutableLiveData<Resource<List<User>>>()
    val users get() = _users

    fun setCurrentUserId(uid: String){
        _userId.value = uid
    }

    fun addUser(name: String, photoUrl: String?) = viewModelScope.launch(Dispatchers.IO) {
        val uid = UUID.randomUUID().toString()
        sharedPreferences.setUid(uid)
        val user = User(uid, name, photoUrl)
        firestoreRepository.addOrUpdateUser(user)
    }

    fun isUserRegistered(): Boolean =
        sharedPreferences.getUid() != null

    fun getUsers() = viewModelScope.launch(Dispatchers.IO) {
        firestoreRepository.getUsers().collect{
            _users.postValue(it)
        }
    }
}