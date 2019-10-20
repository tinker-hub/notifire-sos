package com.tinker.notifire.features

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.tinker.notifire.R
import com.tinker.notifire.common.Constants.FIREBASE.USERS_COLLECTION
import com.tinker.notifire.common.Constants.LOG.FIREBASE
import com.tinker.notifire.common.Constants.SHARED_PREFS.FIREBASE_TOKEN
import com.tinker.notifire.common.Constants.SHARED_PREFS.NOTIFIRE_PREFS
import com.tinker.notifire.common.Constants.SHARED_PREFS.USER_FIREBASE_ID
import com.tinker.notifire.common.Constants.SHARED_PREFS.USER_FIREBASE_NAME
import com.tinker.notifire.data.model.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var fireStore: FirebaseFirestore

    private var firebaseToken: String? = ""

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fireStore = FirebaseFirestore.getInstance()
        sharedPrefs = applicationContext.getSharedPreferences(NOTIFIRE_PREFS, 0)
        editor = sharedPrefs.edit()

        instantiateFirebase()

        submit_button.setOnClickListener {
            saveToFirebase(
                textinput_name.text.toString(),
                textinput_number.text.toString()
            )
        }

        val checkUserLogin = sharedPrefs.getString(USER_FIREBASE_ID, null)

        checkUserLogin?.run {
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        }
    }

    private fun saveToFirebase(name: String, number: String) {
        val data = User(name, number, firebaseToken)

        fireStore.collection(USERS_COLLECTION)
            .add(data)
            .addOnSuccessListener {
                Log.d(FIREBASE, "DocumentSnapshot ID: ${it.id}")
                editor.putString(USER_FIREBASE_ID, it.id)
                editor.putString(USER_FIREBASE_NAME, name)
                editor.commit()
            }
    }

    private fun instantiateFirebase() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(FIREBASE, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result?.token
                token?.let {
                    firebaseToken = it

                    editor.putString(FIREBASE_TOKEN, it)
                    editor.commit()
                }
            })
    }
}
