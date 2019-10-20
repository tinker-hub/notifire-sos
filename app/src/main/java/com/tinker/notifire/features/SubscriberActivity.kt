package com.tinker.notifire.features

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.tinker.notifire.R
import com.tinker.notifire.common.Constants.FIREBASE.USERS_COLLECTION
import com.tinker.notifire.common.Constants.FIREBASE.ZONES_COLLECTION
import com.tinker.notifire.common.Constants.LOG.FIREBASE
import com.tinker.notifire.common.Constants.SHARED_PREFS.FIREBASE_TOKEN
import com.tinker.notifire.common.Constants.SHARED_PREFS.NOTIFIRE_PREFS
import com.tinker.notifire.common.Constants.SHARED_PREFS.USER_FIREBASE_ID
import com.tinker.notifire.data.adapter.ZoneListAdapter
import com.tinker.notifire.data.model.PushNotificationSubscriber
import com.tinker.notifire.data.model.User
import com.tinker.notifire.data.model.Zone
import com.tinker.notifire.data.model.ZoneItem
import kotlinx.android.synthetic.main.activity_subscriber.*

class SubscriberActivity : AppCompatActivity(), ZoneListAdapter.OnSubscribeZoneListener {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var adapter: ZoneListAdapter
    private lateinit var fireStore: FirebaseFirestore

    private lateinit var zoneList: ArrayList<ZoneItem>
    private lateinit var userZoneList: ArrayList<Zone>
    private var userId: String? = ""

    private var firebaseToken: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscriber)

        sharedPrefs = applicationContext.getSharedPreferences(NOTIFIRE_PREFS, 0)
        firebaseToken = sharedPrefs.getString(FIREBASE_TOKEN, null)
        fireStore = FirebaseFirestore.getInstance()
        zoneList = arrayListOf()
        userZoneList = arrayListOf()

        userId = sharedPrefs.getString(USER_FIREBASE_ID, null)

        fireStore.collection(ZONES_COLLECTION)
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val zoneItem = document.toObject(ZoneItem::class.java)
                    zoneItem.apply {
                        this.document = document.id
                    }
                    zoneList.add(zoneItem)
                }

                println(userId)
                fireStore.collection(USERS_COLLECTION)
                    .document(userId ?: "")
                    .get()
                    .addOnSuccessListener {
                        val user = it.toObject(User::class.java)

                        user?.subscribedZones?.let { list ->
                            list.forEach { zone ->
                                userZoneList.add(zone)
                                zoneList.forEachIndexed { index, zoneItem ->
                                    if (zoneItem.code == zone.code) {
                                        println("NICE ${zoneItem.code} ${zone.code}")
                                        zoneList[index].subscribed = true
                                    }
                                }
                            }
                        }

                        adapter = ZoneListAdapter(zoneList, this)

                        recyclerview_zones.layoutManager = LinearLayoutManager(this)
                        recyclerview_zones.adapter = adapter
                    }
            }
    }

    override fun onZoneItemClicked(item: ZoneItem) {
        item.subscribed = !item.subscribed

        val zoneItem = ZoneItem().apply {
            name = item.name
            code = item.code
            id = item.id
            description = item.description
            longLat = item.longLat
            pushAlertSubscribers = item.pushAlertSubscribers
            smsAlertSubscribers = item.smsAlertSubscribers
        }

        adapter.updateZone(item)
        if (item.subscribed) {
            userZoneList.add(zoneItem)

            zoneItem.pushAlertSubscribers?.add(
                PushNotificationSubscriber(userId, firebaseToken)
            )

            fireStore.collection(ZONES_COLLECTION).document(item.document)
                .update(
                    "pushAlertSubscribers",
                    zoneItem.pushAlertSubscribers
                )
                .addOnSuccessListener {
                    Log.d(FIREBASE, "Subscribe to ${zoneItem.name} in zones")
                }

            fireStore.collection(USERS_COLLECTION).document(userId ?: "")
                .update("subscribedZones", userZoneList)
                .addOnSuccessListener {
                    Log.d(FIREBASE, "Subscribe to ${zoneItem.name} in user")
                }

        } else {
            fireStore.collection(ZONES_COLLECTION).document(item.document)
                .update(
                    "pushAlertSubscribers",
                    zoneItem.pushAlertSubscribers?.filter { it.userId != userId }
                )
                .addOnSuccessListener {
                    Log.d(FIREBASE, "Unsubscribe to ${item.name} in zones")
                }

            fireStore.collection(USERS_COLLECTION).document(userId ?: "")
                .update("subscribedZones", userZoneList.filter { it.code != item.code })
                .addOnSuccessListener {
                    Log.d(FIREBASE, "Unsubscribe to ${item.name} in user")
                }
        }
    }
}
