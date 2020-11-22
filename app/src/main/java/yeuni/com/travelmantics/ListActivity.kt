package yeuni.com.travelmantics

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import yeuni.com.travelmantics.DealActivity

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.insert_menu -> {
                val intent = Intent(this, DealActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.logout_menu -> {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener {
                            Log.d("Logout", "onComplete: User logged out")
                            FirebaseUtil.attachListener()
                        }
                FirebaseUtil.detachListener()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        FirebaseUtil.detachListener()
    }

    override fun onResume() {
        super.onResume()
        FirebaseUtil.openFbReference("traveldeals", this)
        val rvDeals = findViewById<RecyclerView>(R.id.rvDeals)
        val adapter = DealAdapter()
        rvDeals.adapter = adapter
        val dealsLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvDeals.layoutManager = dealsLayoutManager
        FirebaseUtil.attachListener()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.list_activity_menu, menu)
        val insertMenu = menu.findItem(R.id.insert_menu)
        if (FirebaseUtil.isAdmin) {
            insertMenu.isVisible = true
        } else {
            insertMenu.isVisible = false
        }
        return true
    }

    fun showMenu() {
        invalidateOptionsMenu()
    }
}