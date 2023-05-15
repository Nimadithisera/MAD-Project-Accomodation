package com.example.accomodationexpense

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransactionActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction2)

        val addtbtn:Button=findViewById(R.id.addTransactionButton)
        val labelInput:TextInputEditText=findViewById(R.id.ExpenseLabelInput)
        val amountInput:TextInputEditText=findViewById(R.id.AmountInput)
        val labelLayout:TextInputLayout=findViewById(R.id.ExpenseLabel)
        val descriptionInput:TextInputEditText=findViewById(R.id.DescriptionInput)
        val amountLayout:TextInputLayout=findViewById(R.id.Amount)
        val closebtn:ImageButton=findViewById(R.id.closeBtn)

        labelInput.addTextChangedListener {
            if(it!!.count()>0)
            {
                labelLayout.error=null
            }
        }
        amountInput.addTextChangedListener {
            if(it!!.count()>0)
            {
                amountLayout.error=null
            }
        }

        addtbtn.setOnClickListener{
            val label:String=labelInput.text.toString()
            val amount:Double?=amountInput.text.toString().toDoubleOrNull()
            val description:String=descriptionInput.text.toString()

            if(label.isEmpty())
                labelLayout.error="Please enter valid Expense Name"

            else if(amount==null)
                amountLayout.error="Please enter valid amount"
            else {
                val transaction=Transaction(0,label,amount, description )
                insert(transaction)
            }
        }
        closebtn.setOnClickListener{
            finish()
        }
    }

    private fun insert(transaction: Transaction){
        val  db= Room.databaseBuilder(this,AppDatabase::class.java,"transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }
}