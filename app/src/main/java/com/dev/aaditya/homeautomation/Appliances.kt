package com.dev.aaditya.homeautomation

class Appliances {
    var applianceName:String?=null
    var applianceId:Int?=null
    var applianceType:String?=null
    var chipId:String?=null
    var pid:Int?=null
    var state:Int?=null

    constructor(applianceName:String,applianceId:Int,applianceType:String,chipId:String,pid:Int,state:Int){

        this.applianceName=applianceName
        this.applianceId=applianceId
        this.applianceType=applianceType
        this.chipId=chipId
        this.pid=pid
        this.state=state
    }
}