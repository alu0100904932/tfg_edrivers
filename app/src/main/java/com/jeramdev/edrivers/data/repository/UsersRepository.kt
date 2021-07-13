package com.jeramdev.edrivers.data.repository

import com.jeramdev.edrivers.data.api.AuthServiceImpl
import com.jeramdev.edrivers.data.api.FirestoreServiceImpl
import com.jeramdev.edrivers.data.model.AchievementModel
import com.jeramdev.edrivers.data.model.UserModel

class UsersRepository {

    private val auth = AuthServiceImpl()
    private val firestore = FirestoreServiceImpl()

    suspend fun logIn(email: String, password: String) =
        auth.logIn(email, password)

    suspend fun signUp(email: String, password: String) =
        auth.signUp(email, password)

    suspend fun addUser(user: UserModel) =
        firestore.addUser(user)

    suspend fun deleteAccount(userEmail: String) {
        auth.deleteAccount()
        firestore.deleteUser(userEmail)
    }

    suspend fun getUser(email: String) =
        firestore.getUser(email)

    suspend fun updateTotalPoints(userEmail:String, points: Int) =
        firestore.updateTotalPoints(userEmail, points)

    suspend fun getTotalPoints(userEmail: String): Int? =
        firestore.getTotalPoints(userEmail)

    suspend fun getRanking(): List<UserModel> =
        firestore.getRanking()

    suspend fun getUserAchievements(userEmail: String): List<AchievementModel> =
        firestore.getUserAchievements(userEmail)

    suspend fun updateRankVisibility(value: Boolean, userEmail: String) =
        firestore.updateRankVisibility(value, userEmail)



}