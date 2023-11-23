package uviwe.app.uviweappv1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uviweappv1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors


class Attendance : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var attAdapter: AttendanceAdapter
    private val Items: MutableList<AttendanceData> = mutableListOf()
    private lateinit var dateRecyclerView: RecyclerView
    private lateinit var dateAdapter: DateAdapter
    private lateinit var spinClass: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        auth = Firebase.auth

        dateRecyclerView = findViewById(R.id.dateRecyclerView)
        dateAdapter = DateAdapter()
        spinClass = findViewById(R.id.spClass)

        val listClass : ArrayList<String> = ArrayList()
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_layout, listClass)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_layout)

        val recordAtt: Button = findViewById(R.id.btnRecordAtt)
        val feed: RecyclerView = findViewById(R.id.feedAtt)
        attAdapter = AttendanceAdapter { clickedAttendance ->
            Log.d("Attendance", "Clicked attendance: ${clickedAttendance.Student}, Attended: ${clickedAttendance.attended}")
        }
        feed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = attAdapter
        }
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        dateRecyclerView.layoutManager = layoutManager
        dateRecyclerView.adapter = dateAdapter

        // Populate the date list with the desired range of dates
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.DAY_OF_MONTH, -7) // Start from 7 days ago

        val endDate = Calendar.getInstance()

        val dateList = ArrayList<Date>()


        while (startDate <= endDate) {
            dateList.add(startDate.time)
            startDate.add(Calendar.DAY_OF_MONTH, 1)
        }

        dateAdapter.setDateList(dateList)

        val database =
            FirebaseDatabase.getInstance("https://wil-prototype-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference()
        val executor = Executors.newSingleThreadExecutor()

        myRef.addValueEventListener(object: ValueEventListener { //must be adjusted to timesheet values

            override fun onDataChange(snapshot: DataSnapshot) {

                for(keys in snapshot.children){
                    listClass.add(keys.key.toString())
                }

                arrayAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase","Failed to read value", error.toException())
            }
        })
        spinClass.adapter= arrayAdapter

        spinClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, arg3: Long) {
                val spinnerValue = parent.getItemAtPosition(position).toString()
                Toast.makeText(baseContext, "Selected Class $spinnerValue", Toast.LENGTH_SHORT).show()
                val myRef = database.getReference(spinnerValue).child("Students")
                executor.execute {

                    myRef.addValueEventListener(object :
                        ValueEventListener { //has been adjusted to timesheet values

                        override fun onDataChange(snapshot: DataSnapshot) {
                            Items.clear()

                            for (unique in snapshot.children) {
                                val Student = unique.key.toString()

                                Items.add(
                                    AttendanceData( Student = Student)
                                )
                            }
                            attAdapter.notifyDataSetChanged()

                        }


                        override fun onCancelled(error: DatabaseError) { //handles errors
                            Log.w("Firebase", "Failed to read value", error.toException())
                        }
                    })
                    Handler(Looper.getMainLooper()).post {
                        attAdapter.submitList(Items)
                    }

                }
            }
            override fun onNothingSelected(arg0: AdapterView<*>) {
                // TODO Auto-generated method stub
            }
        }

        recordAtt.setOnClickListener(){
            val attendedStudents = Items.filter { it.attended }
            // Process the list of attended students as needed
            // For example, you can print their names:
            for (student in attendedStudents) {
                Log.d("Attendance", "Attended: ${student.Student}")
            }
        }

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
        ): DateViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
            return DateViewHolder(view)
        }
        private var selectedItemPosition = RecyclerView.NO_POSITION
        override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
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
                    dateTextView.setTextColor(ContextCompat.getColor(itemView.context,
                        R.color.selected_date_text_color
                    ))
                } else {
                    // Reset background and text color for unselected dates
                    itemView.setBackgroundResource(0)
                    dateTextView.setTextColor(ContextCompat.getColor(itemView.context,
                        R.color.date_text_color
                    ))
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


