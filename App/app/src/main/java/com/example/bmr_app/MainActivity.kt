package com.example.bmr_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.work.*
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import com.example.bmr_app.databinding.ActivityMainBinding
import com.example.bmr_app.userView.FragHome
import com.example.bmr_app.userView.FragProfile
import com.example.bmr_app.viewModels.LocationViewModel
import com.google.android.gms.location.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


import kotlin.concurrent.schedule


//create frag variable
private const val homeTag = "home_Fragment"
private const val profileTag = "profile_Fragment"
private const val stepCountingTag = "StepCounter_fragment"
private const val mapTag = "map_fragment"
private const val weatherTag = "weather_fragment"


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel by viewModels<LocationViewModel>()



    //location permission variables
    val TAG: String = "로그"
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        -------------------------for aws -------------------------------
        try{
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(applicationContext)

            Log.i("amplify", "initialized amplify")

            Amplify.Auth.signInWithWebUI(
                this,
                {result: AuthSignInResult -> Log.i("AuthQuickstart", result.toString())},
                {error: AuthException -> Log.i("AuthQuickStart", error.toString())}
            )

        }catch (error: AmplifyException){
            Log.e("amplify error", "Could not initialize amplify", error)
        }

        val myWorkRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<UploadWorker>(1, TimeUnit.MINUTES).build()

        WorkManager.getInstance(applicationContext).enqueue(myWorkRequest)

//        WorkManager.getInstance(applicationContext).get


//        -------------------------for location permission----------------
        // LocationRequest() deprecated 되서 아래 방식으로 LocationRequest 객체 생성
        // mLocationRequest = LocationRequest() is deprecated
        mLocationRequest =  LocationRequest.create().apply {
            interval = 2000 // 업데이트 간격 단위(밀리초)
            fastestInterval = 1000 // 가장 빠른 업데이트 간격 단위(밀리초)
            Priority.PRIORITY_HIGH_ACCURACY // 정확성
            maxWaitTime= 2000 // 위치 갱신 요청 최대 대기 시간 (밀리초)
        }

//
        if(checkPermissionForLocation(this)){
            startLocationUpdates()
        }


//
//        Timer().schedule(9000){
//            Log.d("Alert","stoplocationUpdate Timer initiated")
//            stoplocationUpdates()
//        }



        //default fragment view when the app is initialized
        setFragment(homeTag, FragHome())

        binding.navigationView.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.home_menu->setFragment(homeTag, FragHome())
                R.id.profile_menu -> setFragment(profileTag, FragProfile())
                R.id.step_menu ->setFragment(stepCountingTag,StepCounter())
                R.id.weather_menu -> setFragment(weatherTag,frag_weather())
                R.id.map_menu -> setFragment(mapTag, frag_map())
//                R.id.map_menu -> startActivity(Intent(this,randomActivity::class.java))
            }
            true
        }

    }

    inner class UploadWorker (appContext: Context, workerParameters: WorkerParameters):
        Worker(appContext, workerParameters){
        override fun doWork(): Result {
            AWSHelper.uploadFile()
            return Result.success()
        }

    }


