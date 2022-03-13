package uz.koinot.stadion.data.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import uz.koinot.stadion.utils.extensions.SingleBlock
import java.util.regex.Matcher
import java.util.regex.Pattern


class SmsReceiver : BroadcastReceiver() {

    companion object {
        private var listener: SingleBlock<String>? = null
        fun setReceiveCodeListener(block: SingleBlock<String>) {
            listener = block
        }
    }

    var p: Pattern = Pattern.compile("(|^)\\d{6}")
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            Log.e("AAA", "SMS received")

            val data = intent.extras
            val pdus = data!!["pdus"] as Array<Any>?
            for (i in pdus!!.indices) {
                val smsMessage: SmsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                val sender: String = smsMessage.getDisplayOriginatingAddress()
                val phoneNumber: String = smsMessage.getDisplayOriginatingAddress()
                val senderNum = phoneNumber
                val messageBody: String = smsMessage.getMessageBody()
                try {
                    val m: Matcher = p.matcher(messageBody)
                    if (m.find()) {
                        listener?.invoke(m.group(0))
                    }
                } catch (e: Exception) {
                }
            }
        }
    }
}