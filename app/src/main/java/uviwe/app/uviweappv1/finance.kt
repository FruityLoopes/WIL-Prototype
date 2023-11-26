package uviwe.app.uviweappv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.uviweappv1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executors

class finance : AppCompatActivity() {
    private var spinnerValue : String = ""
    private var spinnerChild : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance)

        val date = findViewById<TextView>(R.id.txtdate)
        val amountpaid = findViewById<TextView>(R.id.txtamountpaid)
        val FirstName = findViewById<TextView>(R.id.txtFirstName)
        val LastName = findViewById<TextView>(R.id.txtLastName)
        val Contact = findViewById<TextView>(R.id.txtContact)
        val Email = findViewById<TextView>(R.id.txtPayerEmail)
        val ConfirmFi = findViewById<CheckBox>(R.id.cbConfirmationFinance)
        val Record = findViewById<Button>(R.id.btnRecordFinance)
        val spClass = findViewById<Spinner>(R.id.spinClass)
        val spChild = findViewById<Spinner>(R.id.spinChild)
        val FinDisplay = findViewById<Button>(R.id.btnFinDisplay)

        val listClass : ArrayList<String> = ArrayList()
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_layout, listClass)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_layout)

        val Items : ArrayList<String> = ArrayList()
        val attAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_layout, Items)
        attAdapter.setDropDownViewResource(R.layout.spinner_layout)

        val c = Calendar.getInstance().time

        val df = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val fb = SimpleDateFormat("yyyyMMdd" ,  Locale.getDefault())
        val formattedDate = df.format(c)
        val formattedFB = fb.format(c)

        date.text = formattedDate.toString()

        val database =
            FirebaseDatabase.getInstance("https://wil-prototype-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference()
        val executor = Executors.newSingleThreadExecutor()

        myRef.addValueEventListener(object: ValueEventListener { //must be adjusted to timesheet values

            override fun onDataChange(snapshot: DataSnapshot) {
                listClass.clear()
                for(keys in snapshot.children){
                    if(keys.key.toString() != "Donations"){
                        listClass.add(keys.key.toString())
                    }

                }

                Handler(Looper.getMainLooper()).post {
                    spClass.adapter = arrayAdapter
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase","Failed to read value", error.toException())
            }
        })


        FinDisplay.setOnClickListener(){
            val intent = Intent(this, FinanceDisplay::class.java)
            startActivity(intent)
        }
        spClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, arg3: Long) {
                spinnerValue = parent.getItemAtPosition(position).toString()

                val myRef = database.getReference(spinnerValue).child("Students")
                executor.execute {
                    myRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Items.clear()

                            for (unique in snapshot.children) {
                                Items.add(unique.key.toString())
                            }

                            Handler(Looper.getMainLooper()).post {
                                spChild.adapter = attAdapter
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w("Firebase", "Failed to read value", error.toException())
                        }
                    })
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
                // TODO: Implement the behavior when nothing is selected
            }
        }

        spChild.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, arg3: Long) {
                spinnerChild = parent.getItemAtPosition(position).toString()

                val myRef1 = database.getReference(spinnerValue).child("Students").child(spinnerChild)
                executor.execute {

                    myRef1.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            for (unique in snapshot.children) {
                                if(unique.child("FirstName").value != null){
                                    FirstName.text = unique.child("FirstName").getValue().toString()
                                    LastName.text = unique.child("LastName").getValue().toString()
                                    Contact.text = unique.child("Contact").getValue().toString()
                                    Email.text = unique.child("PayerEmail").getValue().toString()
                                } else {
                                    FirstName.text = ""
                                    LastName.text = ""
                                    Contact.text = ""
                                    Email.text = ""
                                }

                            }


                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w("Firebase", "Failed to read value", error.toException())
                        }
                    })
                }
            }
            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }



        Record.setOnClickListener(){
            val myRef = database.getReference(spinnerValue).child("Students").child(spinnerChild).child("FeePayer")
            if(ConfirmFi.isChecked){
                myRef.child("FirstName").setValue(FirstName.text.toString())
                myRef.child("LastName").setValue(LastName.text.toString())
                myRef.child("Contact").setValue(Contact.text.toString())
                myRef.child("PayerEmail").setValue(Email.text.toString())
                myRef.child("Fees Paid").child(formattedFB).setValue(amountpaid.text.toString())

                Toast.makeText(this, "Payment Recorded.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please Confirm", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
