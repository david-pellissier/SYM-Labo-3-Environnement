/**
 * Groupe : Pellissier David, Ruckstuhl Michael, Sauge Ryan
 * Description: Adapter pour une listView contenant des beacons
 **/
package com.heigvd.sym.lab3_environment.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.heigvd.sym.lab3_environment.R


class BeaconAdapter(
    private val beaconUtilsList: ArrayList<BeaconUtils>,
    private val context: Context
) : BaseAdapter() {

    @Override
    override fun getCount(): Int {
        return beaconUtilsList.count()
    }

    @Override
    override fun getItem(position: Int): BeaconUtils {
        return beaconUtilsList[position]
    }

    @Override
    override fun getItemId(p0: Int): Long {
        return 0
    }

    fun addBeacon(beacon: BeaconUtils) {
        this.beaconUtilsList.add(beacon)
        notifyDataSetChanged()
    }

    @Override
    override fun hasStableIds(): Boolean {
        return false;
    }

    override fun getView(position: Int, recycleView: View?, viewGroup: ViewGroup?): View? {
        var recycleViewLocal = recycleView
        if (recycleViewLocal == null) {
            var inflater = LayoutInflater.from(this.context)
            recycleViewLocal = inflater.inflate(R.layout.item_beacon, viewGroup, false);
        }
        val rssi: TextView? = recycleView?.findViewById(R.id.rssi)
        val majeur: TextView? = recycleView?.findViewById(R.id.majeur)
        val mineur: TextView? = recycleView?.findViewById(R.id.mineur)
        val uuid: TextView? = recycleView?.findViewById(R.id.uuid)
        val beaconItem = getItem(position)

        rssi?.text = beaconItem.RSSI
        majeur?.text = beaconItem.majeur
        mineur?.text = beaconItem.mineur
        uuid?.text = beaconItem.uuid

        return recycleViewLocal
    }
}