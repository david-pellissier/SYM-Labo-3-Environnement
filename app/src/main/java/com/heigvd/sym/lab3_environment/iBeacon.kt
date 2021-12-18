/**
 * Groupe : Pellissier David, Ruckstuhl Michael, Sauge Ryan
 * Description : Activité pour la partie "iBeacon" du laboratoire.
 *               L'application demande les permissions nécessaires
 */

package com.heigvd.sym.lab3_environment

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import android.util.Log
import android.widget.ListView
import androidx.lifecycle.Observer
import com.heigvd.sym.lab3_environment.NFCLogin.Companion.TAG
import com.heigvd.sym.lab3_environment.utils.BeaconAdapter
import com.heigvd.sym.lab3_environment.utils.BeaconUtils
import org.altbeacon.beacon.*
import org.altbeacon.beacon.BeaconParser




// https://stackoverflow.com/questions/40142331/how-to-request-location-permission-at-runtime
class iBeacon : AppCompatActivity() {
    private val beaconList : ArrayList<BeaconUtils> = ArrayList()
    private var adapter: BeaconAdapter? = null
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                Toast.makeText(
                    this@iBeacon,
                    "Got Location: " + location.toString(),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ibeacon)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermission()
        // TODO: Add beaconParsers for any properietry beacon formats you wish to detect
        val recyclerView : ListView = findViewById(R.id.recyclerView)
        adapter = BeaconAdapter(beaconList, applicationContext)
        recyclerView.adapter = adapter

        /*with(recyclerView) {
            //layoutManager = LinearLayoutManager(this@iBeacon)
            adapter = BeaconAdapter(beaconList, context)
        }*/
        adapter?.addBeacon(BeaconUtils("test", "test1", "test3", "test4"))
        //beaconList.add(BeaconUtils("test", "test1", "test3", "test4"))

        val beaconManager = BeaconManager.getInstanceForApplication(this)
        val region = Region("all-beacons-region", null, null, null)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        beaconManager.getRegionViewModel(region).rangedBeacons.observe(this, rangingObserver)
        beaconManager.startRangingBeacons(region)
    }



    private val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(TAG, "Ranged: ${beacons.count()} beacons")

        for (beacon: Beacon in beacons) {
            Log.d(TAG, "add beacon")
            beaconList.add(BeaconUtils(
                beacon.id1.toString(),beacon.id2.toString(), beacon.id3.toString(), beacon.rssi.toString()))
            adapter?.addBeacon(BeaconUtils(
                beacon.id1.toString(),beacon.id2.toString(), beacon.id3.toString(), beacon.rssi.toString()))
            Log.d(TAG, "$beacon about ${beacon.distance} meters away")
            Log.d(TAG + "Identifier", beacon.identifiers.toString())
            Log.d(TAG +"uiid", beacon.serviceUuid.toString())
            Log.d(TAG +"id1", beacon.id1.toString())
            Log.d(TAG +"mineur", beacon.id2.toString())
            Log.d(TAG +"mineur", beacon.id3.toString())
            Log.d(TAG +"RSSI", beacon.rssi.toString())

        }
    }


    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }
                return
            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}