package com.dev.aaditya.homeautomation

class Room {
    var roomName:String?=null
    var appliances:ArrayList<Appliances>?=null
    var chipId:String?=null

    constructor(roomName:String,appliances:ArrayList<Appliances>,chipId:String){

        this.roomName=roomName
        this.appliances=appliances
        this.chipId=chipId
    }
}