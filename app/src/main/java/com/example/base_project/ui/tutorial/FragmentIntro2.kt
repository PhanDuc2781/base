package com.example.base_project.ui.tutorial

import com.example.base_project.base.BaseFragment
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.FragmentIntro2Binding


class FragmentIntro2 : BaseFragment<FragmentIntro2Binding>(FragmentIntro2Binding::inflate) {
    override fun initView() {
        super.initView()
        binding.next.setOnSingleClickListener {
            (mActivity as TutorialActivity).binding.viewPager.currentItem += 1
        }
    }
}