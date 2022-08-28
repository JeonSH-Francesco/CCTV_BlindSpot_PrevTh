package com.shjeon.myapplication

import androidx.room.*
@Entity(tableName = "tb_contacts")
data class Contacts(
    @PrimaryKey(autoGenerate = true) val pno: Long,
    var name: String,
    var phone: String
)


