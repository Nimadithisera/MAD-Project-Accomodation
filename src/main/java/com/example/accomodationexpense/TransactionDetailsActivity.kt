package com.example.accomodationexpense



import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TransactionDetailsActivity : AppCompatActivity() {
    private lateinit var transaction: Transaction
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)

        var transaction: Transaction =intent.getSerializableExtra("transaction") as Transaction
        val updatebtn:Button=findViewById(R.id.updateTransactionButton)
        val labelInput: TextInputEditText =findViewById(R.id.ExpenseLabelInput)
        val amountInput: TextInputEditText =findViewById(R.id.AmountInput)
        val labelLayout: TextInputLayout =findViewById(R.id.ExpenseLabel)
        val descriptionInput: TextInputEditText =findViewById(R.id.DescriptionInput)
        val amountLayout: TextInputLayout =findViewById(R.id.Amount)
        val closebtn: ImageButton =findViewById(R.id.closeBtn)
        val rootView:View=findViewById(R.id.rootView)


        labelInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        descriptionInput.setText(transaction.description)

        rootView.setOnClickListener{
            this.window.decorView.clearFocus()

            val imm=getSystemService(Context.INPUT_METHOD_SERVICE)as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken,0)
        }

        labelInput.addTextChangedListener {
            updatebtn.visibility= View.VISIBLE
            if(it!!.count()>0)
            {
                labelLayout.error=null
            }
        }
        amountInput.addTextChangedListener {
            updatebtn.visibility= View.VISIBLE
            if(it!!.count()>0)
            {
                amountLayout.error=null
            }
        }
        descriptionInput.addTextChangedListener {
            updatebtn.visibility= View.VISIBLE
        }

        updatebtn.setOnClickListener{
            val label:String=labelInput.text.toString()
            val amount:Double?=amountInput.text.toString().toDoubleOrNull()
            val description:String=descriptionInput.text.toString()

            if(label.isEmpty())
                labelLayout.error="Please enter valid Expense Name"

            else if(amount==null)
                amountLayout.error="Please enter valid amount"
            else {
                val transaction=Transaction(transaction.id,label,amount, description )
                update(transaction)
            }
        }
        closebtn.setOnClickListener{
            finish()
        }
    }

    private fun update(transaction: Transaction){
        val  db= Room.databaseBuilder(this,AppDatabase::class.java,"transactions").build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }
    }
}