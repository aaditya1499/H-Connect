package com.dev.aaditya.homeautomation

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.appliance_switch.view.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.room_ticket.view.*
import android.view.LayoutInflater
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.*
import com.google.gson.Gson
import org.json.JSONObject
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var listRooms=ArrayList<Room>()
    var listAppliances=ArrayList<Appliances>()
    var roomAdapter:RoomsAdapter?=null
    val db = FirebaseFirestore.getInstance()
    var userEmail:String?=null
    var chipArray= ArrayList<String>()
    var roomNameArray= ArrayList<String>()
    var userName:String?=null
    var changed = true
    lateinit var progressDialog:AlertDialog
    var currentState=JSONObject()
    var currentUserState=JSONObject()
    var currentStateList = ArrayList<JSONObject>()
    protected override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        empty_room_text.visibility = View.INVISIBLE
        val bundle:Bundle = intent.extras
        userEmail = bundle.getString("email")
        userName= bundle.getString("name")
        val stateUrl = "http://gosmartsoftsolutions.com/hconnect/$userEmail/JSON"
        getStates(stateUrl)
        intent.action = "Already created it"
        var roomCount=0
        val progressBuilder = AlertDialog.Builder(this)
        progressBuilder.setView(R.layout.progress)
        progressDialog = progressBuilder.create()
        setDialog(true)
        db.collection("room")
                .whereEqualTo("user-email","$userEmail")
                .get()
                .addOnSuccessListener { documents ->
                    if(documents.isEmpty) {
                        Toast.makeText(this,"No Room Found",Toast.LENGTH_LONG).show()
                        setDialog(false)
                        var params= LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                        params.topMargin = 150
                        empty_room_text.layoutParams = params
                        empty_room_text.visibility = View.VISIBLE
                    }
                    else {
                        var params= LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,0)
                        params.topMargin = 0
                        empty_room_text.layoutParams = params
                        for (document in documents) {
                            Log.d("documenttt", "${document.id} => ${document.data}")
                            chipArray.add(document.getString("chipId")!!)
//                                            chipId= document.getString("chipId")
                            Log.i("chippp",chipArray[roomCount])
//                                            roomName = document.getString("room-name")
                            roomNameArray.add(document.getString("room-name")!!)
                            Log.i("room abcddddeee" ,roomNameArray[roomCount])
                            roomCount++
                        }
                        getStatesCall()

                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("documenttt", "Error getting documents: ", exception)
                    Toast.makeText(this,"No Room Found",Toast.LENGTH_LONG).show()
                    setDialog(false)
                }

        fab.setOnClickListener { view ->
            val intent1 = Intent(this,addRoom::class.java)
            intent1.putExtra("user-email",userEmail)
            intent1.putExtra("name",userName)
            this.startActivity(intent1)
        }
    }

fun getStatesCall() {
    Toast.makeText(this,"Enter",Toast.LENGTH_SHORT).show()
    var timer=Timer()
    timer.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            Log.i("hasssss",currentUserState.has(userEmail).toString())
            Log.i("hasssss",currentUserState.toString())
            if(currentUserState.has(userEmail)) {
                currentState = currentUserState.getJSONObject(userEmail)
                getAppliances()
                timer.cancel()
            }
        }
    } ,0,100)
}

