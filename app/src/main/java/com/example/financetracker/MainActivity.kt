package com.example.financetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.financetracker.entity.AppDatabase
import com.example.financetracker.entity.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var transactions: List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recycleView)

//---------------------init navigation menu------------------------//
        val buttonNavigation: BottomNavigationView = findViewById(R.id.button_navigation)
        buttonNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_nav_home -> {
                }
                R.id.ic_nav_add -> {
                    val intent = Intent(this, AddActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
//------------------------------------------------------------------//

        transactions = arrayListOf()

        transactionAdapter = TransactionAdapter(transactions)
        linearLayoutManager = LinearLayoutManager(this)

        recyclerView.apply {
            adapter = transactionAdapter
            layoutManager = linearLayoutManager
        }

        database = Room.databaseBuilder(
            this,
                AppDatabase::class.java,
            "transactions"
        ).build()
    }

    private fun fetch() {
        GlobalScope.launch {
            transactions = database.transactionDao().findAllTransaction()

            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetch()
    }

    private fun updateDashboard() {

        val totalAmount = transactions.map { it.amount }.sum()
        val budgetAmount = transactions.filter { it.amount>0 }.map{it.amount}.sum()
        val expenseAmount = totalAmount - budgetAmount

        val balance: TextView = findViewById(R.id.balance)
        val budget: TextView = findViewById(R.id.budget)
        val expense: TextView = findViewById(R.id.expense)

        balance.text = "₴ %.1f".format(totalAmount)
        budget.text = "₴ %.1f".format(budgetAmount)
        expense.text = "₴ %.1f".format(expenseAmount)
    }
}