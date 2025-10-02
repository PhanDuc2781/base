package com.example.base_project.folder

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.ViewPager2
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.ActivityMyFolderBinding
import com.example.base_project.ext.dpToPx
import com.example.base_project.ext.gone
import com.example.base_project.ext.parcelable
import com.example.base_project.ext.textChanges
import com.example.base_project.ext.visibleOrGone
import com.example.base_project.folder.adapter.MyFolderTabListFragment
import com.example.base_project.folder.enum.FolderType
import com.example.base_project.folder.repository.FolderAudioRepository
import com.example.base_project.select.bean.Audio
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFolderActivity :
    BaseVMActivity<ActivityMyFolderBinding, MyFolderViewModel>(ActivityMyFolderBinding::inflate) {
    override val viewModel: MyFolderViewModel by viewModels()
    private lateinit var adapterViewPager: MyFolderTabListFragment


    @OptIn(FlowPreview::class)
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        onBackPressedDispatcher.addCallback {
            finish()
        }

        adapterViewPager = MyFolderTabListFragment(this)

        val folderType = intent.extras?.parcelable<FolderType>(FOLDER_TYPE) ?: FolderType.TRIM_DIR

        initTabLayoutViewPager(folderType)

        lifecycleScope.launch {
            binding.edtSearch.textChanges()
                .debounce(200)
                .distinctUntilChanged()
                .collectLatest {
                    viewModel.setQuery(it)
                    binding.icClose.visibleOrGone(it.isNotEmpty())
                }
        }
    }

    private fun initTabLayoutViewPager(folderType: FolderType) {
        binding.tabLayout.getTabAt(0)?.select()
        binding.viewPager.apply {
            offscreenPageLimit = 3
            adapter = adapterViewPager

            currentItem = 0

            TabLayoutMediator(binding.tabLayout, this) { tab, position ->
                val folderType = FolderType.getFolderType(position)
                tab.text = folderType?.let { getString(it.typeName) }
            }.attach()

            val position = FolderType.getPosition(folderType.fileDir)
            currentItem = position

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    /*binding.edtSearch.setText("")
                    binding.edtSearch.clearFocus()
                    viewModel.setQuery("")*/
                }
            })


            for (i in 0 until binding.tabLayout.tabCount) {
                val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
                val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins((8), 0, 8.dpToPx(this@MyFolderActivity), 0)
                tab.requestLayout()
            }
        }
    }

    override fun clickListener() {
        super.clickListener()
        binding.icBack.setOnClickListener {
            finish()
        }

        binding.icClose.setOnSingleClickListener {
            binding.edtSearch.setText("")
            binding.icClose.gone()
        }
    }

    companion object {
        const val FOLDER_TYPE = "FOLDER_TYPE"
        fun onStart(type: FolderType, activity: AppCompatActivity) {
            val intent = Intent(activity, MyFolderActivity::class.java).apply {
                putExtra(FOLDER_TYPE, type as Parcelable)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            activity.startActivity(intent)
            activity.finish()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}

@HiltViewModel
class MyFolderViewModel @Inject constructor(
    private val folderAudioRepository: FolderAudioRepository,
) : BaseViewModel() {

    private val _audios = MutableStateFlow<Map<String, List<Audio>>>(emptyMap())
    val audios: StateFlow<Map<String, List<Audio>>> = _audios

    private val _query = MutableLiveData<String>("")
    val query: LiveData<String> = _query

    fun setQuery(q: String) {
        _query.postValue(q)
    }

    fun loadAudios(fileDir: String, forceRefresh: Boolean = false) {
        val currentCache = _audios.value
        if (!forceRefresh && currentCache.containsKey(fileDir)) {
            return
        }

        viewModelScope.launch {
            folderAudioRepository.getAudiosInFolder(fileDir).collect { list ->
                Log.d("AUDIO_LIST_SIZE", "${list.size}")
                val newMap = currentCache.toMutableMap()
                newMap[fileDir] = list.reversed()
                _audios.value = newMap
            }
        }
    }


    fun deleteAudio(audio: Audio, onSuccess: () -> Unit, onFail: () -> Unit) =
        viewModelScope.launch {
            folderAudioRepository.deleteAudio(audio).collect {
                if (it) {
                    onSuccess()
                } else {
                    onFail()
                }
            }
        }
}