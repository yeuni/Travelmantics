package yeuni.com.travelmantics

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import yeuni.com.travelmantics.DealActivity

class DealActivity : AppCompatActivity() {
    private var mDatabaseReference: DatabaseReference? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null
    var txtTitle: EditText? = null
    var txtPrice: EditText? = null
    var txtDescription: EditText? = null
    var imageView: ImageView? = null
    var deal: TravelDeal? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal)
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase!!.reference.child("traveldeals")
        txtTitle = findViewById(R.id.txtTitle)
        txtDescription = findViewById(R.id.txtDescription)
        txtPrice = findViewById(R.id.txtPrice)
        imageView = findViewById(R.id.imageDeal)
        val intent = intent
        var deal = intent.getSerializableExtra("Deal") as TravelDeal
        if (deal == null) {
            deal = TravelDeal()
        }
        this.deal = deal
        txtDescription.setText(deal.description)
        txtPrice.setText(deal.price)
        txtTitle.setText(deal.title)
        showImage(deal.imageUrl)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_menu -> {
                saveDeal()
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show()
                clean()
                backToList()
                true
            }
            R.id.delete_menu -> {
                deleteDeal()
                Toast.makeText(this, "Deal deleted", Toast.LENGTH_LONG).show()
                backToList()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.save_menu, menu)
        val saveMenu = menu.findItem(R.id.save_menu)
        val deleteMenu = menu.findItem(R.id.delete_menu)
        val imageButton = findViewById<Button>(R.id.btnImage)
        if (FirebaseUtil.isAdmin) {
            saveMenu.isVisible = true
            deleteMenu.isVisible = true
            enableEditTexts(true)
            imageButton.isEnabled = true
        } else {
            saveMenu.isVisible = false
            deleteMenu.isVisible = false
            enableEditTexts(false)
            imageButton.isEnabled = false
        }
        return true
    }

    //
    private fun clean() {
        txtPrice!!.setText("")
        txtDescription!!.setText("")
        txtTitle!!.setText("")
        txtTitle!!.requestFocus()
    }

    private fun saveDeal() {
        deal!!.title = txtTitle!!.text.toString()
        deal!!.description = txtDescription!!.text.toString()
        deal!!.price = txtPrice!!.text.toString()
        if (deal!!.id == null) {
            mDatabaseReference!!.push().setValue(deal)
        } else {
            mDatabaseReference!!.child(deal!!.id!!).setValue(deal)
        }
    }

    private fun deleteDeal() {
        if (deal!!.id == null) {
            Toast.makeText(this, "Please save the deal before attempting to delete", Toast.LENGTH_SHORT).show()
            return
        }
        mDatabaseReference!!.child(deal!!.id!!).removeValue()
        Log.d("Image deleting", "image " + deal!!.imageName)
        if (deal!!.imageName != null && !deal!!.imageName!!.isEmpty()) {
            val picReference = FirebaseUtil.mFirebaseStorage.reference.child(deal!!.imageName!!)
            picReference.delete().addOnSuccessListener { Log.d("Delete Image", "onSuccess: Image succesfully deleted") }.addOnFailureListener { e -> Log.d("Delete Image", "onFailure: " + e.message) }
        }
    }

    private fun backToList() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }

    private fun enableEditTexts(isEnabled: Boolean) {
        txtTitle!!.isEnabled = isEnabled
        txtDescription!!.isEnabled = isEnabled
        txtPrice!!.isEnabled = isEnabled
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK) {
            assert(data != null)
            val imageUri = data!!.data
            val reference = FirebaseUtil.mStorageReference.child(imageUri.lastPathSegment)
            val uploadTask = reference.putFile(imageUri)
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                reference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result!!
                    val imageUrl = downloadUri.toString()
                    val imageName = task.result!!.path
                    Log.d("imageUrl", "onSuccess: $downloadUri")
                    deal!!.imageUrl = imageUrl
                    deal!!.imageName = imageName
                    showImage(imageUrl)
                } else {
                    Toast.makeText(this@DealActivity, "Picture couldn't be uploaded", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun editImage(view: View?) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "Insert Picture"), PICTURE_RESULT)
    }

    fun showImage(url: String?) {
        if (url != null && !url.isEmpty()) {
            val width = Resources.getSystem().displayMetrics.widthPixels
            Picasso.get()
                    .load(url)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(imageView)
        }
    }

    companion object {
        const val PICTURE_RESULT = 3290
    }
}