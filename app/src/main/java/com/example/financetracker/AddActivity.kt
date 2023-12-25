package com.example.financetracker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.financetracker.entity.AppDatabase
import com.example.financetracker.entity.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    private lateinit var addTransactionBtn: Button
    private lateinit var labelInput: TextInputEditText
    private lateinit var amountInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var labelLayout: TextInputLayout
    private lateinit var amountLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        addTransactionBtn = findViewById(R.id.addTransactionBtn)
        labelInput = findViewById(R.id.labelInput)
        amountInput = findViewById(R.id.amountInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        labelLayout = findViewById(R.id.labelLayout)
        amountLayout = findViewById(R.id.amountLayout)


        //---------------------init navigation menu------------------------//
        val buttonNavigation: BottomNavigationView = findViewById(R.id.button_navigation_tr)
        buttonNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.ic_nav_add -> {

                }
            }
            true
        }
        //------------------------------------------------------------------//

        labelInput.addTextChangedListener {
            if(it!!.isNotEmpty())
                labelLayout.error = null
        }

        amountInput.addTextChangedListener {
            if (it!!.isNotEmpty())
                amountLayout.error = null
        }

        addTransactionBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()

            if (label.isEmpty())
                labelLayout.error = "Please enter a valid label"

            else if (amount == null)
                amountLayout.error = "Please enter a valid amount"

            else {
                val transaction = Transaction(0, label, amount, description)
                insert(transaction)
                showSuccessSnackBar()
                clearInputFields()
            }
        }
    }

    private fun insert(transaction: Transaction) {

        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "transactions"
        ).build()

        GlobalScope.launch {
            database.transactionDao().insertAllTransaction(transaction)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showSuccessSnackBar() {
        val rootView: View = findViewById(android.R.id.content)
        val snackbar = Snackbar.make(rootView, "Transaction added successfully", Snackbar.LENGTH_SHORT)

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        val layoutParams = snackbarLayout.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.TOP
        snackbarLayout.layoutParams = layoutParams

        snackbar.show()
    }

    private fun clearInputFields() {
        labelInput.text = null
        amountInput.text = null
        descriptionInput.text = null
    }
}