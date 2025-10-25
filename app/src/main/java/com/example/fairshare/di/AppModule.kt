package com.example.fairshare.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provide Firestore instance
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    // Provide FirebaseAuth instance
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

}
