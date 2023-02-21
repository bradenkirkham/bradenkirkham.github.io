package com.example.bmr_app.weatherData

import androidx.lifecycle.*
import android.app.Application
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.coroutineContext

class weatherApplication : Application() {
    //global scope for all coroutines? meaning the ones that we initiate after this?
    private val applicationScope = CoroutineScope(SupervisorJob())

    //give scope and weatherapplication context to database
    public fun getDatabase() : weatherDatabase { return weatherDatabase.getDatabase(this, applicationScope) }

    //give database dao and scope to repository singleton.
    val repository by lazy { weatherRepository.getInstance(this, applicationScope) }
}