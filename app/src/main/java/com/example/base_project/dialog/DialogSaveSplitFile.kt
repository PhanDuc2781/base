package com.example.base_project.dialog

import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.base_project.base.BaseDialogFragment
import com.example.base_project.databinding.DialogSaveSplitFileBinding

class DialogSaveSplitFile :
    BaseDialogFragment<DialogSaveSplitFileBinding>(DialogSaveSplitFileBinding::inflate) {
    var onClickListener: ((String, String) -> Unit)? = null

    override fun setupView() {
        super.setupView()
        binding.tvNev.setOnClickListener {
            dismiss()
        }
        binding.tvPos.setOnClickListener {
            val file1 = binding.edtFile1.text.toString()
            val file2 = binding.edtFile2.text.toString()

            when {
                file1.isEmpty() && file2.isEmpty() -> showToast("Please Enter File Name")
                file1.isEmpty() -> showToast("Please Enter File 1 Name")
                file2.isEmpty() -> showToast("Please Enter File 2 Name")
                else -> onClickListener?.invoke(file1, file2)
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun show(fmg: FragmentManager, onClickListener: ((String, String) -> Unit)? = null) {
            val dialog = DialogSaveSplitFile()
            dialog.onClickListener = onClickListener
            dialog.show(fmg, "DialogSaveSplitFile")
        }
    }
}