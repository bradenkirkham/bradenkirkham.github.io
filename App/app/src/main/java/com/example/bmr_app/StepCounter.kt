package com.example.bmr_app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bmr_app.viewModels.LocationViewModel
import java.util.*
import kotlin.math.sqrt

class StepCounter : Fragment(),TextToSpeech.OnInitListener {


    private val viewModel by activityViewModels<LocationViewModel>()

    //speech feedback
    private var tts: TextToSpeech? = null

    //sensor related variables
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var counter = 0



    //step related variables
    private var stepping = false
    private var initialStep = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accelerationSensorInitializer(activity as MainActivity,10f,SensorManager.GRAVITY_EARTH,SensorManager.GRAVITY_EARTH)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_step_counter, container, false)

        textToSpeechInitializer(requireContext())

        returnStepCounting()

//        text.text = SensorWidget.returnStepCounting()

        return view
    }
    override fun onResume() {
        sensorListeningResume()
        super.onResume()
    }
    override fun onPause() {
        sensorListeningTermination()
        super.onPause()
    }
    override fun onDestroy() {
        voiceFeedbackTermination()
        super.onDestroy()
    }

    //functions
    //------Initial accelerationSensor setup
    private fun accelerationSensorInitializer(
        activity: MainActivity, accelerationValue: Float, currentAcceleration_value: Float,
        lastAcceleration_value: Float
    ) {
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acceleration = accelerationValue
        currentAcceleration = currentAcceleration_value
        lastAcceleration = lastAcceleration_value
    }

    //-----------------------sensor event Listener and related helper function---------------------
    private val multi_sensor: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            val sensor: Sensor = event!!.sensor

            when(sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> accelerometerSensor(event)
                Sensor.TYPE_STEP_DETECTOR -> stepDetectorSensor(event)

            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    }

    private fun accelerometerSensor(event: SensorEvent?){
        event!!.sensor
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        lastAcceleration = currentAcceleration
        currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta: Float = currentAcceleration - lastAcceleration
        acceleration = acceleration * 0.9f + delta
        if (acceleration > 30) {
            counter++
            Log.e("shake event", "counter ${counter}")
            if (counter == 2) {
                speakWord("Diego Hi You are awesome!")
                startStepping()
            }
            if (counter == 4) {
                speakWord("end, step counter")
                endStepping()
                counter = 0
            }

        }
    }
    private fun stepDetectorSensor(event: SensorEvent?){
        event!!.sensor
        stepping = true
        val text = view?.findViewById<TextView>(R.id.stepcountView)
        if (stepping) {
            initialStep++

            if (text != null) {
                text.text = initialStep.toString()
            }
//            Log.e("step counting", initialStep.toString())
//            viewModel = ViewModelProvider(this)[LocationViewModel::class.java]
            viewModel.setStep(initialStep.toString())

        }

    }

    //sensor termination
    internal fun sensorListeningTermination() {
        stepping = false
        sensorManager!!.unregisterListener(multi_sensor)
    }

    //sensor Resume
    internal fun sensorListeningResume() {
        stepping = true
        sensorManager?.registerListener(
            multi_sensor,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    //--------------------stepping sensor activation and termination--------------------------

    private fun startStepping() {
        val stepCountingSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        sensorManager?.registerListener(
            multi_sensor,
            stepCountingSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }
    private fun endStepping() {
        stepping = false
        val stepCountingSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        sensorManager!!.unregisterListener(multi_sensor, stepCountingSensor)
    }
    internal fun returnStepCounting():String{
//        viewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        viewModel.step.observe(viewLifecycleOwner){
            viewModel.getStep_()?.let { Log.e("step counting from return", it) }
        }


        return initialStep.toString()

    }

    //--------------------voice feedback-----------------

    private fun speakWord(word: String){
        tts!!.speak(word, TextToSpeech.QUEUE_FLUSH, null, "")
    }
    private fun voiceFeedbackInitializer(status:Int){
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.ENGLISH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            }
        }
    }
    internal fun textToSpeechInitializer(context: Context){
        tts = TextToSpeech(context,this)
    }
    internal fun voiceFeedbackTermination(){
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
    }
    override fun onInit(status: Int) {
        voiceFeedbackInitializer(status)
    }



}