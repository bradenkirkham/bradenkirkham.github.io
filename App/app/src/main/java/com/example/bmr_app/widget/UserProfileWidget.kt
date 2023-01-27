package com.example.bmr_app.widget

import android.annotation.SuppressLint
import android.widget.SeekBar
import android.widget.TextView

class UserProfileWidget() {

    companion object Seekbar{

        fun SeekBarHelper(seekBarName: SeekBar, min:Int, max:Int, TextViewWant: TextView){
            return (


                    seekBarName.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            (TextViewWant).text = seekBarName.progress.toString()
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                            val startPoint: Int = min
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            val EdndPoint: Int = max
                        }
                    }))

        }


        fun ActivitySeekBarHelper(seekBarName: SeekBar, min:Int, max:Int, TextViewWant: TextView){
            return (

                    seekBarName.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                        @SuppressLint("SetTextI18n")
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                        (TextViewWant).text = seekBarName.progress.toString()
                            when(seekBarName.progress){
                                0->TextViewWant.text ="Sedentary"
                                1->TextViewWant.text ="Mild"
                                2->TextViewWant.text ="Moderate"
                                3->TextViewWant.text ="Heavy"
                                4->TextViewWant.text ="Extreme"
                            }
//                       val TextViewWant= TextViewWant.text.toString()


                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                            val startPoint: Int = min
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            val EdndPoint: Int = max
                        }
                    }))

        }


        fun calculateBMR(gender: String, weight: Double, height: Double, age: Int): Double{
//height in cm, weight in kg
            val bmr: Double = when (gender) {
                "Male" -> ((weight * 10) + (6.25 * height) - (5 * age) + 5)
                "Female" -> ((weight * 10) + (6.25 * height) - (5 * age) - 161)
                else -> {
                    return 0.0
                }
            }

            return bmr
        }

        fun calDailyCalIntake(bmr:Double, activiyLevel:String): Double {

            var recommenedCalIntake = 0.0

            when(activiyLevel){

                "Sedentary" -> recommenedCalIntake = bmr*1.2
                "Mild" -> recommenedCalIntake = bmr*1.375
                "Moderate" -> recommenedCalIntake = bmr*1.55
                "Heavy"->recommenedCalIntake=bmr*1.725
                "Extreme"->recommenedCalIntake=bmr*1.9

            }

            return recommenedCalIntake
        }









    }




}