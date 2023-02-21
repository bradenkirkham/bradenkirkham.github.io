package com.example.bmr_app.userData

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDAO {

    //addUser method will help to insert user data input
    //user is passing from my userDataClass entity
    //when there is null value on the input(a.k.a "onConflict=" situation),
    //my strategy to handle that conflict is just add null as is
    //I will handle more smart way once initial set-up is good
    //add suspend to use coroutine later
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: UserDataClass)

    @Update
    suspend fun updateUser(user: UserDataClass)
    @Delete
    suspend fun deleteUser(user: UserDataClass)

    @Query("DELETE FROM user_table")
    suspend fun deleteAlluserDate()


    //Basically it will read all data from user_table
    @Query("SELECT * FROM user_table ORDER BY ID DESC")
    fun readAllUserData(): LiveData<List<UserDataClass>>


    @Query("SELECT * FROM user_table ORDER BY ID DESC LIMIT 1")
    fun readLatestUser(): LiveData<UserDataClass>

}