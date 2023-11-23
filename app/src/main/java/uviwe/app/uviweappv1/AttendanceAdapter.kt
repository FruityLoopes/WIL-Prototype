package uviwe.app.uviweappv1


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.uviweappv1.R


class AttendanceAdapter : ListAdapter<AttendanceData, AttendanceAdapter.AttendanceAdapter>(AttendanceViewHolder()){

    class  AttendanceAdapter (view : View): RecyclerView.ViewHolder(view) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  AttendanceAdapter
    {
        val inflater = LayoutInflater.from(parent.context)
        return AttendanceAdapter(inflater.inflate(R.layout.item_attendance, parent , false))
    }
    override fun onBindViewHolder(holder:  AttendanceAdapter, position: Int)
    {

        var attendance = getItem(position)
        holder.itemView.findViewById<TextView>(R.id.cbAttendance).text =  attendance.Student
    }
    class AttendanceViewHolder : DiffUtil.ItemCallback<AttendanceData>() {
        override fun areContentsTheSame(oldItem:AttendanceData, newItem: AttendanceData): Boolean
        {
            return areItemsTheSame(oldItem,newItem)
        }

        override fun areItemsTheSame(oldItem: AttendanceData, newItem: AttendanceData): Boolean
        {
            return oldItem.Student == newItem.Student
        }
    }
}