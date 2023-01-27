package com.example.bmr_app.userData

import androidx.lifecycle.LiveData

class UserRepository (private val userDAO: UserDAO){

    val readAllData: LiveData<List<UserDataClass>> = userDAO.readAllUserData()


    suspend fun addUser(userD: UserDataClass){
        userDAO.addUser(userD)
    }

    suspend fun updateUser(userD: UserDataClass){
        userDAO.updateUser(userD)
    }

    suspend fun deleteUser(userD: UserDataClass){
        userDAO.deleteUser(userD)
    }

    suspend fun deleteAllUser(){
        userDAO.deleteAlluserDate()
    }

    fun readOneUser(){
        userDAO.readLatestUser()
    }



}