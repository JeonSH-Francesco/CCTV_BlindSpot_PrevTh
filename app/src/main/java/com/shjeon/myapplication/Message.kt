package com.shjeon.myapplication

//import kotlinx.android.synthetic.main.activity_layout.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.message.*
import kotlinx.coroutines.InternalCoroutinesApi

class Message : Activity() {

    private var addPhoneNum: Button? = null

    //private var phoneNum: TextView? = null
    private var btnBack: Button? = null

    //val tag = "Message"
    var db: AppDatabase? = null
    var contactsList = mutableListOf<Contacts>()
    val adapter = ContactsListAdapter(contactsList)

    @OptIn(InternalCoroutinesApi::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message)

        db = AppDatabase.getInstance(this)

        //db 연결
        /*db = Room.databaseBuilder(
            applicationContext, AppDatabase::class.java, "database"
        ).allowMainThreadQueries().build()*/

        // db에 저장된 데이터 불러오기
        val savedContacts = db!!.contactsDao().getAll()
        if (savedContacts.isNotEmpty()) {
            //phoneNum?.setText(savedContacts.toString())
            contactsList.addAll(savedContacts)
        }

        //db.contactsDao().insert(Contacts(0, name.toString(), phone.toString()))


        addPhoneNum = findViewById(R.id.addPhoneNum)
        //phoneNum = findViewById(R.id.phoneNum)
        //btnBack = findViewById(R.id.btnBack2)

        //phoneNum?.setMovementMethod(ScrollingMovementMethod())

        addPhoneNum?.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK);
            intent.data = Uri.parse("content://com.android.contacts/data/phones");
            startActivityForResult(intent, 10);

        }

        btnBack?.setOnClickListener {
            super.onBackPressed()
        }



        //어댑터, 아이템 클릭 : 아이템 삭제
        //val adapter = ContactsListAdapter(contactsList)
        adapter.setItemClickListener(object : ContactsListAdapter.OnItemClickListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onClick(v: View, position: Int) {

                val contacts = contactsList[position]

                db?.contactsDao()?.delete(contacts = contacts) //DB에서 삭제
                contactsList.removeAt(position) //리스트에서 삭제
                adapter.notifyDataSetChanged() //리스트뷰 갱신

                Log.d("Message", "remove item($position). name:${contacts.name}")
            }
        })
        phoneNum.adapter = adapter

    }

    //val adapter = ContactsListAdapter(contactsList)
    @SuppressLint("SetTextI18n", "Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 10 && resultCode == RESULT_OK) {

            val result = data?.dataString

            val id = Uri.parse(data?.dataString).lastPathSegment;

            val cursor: Cursor? = contentResolver.query(
                ContactsContract.Data.CONTENT_URI, arrayOf<String>(
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                ContactsContract.Data._ID.toString() + "=" + id, null, null
            )
            cursor?.moveToFirst();

            val name = cursor?.getString(0);

            val phone = cursor?.getString(1);

            val contact = Contacts(0, name.toString(), phone.toString())
            db?.contactsDao()?.insertAll(contact)
            contactsList.add(contact)
            adapter.notifyDataSetChanged()




        }

    }

}
//}