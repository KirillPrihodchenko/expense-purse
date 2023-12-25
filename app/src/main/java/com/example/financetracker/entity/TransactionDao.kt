package com.example.financetracker.entity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions")
    fun findAllTransaction(): List<Transaction>

    @Insert
    fun insertAllTransaction(vararg transaction: Transaction)

    @Update
    fun updateTransaction(vararg transaction: Transaction)

    @Delete
    fun deleteTransaction(transaction: Transaction)
}