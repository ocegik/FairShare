package com.example.fairshare.di

import com.example.fairshare.data.firebase.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

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

    // Provide your repository
    @Provides
    @Singleton
    fun provideFirestoreRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): FirestoreRepository = FirestoreRepository(firestore, auth)
}
