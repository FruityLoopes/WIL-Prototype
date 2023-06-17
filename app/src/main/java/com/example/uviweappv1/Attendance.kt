package com.example.uviweappv1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale


class Attendance : AppCompatActivity() {


    private lateinit var dateRecyclerView: RecyclerView
    private lateinit var dateAdapter: DateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        dateRecyclerView = findViewById(R.id.dateRecyclerView)
        dateAdapter = DateAdapter()

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        dateRecyclerView.layoutManager = layoutManager
        dateRecyclerView.adapter = dateAdapter

        // Populate the date list with the desired range of dates
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.DAY_OF_MONTH, -7) // Start from 7 days ago

        val endDate = Calendar.getInstance()

        val dateList = ArrayList<Date>()
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        while (startDate <= endDate) {
            dateList.add(startDate.time)
            startDate.add(Calendar.DAY_OF_MONTH, 1)
        }

        dateAdapter.setDateList(dateList)
    }

    private inner class DateAdapter : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

        private var dateList = ArrayList<Date>()

        fun setDateList(list: List<Date>) {
            dateList.clear()
            dateList.addAll(list)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DateAdapter.DateViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
            return DateViewHolder(view)
        }
        private var selectedItemPosition = RecyclerView.NO_POSITION
        override fun onBindViewHolder(holder: DateAdapter.DateViewHolder, position: Int) {
            val date = dateList[position]
            val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            val formattedDate = dateFormat.format(date)
            holder.bind(formattedDate, position == selectedItemPosition)
        }

        override fun getItemCount(): Int {
            return dateList.size
        }

        inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

            fun bind(date: String, isSelected: Boolean) {
                dateTextView.text = date
                if (isSelected) {
                    // Apply highlighting to the selected date
                    itemView.setBackgroundResource(R.drawable.selected_date_item_background)
                    dateTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.selected_date_text_color))
                } else {
                    // Reset background and text color for unselected dates
                    itemView.setBackgroundResource(0)
                    dateTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.date_text_color))
                }


                // Add click listener to handle item click
                itemView.setOnClickListener {
                    val clickedDate = dateList[adapterPosition]
                    val previousSelectedPosition = selectedItemPosition
                    selectedItemPosition = adapterPosition
                    notifyItemChanged(previousSelectedPosition)
                    notifyItemChanged(selectedItemPosition)
                    // Handle the click event for the selected date

                    Toast.makeText(
                        itemView.context,
                        "Clicked date: $clickedDate",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}