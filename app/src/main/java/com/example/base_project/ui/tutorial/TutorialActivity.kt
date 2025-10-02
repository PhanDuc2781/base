package com.example.base_project.ui.tutorial

import android.os.Bundle
import com.example.base_project.base.BaseActivity
import com.example.base_project.databinding.ActivityTutorialBinding

class TutorialActivity : BaseActivity<ActivityTutorialBinding>(ActivityTutorialBinding::inflate) {
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        initViewPager()
    }

    private fun initViewPager() {
        val adapter = IntroAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 2
    }
}