//    private fun setFragment(tag: String, fragment: Fragment) {
////        val manager: FragmentManager = supportFragmentManager
//        val fragTransaction = supportFragmentManager.beginTransaction()
////        if(manager.findFragmentByTag(tag) == null){
////            fragTransaction.add(R.id.mainFrameLayout,fragment,tag)
////        }
//
//        val fragToSet = fragment
//
//        fragTransaction.replace(R.id.mainFrameLayout, fragToSet)
//
//        fragTransaction.addToBackStack(null)
//
//        fragTransaction.commit()
//    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()
        if(manager.findFragmentByTag(tag) == null){
            //high chance to have error from here
            fragTransaction.add(R.id.nav_host_fragment_container,fragment,tag)
        }

        val home = manager.findFragmentByTag(homeTag)
        val profile = manager.findFragmentByTag(profileTag)
        val stepC = manager.findFragmentByTag(stepCountingTag)
        val map = manager.findFragmentByTag(mapTag)
        val weather = manager.findFragmentByTag(weatherTag)
        //This is ugly I need to make it better later 10 ifs
        if(home != null){
            fragTransaction.hide(home)
        }
        if(profile != null){
            fragTransaction.hide(profile)
        }
        if(stepC != null){
            fragTransaction.hide(stepC)
        }
        if(map != null){
            fragTransaction.hide(map)
        }
        if(weather != null){
            fragTransaction.hide(weather)
        }
        if(tag == homeTag){
            if(home != null){
                fragTransaction.show(home)
            }
        }
        if(tag == profileTag){
            if(profile != null){
                fragTransaction.show(profile)
            }
        }
        if(tag == stepCountingTag){
            if(stepC != null){
                fragTransaction.show(stepC)
            }
        }
        if(tag == mapTag){
            if(map != null){
                fragTransaction.show(map)
            }
        }
        if(tag == weatherTag){
            if(weather != null){
                fragTransaction.show(weather)
            }
        }
        fragTransaction.commitAllowingStateLoss()

    }



//    -----------------location permission--------------------

    // 시스템으로 부터 위치 정보를 콜백으로 받음: Receive Location Information as CallBack from system
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.d(TAG, "onLocationResult()")
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달 : distribute received location info to onLocationChanged()
            locationResult.lastLocation
            locationResult.lastLocation?.let { onLocationChanged(it) }
        }
    }


    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates()")

        //FusedLocationProviderClient의 인스턴스를 생성. Create the instance of FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "startLocationUpdates() 두 위치 권한중 하나라도 없는 경우 ")
            return
        }
        Log.d(TAG, "startLocationUpdates() 위치 권한이 하나라도 존재하는 경우")
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행; excute method that request periodic update of location
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청합니다.
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }




    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
//    method that update the location info to the monitor
    fun onLocationChanged(location: Location) {
        Log.d(TAG, "onLocationChanged()")
        mLastLocation = location
        val date: Date = Calendar.getInstance().time
        val simpleDateFormat = SimpleDateFormat("hh:mm:ss a")

        //I don't think I need to translate those comments in English...
        Log.d(TAG,"Updated at : " + simpleDateFormat.format(date) ) // 갱신된 날짜
        Log.d(TAG,"LATITUDE : " + mLastLocation.latitude )// 갱신 된 위도
        Log.d(TAG,"LATITUDE : " + mLastLocation.longitude )// 갱신 된 경도


//        dataGetter(mLastLocation.latitude,mLastLocation.longitude)

        viewModel.setlatitude(mLastLocation.latitude)
        viewModel.setlongitude(mLastLocation.longitude)



    }





    // 위치 업데이터를 제거 하는 메서드 : As method title said this is method to stop locaiton update
    private fun stoplocationUpdates() {
        Log.d(TAG, "stoplocationUpdates()")
        // 지정된 위치 결과 리스너에 대한 모든 위치 업데이트를 제거, It will remove all location update.
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }



    // 위치 권한이 있는지 확인하는 메서드
    fun checkPermissionForLocation(context: Context): Boolean {
        Log.d(TAG, "checkPermissionForLocation()")
        // Android 6.0 Marshmallow 이상에서는 지리 확보(위치) 권한에 추가 런타임 권한이 필요합니다.
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkPermissionForLocation() Permission Status : O")
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                Log.d(TAG, "checkPermissionForLocation() Permission Status : X")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACTIVITY_RECOGNITION), REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    //     사용자에게 권한 요청 후 결과에 대한 처리 로직
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult()")
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult() _ 권한 허용 클릭")
                startLocationUpdates()

            } else {
                Log.d(TAG, "onRequestPermissionsResult() _ 권한 허용 거부")
                Toast.makeText(this@MainActivity, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }




}//end line for class