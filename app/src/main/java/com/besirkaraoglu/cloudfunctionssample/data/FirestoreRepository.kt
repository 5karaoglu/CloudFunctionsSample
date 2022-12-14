package com.besirkaraoglu.cloudfunctionssample.data

import android.util.Log
import com.besirkaraoglu.cloudfunctionssample.core.util.FirebaseConstants
import com.besirkaraoglu.cloudfunctionssample.core.util.Resource
import com.besirkaraoglu.cloudfunctionssample.model.Message
import com.besirkaraoglu.cloudfunctionssample.model.Token
import com.besirkaraoglu.cloudfunctionssample.model.User
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirestoreRepository
@Inject constructor(
    private val firestore: FirebaseFirestore
) {
    val TAG = "FirestoreRepository"
    private val userCollection = firestore.collection(FirebaseConstants.FIRESTORE_COLLECTION_USERS)
    private val tokenCollection = firestore.collection(FirebaseConstants.FIRESTORE_COLLECTION_TOKENS)


    suspend fun addOrUpdateUser(user: User) = flow<Resource<Any?>> {
        try {
            emit(Resource.Loading())
            userCollection.document(user.uid!!).set(user).await()
            emit(Resource.Success(null))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Process failed!"))
        }

    }

    suspend fun addOrUpdateToken(uid: String, token: String) = flow<Resource<Any?>> {
        try {
            emit(Resource.Loading())
            tokenCollection.document(uid).set(Token(token)).await()
            emit(Resource.Success(null))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Process failed!"))
        }
    }

    suspend fun getUsers() = callbackFlow<Resource<List<User>>> {
        trySend(Resource.Loading())
        val snapshotListener =
            EventListener<QuerySnapshot> { value, error ->
                if (error != null){
                   trySend(Resource.Error(error.message ?: "Process failed!"))
                }else{
                    val list = mutableListOf<User>()
                    val result = value?.documents
                    result?.forEach {
                        Log.d(TAG, "getUsers: $it")
                        it.toObject(User::class.java)?.let { it1 -> list.add(it1) }
                    }
                    trySend(Resource.Success(list))
                }

            }
        val registration = userCollection.addSnapshotListener(snapshotListener)
        awaitClose { registration.remove() }
    }

    fun addMessagesSnapshot(receiverId: String) = flow<Resource<List<Message>>> {
        withContext(Dispatchers.IO) {
        }
    }
}