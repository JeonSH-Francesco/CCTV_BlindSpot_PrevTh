package com.shjeon.myapplication


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.maps.MapsInitializer
import kotlinx.android.synthetic.main.menu.*
import java.lang.Exception


class Menu : Activity() {
    private var btnRec : Button? = null
    private var btnBack : Button? = null
    private var btnMessage: Button? = null
    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)
        title = "음성 인식설정"
        btnRec = findViewById(R.id.btnRec)
        btnBack = findViewById(R.id.btnBack)
        btnMessage = findViewById(R.id.btnTransmit)

        btnRec?.setOnClickListener{
            val intent1 = Intent(this, Record::class.java)
            startActivity(intent1)
        }

        //imageView.setImageResource(R.drawable.danger)

        btnTransmit?.setOnClickListener {
            val intent = Intent(this, Message::class.java)
            startActivity(intent)
        }


        btnBack?.setOnClickListener{
            //var intent2=Intent(applicationContext,MainActivity::class.java)
            //startActivity(intent2) //main로 화면 뒤로 가는 기능
            super.onBackPressed()
        }


    }

}