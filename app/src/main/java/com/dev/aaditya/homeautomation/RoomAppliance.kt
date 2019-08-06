package com.dev.aaditya.homeautomation

class RoomAppliance {
    var roomId:Int?=null
    var applianceOfRoom:List<Appliances>?=null

    constructor(roomId:Int,applianceOfRoom:List<Appliances>) {
        this.roomId=roomId
        this.applianceOfRoom=applianceOfRoom
    }
}