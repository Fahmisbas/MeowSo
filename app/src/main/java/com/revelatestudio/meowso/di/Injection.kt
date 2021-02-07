package com.revelatestudio.meowso.di

import com.google.firebase.firestore.FirebaseFirestore
import com.revelatestudio.meowso.data.repository.AppRepository


object Injection {
    fun provideRepository(firebaseDb: FirebaseFirestore): AppRepository {
        return AppRepository.getInstance(firebaseDb)
    }
}