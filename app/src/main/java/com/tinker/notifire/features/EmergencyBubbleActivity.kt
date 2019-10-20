package com.tinker.notifire.features

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.location.aravind.getlocation.GeoLocator
import com.tinker.notifire.R
import com.tinker.notifire.common.Constants
import com.tinker.notifire.common.Constants.FIREBASE.FIRE_REPORTS_COLLECTION
import com.tinker.notifire.common.Constants.FIREBASE.ZONES_COLLECTION
import com.tinker.notifire.common.Constants.SHARED_PREFS.NOTIFIRE_PREFS
import com.tinker.notifire.common.Constants.SHARED_PREFS.USER_FIREBASE_ID
import com.tinker.notifire.common.Constants.SHARED_PREFS.USER_FIREBASE_NAME
import com.tinker.notifire.common.extensions.getDistance
import com.tinker.notifire.data.model.FireReport
import com.tinker.notifire.data.model.Point
import com.tinker.notifire.data.model.Zone
import kotlinx.android.synthetic.main.activity_emergency_bubble.*

class EmergencyBubbleActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var geoLocator: GeoLocator

    private var userId: String? = ""
    private var userName: String? = ""
    private var emergencyCancel: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_bubble)

        fireStore = FirebaseFirestore.getInstance()
        sharedPrefs = applicationContext.getSharedPreferences(NOTIFIRE_PREFS, 0)

        instantiateGeoLocator()
        startTimer()

        userId = sharedPrefs.getString(USER_FIREBASE_ID, "")
        userName = sharedPrefs.getString(USER_FIREBASE_NAME, "")

        button_cancel_button.setOnClickListener {
            emergencyCancel = true
            textview_timer_message.text = getString(R.string.cancel_report_message)
        }
    }

    private fun startTimer() {
        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (!emergencyCancel) {
                    textview_timer_message.text =
                        getString(
                            R.string.timer_message,
                            (millisUntilFinished / 1000).toString()
                        )
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.cancel_report_message),
                        LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            override fun onFinish() {
                if (!emergencyCancel) {
                    textview_timer_message.text = getString(R.string.sending_message)
                    button_cancel_button.visibility = View.GONE
                    sendReport()
                }
            }
        }.start()
    }

    private fun sendReport() {
        var currentNearestZone: Zone? = null
        var currentNearestDistance: Float? = null
        val userPoint = Point(geoLocator.lattitude, geoLocator.longitude)

        fireStore.collection(ZONES_COLLECTION)
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val zone = document.toObject(Zone::class.java)

                    zone.longLat?.let { point ->
                        val distance = point.getDistance(userPoint)
                        println("Distance: $distance")
                        if (distance <= 10000f) {
                            if (currentNearestDistance == null ||
                                currentNearestDistance!! > distance
                            ) {
                                currentNearestDistance = distance
                                currentNearestZone = zone
                            }
                        }
                    }
                }

                if (currentNearestZone != null) {
                    sendReport(userPoint, currentNearestZone!!)
                } else {
                    val message = getString(R.string.error_zone_not_supported)
                    Toast.makeText(applicationContext, message, LENGTH_SHORT).show()
                    textview_timer_message.text = message
                }
            }
    }

    private fun sendReport(point: Point, zone: Zone) {
        val data = FireReport(
            userId,
            userName,
            "app",
            point,
            zone = zone
        )

        fireStore.collection(FIRE_REPORTS_COLLECTION)
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.report_sent),
                    LENGTH_SHORT
                ).show()
                finish()
            }
            .addOnFailureListener {
                sendToSMS()
            }
    }

    private fun instantiateGeoLocator() {
        geoLocator = GeoLocator(this, this)

        Log.d(Constants.LOG.TRACKING, geoLocator.toString())
    }

    private fun sendToSMS() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage(R.string.send_to_sms)
            setPositiveButton(android.R.string.ok) { dialog, id ->
                val smsIntent =
                    Intent(
                        ACTION_VIEW, Uri.fromParts(
                            "sms",
                            "09210432052",
                            null
                        )
                    )
                smsIntent.putExtra("sms_body", "SUNOG <location>")
                startActivity(smsIntent)
            }
            setNegativeButton(android.R.string.cancel) { _, _ ->

            }
        }

        builder.create().show()
    }
}
