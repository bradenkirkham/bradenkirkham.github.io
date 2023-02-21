package com.example.bmr_app.weatherData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [weatherTable::class], version = 1, exportSchema = false)
abstract class weatherDatabase : RoomDatabase() {

    abstract fun weatherDao() : weatherDao

    //singleton pattern again
    companion object{
        @Volatile
        private var mInstance: weatherDatabase? = null
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ):weatherDatabase{
            return mInstance?: createDatabase(context, scope)

        }
        fun createDatabase(context: Context, scope: CoroutineScope) : weatherDatabase{
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, weatherDatabase::class.java, "weather.db"
                ).fallbackToDestructiveMigration().setJournalMode(JournalMode.TRUNCATE)
                    .build() //.addCallback(RoomDatabaseCallback(scope))

                mInstance = instance
                return instance
            }
        }
    }

    //not really sure what this does, looks like it triggers on database creation
    //it calls populateDBTask, which means this is probably to put some initial data
    //in the database on creation, this probably means that we don't need the following two functions
//    private class RoomDatabaseCallback(
//        private val scope: CoroutineScope
//    ): RoomDatabase.Callback() {
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            super.onCreate(db)
//            mInstance?.let{database ->
//                scope.launch(Dispatchers.IO){
//                    populateDbTask(database.weatherDao())
//                }
//            }
//        }
//    }
//
//    //don't know what this does besides initially populate weather data with a specific location?
//    suspend fun populateDbTask (weatherDao: weatherDao){
//        weatherDao.insert(weatherData(
//            weatherData.lat(100.0, 100.0),
//            weatherData.Main(300.0, 15.0, 25.0)
//        ))
//    }
}