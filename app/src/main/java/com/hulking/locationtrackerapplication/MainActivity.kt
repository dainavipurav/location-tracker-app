package com.hulking.locationtrackerapplication

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mTextViewLabel: TextView
    private lateinit var mTextViewLocation: TextView
    private lateinit var mButtonLocateMe: Button
    private val REQUEST_CODE_LOCATION_PERMISSION = 1
    private lateinit var mTextViewAddress : TextView
    private lateinit var mGeoCoder : Geocoder
    private var addresses : List<Address> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTextViewLabel = findViewById(R.id.textViewLabel)
        mTextViewLocation = findViewById(R.id.textViewLocation)
        mButtonLocateMe = findViewById(R.id.buttonLocateMe)
        mButtonLocateMe.setOnClickListener(this@MainActivity)
        mTextViewAddress = findViewById(R.id.textViewAddress)

        mGeoCoder = Geocoder(this@MainActivity, Locale.getDefault())

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonLocateMe -> {
                if (ContextCompat.checkSelfPermission(
                        applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE_LOCATION_PERMISSION
                    )
                } else {
                    getCurrentLocation()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentLocation() {
        val mLocationRequest: LocationRequest = LocationRequest()
        mLocationRequest.setInterval(10000)
        mLocationRequest.setFastestInterval(3000)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        LocationServices.getFusedLocationProviderClient(this@MainActivity)
            .requestLocationUpdates(mLocationRequest, object : LocationCallback() {
                override fun onLocationResult(mLocationResult: LocationResult?) {
                    super.onLocationResult(mLocationResult)
                    LocationServices.getFusedLocationProviderClient(this@MainActivity)
                        .removeLocationUpdates(this)
                    if (mLocationResult != null && mLocationResult.locations.size > 0)
                        {
                            val mLatestLocationIndex: Int = mLocationResult.locations.size - 1
                            val latitude =
                                mLocationResult.locations.get(mLatestLocationIndex).latitude
                            val longitude =
                                mLocationResult.locations.get(mLatestLocationIndex).longitude

                            mTextViewLocation.text = String.format(
                                "Latitude: %s\nLongitude: %s",
                                latitude,
                                longitude
                            )

                            try {
                                addresses =  mGeoCoder.getFromLocation(latitude,longitude,1)

                                val address : String = addresses.get(0).getAddressLine(0)
                                val area : String = addresses.get(0).locality
                                val city : String = addresses.get(0).adminArea
                                val country : String = addresses.get(0).countryName
                                val postalCode : String = addresses.get(0).postalCode

                                val fullAddress : String = address+", " + area + ", " + city + ", " + country + ", " + postalCode

                                mTextViewAddress.text = fullAddress
                            }
                            catch (e:IOException){
                                e.printStackTrace()
                            }
                        }
                }
            }, Looper.getMainLooper())
    }
}
