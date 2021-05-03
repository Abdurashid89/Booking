package uz.koinot.stadion.data.model

data class ResponseRegister (
    var headers:Any,
    var body:Body,
    var statusCode:String,
    var statusCodeValue:Int
    )

data class Body(
    var accessToken:String,
    var tokenType:String
)