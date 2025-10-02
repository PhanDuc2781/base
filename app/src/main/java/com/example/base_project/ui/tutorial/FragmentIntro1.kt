package com.example.base_project.ui.tutorial

import com.example.base_project.base.BaseFragment
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.FragmentIntro1Binding


class FragmentIntro1 : BaseFragment<FragmentIntro1Binding>(FragmentIntro1Binding::inflate) {

    override fun initView() {
        super.initView()
        binding.next.setOnSingleClickListener {
            (mActivity as TutorialActivity).binding.viewPager.currentItem += 1
        }
    }

}