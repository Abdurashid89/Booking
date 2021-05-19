package uz.koinot.stadion.data.model

data class CreateOrder(
    var id:  Int?,
    var stadiumId: Int,
    var startDate: String,
    var endDate: String,
    var time: String,
    var firstName: String,
    var lastName: String,
    var phoneNumber: String
)