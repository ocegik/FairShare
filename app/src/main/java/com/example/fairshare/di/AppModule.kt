package com.example.fairshare.di

import com.example.fairshare.data.firebase.FirestoreService
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

    @Provides
    @Singleton
    fun provideFirestoreService(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): FirestoreService = FirestoreService(firestore, auth)

    @Provides
    @Singleton
    fun provideExpenseRepository(
        firestoreService: FirestoreService
    ): ExpenseRepository = ExpenseRepository(firestoreService)

    @Provides
    @Singleton
    fun provideGroupRepository(
        firestoreService: FirestoreService
    ): GroupRepository = GroupRepository(firestoreService)

    @Provides
    @Singleton
    fun provideUserRepository(
        firestoreService: FirestoreService
    ): UserRepository = UserRepository(firestoreService)

}
