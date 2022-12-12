package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.udacity.project4.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    companion object{
        const val SIGN_IN_REQUEST_CODE=1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        //if there is a current user logged in, navigate him
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navigate()
        }
        //set on click listener on the sign in button to begin the sign in flow
        findViewById<Button>(R.id.login_button).setOnClickListener {
            launchSignInFlow()
        }
    }
    //TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google
    private fun launchSignInFlow() {
        //adding sign in options
        val providers= arrayListOf(AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())
        //listen to the response
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
            SIGN_IN_REQUEST_CODE
        )
    }
    //TODO: If the user was authenticated, send him to RemindersActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== SIGN_IN_REQUEST_CODE){
            //if the user signed in successfully navigate him to reminders activity
            if(resultCode== Activity.RESULT_OK){
                navigate()
            }
        }
    }
    private fun navigate() {
        val intent=Intent(this, RemindersActivity::class.java)
        startActivity(intent)
        finish()
    }
}
