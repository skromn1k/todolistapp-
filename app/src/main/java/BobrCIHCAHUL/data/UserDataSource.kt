package BobrCIHCAHUL.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object UserDataSource {

    fun getUserName(): String {
        return Firebase.auth.currentUser?.displayName ?: "Гость"
    }

    fun getUserPicture(): String {
        return Firebase.auth.currentUser?.photoUrl?.toString() ?: ""
    }

}
