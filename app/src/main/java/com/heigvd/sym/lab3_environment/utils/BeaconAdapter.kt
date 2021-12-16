package com.heigvd.sym.lab3_environment.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heigvd.sym.lab3_environment.R

class BeaconAdapter(private val beaconUtilsList: ArrayList<BeaconUtils>, private val context: Context)
    : RecyclerView.Adapter<BeaconAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return(ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_beacon, parent, false)
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val beaconUtils : BeaconUtils = beaconUtilsList[position]

        holder.bind(beaconUtils)

        holder.itemView.setOnClickListener {
            // Ouvre wikipédia dans un navigateur
           // Utils.openBrowser(context, planete.lien)
        }
    }

    override fun getItemCount(): Int = beaconUtilsList.size

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