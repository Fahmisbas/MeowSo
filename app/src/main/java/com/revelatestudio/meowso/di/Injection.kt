package com.revelatestudio.meowso.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.revelatestudio.meowso.data.repository.AppRepository


object Injection {
    fun provideRepository(): AppRepository {
        val firebaseDb = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseStorage = FirebaseStorage.getInstance("gs://meowso-9c708.appspot.com")
        return AppRepository.getInstance(firebaseDb, firebaseAuth, firebaseStorage)
    }
}