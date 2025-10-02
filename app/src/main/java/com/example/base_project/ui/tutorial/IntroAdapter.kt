package com.example.base_project.ui.tutorial

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class IntroAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    private val listFragments = listOf(
        FragmentIntro1(),
        FragmentIntro2(),
        FragmentIntro3(),
        FragmentIntro4()
    )

    override fun createFragment(position: Int): Fragment {
        return listFragments[position]
    }

    override fun getItemCount(): Int = 4
}