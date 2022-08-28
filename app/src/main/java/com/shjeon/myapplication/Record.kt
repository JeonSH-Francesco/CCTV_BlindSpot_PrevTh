package com.shjeon.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.telephony.SmsManager
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.InternalCoroutinesApi
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer


class Record : AppCompatActivity() {
    private var btnBack : Button? = null
    private var timerTask : Timer? = null
    private var mediaRecorder: MediaRecorder? = null

    var db: AppDatabase? = null

    var intent3: Intent? = null


    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record)
        title = "음성 인식설정"
        db = AppDatabase.getInstance(this)
        var intent2 = Intent(this, MainActivity::class.java)
        intent3 = intent2

        btnBack = findViewById(R.id.btnBack)

        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

        val mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        mRecognizer.setRecognitionListener(listener)

        val reccon: Button = findViewById<View>(R.id.reccon) as Button
        val recunocn: Button = findViewById<View>(R.id.recunocn) as Button

        reccon.setOnClickListener(object : View.OnClickListener {//음성 인식 자동
        override fun onClick(view: View?) {
            reccon.isEnabled = false
            recunocn.isEnabled = true
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
                return
            }
            try {
                timerTask = timer(period = 3000) {
                    runOnUiThread{
                        mRecognizer.startListening(i)
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }

        }
        })

        recunocn.setOnClickListener(object : View.OnClickListener {//음성 인식 해제
        override fun onClick(view: View?) {
            reccon.isEnabled = true
            recunocn.isEnabled = false
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
                return
            }
            try {
                timerTask?.cancel()

            } catch (e: SecurityException) {
                e.printStackTrace()
            }

        }
        })

        btnBack?.setOnClickListener{
            super.onBackPressed()
        }

    }





    private val listener: RecognitionListener = object : RecognitionListener {

        override fun onRmsChanged(rmsdb: Float) {

            // TODO Auto-generated method stub

        }



        @SuppressLint("SetTextI18n", "UnlocalizedSms", "SimpleDateFormat")
        override fun onResults(results: Bundle) {

            // TODO Auto-generated method stub
            var key = ""
            key = SpeechRecognizer.RESULTS_RECOGNITION
            val mResult = results.getStringArrayList(key)
            val rs = arrayOfNulls<String>(mResult!!.size)
            mResult.toArray(rs)

            Toast.makeText(this@Record, "${rs[0].toString()}", Toast.LENGTH_SHORT).show()

            val smsManager = SmsManager.getDefault()

            //db = AppDatabase.getInstance(Record())
            val selectDB = db!!.contactsDao().get()

            //val selectDB = db?.contactsDao()?.get()
            if(rs[0].toString().replace(" ","").contains("살려줘")) {
                startActivity(intent3)
                onMapReady()

                startActivity(Intent("android.intent.action.CALL", Uri.parse("tel:112")))//전화기능
                timerTask?.cancel()

                for (i in 0 until selectDB.size) {
                    smsManager.sendTextMessage(selectDB[i], null, "위험이 발생했습니다.", null, null);//메세지 기능
                }

                // 녹화기능
                val date = Date()
                val dateFormat1 = SimpleDateFormat("HH:mm:ss")
                val calendar = Calendar.getInstance()

                calendar.time = date
                calendar.add(Calendar.MINUTE, 1)

                val time = calendar.time
                val futureTime = dateFormat1.format(time)


                timer(period = 1000) {
                    runOnUiThread {
                        val current = System.currentTimeMillis()
                        val dateFormat = SimpleDateFormat("HH:mm:ss")
                        val currentTime = dateFormat.format(current)

                        if (currentTime == futureTime) {
                            stopRecording()//녹화중단
                            SendMail().sendEmail(fileNameRecord, outputRecord)
                            Toast.makeText(applicationContext, "$outputRecord", Toast.LENGTH_SHORT).show()
                            Toast.makeText(applicationContext, "$fileNameRecord", Toast.LENGTH_SHORT).show()//메일보내기
                            Toast.makeText(applicationContext,
                                "이메일을 성공적으로 보냈습니다.",
                                Toast.LENGTH_SHORT).show()//메일보내기
                        }
                    }
                }

            }

        }

        override fun onReadyForSpeech(params: Bundle) {

            // TODO Auto-generated method stub
        }

        override fun onPartialResults(partialResults: Bundle) {

            // TODO Auto-generated method stub
        }

        override fun onEvent(eventType: Int, params: Bundle) {

            // TODO Auto-generated method stub
        }

        override fun onError(error: Int) {

            // TODO Auto-generated method stub
        }

        override fun onEndOfSpeech() {
            //Toast.makeText(this@MainActivity, "end", Toast.LENGTH_SHORT).show()
            // TODO Auto-generated method stub
            //mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_RESTART);
        }

        override fun onBufferReceived(buffer: ByteArray) {

            // TODO Auto-generated method stub
        }

        override fun onBeginningOfSpeech() {

            // TODO Auto-generated method stub
        }
    }


    private var fileNameRecord: String? = null
    private var outputRecord: String? = null


    @SuppressLint("SimpleDateFormat")
    fun startRecording() {//녹화시작
        Toast.makeText(this, "녹화가 시작되었습니다.", Toast.LENGTH_SHORT).show()
        //config and create MediaRecorder Object
        val current = System.currentTimeMillis()
        val date = Date(current)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd kk-mm-ss", Locale("ko", "KR"))//녹화파일명

        fileNameRecord = dateFormat.format(date) + ".mp3"

        outputRecord = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + fileNameRecord //내장메모리 밑에 위치
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource((MediaRecorder.AudioSource.MIC))
        mediaRecorder?.setOutputFormat((MediaRecorder.OutputFormat.THREE_GPP))
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder?.setOutputFile(outputRecord)
        mediaRecorder?.prepare()
        mediaRecorder?.start()
    }

    private fun stopRecording() {//녹화완료
        Toast.makeText(this, "중지 되었습니다.", Toast.LENGTH_SHORT).show()
        mediaRecorder?.stop()
        mediaRecorder?.reset()
        mediaRecorder?.release()

    }


    private var fileNameScreen: String? = null
    private var outputScreen: String? = null

    fun onMapReady() {
        var googleMap = MainActivity.map2
        googleMap?.snapshot {
            it?.let{
                saveMediaToStorage(it)
            } }}
    private fun saveMediaToStorage(bitmap: Bitmap) {
        // Generating a file name
        fileNameScreen = "${System.currentTimeMillis()}.jpg"
        // Output stream
        var fos: OutputStream? = null
        outputScreen = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/" + fileNameScreen //내장메모리 밑에 위치
        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            this.contentResolver?.also { resolver ->
                // Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {
                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileNameScreen)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    //  SendMail().sendEmail(fileName, Environment.DIRECTORY_PICTURES)
                }
                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }

            }
        } else {
            // These for devices running on android < Q : 10
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, fileNameScreen)
            fos = FileOutputStream(image)
            //SendMail().sendEmail(fileName, output)
        }
        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this , "Captured View and saved to Gallery" , Toast.LENGTH_SHORT).show()
            //SendMail().sendEmail(fileName, Environment.DIRECTORY_PICTURES)
            SendMail().sendEmail(fileNameScreen, outputScreen)
        }
    }



}
