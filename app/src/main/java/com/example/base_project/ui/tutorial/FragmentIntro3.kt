package com.example.base_project.ui.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.base_project.R
import com.example.base_project.base.BaseFragment
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.FragmentIntro3Binding

class FragmentIntro3 : BaseFragment<FragmentIntro3Binding>(FragmentIntro3Binding::inflate) {

    override fun initView() {
        super.initView()
        binding.next.setOnSingleClickListener {
            (mActivity as TutorialActivity).binding.viewPager.currentItem += 1
        }
    }
}