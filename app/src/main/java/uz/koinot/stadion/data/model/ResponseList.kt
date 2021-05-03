package uz.koinot.stadion.data.model

data class ResponseList <T>(
    var success:Int,
    var message:String,
    var objectKoinot:List<T>?
    )