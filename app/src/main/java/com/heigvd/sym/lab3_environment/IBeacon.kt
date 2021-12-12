package com.heigvd.sym.lab3_environment

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Observer
import com.heigvd.sym.lab3_environment.NFCLogin.Companion.TAG
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.MonitorNotifier
import org.intellij.lang.annotations.Identifier
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

class IBeacon: AppCompatActivity() {

    /*
    https://altbeacon.github.io/android-beacon-library/samples.html
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ibeacon)

        // TODO: Add code here to obtain location permission from user
        // TODO: Add beaconParsers for any properietry beacon formats you wish to detect

        val beaconManager =  BeaconManager.getInstanceForApplication(this)
        val region = Region("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24", null, null, null)
        // Set up a Live Data observer so this Activity can get monitoring callbacks
        // observer will be called each time the monitored regionState changes (inside vs. outside region)
        beaconManager.getRegionViewModel(region).regionState.observe(this, monitoringObserver)
        beaconManager.startMonitoring(region)
    }

    val monitoringObserver = Observer<Int> { state ->
        if (state == MonitorNotifier.INSIDE) {
            Log.e(TAG, "Detected beacons(s)")
        }
        else {
            Log.e(TAG, "Stopped detecteing beacons")
        }
    }
}