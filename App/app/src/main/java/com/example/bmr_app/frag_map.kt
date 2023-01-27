package com.example.bmr_app

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.example.bmr_app.NPS_API.NationalParkData
import com.example.bmr_app.viewModels.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class frag_map : Fragment(), OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback{

    lateinit var mContext: Context
    private lateinit var mMap: GoogleMap
    private val viewModel by activityViewModels<LocationViewModel>()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity){
            mContext = context
        }
    }

    private lateinit var mView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var rootView = inflater.inflate(R.layout.fragment_frag_map, container, false)

        //place API INITIATION


//        Build the map
        mView = rootView.findViewById(R.id.mapView)
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this)


        return rootView
    }







    override fun onMapReady(googleMap: GoogleMap) {



        loadParkLibrary(googleMap)



        val zoom: Float = 8f
//        val marker = LatLng(37.680492,-113.061722)
        val marker = LatLng(viewModel.returnLatitude()!!,viewModel.returnLogitude()!!)

        val markerOptions = MarkerOptions()
            .position(marker)
            .title("Hello Me!")


        val cameraPosition = CameraPosition.Builder()
            .target(marker)
            .zoom(zoom)
            .build()
        googleMap.addMarker(markerOptions)
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

    }


    fun loadParkLibrary(googleMap: GoogleMap){



        val retrofit = Retrofit.Builder()
            .baseUrl(NationalParkAPI.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NationalParkAPI.NationalParkLoc::class.java)
        service.getPark(NationalParkAPI.API_KEY,686)
            .enqueue(object : Callback<NationalParkData> {
                override fun onResponse(call: Call<NationalParkData>, response: Response<NationalParkData>) {
//                    Toast.makeText(activity,"API IS TRANSFERRING DATA",Toast.LENGTH_LONG).show()
                    val result = response.body()
                    showData(result,googleMap)
                }

                override fun onFailure(call: Call<NationalParkData>, t: Throwable) {
                    Toast.makeText(activity,"Go check your API connection", Toast.LENGTH_LONG).show()
                }

            })
    }

    fun showData(result: NationalParkData?, googleMap: GoogleMap){

        Toast.makeText(activity,"SHOWDATA", Toast.LENGTH_SHORT).show()



        result?.let{

            val latlngbounds = LatLngBounds.Builder()

            for(data in it.data){



                val lati = ParseDouble_(data.latitude)
                val long = ParseDouble_(data.longitude)
                val position = LatLng(lati,long)

//                println("위도: $lati"+" 경도: $long")

                val marker = MarkerOptions().position(position).title(data.name)
                googleMap.addMarker(marker)
                latlngbounds.include(position)

            }



        }//let end


    }//showdata end



    fun ParseDouble_(number:String): Double {
        val zero:Double = 0.0
        if(number.isNotEmpty()){
            return number.toDouble()

        }else{

            return zero
        }

    }


    override fun onStart() {
        super.onStart()
        mView.onStart()
    }
    override fun onStop() {
        super.onStop()
        mView.onStop()
    }
    override fun onResume() {
        super.onResume()
        mView.onResume()
    }
    override fun onPause() {
        super.onPause()
        mView.onPause()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mView.onLowMemory()
    }
    override fun onDestroy() {
        mView.onDestroy()
        super.onDestroy()
    }


}