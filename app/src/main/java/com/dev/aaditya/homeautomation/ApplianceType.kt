package com.dev.aaditya.homeautomation

class ApplianceType {
    var applianceType:String?=null
    var appliance: ArrayList<Appliances>?=null
    var number:Int?=null

    constructor(applianceType:String,appliance:ArrayList<Appliances>,number:Int) {
        this.applianceType = applianceType
        this.appliance = appliance
        this.number = number
    }
}
