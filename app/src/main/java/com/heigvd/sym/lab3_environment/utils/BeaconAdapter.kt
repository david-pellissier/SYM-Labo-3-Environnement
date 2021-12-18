package com.heigvd.sym.lab3_environment.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heigvd.sym.lab3_environment.NFCLogin
import com.heigvd.sym.lab3_environment.R




class BeaconAdapter(private val beaconUtilsList: ArrayList<BeaconUtils>, private val context: Context)
    : BaseAdapter() {


    override fun getCount(): Int {
        return beaconUtilsList.count()
    }

    override fun getItem(position: Int): BeaconUtils {
        return beaconUtilsList[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    fun addBeacon(beacon : BeaconUtils ){
        this.beaconUtilsList.add(beacon)
        notifyDataSetChanged()
    }
    @Override
    public override fun hasStableIds() : Boolean{
        return false;
    }



    override fun getView(position: Int, recycleView: View?, viewGroup: ViewGroup?): View? {
        var recyLocal = recycleView
      if(recyLocal == null){
           var inflater = LayoutInflater.from(this.context)
          recyLocal = inflater.inflate(R.layout.item_beacon,viewGroup, false);
       }

        val rssi: TextView? = recycleView?.findViewById(R.id.rssi)
        val majeur : TextView? = recycleView?.findViewById(R.id.majeur)
        val mineur : TextView? = recycleView?.findViewById(R.id.mineur)
        val uuid : TextView? = recycleView?.findViewById(R.id.uuid)
        Log.e(NFCLogin.TAG, "link gui")
        //link to Gui
        val beaconItem = getItem(position)


        rssi?.text = beaconItem.RSSI
        majeur?.text = beaconItem.majeur
        mineur?.text = beaconItem.mineur
        uuid?.text = beaconItem.uuid

        Log.e(NFCLogin.TAG, "Emd link gui")

        return recyLocal
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