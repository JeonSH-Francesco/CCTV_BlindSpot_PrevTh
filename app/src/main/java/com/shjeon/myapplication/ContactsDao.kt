package com.shjeon.myapplication

import androidx.room.*

@Dao
interface ContactsDao {
    @Query("SELECT * FROM tb_contacts")
    fun getAll(): List<Contacts>

    @Insert
    fun insertAll(vararg contacts: Contacts)

    @Delete
    fun delete(contacts: Contacts)

    @Query("SELECT phone FROM tb_contacts")
    fun get(): List<String>

}
