package uz.koinot.stadion.data.model

data class ResponseObject <T>(
    var success:Int,
    var message:String,
    var objectKoinot:T?
    )