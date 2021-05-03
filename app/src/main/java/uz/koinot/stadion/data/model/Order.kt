package uz.koinot.stadion.data.model

data class Order(
    var id:Int,
    var user: User,
    var latitude:Double,
    var longitude:Double,
    var sum:Double,
    var startDate:String,
    var endDate:String,
    var time:String,
    var active:Boolean,
    var cancelOrder:Boolean
)
