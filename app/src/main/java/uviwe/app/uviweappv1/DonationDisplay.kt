package uviwe.app.uviweappv1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uviweappv1.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class DonationDisplay : AppCompatActivity() {
    lateinit var donAdapter: DonDisplayAdapter
    private val Items: MutableList<DonationData> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_display)

        donAdapter = DonDisplayAdapter()

        val txtStartDate = findViewById<EditText>(R.id.txtStartDate)
        val txtEndDate = findViewById<EditText>(R.id.txtEndDate)
        val btnFilter = findViewById<Button>(R.id.btnFilter)
        val feed = findViewById<RecyclerView>(R.id.feedDonDisplay)
        val btnExport = findViewById<Button>(R.id.btnExport)

        feed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = donAdapter
        }

        val database =
            FirebaseDatabase.getInstance("https://wil-prototype-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("Donations")
        val executor = Executors.newSingleThreadExecutor()
        myRef.addValueEventListener(object :
            ValueEventListener { //has been adjusted to timesheet values

            override fun onDataChange(snapshot: DataSnapshot) {
                Items.clear()

                for (unique in snapshot.children) {
                    val Date = unique.child("Date").getValue().toString()
                        Items.add(
                            DonationData(
                                Name = unique.child("FirstName").getValue().toString(),
                                Surname = unique.child("LastName").getValue().toString(),
                                Note = unique.child("Notes").getValue().toString(),
                                Amount = unique.child("Amount").getValue().toString(),
                                Date = Date,
                                Contact = unique.child("Contact").getValue().toString(),
                                Email = unique.child("PayerEmail").getValue().toString())
                        )
                    }


                donAdapter.notifyDataSetChanged()

                Handler(Looper.getMainLooper()).post {
                    donAdapter.submitList(Items)
                }

            }


            override fun onCancelled(error: DatabaseError) { //handles errors
                Log.w("Firebase", "Failed to read value", error.toException())
            }
        })


        btnFilter.setOnClickListener(){
            val startDate = txtStartDate.text.toString().trim()
            val endDate = txtEndDate.text.toString().trim()

            if (startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Please enter both start date and end date", Toast.LENGTH_SHORT).show()
            } else {
            executor.execute {

                myRef.addValueEventListener(object :
                    ValueEventListener { //has been adjusted to timesheet values

                    override fun onDataChange(snapshot: DataSnapshot) {
                        Items.clear()

                        for (unique in snapshot.children) {
                            val Date = unique.child("Date").getValue().toString()
                            if (Date in startDate.. endDate){
                                Items.add(
                                    DonationData(
                                        Name = unique.child("FirstName").getValue().toString(),
                                        Surname = unique.child("LastName").getValue().toString(),
                                        Note = unique.child("Notes").getValue().toString(),
                                        Amount = unique.child("Amount").getValue().toString(),
                                        Date = Date,
                                        Contact = unique.child("Contact").getValue().toString(),
                                        Email = unique.child("PayerEmail").getValue().toString())
                                )
                            }

                        }
                        donAdapter.notifyDataSetChanged()

                        Handler(Looper.getMainLooper()).post {
                            donAdapter.submitList(Items)
                        }

                    }


                    override fun onCancelled(error: DatabaseError) { //handles errors
                        Log.w("Firebase", "Failed to read value", error.toException())
                    }
                })

                }
            }
            }
        btnExport.setOnClickListener {
            exportToTxt(Items)
        }
        }

    private fun exportToTxt(dataList: List<DonationData>) {
        try {
            // Create a file object for the external storage directory
            val file = File(
                Environment.getExternalStorageDirectory(),
                "donation_Report.txt"
            )

            // Create a FileOutputStream and an OutputStreamWriter to write to the file
            val fileOutputStream = FileOutputStream(file)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)

            // Write header to the file
            outputStreamWriter.write("Name, Surname, Note, Amount, Date, Contact, Email\n")

            // Write data to the file
            for (item in dataList) {
                val line = "${item.Name}, ${item.Surname}, ${item.Note}, ${item.Amount}, ${item.Date}, ${item.Contact}, ${item.Email}\n\n"
                outputStreamWriter.write(line)
            }

            // Close the OutputStreamWriter and FileOutputStream
            outputStreamWriter.close()
            fileOutputStream.close()

            // Show a toast indicating successful export
            Toast.makeText(
                this,
                "Data exported to ${file.absolutePath}",
                Toast.LENGTH_SHORT
            ).show()
            Log.i("DonationDisplay", "Data exported to ${file.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error exporting data", Toast.LENGTH_SHORT).show()
            // Handle the exception (e.g., show an error message to the user)
            Log.e("DonationDisplay", "Error exporting data", e)
        }
    }

    }
