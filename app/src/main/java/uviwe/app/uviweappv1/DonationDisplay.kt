package uviwe.app.uviweappv1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        }



    }
