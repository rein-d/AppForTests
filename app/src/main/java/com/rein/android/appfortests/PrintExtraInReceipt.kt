package com.rein.android.appfortests

import android.util.Log
import org.jetbrains.annotations.NotNull
import ru.evotor.framework.core.IntegrationService
import ru.evotor.framework.core.action.event.receipt.changes.receipt.print_extra.SetPrintExtra
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEvent
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventProcessor
import ru.evotor.framework.core.action.processor.ActionProcessor
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi
import javax.security.auth.callback.Callback

class PrintExtraInReceipt : IntegrationService() {
    override fun createProcessors(): MutableMap<String, ActionProcessor>? {
        val map : HashMap<String, ActionProcessor> = HashMap()

    return map}
}