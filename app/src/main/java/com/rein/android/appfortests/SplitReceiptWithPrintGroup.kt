package com.rein.android.appfortests

import android.util.Log
import ru.evotor.framework.core.IntegrationService
import ru.evotor.framework.core.action.event.receipt.changes.position.SetPrintGroup
import ru.evotor.framework.core.action.event.receipt.changes.receipt.SetExtra
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEvent
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventProcessor
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventResult
import ru.evotor.framework.core.action.processor.ActionProcessor
import ru.evotor.framework.receipt.PrintGroup
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi
import java.lang.RuntimeException
import java.util.*

private const val TAG = "RPINTGOUP"
class SplitReceiptWithPrintGroup : IntegrationService() {

    override fun createProcessors(): MutableMap<String, ActionProcessor>? {
        Log.d(TAG, "Init")
        var printGroup = PrintGroup(
            //Идентифискатор печатной группы (чека покупателя).
            "0505cecd-a57b-43cd-98ba-bcd54793a19e",
            //Тип чека, например, кассовый чек.
            PrintGroup.Type.CASH_RECEIPT,
            //Наименование покупателя.
            "OOO Vector",
            //ИНН покупателя.
            "606053449439",
            //Адрес покупателя.
            "12, 3k2, Dark street, Nsk, Russia",
            /*
            Система налогообложения, которая применялась при расчёте.
            Смарт-терминал печатает чеки с указанной системой налогообложения, если она попадает в список разрешённых систем. В противном случае смарт-терминал выбирает систему налогообложения, заданную по умолчанию.
            */
            null,
            //Указывает на необходимость печати чека.
            false,
            //Реквизиты покупателя.
            null,
            null
        )
        var receipt = ReceiptApi.getReceipt(this, Receipt.Type.SELL) ?: throw RuntimeException("Чек пустой")
        var firstPurchaserPositions = receipt.getPositions()
        var firstPaymentPurposeId = receipt.getPayments().map { it.purposeIdentifier.toString() }

        var firstPrintGroup =
            SetPrintGroup(
                printGroup,
                firstPaymentPurposeId,
                listOf(firstPurchaserPositions[0].uuid)
            )

        var setAllPurchaserReceipts = listOf(firstPrintGroup)

        var eventProcessor = object : PrintGroupRequiredEventProcessor() {
            override fun call(action: String, event: PrintGroupRequiredEvent, callback: Callback) {
                try {
                    callback.onResult(
                        PrintGroupRequiredEventResult(
                            SetExtra(null),
                            setAllPurchaserReceipts
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val eventProcessingResult = hashMapOf<String, ActionProcessor>()
        eventProcessingResult[PrintGroupRequiredEvent.NAME_SELL_RECEIPT] = eventProcessor

        return eventProcessingResult
    }
}