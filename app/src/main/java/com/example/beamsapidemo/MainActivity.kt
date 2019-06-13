package com.example.beamsapidemo


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog
import com.google.firebase.messaging.RemoteMessage
import com.pusher.pushnotifications.*
import com.pusher.pushnotifications.auth.AuthData
import com.pusher.pushnotifications.auth.AuthDataGetter
import com.pusher.pushnotifications.auth.BeamsTokenProvider
import android.content.Intent


class MainActivity : AppCompatActivity() {

    lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rootView = findViewById(R.id.rootView)
    }


    // SDK

    companion object {
        // replace this with your Beams instance ID
        const val INSTANCE_ID = "afc7aaf1-3018-4709-adf8-e779bcd48551"
    }

    fun onStartButtonClick(view: View) {
        PushNotifications.start(this, INSTANCE_ID)
        showInSnackBar(view, "SDK started")
    }

    fun onStopButtonClick(view: View) {
        PushNotifications.stop()
        showInSnackBar(view, "SDK stopped")
    }


    // Device Interests

    fun onGetInterestsButtonClick(view: View) {
        val interests = PushNotifications.getDeviceInterests()
        showInSnackBar(view, "Interests:   $interests")
    }

    fun onSetInterestsButtonClick(view: View) {
        showMultiChoiceDialog()
    }

    private fun onReturnFromUserSelectedSetInterests(interests: Set<String>) {
        PushNotifications.setDeviceInterests(interests)
        showInSnackBar(rootView, "Set interests:   $interests")
    }

    fun onClearInterestsButtonClick(view: View) {
        PushNotifications.clearDeviceInterests()
        showInSnackBar(view, "Device interests cleared")
    }

    fun onAddInterestsButtonClick(view: View) {
        showSingleChoiceDialog(DialogRequestType.ADD_INTEREST)
    }

    private fun onReturnFromUserSelectedAddInterest(interest: String) {
        PushNotifications.addDeviceInterest(interest)
        showInSnackBar(rootView, "added device interest: \"$interest\"")
    }

    fun onRemoveInterestsButtonClick(view: View) {
        showSingleChoiceDialog(DialogRequestType.REMOVE_INTEREST)
    }

    private fun onReturnFromUserSelectedRemoveInterest(interest: String) {
        PushNotifications.removeDeviceInterest(interest)
        showInSnackBar(rootView, "removed device interest: \"$interest\"")
    }


    // User

    fun onSetIdButtonClick(view: View) {

        // hardcoding a valid user ID and password
        val userId = "Mary"
        val password = "mypassword"
        val text = "$userId:$password"
        val data = text.toByteArray()
        val base64 = Base64.encodeToString(data, Base64.NO_WRAP)

        // get the token from the server
        val serverUrl = "http://10.0.2.2:8888/token"
        val tokenProvider = BeamsTokenProvider(
            serverUrl,
            object: AuthDataGetter {
                override fun getAuthData(): AuthData {
                    return AuthData(
                        headers = hashMapOf(
                            "Authorization" to "Basic $base64"
                        )
                    )
                }
            }
        )

        // send the token to Pusher
        PushNotifications.setUserId(
            userId,
            tokenProvider,
            object : BeamsCallback<Void, PusherCallbackError> {
                override fun onFailure(error: PusherCallbackError) {
                    Log.e("BeamsAuth",
                        "Could not login to Beams: ${error.message}")
                }
                override fun onSuccess(vararg values: Void) {
                    Log.i("BeamsAuth", "Beams login success")
                }
            }
        )
    }

    fun onClearStateButtonClick(view: View) {
        PushNotifications.clearAllState()
        showInSnackBar(view, "cleared all state")
    }


    // Listeners

    fun onInterestsChangedButtonClick(view: View) {
        PushNotifications.setOnDeviceInterestsChangedListener(object : SubscriptionsChangedListener {
            override fun onSubscriptionsChanged(interests: Set<String>) {
                Toast.makeText(applicationContext,
                    "interests changed to: $interests",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        })
        showInSnackBar(view, "Listening for a change in device interests")
    }

    fun onMessageReceivedButtonClick(view: View) {
        PushNotifications.setOnMessageReceivedListenerForVisibleActivity(this, object :
            PushNotificationReceivedListener {
            override fun onMessageReceived(remoteMessage: RemoteMessage) {
                showInSnackBar(rootView,
                    "Message received: " +
                            "Title: \"${remoteMessage.notification?.title}\"" +
                            "Body \"${remoteMessage.notification?.body}\""
                )
            }
        })
        showInSnackBar(view, "Listening for a message received")
    }

    override fun onResume() {
        super.onResume()
        // Normally you would call PushNotifications.setOnMessageReceivedListenerForVisibleActivity() here.
        // I put it in the onMessageReceivedButtonClick method for testing purposes.
    }

    fun onMessagingServiceButtonClicked(view: View) {
        // Starting a new blank activity to show that message notifications
        // are still received from the service.
        val myIntent = Intent(this, AnotherActivity::class.java)
        startActivity(myIntent)
    }


    // helper methods

    private fun showInSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
        Log.i("DEMO", message)
    }

    private fun showSingleChoiceDialog(requestType: DialogRequestType) {
        val builder = AlertDialog.Builder(this)
        if (requestType == DialogRequestType.ADD_INTEREST) {
            builder.setTitle("Select interest to add")
        } else {
            builder.setTitle("Select interest to remove")
        }
        val interests = arrayOf("apple", "pear", "orange", "banana")
        var chosenInterest = ""
        builder.setSingleChoiceItems(interests, -1) { _, which ->
            chosenInterest = interests[which]
        }
        builder.setPositiveButton("OK") { _, _ ->
            if (requestType == DialogRequestType.ADD_INTEREST) {
                onReturnFromUserSelectedAddInterest(chosenInterest)
            } else {
                onReturnFromUserSelectedRemoveInterest(chosenInterest)
            }
        }
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun showMultiChoiceDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select your interests")
        val interests = arrayOf("apple", "pear", "orange", "banana")
        val checkedItems = booleanArrayOf(false, false, false, false, false)
        builder.setMultiChoiceItems(interests, checkedItems) { _, which, isChecked ->
            checkedItems[which] = isChecked
        }
        builder.setPositiveButton("OK") { _, _ ->
            val chosenInterests = ArrayList<String>()
            for (i in interests.indices) {
                if (checkedItems[i]) {
                    chosenInterests.add(interests[i])
                }
            }
            onReturnFromUserSelectedSetInterests(chosenInterests.toSet())
        }
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()
    }

}


enum class DialogRequestType {
    ADD_INTEREST,
    REMOVE_INTEREST,
    SET_INTERESTS
}

