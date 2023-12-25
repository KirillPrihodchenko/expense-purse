package com.example.financetracker

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.financetracker.entity.AppDatabase
import com.example.financetracker.entity.Transaction
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditAndDeleteActivity : AppCompatActivity() {

    private lateinit var transaction: Transaction
    private lateinit var updateBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var closeBtn: ImageButton
    private lateinit var orientationInput: TextInputEditText
    private lateinit var amountInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var orientationLayout: TextInputLayout
    private lateinit var amountLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val transaction = intent.getSerializableExtra("transactions") as Transaction
         updateBtn = findViewById(R.id.updateBtn)
         deleteBtn = findViewById(R.id.deleteBtn)
         closeBtn = findViewById(R.id.closeBtn)
         orientationInput = findViewById(R.id.labelInput)
         amountInput = findViewById(R.id.amountInput)
         descriptionInput = findViewById(R.id.descriptionInput)
         orientationLayout = findViewById(R.id.labelLayout)
         amountLayout = findViewById(R.id.amountLayout)

        orientationInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        descriptionInput.setText(transaction.description)

        orientationInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if(it!!.isNotEmpty())
                orientationLayout.error = null
        }

        amountInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if (it!!.isNotEmpty())
                amountLayout.error = null
        }

        updateBtn.setOnClickListener {
            val label = orientationInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()

            if (label.isEmpty())
                orientationLayout.error = "Please enter a valid label"

            else if (amount == null)
                amountLayout.error = "Please enter a valid amount"

            else {
                val transaction = Transaction(transaction.id, label, amount, description)
                update(transaction)
                showSuccessSnackBar()
            }
        }

        deleteBtn.setOnClickListener {
            delete(transaction)
            setResult(RESULT_OK)
            finish()
        }

        closeBtn.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun update(transaction: Transaction) {

        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "transactions"
        ).build()

        GlobalScope.launch {
            database.transactionDao().updateTransaction(transaction)
        }
    }

    private fun delete(transaction: Transaction) {

        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "transactions"
        ).build()

        GlobalScope.launch {
            database.transactionDao().deleteTransaction(transaction)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showSuccessSnackBar() {
        val rootView: View = findViewById(android.R.id.content)
        val snackbar = Snackbar.make(rootView, "Transaction changed successfully", Snackbar.LENGTH_SHORT)

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        val layoutParams = snackbarLayout.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.TOP
        snackbarLayout.layoutParams = layoutParams

        snackbar.show()
    }
}