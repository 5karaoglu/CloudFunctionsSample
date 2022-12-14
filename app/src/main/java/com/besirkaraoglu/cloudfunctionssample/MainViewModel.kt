package com.besirkaraoglu.cloudfunctionssample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val firestoreRepository: FirestoreRepository
): ViewModel(){

    private val _userId = MutableLiveData<String>()
    val userId get() = _userId

    fun addUser(name: String, photoUrl: String) = viewModelScope.launch(Dispatchers.IO) {
        val uid = UUID.randomUUID().toString()
        val user = User(uid, name, photoUrl)
        firestoreRepository.addOrUpdateUser(user)
    }

}