package uviwe.app.uviweappv1




import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.uviweappv1.R
import com.google.firebase.database.FirebaseDatabase


class FinDisplayAdapter (): ListAdapter<FinanceData, FinDisplayAdapter.FinDisplayAdapter>(FinanceViewHolder()) {
    val database =
        FirebaseDatabase.getInstance("https://wil-prototype-default-rtdb.europe-west1.firebasedatabase.app/")
    class FinDisplayAdapter(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):FinDisplayAdapter{
        val inflater = LayoutInflater.from(parent.context)
        return FinDisplayAdapter(inflater.inflate(R.layout.item_fin_display, parent, false))
    }

    override fun onBindViewHolder(holder: FinDisplayAdapter, position: Int) {
        val fin = getItem(position)

        holder.itemView.apply {
            findViewById<TextView>(R.id.txtDateFin).text = "Date Paid: " + fin.Date
            findViewById<TextView>(R.id.txtAmountFin).text = "Amount: " + fin.Amount
        }

    }
    class FinanceViewHolder : DiffUtil.ItemCallback<FinanceData>() {
        override fun areContentsTheSame(
            oldItem: FinanceData,
            newItem: FinanceData
        ): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }

        override fun areItemsTheSame(
            oldItem: FinanceData,
            newItem: FinanceData
        ): Boolean {
            return oldItem.Date == newItem.Date
        }
    }
}