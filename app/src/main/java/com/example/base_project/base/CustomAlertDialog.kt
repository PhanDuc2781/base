package com.example.base_project.base

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.example.base_project.R
import com.example.base_project.applicattion.MainApplication
import com.example.base_project.base.CustomAlertDialog.Companion.ALERT_MSG_KEY
import com.example.base_project.base.CustomAlertDialog.Companion.ALERT_NEV_TITLE_KEY
import com.example.base_project.base.CustomAlertDialog.Companion.ALERT_POS_TITLE_KEY
import com.example.base_project.base.CustomAlertDialog.Companion.ALERT_TITLE_KEY
import com.example.base_project.databinding.CustomDialogNotiBinding
import com.example.base_project.ext.gone
import com.example.base_project.ext.show

data class AlertData(
    val title: String = MainApplication.instance.getString(R.string.error_deffault_title),
    val msg: String,
    val posTitle: String,
    val nevTitle: String? = null,
    val callback: ((Boolean) -> Unit)? = null
)

fun AlertData.toBundle(): Bundle {
    return bundleOf(
        ALERT_MSG_KEY to msg,
        ALERT_POS_TITLE_KEY to posTitle,
        ALERT_NEV_TITLE_KEY to nevTitle,
        ALERT_TITLE_KEY to title
    )
}

class CustomAlertDialog :
    BaseDialogFragment<CustomDialogNotiBinding>(CustomDialogNotiBinding::inflate) {

    companion object {
        private var alertCallback: ((Boolean) -> Unit)? = null
        private var currentAlert: CustomAlertDialog? = null
        const val ALERT_MSG_KEY = "ALERT_MSG_KEY"
        const val ALERT_TITLE_KEY = "ALERT_TITLE_KEY"
        const val ALERT_POS_TITLE_KEY = "ALERT_POS_TITLE_KEY"
        const val ALERT_NEV_TITLE_KEY = "ALERT_NEV_TITLE_KEY"

        @Synchronized
        fun show(fMNG: FragmentManager, data: AlertData) {
            if (currentAlert?.isVisible == true) {
                currentAlert?.dismiss(); currentAlert = null
            }

            if (fMNG.findFragmentByTag(CustomAlertDialog::class.simpleName) != null) return

            currentAlert = CustomAlertDialog().apply {
                arguments = data.toBundle()
                alertCallback = data.callback
                show(fMNG, CustomAlertDialog::class.simpleName)
            }
        }
    }

    override fun setupView() {
        super.setupView()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.tvMessage.text = arguments?.getString(ALERT_MSG_KEY)
        binding.tvTitle.text = arguments?.getString(ALERT_TITLE_KEY)
        binding.tvPos.text = arguments?.getString(ALERT_POS_TITLE_KEY)
        binding.tvPos.setOnSingleClickListener {
            alertCallback?.let { it(true) }
            dismiss()
        }

        if (arguments?.getString(ALERT_NEV_TITLE_KEY) != null) {
            binding.tvNev.show()
            binding.tvNev.text = arguments?.getString(ALERT_NEV_TITLE_KEY)
            binding.tvNev.setOnSingleClickListener {
                alertCallback?.let { it(false) }
                dismiss()
            }
        } else binding.tvNev.gone()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(
            STYLE_NO_FRAME,
            R.style.FullScreenDialog
        )
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )
        }
    }
}