
package com.shjeon.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton
import com.nightonke.boommenu.BoomMenuButton
import com.nightonke.boommenu.ButtonEnum
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.pedro.library.AutoPermissions.Companion.loadAllPermissions
import com.pedro.library.AutoPermissions.Companion.parsePermissions
import com.pedro.library.AutoPermissionsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*


class MainActivity : AppCompatActivity(), AutoPermissionsListener {
    private var button2: Button? = null
    private var btnDetect: Button? = null
    private var textView2: TextView? = null
    private var textView3: TextView? = null
    private var textView4: TextView? = null

    private val handler= Handler(Looper.getMainLooper())
    private val runnable= Runnable{
        btnDetect?.performClick()
    }

    var manager: LocationManager? = null
    var gpsListener: GPSListener? = null
    var mapFragment: SupportMapFragment? = null
    var map: GoogleMap? = null
    var myMarker: Marker? = null
    var myLocationMarker: MarkerOptions? = null
    var circle: Circle? = null
    var circle1KM: CircleOptions? = null

    var latitude : Double? = null
    var longitude : Double? = null
    var cnt: Int? = 0
    var tgr: Int? = null

    var det: Int? = 0
    var sum: Int? = 0

    companion object{
        var map2 : GoogleMap? = null
    }

    private val TAG: String? = "MyActivity"
    private var bmb: BoomMenuButton? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "CCTV 사각지대 위험 방지 앱"
        button2 = findViewById(R.id.button2)
        //btnGo = findViewById(R.id.btnGo)
        btnDetect = findViewById(R.id.btnDetect)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView3)
        textView4 = findViewById(R.id.textView4)
        manager = getSystemService(LOCATION_SERVICE) as LocationManager
        bmb = findViewById<View>(R.id.bmb) as BoomMenuButton

        boomMenu() //메뉴 이펙트

        gpsListener = GPSListener()
        try {
            MapsInitializer.initialize(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        btnDetect?.setOnClickListener {

            val assetManager: AssetManager = resources.assets

            val inputStream2: InputStream = assetManager.open("CCTVsurround_1.txt")

            inputStream2.bufferedReader().readLines().forEach {

                val token = it.split("\t")

                val dist = DistanceManager.getDistance(latitude!!, longitude!!, token[0].toDouble(), token[1].toDouble()).toString().toInt()

                if(dist >= 25){//25m 밖에
                    det = 0
                    sum = sum?.plus(det!!)
                }
                else { //25m 이내
                    det = 1
                    sum = sum?.plus(det!!)
                }
            }
            if(sum == 0){ //25m 밖에
                textView4?.text = "CCTV 탐지 존 밖입니다." + latitude + "\t" + longitude
                btnDetect?.setBackgroundColor(Color.RED)
                btnDetect?.text = "CCTV 탐지 존 밖입니다."
                Toast.makeText(applicationContext, "CCTV 탐지 존 밖입니다.", Toast.LENGTH_SHORT).show()
                tgr = 1
            }
            else{ //25m 이내
                textView4?.text = "CCTV 탐지 존 안입니다." + latitude + "\t" + longitude
                btnDetect?.setBackgroundColor(Color.GREEN)
                btnDetect?.text = "CCTV 탐지 존 안입니다."
                Toast.makeText(applicationContext, "CCTV 탐지 존 안입니다.", Toast.LENGTH_SHORT).show()
                tgr = 0
                sum = 0
            }
            handler.postDelayed(runnable,5000)

            if(tgr==1){
                cnt= cnt!! +1
            }

            if(cnt==1000 || cnt==2000){
                ShowNotification("Alert","CCTV 탐지 존 밖에 오래 머물러 있습니다.")
            }
        }


        try {
            MapsInitializer.initialize(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(OnMapReadyCallback { googleMap ->
            //Log.i("MyLocTest", "지도 준비됨")
            map = googleMap
            map2 = googleMap
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@OnMapReadyCallback
            }
            //map!!.isMyLocationEnabled = true

            startLocationService()

            val assetManager: AssetManager = resources.assets
            val inputStream: InputStream = assetManager.open("CCTVcheonan.txt")

            inputStream.bufferedReader().readLines().forEach {
                var token = it.split("\t")
                CoroutineScope(Dispatchers.Main).launch{
                    //Log.d("file_test", token[0].toString()) // for test
                    val cctv_loc = LatLng(token[11].toDouble(), token[12].toDouble())
                    map!!.addMarker(MarkerOptions().position(cctv_loc).title(token[3]).icon(BitmapDescriptorFactory.fromResource(R.drawable.cctv5)))
                    map!!.addCircle(CircleOptions().center(cctv_loc).radius(25.0).strokeWidth(1f).fillColor(Color.parseColor("#0DCBFF75")))//#1AFFFFFF
                }
            }
        })


        with(button2) {
            this?.setOnClickListener(View.OnClickListener { // 지도 보임
                startLocationService()
            })
        }
        loadAllPermissions(this, 101)

    }


    fun ShowNotification(Title:String, Body:String){
        val pending = PendingIntent.getActivity(this,0,Intent(this,MainActivity::class.java),PendingIntent.FLAG_CANCEL_CURRENT)
        val builder = NotificationCompat.Builder(this,"id")
        builder.setSmallIcon(R.drawable.danger)
            .setContentTitle("$Title")
            .setContentText("$Body")
            .setContentIntent(pending)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)

        val NManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NManager.createNotificationChannel(NotificationChannel("id","name", NotificationManager.IMPORTANCE_HIGH))

        }
        NManager.notify(0,builder.build())
    }

    object DistanceManager {

        private const val rs = 6372.8 * 1000

        fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
            val dLat = abs(Math.toRadians(lat2 - lat1))
            val dLon = abs(Math.toRadians(lon2 - lon1))
            val a =
                sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            val c = 2 * asin(sqrt(a))
            return (rs * c).toInt()
        }
    }

    private fun boomMenu(){//메뉴 이펙트
        assert(bmb != null)
        bmb!!.buttonEnum = ButtonEnum.Ham
        bmb!!.piecePlaceEnum = PiecePlaceEnum.HAM_2
        bmb!!.buttonPlaceEnum = ButtonPlaceEnum.HAM_2

        bmb!!.addBuilder(HamButton.Builder()
            .normalImageRes(R.drawable.butterfly)
            .normalText("녹음 설정")
            .subNormalText("음성인식기능을 할 수 있습니다.")
            .listener{
                val intent = Intent(this, Record::class.java)
                startActivity(intent)
            })


        bmb!!.addBuilder(HamButton.Builder()
            .normalImageRes(R.drawable.butterfly)
            .normalText("전송 설정")
            .subNormalText("위험 상황 탐지 후 자동 전송할 번호를 지정하세요.")
            .listener{
                val intent = Intent(this, Message::class.java)
                startActivity(intent)
            })

    }

    private fun startLocationService() {
        try {
            var location: Location? = null
            val minTime: Long = 0 // 0초마다 갱신 - 바로바로갱신
            val minDistance = 0f
            if (manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = manager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude

                    val message1 = "위도 : $latitude"
                    val message2 = "경도: $longitude"

                    textView2!!.text = message1
                    textView3!!.text = message2
                    showCurrentLocation(latitude!!, longitude!!)
                    //Log.i("MyLocTest", "최근 위치1 호출")
                }

                //위치 요청하기
                manager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener!!
                )
                //manager.removeUpdates(gpsListener);
                Toast.makeText(applicationContext, "내 위치1확인 요청함", Toast.LENGTH_SHORT).show()
                //Log.i("MyLocTest", "requestLocationUpdates() 내 위치1에서 호출시작 ~~ ")
            } else if (manager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = manager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    val message = "최근 위치2 \n위치 : $latitude\n 경도 : $longitude"
                    val message1 = "위도 : $latitude"
                    val message2 = "경도: $longitude"

                    textView2!!.text = message1
                    textView3!!.text = message2
                    showCurrentLocation(latitude!!, longitude!!)
                    Log.i("MyLocTest", "최근 위치2 호출")
                }


                //위치 요청하기
                manager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener!!
                )
                //manager.removeUpdates(gpsListener);
                Toast.makeText(applicationContext, "내 위치2확인 요청함", Toast.LENGTH_SHORT).show()
                //Log.i("MyLocTest", "requestLocationUpdates() 내 위치2에서 호출시작 ~~ ")
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    inner class GPSListener : LocationListener {
        // 위치 확인되었을때 자동으로 호출됨 (일정시간 and 일정거리)
        override fun onLocationChanged(location: Location) {
            latitude = location.latitude
            longitude = location.longitude

            val message1 = "위도 : $latitude"
            val message2 = "경도 : $longitude"

            textView2!!.text = message1
            textView3!!.text = message2

            showCurrentLocation(latitude!!, longitude!!)
            Log.i("MyLocTest", "onLocationChanged() 호출되었습니다.")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onResume() {
        super.onResume()

        // GPS provider를 이용전에 퍼미션 체크
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Toast.makeText(applicationContext, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        } else {
            if (manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, gpsListener!!)
                //manager.removeUpdates(gpsListener);
            } else if (manager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                manager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0f,
                    gpsListener!!
                )
                //manager.removeUpdates(gpsListener);
            }
            if (map != null) {
                map!!.isMyLocationEnabled = true
            }
            Log.i("MyLocTest", "onResume에서 requestLocationUpdates() 되었습니다.")
        }
    }

    override fun onPause() {
        super.onPause()
        manager!!.removeUpdates(gpsListener!!)
        if (map != null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map!!.isMyLocationEnabled = false
        }
        Log.i("MyLocTest", "onPause에서 removeUpdates() 되었습니다.")
    }

    private fun showCurrentLocation(latitude: Double, longitude: Double) {
        val curPoint = LatLng(latitude, longitude)
        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 17f))
        showMyLocationMarker(curPoint)
    }

    private fun showMyLocationMarker(curPoint: LatLng) {
        if (myLocationMarker == null) {
            myLocationMarker = MarkerOptions() // 마커 객체 생성
            myLocationMarker!!.position(curPoint)
            myLocationMarker!!.title("최근위치 \n")
            myLocationMarker!!.snippet("*GPS로 확인한 최근위치")
            myLocationMarker!!.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation))
            myMarker = map!!.addMarker(myLocationMarker!!)
        } else {
            myMarker!!.remove() // 마커삭제
            myLocationMarker!!.position(curPoint)
            myMarker = map!!.addMarker(myLocationMarker!!)
        }

        // 반경추가
        if (circle1KM == null) {
            circle1KM = CircleOptions().center(curPoint) // 원점
                .radius(1000.0) // 반지름 단위 : m
                .strokeWidth(1.0f) // 선너비 0f : 선없음
            //.fillColor(Color.parseColor("#1AFFFFFF")); // 배경색
            circle = map!!.addCircle(circle1KM!!)
        } else {
            circle!!.remove() // 반경삭제
            circle1KM!!.center(curPoint)
            circle = map!!.addCircle(circle1KM!!)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        parsePermissions(this, requestCode, permissions, this)
        /*Toast.makeText(
            this,
            "requestCode : $requestCode  permissions : $permissions  grantResults :$grantResults",
            Toast.LENGTH_SHORT
        ).show()*/
    }

    override fun onDenied(requestCode: Int, permissions: Array<String>) {
        /*Toast.makeText(
            applicationContext,
            "permissions denied : " + permissions.size,
            Toast.LENGTH_SHORT
        ).show()*/
    }

    override fun onGranted(requestCode: Int, permissions: Array<String>) {
        /*Toast.makeText(
            applicationContext,
            "permissions granted : " + permissions.size,
            Toast.LENGTH_SHORT
        ).show()*/
    }
}
