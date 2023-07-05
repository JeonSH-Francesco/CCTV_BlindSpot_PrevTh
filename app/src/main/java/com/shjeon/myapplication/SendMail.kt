package com.shjeon.myapplication

import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.util.Properties;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.Message
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart

class SendMail : Authenticator() {
    private val fromEmail = "Your email"
    private val password = "Your Password"

    // 보내는 사람 계정 확인
    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(fromEmail, password)

    }

    // 메일 보내기
    fun sendEmail(fileName: String?, outPut: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val props = Properties()
            props.setProperty("mail.transport.protocol", "smtp")
            props.setProperty("mail.host", "smtp.gmail.com")
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.port"] = "465"
            props["mail.smtp.socketFactory.port"] = "465"
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.socketFactory.fallback"] = "false"
            props.setProperty("mail.smtp.quitwait", "false")

            // 구글에서 지원하는 smtp 정보를 받아와 MimeMessage 객체에 전달
            val session = Session.getDefaultInstance(props, this@SendMail)

            // 메시지 객체 만들기
            val message = MimeMessage(session)
            message.sender = InternetAddress(fromEmail)                                 // 보내는 사람 설정
            message.addRecipient(Message.RecipientType.TO, InternetAddress("Email to send"))    // 받는 사람 설정
            message.subject = "이메일 제목"                                              // 이메일 제목
            message.setText("이메일 내용")                                               // 이메일 내용

            // 파일을 담기 위한 Multipart 생성
            //val fileName = "2022-05-03 15-05-22.mp3"
            //val filePath  =  Environment.getExternalStorageDirectory().absolutePath + "/Download/" + fileName
            //Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show()
            //Log.d(filePath)
            val multipart = MimeMultipart()
            val messageBodyPart = MimeBodyPart()
            val source = FileDataSource(outPut)



            messageBodyPart.dataHandler = DataHandler(source)
            messageBodyPart.fileName = fileName
            multipart.addBodyPart(messageBodyPart)

            // 메시지에 파일 담고
            message.setContent(multipart)

            // 전송
            Transport.send(message)
        }

    }

}
