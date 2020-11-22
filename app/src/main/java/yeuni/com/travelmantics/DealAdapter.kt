package yeuni.com.travelmantics

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import yeuni.com.travelmantics.DealActivity
import yeuni.com.travelmantics.DealAdapter.DealViewHolder
import java.util.*

class DealAdapter : RecyclerView.Adapter<DealViewHolder>() {
    private val mChildEventListener: ChildEventListener
    private val mDatabaseReference: DatabaseReference
    private val mFirebaseDatabase: FirebaseDatabase
    private var imageDeal: ImageView? = null
    var deals: ArrayList<TravelDeal?>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val context = parent.context
        val itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_row, parent, false)
        return DealViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val deal = deals[position]
        holder.bind(deal)
    }

    override fun getItemCount(): Int {
        return deals.size
    }

    inner class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tvTitle: TextView
        var tvDescription: TextView
        var tvPrice: TextView
        fun bind(deal: TravelDeal?) {
            tvTitle.text = deal!!.title
            tvDescription.text = deal.description
            tvPrice.text = deal.price
            showImage(deal.imageUrl)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            Log.d("onClick", position.toString())
            val selectedDeal = deals[position]
            val intent = Intent(v.context, DealActivity::class.java)
            intent.putExtra("Deal", selectedDeal)
            v.context.startActivity(intent)
        }

        fun showImage(url: String?) {
            if (url != null && !url.isEmpty()) {
                Picasso.get()
                        .load(url)
                        .resize(160, 160)
                        .centerCrop()
                        .into(imageDeal)
            }
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvDescription = itemView.findViewById(R.id.tvDescription)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            imageDeal = itemView.findViewById(R.id.imageDeal)
            itemView.setOnClickListener(this)
        }
    }

    init {
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        deals = FirebaseUtil.mDeals
        mChildEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val td = dataSnapshot.getValue(TravelDeal::class.java)!!
                Log.d("Deals: ", td.title)
                td.id = dataSnapshot.key
                deals.add(td)
                notifyItemInserted(deals.size - 1)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        mDatabaseReference.addChildEventListener(mChildEventListener)
    }
}