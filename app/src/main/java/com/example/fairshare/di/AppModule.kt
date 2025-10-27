package com.example.fairshare.di

import com.example.fairshare.data.firebase.FirebaseAuthService
import com.example.fairshare.data.firebase.FirestoreService
import com.example.fairshare.repository.AuthRepository
import com.example.fairshare.repository.ExpenseRepository
import com.example.fairshare.repository.GroupRepository
import com.example.fairshare.repository.UserRepository
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

    // Provide Firebase Firestore instance
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    // Provide Firebase Auth instance
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    // Provide FirestoreService
    @Provides
    @Singleton
    fun provideFirestoreService(
        firestore: FirebaseFirestore
    ): FirestoreService = FirestoreService(firestore)

    // Provide ExpenseRepository
    @Provides
    @Singleton
    fun provideExpenseRepository(
        firestoreService: FirestoreService
    ): ExpenseRepository = ExpenseRepository(firestoreService)

    // Provide GroupRepository
    @Provides
    @Singleton
    fun provideGroupRepository(
        firestoreService: FirestoreService
    ): GroupRepository = GroupRepository(firestoreService)

    // Provide UserRepository
    @Provides
    @Singleton
    fun provideUserRepository(
        firestoreService: FirestoreService
    ): UserRepository = UserRepository(firestoreService)

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuthService: FirebaseAuthService,
        userRepository: UserRepository
    ): AuthRepository = AuthRepository(firebaseAuthService, userRepository)
}
