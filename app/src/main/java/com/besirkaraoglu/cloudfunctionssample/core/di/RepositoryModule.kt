package com.besirkaraoglu.cloudfunctionssample.core.di

import com.besirkaraoglu.cloudfunctionssample.data.FirestoreRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFirestoreRepository(): FirestoreRepository =
        FirestoreRepository(Firebase.firestore)
}