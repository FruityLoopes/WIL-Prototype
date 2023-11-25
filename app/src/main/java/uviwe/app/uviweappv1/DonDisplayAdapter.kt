package uviwe.app.uviweappv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.uviweappv1.R

class DonDisplayAdapter : ListAdapter<DonationData, DonDisplayAdapter.DonDisplayAdapter >(DonDisplayHolder()){

    class DonDisplayAdapter (view : View): RecyclerView.ViewHolder(view)
    {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonDisplayAdapter
    {
        val inflater = LayoutInflater.from(parent.context)
        return DonDisplayAdapter(inflater.inflate(R.layout.item_don_display, parent, false))
    }

    override fun onBindViewHolder(holder: DonDisplayAdapter , position: Int)
    {
        var don = getItem(position)
        holder.itemView.findViewById<TextView>(R.id.txtNameSurnameDon).text = "Full Name: " + don.Name + " " + don.Surname
        holder.itemView.findViewById<TextView>(R.id.txtAmountDon).text = "Amount: " + "R"+ don.Amount
        holder.itemView.findViewById<TextView>(R.id.txtNoteDon).text = "Note: " + don.Note
        holder.itemView.findViewById<TextView>(R.id.txtDateDon).text ="Date: " + don.Date
        holder.itemView.findViewById<TextView>(R.id.txtEmailDon).text ="Email: " + don.Email
        holder.itemView.findViewById<TextView>(R.id.txtContactDon).text = "Contact: " +don.Contact

    }


    class DonDisplayHolder : DiffUtil.ItemCallback<DonationData>()
    {
        override fun areContentsTheSame(oldItem: DonationData, newItem: DonationData): Boolean
        {
            return areItemsTheSame(oldItem,newItem)
        }

        override fun areItemsTheSame(oldItem: DonationData, newItem: DonationData): Boolean
        {
            return oldItem.Name == newItem.Name
        }

    }
}