package com.tinker.notifire.features

import android.Manifest
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bsk.floatingbubblelib.FloatingBubblePermissions
import com.tinker.notifire.R
import com.tinker.notifire.common.extensions.checkPermission
import com.tinker.notifire.common.extensions.requestPermissions
import com.tinker.notifire.common.services.EmergencyBubbleService
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        FloatingBubblePermissions.startPermissionRequest(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        checkGeoLocationPermissions()
        button_send_emergency.setOnClickListener {
            startActivity(Intent(this, EmergencyBubbleActivity::class.java))
        }

        imageview_zones.setOnClickListener {
            startActivity(Intent(this, SubscriberActivity::class.java))
        }
    }

    private fun checkGeoLocationPermissions() {
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        if (!checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
            requestPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)

        when {
            !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_gps_unavailable),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            else -> {
                startService(
                    Intent(this, EmergencyBubbleService::class.java)
                )
            }
        }
    }
}
