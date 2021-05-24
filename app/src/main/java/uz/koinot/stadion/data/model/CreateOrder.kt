package uz.koinot.stadion.data.model

data class CreateOrder(
    var id:  Int?,
    var stadiumId: Long,
    var startDate: String,
    var endDate: String,
    var time: String,
    var phoneNumber: String
)