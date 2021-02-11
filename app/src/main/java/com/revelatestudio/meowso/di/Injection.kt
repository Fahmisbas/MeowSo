package com.revelatestudio.meowso.di

import com.google.firebase.firestore.FirebaseFirestore
import com.revelatestudio.meowso.data.repository.AppRepository


object Injection {
    fun provideRepository(): AppRepository {
        val firebaseDb = FirebaseFirestore.getInstance()
        return AppRepository.getInstance(firebaseDb)
    }
}