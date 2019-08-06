package com.dev.aaditya.homeautomation

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.activity_login.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.DocumentSnapshot
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener




class login : AppCompatActivity() {
    protected override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
    private var mAuth: FirebaseAuth? = null

    var mGoogleSignInClient:GoogleSignInClient?=null
    var RC_SIGN_IN:Int=0
//    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account!=null) {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("email",account.email)
            intent.putExtra("name", account.givenName+" " +account.familyName)
            startActivity(intent)
            Log.i("logged in user :",account.givenName)
        }
    }

    fun signIn(view: View) {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            if(account!=null) {
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("email",account.email)
                intent.putExtra("name", account.givenName+account.familyName)
                Log.i("logged in user :",account.givenName)

                var user = HashMap<String,Any>()
                user["email"] = account.email.toString()
                user["no-of-rooms"] = 0

                val userDoc = db.collection("users").document("${account.email}-details")
                userDoc.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document!!.exists()) {
                            Log.d("document", "Document exists!")
                        } else {
                            Log.d("document error", "Document does not exist!")
                            userDoc.set(user, SetOptions.merge())
                        }
                    } else {
                        Log.d("document error", "Failed with: ", task.exception)
                    }
                })
                startActivity(intent)
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error ", "signInResult:failed code=" + e.statusCode)
//            updateUI(null)
        }

    }

}
