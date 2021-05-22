package uz.koinot.stadion.data.model

class CreateStadium(
    var id: Long?,
    var name: String,
    var latitude: Double,
    var phone_number: String,
    var longitude: Double,
    var address: String,
    var opening_time: String,
    var closing_time: String,
    var change_price_time: String,
    var price_day_time: Double,
    var price_night_time: Double,
    var active: Boolean,
    var width: Int,
    var height: Int
)