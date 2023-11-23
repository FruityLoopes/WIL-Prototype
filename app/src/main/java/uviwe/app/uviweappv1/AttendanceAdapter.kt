package uviwe.app.uviweappv1


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.uviweappv1.R


class AttendanceAdapter (private val onAttendanceClickListener: (AttendanceData) -> Unit): ListAdapter<AttendanceData, AttendanceAdapter.AttendanceAdapter>(AttendanceViewHolder()) {

    class AttendanceAdapter(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return AttendanceAdapter(inflater.inflate(R.layout.item_attendance, parent, false))
    }

    override fun onBindViewHolder(holder: AttendanceAdapter, position: Int) {
        val attendance = getItem(position)
        holder.itemView.apply {
            findViewById<TextView>(R.id.cbAttendance).apply {
                text = attendance.Student
            }

            // Update checkbox state based on the attended property
            findViewById<CheckBox>(R.id.cbAttendance).apply {
                isChecked = attendance.attended
                setOnClickListener {
                    // Toggle the attended property when the checkbox is clicked
                    attendance.attended = isChecked
                    onAttendanceClickListener.invoke(attendance)
                }
            }
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
