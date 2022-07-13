package com.rein.android.appfortests.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rein.android.appfortests.HttpRequest
import com.rein.android.appfortests.databinding.FragmentStartBinding
import ru.evotor.framework.core.IntegrationManagerCallback
import ru.evotor.framework.core.IntegrationManagerFuture
import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommand
import java.lang.RuntimeException

class StartFragment : Fragment() {
    private lateinit var binding: FragmentStartBinding

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        binding.correctionButton.setOnClickListener { HttpRequest.newRequest() }
        binding.zreportButton.setOnClickListener { closeSession() }
        binding.errorButton.setOnClickListener { throw RuntimeException("No Results") }

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

    companion object {
        @JvmStatic
        fun newInstance(): StartFragment {
            return StartFragment()
        }
    }

}