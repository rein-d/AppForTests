package com.rein.android.appfortests.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rein.android.appfortests.HttpRequest
import com.rein.android.appfortests.databinding.FragmentStartBinding
import okhttp3.internal.wait
import ru.evotor.framework.component.PaymentPerformer
import ru.evotor.framework.component.PaymentPerformerApi
import ru.evotor.framework.core.IntegrationException
import ru.evotor.framework.core.IntegrationManagerCallback
import ru.evotor.framework.core.IntegrationManagerFuture
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand
import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommand
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd
import ru.evotor.framework.core.action.event.receipt.changes.receipt.SetPurchaserContactData
import ru.evotor.framework.core.action.event.receipt.payment.system.result.PaymentSystemPaymentResult
import ru.evotor.framework.navigation.NavigationApi
import ru.evotor.framework.payment.PaymentSystem
import ru.evotor.framework.payment.PaymentType
import ru.evotor.framework.receipt.*
import ru.evotor.framework.receipt.formation.api.ReceiptFormationCallback
import ru.evotor.framework.receipt.formation.api.ReceiptFormationException
import ru.evotor.framework.receipt.formation.api.SellApi
import java.lang.RuntimeException
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class StartFragment : Fragment() {
    private lateinit var binding: FragmentStartBinding

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        binding.correctionButton.setOnClickListener {
            NavigationApi.createIntentForSellReceiptEdit(
                true
            )
        }
        binding.zreportButton.setOnClickListener { closeSession() }
        binding.errorButton.setOnClickListener { throw RuntimeException("No Results") }
        binding.sellReceiptButton.setOnClickListener { openSellReceiptwithSellApi() }
        binding.printReceiptButton.setOnClickListener {
            for (n in 1..21) {
                printSellReceipt()
            }
        }

        return binding.root
    }

    private fun closeSession() {
        return PrintZReportCommand().process(requireContext(), IntegrationManagerCallback {
            when (it.result.type) {
                IntegrationManagerFuture.Result.Type.OK -> PrintZReportCommand.create(it.result.data)
                IntegrationManagerFuture.Result.Type.ERROR ->
                    Toast.makeText(requireContext(), it.result.error.message, Toast.LENGTH_SHORT)
                        .show()
                null -> throw RuntimeException("No Results")
            }
        })
    }

    private fun openSellReceipt() {
        val positions: ArrayList<PositionAdd> = ArrayList()

        positions.add(
            PositionAdd(
                Position.Builder.newInstance(
                    //UUID позиции
                    UUID.randomUUID().toString(),
                    //UUID товара
                    UUID.randomUUID().toString(),
                    // наименование товара
                    "Тестовый товар",
                    // наименование единицы измерения
                    Measure("шт", 0, 0),
                    // цена без скидок
                    BigDecimal(1000),
                    // количество
                    BigDecimal(1)
                )
                    .build()
            )
        )

        OpenSellReceiptCommand(positions, null).process(
            requireActivity(),
            IntegrationManagerCallback {
                try {
                    val result = it.result
                    if (result.type == IntegrationManagerFuture.Result.Type.OK) {
                        startActivity(Intent("evotor.intent.action.payment.SELL"))
                    }
                } catch (e: IntegrationException) {
                    e.printStackTrace()
                }
            })
    }

    private fun openSellReceiptwithSellApi() {
        val positions: ArrayList<PositionAdd> = ArrayList()

        positions.add(
            PositionAdd(
                Position.Builder.newInstance(
                    //UUID позиции
                    UUID.randomUUID().toString(),
                    //UUID товара
                    UUID.randomUUID().toString(),
                    // наименование товара
                    "Тестовый товар",
                    // наименование единицы измерения
                    Measure("шт", 0, 0),
                    // цена без скидок
                    BigDecimal(1000),
                    // количество
                    BigDecimal(1)
                )
                    .build()
            )
        )

        val purchaserData = SetPurchaserContactData.createForPhone("79776020338")

        OpenSellReceiptCommand(positions, null, purchaserData).process(
            requireActivity(),
            IntegrationManagerCallback {
                try {
                    val result = it.result
                    if (result.type == IntegrationManagerFuture.Result.Type.OK) {
                        val performers = PaymentPerformerApi.getAllPaymentPerformers(requireActivity().packageManager)
                        SellApi.moveCurrentReceiptDraftToPaymentStage(requireContext(), performers[0], object : ReceiptFormationCallback {
                            override fun onSuccess() {
                                Toast.makeText(context, "Передаем чек на оплату", Toast.LENGTH_LONG).show()
                            }

                            override fun onError(error: ReceiptFormationException) {
                                Toast.makeText(context, "${error.code}: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        })
                    }
                } catch (e: IntegrationException) {
                    e.printStackTrace()
                }
            })
    }

    private fun printSellReceipt() {
        val positions = ArrayList<Position>()
        val payments: HashMap<Payment, BigDecimal> = HashMap()

        positions.add(
            Position.Builder.newInstance(
                //UUID позиции
                UUID.randomUUID().toString(),
                //UUID товара
                UUID.randomUUID().toString(),
                // наименование товара
                "Тестовый товар",
                // наименование единицы измерения
                Measure("шт", 0, 0),
                // цена без скидок
                BigDecimal(1000),
                // количество
                BigDecimal(1)
            )
                .build()

        )

        val payment = Payment(
            UUID.randomUUID().toString(),
            BigDecimal(1000),
            null,
            PaymentPerformer(
                PaymentSystem(PaymentType.ELECTRON, "", ""),
                null,
                null,
                null,
                null
            ),
            null,
            null,
            null
        )

        payments[payment] = BigDecimal(1000)
        val printGroup = PrintGroup(
            UUID.randomUUID().toString(),
            PrintGroup.Type.CASH_RECEIPT, null, null, null,
            null, true, null, null
        )
        val printReceipt = Receipt.PrintReceipt(
            printGroup,
            positions,
            payments,
            HashMap(),
            HashMap()
        )

        val listDocs = ArrayList<Receipt.PrintReceipt>()
        listDocs.add(printReceipt)

        PrintSellReceiptCommand(
            listDocs,
            null,
            "+79776020339",
            "room085@gmail.com",
            null,
            null,
            null,
            null
        )
            .process(requireActivity(), IntegrationManagerCallback {
                val result = it.result
                if (result.type == IntegrationManagerFuture.Result.Type.OK) {
                    val printSellReceiptResult = PrintReceiptCommandResult.create(result.data)
                } else {
                    val error = result.error;
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })


    }

    companion object {
        @JvmStatic
        fun newInstance(): StartFragment {
            return StartFragment()
        }
    }

}