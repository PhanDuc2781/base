package com.example.base_project.folder.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.base_project.folder.FolderTabFragment
import com.example.base_project.folder.enum.FolderType

class MyFolderTabListFragment(
    fa: FragmentActivity
): FragmentStateAdapter(fa) {
    override fun createFragment(position: Int): Fragment {
        val type = FolderType.getFolderType(position) ?: FolderType.TRIM_DIR
        return FolderTabFragment.newInstance(type.fileDir)
    }

    override fun getItemCount(): Int = FolderType.entries.size
}