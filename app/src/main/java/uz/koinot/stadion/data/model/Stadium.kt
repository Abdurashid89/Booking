package uz.koinot.stadion.data.model

data class Stadium(
    var id:Long = 0,
    var name: String = "",
    var countVerify:Int = 0,
    var countNotVerify:Int = 0,
    var latitude:Double = 0.0,
    var phone_number:String = "",
    var longitude:Double = 0.0,
    var address:String = "",
    var opening_time:String = "",
    var closing_time:String = "",
    var stadium_like:Int = 0,
    var change_price_time:String = "",
    var price_day_time:Double = 0.0,
    var price_night_time:Double = 0.0,
    var width:Int = 0,
    var height:Int = 0,
    var count_order:Double = 0.0,
    var open:Boolean = false,
    var active:Boolean = false,
    var verify:Boolean = false,
    var money:Double = 0.0,
    var photos:List<Photos> = emptyList()
    )