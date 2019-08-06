package com.dev.aaditya.homeautomation

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_appliance.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener



class add_appliance : AppCompatActivity() {

    lateinit var chipId:String
    var userEmail:String?=null
    var userName:String?=null
    protected override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_appliance)
        val bundle = intent.extras
        chipId = bundle.getString("chip-id")
        userEmail = bundle.getString("user-email")
        userName = bundle.getString("user-name")
        Toast.makeText(this,"chipId: $chipId",Toast.LENGTH_LONG).show()
        company.visibility = View.INVISIBLE
        company_drop_down.visibility = View.INVISIBLE
//        company_drop_down.onItemSelectedListener = handleSpinneChange()
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                var selectedType =  parentView.selectedItem.toString()
                when(selectedType) {
                    "AC","TV" -> {
                        company.visibility = View.VISIBLE
                        company_drop_down.visibility = View.VISIBLE
                        pid_text.visibility = View.INVISIBLE
                    }
                    "Light" -> {
                        company.visibility = View.INVISIBLE
                        company_drop_down.visibility = View.INVISIBLE
                        pid_text.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }
    }

    fun handleSpinneChange() {

    }
    fun addAppliance(view: View) {
        val app_name = appliance_name.text.toString()
        val app_type = spinner.selectedItem.toString()
        val pidText = pid_text.text.toString()
        val appliance = HashMap<String,Any>()

        if(app_name=="") {
            Toast.makeText(this,"Enter an appliance name",Toast.LENGTH_LONG).show()
            return
        }
        if(pidText=="" && app_type == "Light" ) {
            Toast.makeText(this,"Enter pid for the appliance",Toast.LENGTH_LONG).show()
            return
        }

        appliance["chipId"] = chipId
        appliance["appliance-name"] = app_name
        appliance["appliance-type"] = app_type
        appliance["state"] = "0"
        when(app_type) {
            "AC" -> {
                appliance["Company"] = company_drop_down.selectedItem.toString()
                appliance["pid"] = 9
            }
            "AC" -> {
                appliance["Company"] = company_drop_down.selectedItem.toString()
                appliance["pid"] = 10
            }
            else -> {
                appliance["Company"] = "N/A"
                appliance["pid"] = pidText.toInt()
            }
        }

        db.collection("appliances")
                .add(appliance)
                .addOnSuccessListener { documentReference ->
                    Log.d("yooo", "DocumentSnapshot written with ID: ${documentReference.id}")
                    Toast.makeText(this,"Appliance added Successfully",Toast.LENGTH_LONG).show()
                    val intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("email",userEmail)
                    intent.putExtra("name",userName)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.w("yooo", "Error adding document", e)
                    Toast.makeText(this,"Room not added",Toast.LENGTH_LONG).show()
                }
    }

}
