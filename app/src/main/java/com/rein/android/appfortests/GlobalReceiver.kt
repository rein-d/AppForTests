package com.rein.android.appfortests

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClosedEvent
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi
import ru.evotor.framework.receipt.event.ReceiptCreatedEvent
import ru.evotor.framework.receipt.event.ReceiptEvent

private const val LOGTAG = "MyApp123"

class GlobalReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val action = intent.action
            if (action == "evotor.intent.action.receipt.sell.OPENED" ) {
                val receipt = ReceiptApi.getReceipt(context, Receipt.Type.SELL)
                val receiptNumber = receipt?.header?.number
                Log.d(LOGTAG, receiptNumber.toString())
            } else {
                val receiptUuid = ReceiptCreatedEvent.from(intent.extras)?.receiptUuid
                val receipt = ReceiptApi.getReceipt(context, receiptUuid.toString())
                val receiptNumber = receipt?.header?.number
                Log.d(LOGTAG, receiptNumber.toString())
            }
        }
    }
}