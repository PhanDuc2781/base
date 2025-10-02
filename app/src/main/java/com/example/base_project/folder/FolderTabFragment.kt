package com.example.base_project.folder

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.base_project.R
import com.example.base_project.base.BaseVMFragment
import com.example.base_project.databinding.FragmentFolderTabBinding
import com.example.base_project.ext.shareFilePath
import com.example.base_project.ext.visibleOrGone
import com.example.base_project.folder.enum.FolderType
import com.example.base_project.select.adapter.SelectAudioAdapter
import com.example.base_project.select.bean.Audio
import com.example.base_project.sound.SoundManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.base_project.base.AlertData
import com.example.base_project.base.showCustomAlertDialog
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FolderTabFragment :
    BaseVMFragment<FragmentFolderTabBinding, MyFolderViewModel>(FragmentFolderTabBinding::inflate) {
    override val viewModel: MyFolderViewModel by viewModels()
    private var fileDir: String? = null
    private lateinit var audioAdapter: SelectAudioAdapter

    private var currentAudioPlay: Audio? = null

    @Inject
    lateinit var soundManager: SoundManager
    override fun initView() {
        super.initView()

        fileDir = arguments?.getString(ARG_FILE_DIR) ?: FolderType.TRIM_DIR.fileDir
        Log.d("FILE_DIR", fileDir.toString())
        observeData()

        audioAdapter = SelectAudioAdapter(
            (mActivity as MyFolderActivity),
            onClickPlay = { audio, isPlaying ->
                if (!isPlaying) {
                    soundManager.pauseSound()
                } else {
                    currentAudioPlay = audio
                    soundManager.playSound(audioModel = audio)
                }
            },
            onClickMore = { audio, view ->
                showPopupMore(audio, view)
                Log.d("AUDIO_PATH", audio.path)
            },
            isFolder = true
        )


        binding.recTabAudio.adapter = audioAdapter

        binding.swipeLayout.setOnRefreshListener {
            viewModel.loadAudios(fileDir.toString(), forceRefresh = true)
            binding.swipeLayout.isRefreshing = false
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.audios.collect { map ->
                    val list = map[fileDir].orEmpty()
                    applyFilter(list, (mActivity as MyFolderActivity).viewModel.query.value)
                }
            }
        }

        (mActivity as MyFolderActivity).viewModel.query.observe(viewLifecycleOwner) { q ->
            val list = viewModel.audios.value?.get(fileDir).orEmpty()
            applyFilter(list, q)
        }

        fileDir?.let {
            viewModel.loadAudios(it)
        }
    }

    private fun showPopupMore(audio: Audio, view: View) {
        val popupMore = PopupMore(
            requireContext(),
            onDelete = {
                onDeleteAudio(audio)
            },
            onShare = {
                (mActivity as MyFolderActivity).shareFilePath(audio.path)
            },
            onDetails = {
                openDetails(audio)
            }
        )
        popupMore.show(view)
    }

    private fun openDetails(audio: Audio) {
        mActivity?.showCustomAlertDialog(
            AlertData(
                title = getString(R.string.details),
                msg = "Name: ${audio.title + "\n\n" + "Type: " + "Size :" + audio.extension + "\n\n" + "Path: " + audio.path} ",
                posTitle = getString(R.string.cancel)
            )
        )
    }

    private fun onDeleteAudio(audio: Audio) {
        mActivity?.showCustomAlertDialog(
            AlertData(
                title = getString(R.string.mes_delete_audio),
                posTitle = getString(R.string.delete),
                nevTitle = getString(R.string.cancel),
                callback = { isDelete ->
                    if (isDelete) {
                        viewModel.deleteAudio(audio = audio, onSuccess = {
                            if (currentAudioPlay == audio) {
                                soundManager.pauseSound()
                            }
                            viewModel.loadAudios(fileDir.toString(), forceRefresh = true)
                        }, onFail = {
                            Toast.makeText(
                                requireContext(),
                                "DELETE_FAIL",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    }
                }
            )
        )
    }


    private fun applyFilter(list: List<Audio>?, query: String?) {
        val filtered = if (query.isNullOrBlank()) list
        else list?.filter { it.title.contains(query, ignoreCase = true) }

        audioAdapter.submitList(filtered)

        if (filtered.isNullOrEmpty()) {
            binding.frameEmpty.visibleOrGone(true)
            if (!query.isNullOrBlank()) {
                binding.layoutEmpty.txtContentEmpty.text = getString(R.string.no_result_found)
                binding.layoutEmpty.icEmpty.setImageResource(R.drawable.img_null_search)
            } else {
                binding.layoutEmpty.txtContentEmpty.text = getString(R.string.empty_audio_file)
                binding.layoutEmpty.icEmpty.setImageResource(R.drawable.img_empty_audio)
            }
        } else {
            binding.frameEmpty.visibleOrGone(false)
        }
    }

    override fun onPause() {
        super.onPause()
        soundManager.pauseSound()
        audioAdapter.togglePlayPause(-1)
        audioAdapter.isPlaying = false
    }


    companion object {
        private const val ARG_FILE_DIR = "ARG_FILE_DIR"

        fun newInstance(fileDir: String): FolderTabFragment {
            val fragment = FolderTabFragment()
            fragment.arguments = bundleOf(ARG_FILE_DIR to fileDir)
            return fragment
        }
    }
}