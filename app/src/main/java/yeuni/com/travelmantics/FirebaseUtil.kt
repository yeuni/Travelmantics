package yeuni.com.travelmantics

import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

object FirebaseUtil {
    var mFirebaseDatabase: FirebaseDatabase? = null
    var mDatabaseReference: DatabaseReference? = null
    var mFirebaseStorage: FirebaseStorage? = null
    var mStorageReference: StorageReference? = null
    var firebaseUtil: FirebaseUtil? = null
    var mFireBaseAuth: FirebaseAuth? = null
    var mAuthListener: AuthStateListener? = null
    var mDeals: ArrayList<TravelDeal>? = null
    var isAdmin = false
    private const val RC_SIGN_IN = 123
    private var caller: ListActivity? = null
    fun openFbReference(ref: String?, callerActivity: ListActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = FirebaseUtil()
            mFirebaseDatabase = FirebaseDatabase.getInstance()
            mFireBaseAuth = FirebaseAuth.getInstance()
            caller = callerActivity
            mAuthListener = AuthStateListener { firebaseAuth ->
                if (firebaseAuth.currentUser == null) {
                    signIn()
                } else {
                    val userId = firebaseAuth.uid
                    checkAdmin(userId)
                    Toast.makeText(callerActivity.baseContext, "Welcome back!", Toast.LENGTH_LONG).show()
                }
            }
            connectStorage()
        }
        mDeals = ArrayList()
        mDatabaseReference = mFirebaseDatabase!!.reference.child(ref!!)
    }

    private fun checkAdmin(userId: String?) {
        isAdmin = false
        val databaseReference = mFirebaseDatabase!!.reference.child("administrators").child(userId!!)
        val listener: ChildEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                isAdmin = true
                Log.d("Admin", "onChildAdded: Admin status verified!")
                caller!!.showMenu()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        databaseReference.addChildEventListener(listener)
    }

    private fun signIn() {
        val providers = Arrays.asList(
                EmailBuilder().build(),
                GoogleBuilder().build())
        caller!!.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo)
                        .build(),
                RC_SIGN_IN)
    }

    fun attachListener() {
        mFireBaseAuth!!.addAuthStateListener(mAuthListener!!)
    }

    fun detachListener() {
        mFireBaseAuth!!.removeAuthStateListener(mAuthListener!!)
    }

    fun connectStorage() {
        mFirebaseStorage = FirebaseStorage.getInstance()
        mStorageReference = mFirebaseStorage!!.reference.child("deals_pictures")
    }
}