package uviwe.app.uviweappv1

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.uviweappv1.R
import com.google.firebase.database.FirebaseDatabase


class AttDisplayAdapter (): ListAdapter<AttendanceData, AttDisplayAdapter.AttDisplayAdapter>(AttendanceViewHolder()) {
    val database =
        FirebaseDatabase.getInstance("https://wil-prototype-default-rtdb.europe-west1.firebasedatabase.app/")
    class AttDisplayAdapter(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):AttDisplayAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return AttDisplayAdapter(inflater.inflate(R.layout.item_att_display, parent, false))
    }

    override fun onBindViewHolder(holder: AttDisplayAdapter, position: Int) {
        val attendance = getItem(position)

        holder.itemView.apply {
            findViewById<TextView>(R.id.txtChildName).text = "Name: " + attendance.Student
            findViewById<TextView>(R.id.txtDays).text = "Days Attended: " + attendance.Days
        }
        holder.itemView.findViewById<Button>(R.id.btnDelteAtt).setOnClickListener(){

            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this item?")

            builder.setPositiveButton("Delete") { dialog, which ->

                val itemReference = database.getReference(spinnerValueDisplay ).child("Students").child(attendance.Student)
                itemReference.removeValue()
            }

            builder.setNegativeButton("Cancel") { dialog, which ->

            }

            val dialog = builder.create()
            dialog.show()
        }

    }
    class AttendanceViewHolder : DiffUtil.ItemCallback<AttendanceData>() {
        override fun areContentsTheSame(
            oldItem: AttendanceData,
            newItem: AttendanceData
        ): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }

        override fun areItemsTheSame(
            oldItem: AttendanceData,
            newItem: AttendanceData
        ): Boolean {
            return oldItem.Student == newItem.Student
        }
    }
}