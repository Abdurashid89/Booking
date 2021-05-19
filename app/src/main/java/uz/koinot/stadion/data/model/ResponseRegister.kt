package uz.koinot.stadion.data.model

data class ResponseRegister (
    var headers:Any,
    var body:TokenBody,
    var statusCode:String,
    var statusCodeValue:Int
    )

data class TokenBody(
    var accessToken:String,
    var tokenType:String
)