fun getStates (initialUrl:String) {

//    val initialUrl = "http://gosmartsoftsolutions.com/hconnect/3463663/read/RelayStatus/JSON"
//
// Instantiate the RequestQueue.
    val queue = Volley.newRequestQueue(this)
    var str:String?=null
//
// Request a string response from the provided URL.
    val stringRequest = StringRequest(Request.Method.GET, initialUrl,
            Response.Listener<String> { response ->
                //  Display the first 500 characters of the response string.
//                    var res= "Response is: " + response
//                    Log.i("resssppp",res)
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
//                currentState= jsonObj.getJSONObject("States")
                currentUserState= jsonObj
//                    var pid: String ="pid1 is " + currentState.getString("pid1")
                Log.i("length: ",currentState.length().toString())
//                currentStateList.add(currentState)
                Log.i("currstatussss3",currentStateList.toString())
                Toast.makeText(this,"Doneeee",Toast.LENGTH_LONG).show()
//                publishResult(currentState)
            },
            Response.ErrorListener { Toast.makeText(this,"That didn't work!",Toast.LENGTH_LONG).show() })

// Add the request to the RequestQueue.
    queue.add(stringRequest)
//    Log.i("resssppp",str)
}


    fun getAppliances() {
        //Get list of Appliances
//        setDialog(true)
        Log.i("yoooo" , "aaagggya")
        Log.i("yoooo1" , roomNameArray.toString())
        for (i in roomNameArray.indices) {
//            var url = "http://gosmartsoftsolutions.com/hconnect/${chipArray[i]}/read/RelayStatus/JSON"
//            getStates(url)

//            Handler().postDelayed({
//                Toast.makeText(this,"Doneeee 1",Toast.LENGTH_LONG).show()
                db.collection("appliances")
                        .whereEqualTo("chipId",chipArray[i])
                        .get()
                        .addOnSuccessListener{documents1 ->
                            var appCount = 0
                            var x=0
                            for (documents in documents1) {
                                x++
                                Log.i("documenttt", "${documents.id} => ${documents.data}")
                                var chipId = documents.getString("chipId")!!
                                var pid = documents.get("pid").toString().toInt()
                                var appId = ""+chipId+ pid
                                Log.i("idddd",appId)
                                var state = currentState.getJSONObject(chipId)
                                Log.i("hasssss",state.toString())
                                listAppliances.add(Appliances(documents.getString("appliance-name")!!,
                                        appId.toInt() ,documents.getString("appliance-type")!!,chipId,
                                        pid,state.getString("pid$pid").toInt()))
                                Log.i("listtttt",listAppliances[appCount].state.toString())
                                appCount++
//                        Log.i("room app id",roomCount.toString())
                                Log.i("room app",appId)
                                if(i == (chipArray.size-1)) {
                                    if(x == documents1.size()){
                                        addApplaincesToRoom(listAppliances)
                                    }
                                }
                            }
//                            val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
//                            var json = Gson().toJson(listAppliances)
//                            Log.i("aaddd json:",json)
//                            sharedPref.edit().putString("appliance_list",json).commit()
                        }
                        .addOnFailureListener { exception ->
                            Log.w("documenttt", "Error getting documents: ", exception)
                            Toast.makeText(this,"No Appliance Found for $roomNameArray[i]",Toast.LENGTH_LONG).show()
                        }
//            },2500)

        }
//        Handler().postDelayed({
//            addApplaincesToRoom(listAppliances)
//        },3500)
    }
    private fun setDialog(show: Boolean) {
        if (show)
            progressDialog.show()
        else
            progressDialog.dismiss()
    }

    fun addApplaincesToRoom(listNewAppliances:ArrayList<Appliances>) {
        Log.i("yoooo" , "aaaya")
        Log.i("yoooo" ,roomNameArray.toString())
        Log.i("yoooo" ,chipArray.toString() )
//            val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
//            var jsonGet:String =sharedPref.getString("appliance_list","")
//            var listNewAppliances:ArrayList<Appliances> = Gson().fromJson(jsonGet,object: TypeToken<ArrayList<Appliances>>() {}.type)
        Log.i("yoooo" ,listNewAppliances.toString())
        for (i in roomNameArray.indices) {
            Log.i("abcdddd" , "aaaya")
            var x=0
            var listofMatchedAppliances=ArrayList<Appliances>()
            for(j in listNewAppliances) {
                if(j.chipId == chipArray[i]){
                    listofMatchedAppliances.add(listNewAppliances[x])
                }
                x++
            }
            listRooms.add(Room(roomNameArray[i],listofMatchedAppliances,chipArray[i]))
//            Log.i("yoooo" , listofMatchedAppliances.toString())
//            listRooms.add(Room(roomNameArray[i],listofMatchedAppliances,chipArray[i]))
//            Log.i("abcdddd" , listRooms[0].roomName.toString())
//            Log.i("added:","room added")
//            Log.i("abcddd",listNewAppliances[0].toString())
        }
        addRoomToList()
    }

    fun addRoomToList() {
        roomAdapter = RoomsAdapter(this,listRooms)
        listRoom.adapter=roomAdapter
        Toast.makeText(this,"Rooms Added",Toast.LENGTH_LONG).show()
        setDialog(false)
    }

    inner class RoomsAdapter: BaseAdapter,Filterable {
        var listRoom=ArrayList<Room>()
        var context: Context?=null
        constructor(context: Context ,listRoom:ArrayList<Room>):super(){

            this.listRoom=listRoom
            this.context=context
        }
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            val Room=listRoom[p0]
            var powerState = 0
            var inflater=context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var myView=inflater.inflate(R.layout.room_ticket,null)
            myView.name.text=Room.roomName!!
            if (powerState==0) {

            }
            else if (powerState == 1) {

            }
            myView.name.setOnClickListener {

            }
            myView.powerButtton.setOnClickListener {

            }
            Log.i("room namee: ",Room.roomName)
            Log.i("room no: ",Room.appliances!!.size.toString())
            for (i in Room.appliances!!.indices) {
                var state:Int = Room.appliances!!.get(i).state!!
                Log.i("documentttttt",Room.appliances!!.get(i).applianceName+":"+Room.appliances!!.get(i).state)
                var inflater1=context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val v = inflater1.inflate(R.layout.appliance_switch,null)
                v.id = Room.appliances!!.get(i).applianceId!!
                Log.i("room apppp idd",Room.appliances!!.get(i).applianceId!!.toString())
                v.appliance_name.text=Room.appliances!!.get(i).applianceName
                if(state ==1) {
                    when(Room.appliances!!.get(i).applianceType) {
                        "Light" -> v.appliance_img.setImageResource(R.drawable.light_on)
                        "TV" -> v.appliance_img.setImageResource(R.drawable.tv_on)
                        "AC" -> v.appliance_img.setImageResource(R.drawable.ac_on)
                    }
                    myView.powerButtton.setImageResource(R.drawable.power_on_normal)
                    powerState=1
                } else if(state==0) {
                    when(Room.appliances!!.get(i).applianceType) {
                        "Light" -> v.appliance_img.setImageResource(R.drawable.light_off)
                        "TV" -> v.appliance_img.setImageResource(R.drawable.tv_off)
                        "AC" -> v.appliance_img.setImageResource(R.drawable.ac_off)
                    }
                }
                v.appliance_img.setOnClickListener {
                    if(state ==0) {
                        var url= "http://gosmartsoftsolutions.com/hconnect/${Room.chipId}/write/RelayStatus/1${Room.appliances!!.get(i).pid}"
                        when(Room.appliances!!.get(i).applianceType) {
                            "Light" -> v.appliance_img.setImageResource(R.drawable.light_on)
                            "TV" -> v.appliance_img.setImageResource(R.drawable.tv_on)
                            "AC" -> v.appliance_img.setImageResource(R.drawable.ac_on)
                        }
                        myView.powerButtton.setImageResource(R.drawable.power_on_normal)
                        powerState=1
                        state = 1
                        var statusCode = myAsyncTask().execute(url).status
                        Room.appliances!!.set(i, Appliances(Room.appliances!!.get(i).applianceName!!,
                                Room.appliances!!.get(i).applianceId!!,Room.appliances!!.get(i).applianceType!!,
                                Room.chipId!!,Room.appliances!!.get(i).pid!!,state))
                        Log.i("resCode",statusCode.toString())
                    } else if(state==1) {

                        var url= "http://gosmartsoftsolutions.com/hconnect/${Room.chipId}/write/RelayStatus/0${Room.appliances!!.get(i).pid}"
                        when(Room.appliances!!.get(i).applianceType) {
                            "Light" -> v.appliance_img.setImageResource(R.drawable.light_off)
                            "TV" -> v.appliance_img.setImageResource(R.drawable.tv_off)
                            "AC" -> v.appliance_img.setImageResource(R.drawable.ac_off)
                        }
                        state = 0
                        myAsyncTask().execute(url)
                        Room.appliances!!.set(i, Appliances(Room.appliances!!.get(i).applianceName!!,
                                Room.appliances!!.get(i).applianceId!!,Room.appliances!!.get(i).applianceType!!,
                                Room.chipId!!,Room.appliances!!.get(i).pid!!,state))
                    }
                }

                myView.powerButtton.setOnClickListener {
                    if (powerState == 1) {

                        myView.powerButtton.setImageResource(R.drawable.power_off_normal)
                        powerState=0
                        var url = "http://gosmartsoftsolutions.com/hconnect/${Room.chipId}/allOff"
                        var statusCode = myAsyncTask().execute(url).status
                        Log.i("resCode",statusCode.toString())

                        // Pull all appliances states to off
                        for (j in Room.appliances!!.indices) {
                            var buttonView = findViewById<View>(Room.appliances!!.get(j).applianceId!!)
                            Log.i("room namee id: ", Room.appliances!!.get(j).applianceId!!.toString())
                            when(Room.appliances!!.get(j).applianceType) {
                                "Light" -> buttonView.appliance_img.setImageResource(R.drawable.light_off)
                                "TV" -> buttonView.appliance_img.setImageResource(R.drawable.tv_off)
                                "AC" -> buttonView.appliance_img.setImageResource(R.drawable.ac_off)
                            }
                            var url1 = "http://gosmartsoftsolutions.com/hconnect/${Room.chipId}/write/RelayStatus/0$${Room.appliances!!.get(j).pid}"
                            Room.appliances!!.set(j, Appliances(Room.appliances!!.get(j).applianceName!!,
                                    Room.appliances!!.get(j).applianceId!!,Room.appliances!!.get(j).applianceType!!,
                                    Room.chipId!!,Room.appliances!!.get(j).pid!!,state))
                            var statusCode = myAsyncTask().execute(url1).status
                            Log.i("resCode",statusCode.toString())

                        }

                    }
                }


                myView.appliance_layout.addView(v,0)
//                Log.i("app name: ",Room.appliances!!.get(i).applianceName)
            }
                myView.name.setOnClickListener {

                    /*add(p0)
                    delete(p0) */
                    val intent= Intent(context,room_details::class.java)
                    var json = Gson().toJson(Room.appliances)
                    intent.putExtra("name",Room.roomName)
                    intent.putExtra("chip-id",Room.chipId)
                    intent.putExtra("appliances",json)
                    intent.putExtra("user-email",userEmail)
                    intent.putExtra("user-name",userName)

//                    intent.putExtra("des",Room.des!!)
//                    intent.putExtra("image",Room.image!!)
                    startActivityForResult(intent,2)
//                    context!!.startActivityForResult(intent,2)

                }

                return myView
            }

        override fun getFilter(): Filter {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.


        }

        override fun getItem(p0: Int): Any {
            return listRoom[p0]
        }

        override fun getItemId(p0: Int): Long {

            return p0.toLong()
        }

        override fun getCount(): Int {

            return listRoom.size
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2) {
            setDialog(true)
            listAppliances.clear()
            listRooms.clear()
            currentStateList.clear()
            listRoom.adapter = null
            currentUserState.remove(userEmail)
            val stateUrl = "http://gosmartsoftsolutions.com/hconnect/$userEmail/JSON"
            getStates(stateUrl)
            getStatesCall()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
//    override fun onResume() {
//        Log.v("Example", "onResume")
//
//        val action = intent.action
//        // Prevent endless loop by adding a unique action, don't restart if action is present
//        if(changed) {
//            if (action == null || action != "Already created it") {
////            Log.v("Example", "Force restart")
////            val intent = Intent(this, MainActivity::class.java)
////            intent.putExtra("email",userEmail)
////            intent.putExtra("name",userName)
////            startActivity(intent)
////            finish()
//                setDialog(true)
//                listAppliances.clear()
//                listRooms.clear()
//                currentStateList.clear()
//                listRoom.adapter = null
////            val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
////            var jsonGet:String =sharedPref.getString("updated_appliance_list","")
////            Log.i("aaddd json1:",jsonGet)
////            var updatedApplianceList:ArrayList<Appliances> = Gson().fromJson(jsonGet,object: TypeToken<ArrayList<Appliances>>() {}.type)
////            addApplaincesToRoom(updatedApplianceList)
//                getStatesCall()
//            } else
//                intent.action = null// Remove the unique action so the next time onResume is called it will restart
//        }   else
//                intent.action = null// Remove the unique action so the next time onResume is called it will restart
//
//        super.onResume()
//    }

    var backPressed:Long=0
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        if (backPressed +2000 > System.currentTimeMillis()){
            super.onBackPressed()
            finishAffinity()
        }
        else if(!drawer_layout.isDrawerOpen(GravityCompat.START)){
            Toast.makeText(this,
                    "Press back once again to exit!", Toast.LENGTH_SHORT)
                    .show()
            backPressed = System.currentTimeMillis()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_change_wifi -> {
                val intent1 = Intent(this,ChangeWifi::class.java)
                intent1.putExtra("user-email",userEmail)
                intent1.putExtra("name",userName)
                this.startActivity(intent1)
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    inner class myAsyncTask:AsyncTask<String,String,String>(){

        override fun doInBackground(vararg p0: String?): String {
            try{
                val url=URL(p0[0])
                val urlConnect=url.openConnection() as HttpURLConnection
                urlConnect.connectTimeout=7000
                urlConnect.requestMethod="GET"
                var resultCode = urlConnect.responseCode
                Log.i("returnCode",resultCode.toString())
                var result=convertStreamToString(urlConnect.inputStream)
                publishProgress(result)

            }catch (ex:Exception){}

            return "" }

        override fun onPostExecute(result: String?) {

        }


        override fun onPreExecute() {
            super.onPreExecute()
        }


    }


    fun convertStreamToString(inputStream:InputStream):String {

        val buffer = BufferedReader(InputStreamReader(inputStream))
        var line: String
        var fullResult = ""
        try {
            do {line=buffer.readLine()
                if (line!=null)
                    fullResult+=line
            }while(line!=null)
            inputStream.close()
        } catch (ex: Exception) {
        }
        return fullResult
    }

}
