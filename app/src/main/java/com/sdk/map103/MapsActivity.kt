package com.sdk.map103

import android.Manifest
import android.R
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnPolygonClickListener
import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.sdk.map103.databinding.ActivityMapsBinding
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnPolylineClickListener,
    OnPolygonClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var locationPermissionGranted = false
    private var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var location: Location? = null
    var DEFAULT_ZOOM = 4f
    var defaultLocation = LatLng(40.747451466827094, 72.35951825384541)
    var fusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getLocationPermission()

        val mapFragment = supportFragmentManager
            .findFragmentById(com.sdk.map103.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val markerOptions = MarkerOptions().position(defaultLocation).title("I am here!")


           
        //region::marker
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(56.3, 156.2)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

//        mMap.addPolyline(
//            PolylineOptions()
//                .clickable(true)
//                .add(
//                    LatLng(-35.016, 143.321),
//                    LatLng(-34.747, 145.592),
//                    LatLng(-34.364, 147.891),
//                    LatLng(-33.501, 150.217),
//                    LatLng(-32.306, 149.248),
//                    LatLng(-32.491, 148.309),
//                    LatLng(-31.501, 147.217),
//                    LatLng(-30.306, 146.248),
//                    LatLng(-29.491, 145.309)
//                )
//        )
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))

//        // Add polygons to indicate areas on the map.
//        val polygon1 = mMap.addPolygon(
//            PolygonOptions()
//                .clickable(true)
//                .add(
//                    LatLng(-27.457, 153.040),
//                    LatLng(-33.852, 151.211),
//                    LatLng(-37.813, 144.962),
//                    LatLng(-34.928, 138.599)
//                )
//        )
//// Store a data object with the polygon, used here to indicate an arbitrary type.
//        polygon1.tag = "alpha"
//// Style the polygon.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-27.684, 133.903), 4f))
//endregion
        getDeviceLocation()
//        updateLocationUI()

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(defaultLocation))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5f))
        mMap.addMarker(markerOptions)
        mMap.setOnPolylineClickListener(this)
        mMap.setOnPolygonClickListener(this)
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult: Task<Location>? = fusedLocationClient?.lastLocation
                locationResult?.addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        location = task.result
                        if (location != null) {
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        location!!.latitude,
                                        location!!.longitude
                                    ), DEFAULT_ZOOM
                                )
                            )
                        }
                    } else {
                        mMap.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM)
                        )
                        mMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun updateLocationUI() {
        if (mMap ==null){
            return
        }
        try {
            if (locationPermissionGranted) {
                location = null
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                location = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message.toString())
        }
    }

    override fun onPolylineClick(p0: Polyline) {
        Toast.makeText(this, "${p0.width}", Toast.LENGTH_SHORT).show()
    }

    override fun onPolygonClick(p0: Polygon) {
        Toast.makeText(this, "${p0.strokeWidth}", Toast.LENGTH_SHORT).show()
        p0.fillColor = Color.RED
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        if (requestCode
            == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        ) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionGranted = true
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

}