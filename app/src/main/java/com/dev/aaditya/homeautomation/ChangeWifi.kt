package com.dev.aaditya.homeautomation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_change_wifi.*
import kotlinx.android.synthetic.main.password_dialog.view.*
import kotlinx.android.synthetic.main.progress.view.*
import org.json.JSONObject
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class ChangeWifi : AppCompatActivity() {

    lateinit var wifiManager:WifiManager
    var resultList = ArrayList<ScanResult>()
    private var ACCESLOCATION = 1547
    var chipiD:String?=null
    val db = FirebaseFirestore.getInstance()
    var userName:String?=null
    var selectedSSID:String?=null
    var pass:String=""
    lateinit var progressDialog: android.app.AlertDialog
    var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            resultList = wifiManager.scanResults as ArrayList<ScanResult>
            Log.d("TESTING", "onReceive Called")
        }
    }
    var userEmail:String?=null
    protected override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_wifi)
        wifi_wifiSelect.visibility = View.INVISIBLE
        wifi_add_room.visibility = View.INVISIBLE
        wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var wasEnabled:Boolean = wifiManager.isWifiEnabled
        wifiManager.setWifiEnabled(true)
        while(!wifiManager.isWifiEnabled);
        val bundle:Bundle = intent.extras
        userEmail = bundle.getString("user-email")
        userName = bundle.getString("name")


        val progressBuilder = android.app.AlertDialog.Builder(this)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val progress = inflater.inflate(R.layout.progress,null)
        progress.loading_msg.text = "Scanning for WIFI"
        progressBuilder.setView(progress)
        progressDialog = progressBuilder.create()
    }

    fun checkPermissions(){
        if (Build.VERSION.SDK_INT>=23){
            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESLOCATION)
                return
            }
            else {
                startScanning()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            ACCESLOCATION -> {
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Scanning for chip", Toast.LENGTH_LONG).show()
                    startScanning()
                }
                else {
                    Toast.makeText(this,"We need Location Permission to scan WIFI networks", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setDialog(show: Boolean) {
        if (show)
            progressDialog.show()
        else
            progressDialog.dismiss()
    }

    fun scanForChip(view: View) {
        checkPermissions()
    }

    fun startScanning() {
        setDialog(true)
        this.registerReceiver(broadcastReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        wifiManager.startScan()
        Handler().postDelayed({
            stopScanning()
        }, 5000)
    }

    fun stopScanning() {
        setDialog(false)
        unregisterReceiver(broadcastReceiver)
        Toast.makeText(this,"Scan complete", Toast.LENGTH_LONG).show()
        var ssidList = ArrayList<String>()

        for (result in resultList) {
            chipiD  = splitSSID(result.SSID)
            Log.i("cccccc",splitSSID(result.SSID))
            if(chipiD!="invalid"){
                ssidList.add(result.SSID)
                break
            }
        }
        if (chipiD!="invalid") {
            // add chip and appliance.
            Log.i("cccccd",ssidList[0])
            val wifiConfiguration = WifiConfiguration()
            wifiConfiguration.SSID = "\"" + ssidList[0] + "\""
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            val wifiManager =this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.addNetwork(wifiConfiguration)

            val list = wifiManager.configuredNetworks
            for (i in list) {
                if (i.SSID != null && i.SSID == "\"" + ssidList[0] + "\"") {
                    wifiManager.disconnect()
                    wifiManager.enableNetwork(i.networkId, true)
                    wifiManager.reconnect()
                    break
                }
            }
            Log.i("cccccddd",ssidList[0])
            Log.i("cccccddddddd",chipiD)
            Toast.makeText(this,"Chip Found - Now register your wifi", Toast.LENGTH_LONG).show()
            wifi_wifiSelect.visibility = View.VISIBLE
        }
        else
            Toast.makeText(this,"No Chip Found", Toast.LENGTH_LONG).show()
        Log.d("TESTING", ssidList.toString())

    }

    fun registerWifi(view: View) {
        val builder = AlertDialog.Builder(this)
        var ssidList = ArrayList<String>()
        val ssidArray = arrayOfNulls<String>(ssidList.size)
        for(result in resultList) {
            ssidList.add(result.SSID)
        }
        Log.i("ssiddddddd",ssidList.toString())
        var wifiAdapter= ArrayAdapter<String>(this,R.layout.wifi_name,R.id.wifi_txt,ssidList)
        ssidList.toArray(ssidArray)
//        builder.setTitle("Choose your Wifi network")
//        val build= Dialog(this)
        builder.setTitle( Html.fromHtml("<font color='#FFFFFF' >Select your wifi network</font>"))
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.password_dialog,null)
        builder.setView(dialogView)
        val diag = builder.create()
        dialogView.wifi_list.adapter = wifiAdapter
        dialogView.okButton.visibility  = View.INVISIBLE
        dialogView.textSSID1.visibility = View.INVISIBLE
        dialogView.textPassword.visibility = View.INVISIBLE
        diag.show()
        diag.window.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        val wifiView = inflater.inflate(R.layout.wifi_name,null)

        dialogView.wifi_list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectedSSID = parent.getItemAtPosition(position).toString()
            Toast.makeText(this,"selected wifi:$selectedSSID", Toast.LENGTH_LONG).show()
            dialogView.wifi_list.visibility  = View.INVISIBLE
            dialogView.okButton.visibility  = View.VISIBLE
            dialogView.textSSID1.visibility = View.VISIBLE
            dialogView.textSSID1.text = "SSID: $selectedSSID"
            dialogView.textPassword.visibility = View.VISIBLE
        }

        dialogView.okButton.setOnClickListener { view ->
            pass = dialogView.textPassword.text.toString()
            if(pass!="") {
                Toast.makeText(this,"Password: ${pass}", Toast.LENGTH_LONG).show()
                wifi_wifiSelect.visibility = View.INVISIBLE
                wifi_chip_scan.visibility = View.INVISIBLE
                wifi_add_room.visibility = View.VISIBLE
            }
            else {
                Toast.makeText(this,"Enter a valid password", Toast.LENGTH_LONG).show()
            }
            diag.dismiss()
        }

    }

    fun changeWifi(view: View) {
        val url = "http://192.168.4.1/wifisave?s=$selectedSSID&p=$pass"
        executeURL(url)
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("email",userEmail)
        intent.putExtra("name",userName)
        startActivity(intent)
    }

    fun executeURL(url:String) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)

// Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    var strResp = response.toString()
                    val jsonObj: JSONObject = JSONObject(strResp)
                    val currentState: JSONObject = jsonObj.getJSONObject("currentState")
                    Log.i("length: ",currentState.length().toString())
                    for (i in 1..currentState.length()) {
                        var str:String = "pid" + i + " is " + currentState.getString("pid"+i)
                        Log.i("resssppp",str)
                    }
                    Toast.makeText(this,"Doneeee", Toast.LENGTH_LONG).show()
                },
                Response.ErrorListener { Toast.makeText(this,"That didn't work!", Toast.LENGTH_LONG).show() })

// Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun splitSSID(str:String):String {
        val parts = str.split("ESP","_")
        var id:String=""
        if(str.contains("ESP")) {
            if(parts.size>1) {
                if(parts.size==2) {
                    if(parts[1].matches("-?\\d+(\\.\\d+)?".toRegex())) {
                        id = parts[1]
                    }
                    else {
                        id = "invalid"
                    }
                }
                else if(parts.size==3) {
                    id = parts[2]
                }
            }
            else {
                id = "invalid"
            }
        } else {
            id = "invalid"
        }
        return id
    }
}
