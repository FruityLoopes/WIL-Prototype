package uviwe.app.uviweappv1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.example.uviweappv1.R

class finance : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance)

        val date = findViewById<EditText>(R.id.txtdate)
        val amountpaid = findViewById<EditText>(R.id.txtamountpaid)
        val FirstName = findViewById<EditText>(R.id.txtFirstName)
        val LastName = findViewById<EditText>(R.id.txtLastName)
        val Contact = findViewById<EditText>(R.id.txtContact)
        val Email = findViewById<EditText>(R.id.txtPayerEmail)
        val Confirm = findViewById<CheckBox>(R.id.cbConfirmationFinance)
        val Record = findViewById<Button>(R.id.btnRecordFinance)
    }
}