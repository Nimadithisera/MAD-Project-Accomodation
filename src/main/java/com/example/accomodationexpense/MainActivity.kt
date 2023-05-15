package com.example.accomodationexpense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var deletedTransaction: Transaction
    private lateinit var transactions:List<Transaction>
    private lateinit var oldTransactions:List<Transaction>
    private lateinit var transactionAdapter:TransactionAdapter
    private lateinit var linearLayoutManager:LinearLayoutManager
    private lateinit var db:AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        transactions= arrayListOf()


        val addbtn:FloatingActionButton=findViewById(R.id.addbtn)
        linearLayoutManager= LinearLayoutManager(this)
        transactionAdapter=TransactionAdapter(transactions)

        db= Room.databaseBuilder(this,AppDatabase::class.java,"transactions").build()


        val recyclerView:RecyclerView=findViewById(R.id.rv_list)
        recyclerView.apply {
            adapter=transactionAdapter
            layoutManager=linearLayoutManager
        }



        //swipe to delete
        val itemTouchHelper=object :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }

        }

        val swipeHelper=ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerView)
        addbtn.setOnClickListener{
            val intent=Intent(this,AddTransactionActivity2::class.java)
            startActivity(intent)
        }
    }

    private fun fetchAll(){
        GlobalScope.launch {

            transactions=db.transactionDao().getAll()
            runOnUiThread{
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }
    private fun updateDashboard(){
        val totalAmount: Double = transactions.map{it.amount}.sum()
        val budgetAmount:Double=transactions.filter{it.amount>0}.map { it.amount }.sum()
        val totalexpenses:Double=totalAmount-budgetAmount

        val balance:TextView=findViewById(R.id.balance)
        val budget:TextView=findViewById(R.id.budget)
        val expense:TextView=findViewById(R.id.expense)

        balance.text="Rs %.2f".format(totalAmount)
        budget.text="Rs %.2f".format(budgetAmount)
        expense.text="Rs %.2f".format(totalexpenses)

    }

    private fun undoDelete(){
        GlobalScope.launch {
            db.transactionDao().insertAll(deletedTransaction)

            transactions=oldTransactions
            runOnUiThread(){
                transactionAdapter.setData(transactions)
                updateDashboard()
            }
        }
    }

    private fun showSnackBar(){
        val view=findViewById<View>(R.id.coordinator)
        val snackbar=Snackbar.make(view,"Transaction Deleted",Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()
        }

            .setActionTextColor(ContextCompat.getColor(this,R.color.Red))
            .setActionTextColor(ContextCompat.getColor(this,R.color.white))
            .show()
    }

    private fun deleteTransaction(transaction: Transaction){
        deletedTransaction=transaction
        oldTransactions=transactions
        GlobalScope.launch {
            db.transactionDao().delete(transaction)
            transactions=transactions.filter { it.id!=transaction.id}
            runOnUiThread{
                updateDashboard()
                transactionAdapter.setData(transactions)
                showSnackBar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
    }
}