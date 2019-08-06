package com.dev.aaditya.homeautomation

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlinx.android.synthetic.main.activity_room_details.*
import kotlinx.android.synthetic.main.appliance_switch.view.*
import kotlinx.android.synthetic.main.content_room_details.*
import kotlinx.android.synthetic.main.room_ticket.view.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.net.HttpURLConnection
import java.net.URL

class room_details : AppCompatActivity() {

    var roomName:String?=null
    var chipId:String?=null
    var applianceList= ArrayList<Appliances>()
    var applianceAdapter:ApplianceAdapter?=null
    var lightList= ArrayList<Appliances>()
    var tvList= ArrayList<Appliances>()
    var acList= ArrayList<Appliances>()
    var applianceTypeList = ArrayList<ApplianceType>()
    var userEmail:String?=null
    var userName:String?=null
    protected override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_details)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
            val intent = Intent(this,add_appliance::class.java)
            intent.putExtra("chip-id",chipId)
            intent.putExtra("user-email",userEmail)
            intent.putExtra("user-name",userName)
            startActivity(intent)
        }

        val bundle:Bundle = intent.extras
        roomName = bundle.getString("name")
        toolbar.setTitle(roomName)
        chipId= bundle.getString("chip-id")
        userEmail = bundle.getString("user-email")
        userName = bundle.getString("user-name")
        var applianceString = bundle.getString("appliances")
        applianceList = Gson().fromJson(applianceString,object: TypeToken<ArrayList<Appliances>>() {}.type)
        for(i in applianceList.indices) {
            when(applianceList[i].applianceType) {
                "Light" -> lightList.add(applianceList[i])
                "TV" -> tvList.add(applianceList[i])
                "AC" -> acList.add(applianceList[i])
            }
        }
        if(lightList.size>0)
            applianceTypeList.add(ApplianceType("Light",lightList,lightList.size))
        if(tvList.size>0)
            applianceTypeList.add(ApplianceType("TV",tvList,tvList.size))
        if(acList.size>0)
            applianceTypeList.add(ApplianceType("AC",acList,acList.size))
        applianceAdapter = ApplianceAdapter(this,applianceTypeList)
        room_details_list.adapter = applianceAdapter
    }

    inner class ApplianceAdapter: BaseAdapter {                      //,Filterable aayega shayad
        var applianceTypeList = ArrayList<ApplianceType>()
        var context: Context?=null
        constructor(context: Context, applianceTypeList:ArrayList<ApplianceType>):super(){

            this.applianceTypeList=applianceTypeList
            this.context=context
        }
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            val appliance=applianceTypeList[p0]
            var powerState = 0
            var inflater=context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var myView=inflater.inflate(R.layout.room_ticket,null)
            myView.name.text=appliance.applianceType
            if (powerState==0) {

            }
            else if (powerState == 1) {

            }
            myView.name.setOnClickListener {

            }
            myView.powerButtton.setOnClickListener {

            }
//            Log.i("room namee: ",Room.roomName)
//            Log.i("room no: ",Room.appliances!!.size.toString())
//            when(appliance.applianceType) {
//
//            }
            for (i in 0..(appliance.number!!-1)) {
                var state = appliance.appliance!!.get(i).state
                var inflater1=context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val v = inflater1.inflate(R.layout.appliance_switch,null)
                Log.i("room apppp idd",appliance.applianceType)
                v.id = appliance.appliance!!.get(i).applianceId!!
                Log.i("room apppp idd",appliance.appliance!!.get(i).applianceId!!.toString())
                v.appliance_name.text=appliance.appliance!!.get(i).applianceName
                if(state ==1) {
                    when(appliance.appliance!!.get(i).applianceType) {
                        "Light" -> v.appliance_img.setImageResource(R.drawable.light_on)
                        "TV" -> v.appliance_img.setImageResource(R.drawable.tv_on)
                        "AC" -> v.appliance_img.setImageResource(R.drawable.ac_on)
                    }
                    myView.powerButtton.setImageResource(R.drawable.power_on_normal)
                    powerState=1
                } else if(state==0) {
                    when(appliance.appliance!!.get(i).applianceType) {
                        "Light" -> v.appliance_img.setImageResource(R.drawable.light_off)
                        "TV" -> v.appliance_img.setImageResource(R.drawable.tv_off)
                        "AC" -> v.appliance_img.setImageResource(R.drawable.ac_off)
                    }
                }
                v.appliance_img.setOnClickListener {
                    if(state ==0) {
                        var url= "http://gosmartsoftsolutions.com/hconnect/$chipId/write/RelayStatus/1${appliance.appliance!!.get(i).pid}"
                        when(appliance.appliance!!.get(i).applianceType) {
                            "Light" -> v.appliance_img.setImageResource(R.drawable.light_on)
                            "TV" -> v.appliance_img.setImageResource(R.drawable.tv_on)
                            "AC" -> v.appliance_img.setImageResource(R.drawable.ac_on)
                        }
                        myView.powerButtton.setImageResource(R.drawable.power_on_normal)
                        powerState=1
                        state = 1
                        var statusCode = MainActivity().myAsyncTask().execute(url).status
                        applianceList.set(i, Appliances(appliance.appliance!!.get(i).applianceName!!,
                                appliance.appliance!!.get(i).applianceId!!,appliance.appliance!!.get(i).applianceType!!,
                                chipId!!,appliance.appliance!!.get(i).pid!!,1))
//                        Log.i("resCode",statusCode.toString())
                    } else if(state==1) {

                        var url= "http://gosmartsoftsolutions.com/hconnect/$chipId/write/RelayStatus/0${appliance.appliance!!.get(i).pid}"
                        when(appliance.appliance!!.get(i).applianceType) {
                            "Light" -> v.appliance_img.setImageResource(R.drawable.light_off)
                            "TV" -> v.appliance_img.setImageResource(R.drawable.tv_off)
                            "AC" -> v.appliance_img.setImageResource(R.drawable.ac_off)
                        }
                        var statusCode = MainActivity().myAsyncTask().execute(url).status
                        applianceList.set(i, Appliances(appliance.appliance!!.get(i).applianceName!!,
                                appliance.appliance!!.get(i).applianceId!!,appliance.appliance!!.get(i).applianceType!!,
                                chipId!!,appliance.appliance!!.get(i).pid!!,0))
                        state = 0
//                        myAsyncTask().execute(url)
                    }
                }

                myView.powerButtton.setOnClickListener {
                    if (powerState == 1) {

                        myView.powerButtton.setImageResource(R.drawable.power_off_normal)
                        powerState=0
                        var url = "http://gosmartsoftsolutions.com/hconnect/$chipId/allOff"
//                        var statusCode = myAsyncTask().execute(url).status
//                        Log.i("resCode",statusCode.toString())

                        // Pull all appliances states to off
                        for (j in appliance.appliance!!.indices) {
                            var buttonView = findViewById<View>(appliance.appliance!!.get(j).applianceId!!)
                            Log.i("room namee id: ", appliance.appliance!!.get(j).applianceId!!.toString())
                            when(appliance.appliance!!.get(j).applianceType) {
                                "Light" -> buttonView.appliance_img.setImageResource(R.drawable.light_off)
                                "TV" -> buttonView.appliance_img.setImageResource(R.drawable.tv_off)
                                "AC" -> buttonView.appliance_img.setImageResource(R.drawable.ac_off)
                            }
                            applianceList.set(j, Appliances(appliance.appliance!!.get(j).applianceName!!,
                                    appliance.appliance!!.get(j).applianceId!!,appliance.appliance!!.get(j).applianceType!!,
                                    chipId!!,appliance.appliance!!.get(j).pid!!,0))
                            var url1 = "http://gosmartsoftsolutions.com/hconnect/$chipId/write/RelayStatus/0$${appliance.appliance!!.get(j).pid}"
                            var statusCode = MainActivity().myAsyncTask().execute(url1).status
                            Log.i("resCodeNew",statusCode.toString())

                        }

                    }
                }


                myView.appliance_layout.addView(v,0)
//                Log.i("app name: ",Room.appliances!!.get(i).applianceName)
            }

            return myView
        }

//        override fun getFilter(): Filter {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//
//        }
//
//        private inner class ValueFilter:Filter() {
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//        }


        override fun getItem(p0: Int): Any {
            return applianceTypeList[p0]
        }

        override fun getItemId(p0: Int): Long {

            return p0.toLong()
        }

        override fun getCount(): Int {

            return applianceTypeList.size
        }
    }

    override fun onBackPressed() {
        setResult(2)
        finish()
        super.onBackPressed()
    }
//    inner class myAsyncTask: AsyncTask<String, String, String>(){
//
//        override fun doInBackground(vararg p0: String?): String {
//            try{
//                val url= URL(p0[0])
//                val urlConnect=url.openConnection() as HttpURLConnection
//                urlConnect.connectTimeout=7000
//                urlConnect.requestMethod="GET"
//                var resultCode = urlConnect.responseCode
//                Log.i("returnCode",resultCode.toString())
//                var result=MainActivity().convertStreamToString(urlConnect.inputStream)
//                publishProgress(result)
//
//            }catch (ex:Exception){}
//
//            return "" }
//
//        override fun onPostExecute(result: String?) {
//
//        }
//
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//        }
//
//
//    }
}
