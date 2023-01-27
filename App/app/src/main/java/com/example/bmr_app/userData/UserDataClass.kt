package com.example.bmr_app.userData

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user_table")
data class UserDataClass(
    //primary key creation
    @PrimaryKey(autoGenerate = true) var ID:Int,
    //standard info that we will collect from users. I will add bitmap info later for the picture
    var name:String,
    var age: Int,
    var Height:Double,
    var weight:Double,
    var bmr:Double,
    var gender:String,
    var image: Bitmap,
    var caloriesIntake:Double,
//    val starTimeMilli:Long=System.currentTimeMillis()
    var step: Int
): Parcelable
