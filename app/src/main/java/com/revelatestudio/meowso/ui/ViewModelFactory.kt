package com.revelatestudio.meowso.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.revelatestudio.meowso.data.repository.AppRepository
import com.revelatestudio.meowso.di.Injection
import com.revelatestudio.meowso.ui.auth.signup.SignUpViewModel

class ViewModelFactory(private val repository: AppRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(firebaseDb: FirebaseFirestore): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(firebaseDb))
            }
    }

}