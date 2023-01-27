package com.example.bmr_app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bmr_app.userData.RoomConverters
import com.example.bmr_app.userData.UserDAO
import com.example.bmr_app.userData.UserDataClass

//the annotation explains how I want to set the database.
//I set false for exportSchema, since I don't want to keep history of version in the memory
//and....I'm not high speed yet to control the version yet.
//right now I have user info only so I will leave this as is however, it will change drastically or subtle way possible(I prefer small change).
//if there is multiple entities, [] -> {} and add DAO
//https://stackoverflow.com/questions/57473172/android-room-one-database-with-multiple-tables
@Database(entities = [UserDataClass::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters ::class)
abstract class DataBase: RoomDatabase() {

    abstract fun userDataDao(): UserDAO




    //everything within companion object will be visible to other class
    //try to make my user database singleton class
    //singleton class? it means I will have ONLY ONE instance of its class
    companion object{
        //volatile = this variable will immediately visible to other thread
        @Volatile
        private var INSTANCE:DataBase?=null


        //assign INSTANCE to variable tempIns
        //if INSTANCE(a.k.a tempIns) is not null in this case, it will return INSTANCE
        //In synchronized block, I create new instance in case INSTANCE is null
        //since it is in the synchronized block, it will be protected from a concurrent execution
        //by multiple thread. Also this block will create an instance of room database so I'm passing
        //the context which are my database and the data and the name of our database
        fun getDatabase(context: Context): DataBase{
            val tempInstance = INSTANCE
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBase::class.java,
                    "user_database"
                ).setJournalMode(JournalMode.TRUNCATE).build()
                INSTANCE = instance
                return instance
            }


        }
    }

}