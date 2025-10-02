package com.example.base_project.ui.tutorial

import android.content.Intent
import com.example.base_project.base.BaseFragment
import com.example.base_project.databinding.FragmentIntro4Binding
import com.example.base_project.ui.main.MainActivity

class FragmentIntro4 : BaseFragment<FragmentIntro4Binding>(FragmentIntro4Binding::inflate) {

    override fun initView() {
        super.initView()
        binding.txtStartNow.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}