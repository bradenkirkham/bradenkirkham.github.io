package com.example.bmr_app.userView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.bmr_app.R
import com.example.bmr_app.userData.UserDataClass

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>(){


    private var userList = emptyList<UserDataClass>()


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_view,parent,false)

        return MyViewHolder(inflatedView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.itemView.findViewById<TextView>(R.id.txtUser_name).text =currentItem.name
        "Age: ${currentItem.age}".also { holder.itemView.findViewById<TextView>(R.id.txtUser_age).text = it }
        "Height: ${currentItem.Height}cm".also { holder.itemView.findViewById<TextView>(R.id.txtUser_Height).text = it }
        "Weight: ${currentItem.weight}kg".also { holder.itemView.findViewById<TextView>(R.id.txtUser_Weight).text = it }
        //image setting trial please work
        holder.itemView.findViewById<ImageView>(R.id.circle_iv).setImageBitmap(currentItem.image)
        holder.itemView.findViewById<TextView>(R.id.txtUser_BMR).text = "BMR: ${currentItem.bmr}"
        holder.itemView.findViewById<TextView>(R.id.txtUser_gender).text = currentItem.gender
        holder.itemView.findViewById<TextView>(R.id.txtUser_recomCal).text = "Rec Cal: ${currentItem.caloriesIntake}"
        holder.itemView.findViewById<TextView>(R.id.txtUser_stepView).text = "Step: ${currentItem.step}"


//when update frag is good to go
//        holder.itemView.findViewById<LinearLayout>(R.id.item_View).setOnClickListener {
//            val action = frag_homeDirections.actionFragHomeToFragProfile(currentItem)
//            holder.itemView.findNavController().navigate(action)
//
//        }







    }
    @SuppressLint("NotifyDataSetChanged")
    fun setData(user:List<UserDataClass>){
        this.userList = user
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = userList.size




}