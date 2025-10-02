package com.example.base_project.dialog

import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.base_project.base.BaseDialogFragment
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.DialogSaveFileBinding

class DialogSaveFile : BaseDialogFragment<DialogSaveFileBinding>(DialogSaveFileBinding::inflate) {
    var onClickSave: (String) -> Unit = {}
    override fun setupView() {
        super.setupView()
        binding.tvNev.setOnSingleClickListener {
            dismiss()
        }

        binding.tvPos.setOnSingleClickListener {
            if (binding.edtFile.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a file name", Toast.LENGTH_SHORT)
                    .show()
                return@setOnSingleClickListener
            }
            onClickSave(binding.edtFile.text.toString().trim())
            dismiss()
        }
    }

    companion object {
        fun show(fragmentManager: FragmentManager, onClickSave: (String) -> Unit) {
            val dialog = DialogSaveFile()
            dialog.onClickSave = onClickSave
            dialog.show(fragmentManager, "DialogSaveFile")
        }
    }
}