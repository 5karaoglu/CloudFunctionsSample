package com.besirkaraoglu.cloudfunctionssample.data

import android.util.Log
import com.besirkaraoglu.cloudfunctionssample.core.SharedPreferences
import com.besirkaraoglu.cloudfunctionssample.core.util.FirebaseConstants
import com.besirkaraoglu.cloudfunctionssample.core.util.Resource
import com.besirkaraoglu.cloudfunctionssample.core.util.getMessageId
import com.besirkaraoglu.cloudfunctionssample.model.Message
import com.besirkaraoglu.cloudfunctionssample.model.Token
import com.besirkaraoglu.cloudfunctionssample.model.User
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirestoreRepository
@Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences
) {
    val TAG = "FirestoreRepository"
    private val userCollection = firestore.collection(FirebaseConstants.FIRESTORE_COLLECTION_USERS)
    private val tokenCollection = firestore.collection(FirebaseConstants.FIRESTORE_COLLECTION_TOKENS)
    private val messageCollection = firestore.collection(FirebaseConstants.FIRESTORE_COLLECTION_MESSAGES)


    suspend fun addOrUpdateUser(user: User) = flow<Resource<Any?>> {
        try {
            emit(Resource.Loading())
            userCollection.document(user.uid!!).set(user).await()
            val token = Firebase.messaging.token.await()
            tokenCollection.document(user.uid!!).set(Token(token)).await()
            emit(Resource.Success(null))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Process failed!"))
            e.printStackTrace()
        }
    }

    suspend fun addToken() = flow<Resource<Any?>> {
        try {
            emit(Resource.Loading())
            val token = Firebase.messaging.token.await()
            tokenCollection.document(sharedPreferences.getUid()!!).set(Token(token)).await()
            emit(Resource.Success(null))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Process failed!"))
        }
    }

    suspend fun updateToken(token:String) = flow<Resource<Any?>> {
        try {
            emit(Resource.Loading())
            tokenCollection.document(sharedPreferences.getUid()!!).set(Token(token)).await()
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

    suspend fun getChatUsers(userId: String, receiverId: String) = flow<Resource<Pair<User,User>>> {
        try {
            emit(Resource.Loading())
            val cUser = userCollection.document(userId).get().await()
            val rUser = userCollection.document(receiverId).get().await()
            val pair = Pair(cUser.toObject(User::class.java)!!
                ,rUser.toObject(User::class.java)!!)

            emit(Resource.Success(pair))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Process failed!"))
        }
    }

    fun addMessagesSnapshot(receiverId: String) = flow<Resource<List<Message>>> {
        withContext(Dispatchers.IO) {
        }
    }

    suspend fun getMessages(receiverId: String) = callbackFlow<Resource<List<Message>>> {
        trySend(Resource.Loading())
        val messageId = getMessageId(sharedPreferences.getUid()!!,receiverId)
        val snapshotListener =
            EventListener<QuerySnapshot> { value, error ->
                if (error != null){
                    trySend(Resource.Error(error.message ?: "Process failed!"))
                }else{
                    val list = mutableListOf<Message>()
                    val result = value?.documents
                    Log.d(TAG, "getMessages: $result")
                    result?.forEach {
                        Log.d(TAG, "getMessages: $it")
                        it.toObject(Message::class.java)?.let { it1 -> list.add(it1) }
                    }
                    trySend(Resource.Success(list))
                }

            }
        val registration = messageCollection.document(messageId).collection("messages").addSnapshotListener(snapshotListener)
        awaitClose { registration.remove() }
    }

    suspend fun sendMessage(receiverId: String, content: String) = callbackFlow<Resource<Any?>> {
        try {
            trySend(Resource.Loading())
            val messageId = getMessageId(sharedPreferences.getUid()!!,receiverId)
            val message = Message(System.currentTimeMillis().toString(),sharedPreferences.getUid()!!,receiverId,content)
            messageCollection.document(messageId)
                .collection("messages").document().set(message)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Log.d(TAG, "sendMessage: Successful.")
                        trySend(Resource.Success(null))
                    }else{
                        Log.e(TAG, "sendMessage: Failed!")
                       trySend(Resource.Error(it.exception!!.message!!))
                    }
                }
        }catch (e: Exception){
            trySend(Resource.Error(e.message ?: "GetMessages failed!"))
        }

        awaitClose {  }
    }
}