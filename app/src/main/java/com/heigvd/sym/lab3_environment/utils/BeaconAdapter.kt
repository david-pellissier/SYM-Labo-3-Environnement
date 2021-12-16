package com.heigvd.sym.lab3_environment.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heigvd.sym.lab3_environment.R

class BeaconAdapter(private val beaconUtilsList: ArrayList<BeaconUtils>, private val context: Context)
    : BaseAdapter() {


    override fun getCount(): Int {
        return beaconUtilsList.count()
    }

    override fun getItem(p0: Int): Any {
        return beaconUtilsList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    public fun addBeacon(beacon : BeaconUtils ){
        this.beaconUtilsList.add(beacon)
        notifyDataSetChanged()
    }


    override fun getView(p0: Int, recycleView: View?, p2: ViewGroup?): View {
       if(recycleView == null){
           LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
       }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val nomView: TextView = itemView.findViewById(R.id.rssi)
        val majeur : TextView = itemView.findViewById(R.id.majeur)
        val mineur : TextView = itemView.findViewById(R.id.mineur)

        fun bind(beaconUtils: BeaconUtils) {
            nomView.text = beaconUtils.RSSI
            majeur.text = beaconUtils.majeur
            mineur.text = beaconUtils.mineur
            mineur.text = beaconUtils.uuid
        }
    }


}