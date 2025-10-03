package com.example.base_project.trim

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import coil.util.Logger
import com.example.base_project.R
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.base.setStatusBarColor
import com.example.base_project.databinding.ActivityTrimBinding
import com.example.base_project.dialog.DialogSaveFile
import com.example.base_project.dialog.DialogSaveSplitFile
import com.example.base_project.enum.Type
import com.example.base_project.ext.durationToSecond
import com.example.base_project.ext.formatSecondsToTime
import com.example.base_project.ext.gone
import com.example.base_project.ext.hide
import com.example.base_project.ext.parcelable
import com.example.base_project.ext.show
import com.example.base_project.folder.MyFolderActivity
import com.example.base_project.folder.enum.FolderType
import com.example.base_project.select.bean.Audio
import com.example.base_project.util.AppConstance.AUDIO_ARG
import com.example.base_project.util.AppConstance.TYPE_ARG
import com.example.base_project.util.DurationFormatter
import com.example.base_project.wave_form.Pixels
import com.example.base_project.wave_form.soundeditor.utils.CheapSoundFile
import com.example.base_project.wave_form.soundeditor.widget.MarkerView
import com.example.base_project.wave_form.soundeditor.widget.WaveformView
import com.nekosoft.calltheme.ui.activity.ringtone_maker.soundeditor.utils.MediaStoreHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.*
import kotlin.math.min
import kotlin.reflect.typeOf

@AndroidEntryPoint
class ActivityEditorAudio :
    BaseVMActivity<ActivityTrimBinding, TrimViewmodel>(ActivityTrimBinding::inflate),
    MarkerView.MarkerListener, WaveformView.WaveformListener, View.OnClickListener {
    private var thread2: Thread? = null
    private var thread1: Thread? = null
    private var mSaveSoundFileThread: Thread? = null
    private val Supported_Format = arrayOf(".aac", ".AMR", ".mp3", ".wav", ".m4a")
    private var mNewFileKind: Int = 0
    private var mMarkerLeftInset: Int = 0
    private var mMarkerRightInset: Int = 0
    private var mLoadingLastUpdateTime: Long = 0
    private var mLoadingKeepGoing: Boolean = false
    private var mProgressDialog: ProgressDialog? = null
    private var mSoundFile: CheapSoundFile? = null
    private var mFile: File? = null
    private var mFilename: String? = null
    private var mWaveformView: WaveformView? = null
    private var mStartMarker: MarkerView? = null
    private var mEndMarker: MarkerView? = null
    private var mStartText: TextView? = null
    private var mStartSplitSelected: TextView? = null
    private var mEndSplitSelected: TextView? = null
    private var mEndText: TextView? = null
    private var mKeyDown: Boolean = false

    private var audioDuration: Int = -1
    private var mWidth: Int = 0
    private var mMaxPos: Int = 0
    private var mStartPos = -1
    private var mEndPos = -1
    private var mStartVisible: Boolean = false
    private var mEndVisible: Boolean = false
    private var mLastDisplayedStartPos: Int = 0
    private var mLastDisplayedEndPos: Int = 0
    private var mOffset: Int = 0
    private var mOffsetGoal: Int = 0
    private var mFlingVelocity: Int = 0
    private var mPlayStartMsec: Int = 0
    private var mPlayStartOffset: Int = 0
    private var mPlayEndMsec: Int = 0
    private var mHandler: Handler? = null
    private var mIsPlaying: Boolean = false
    private var mPlayer: MediaPlayer? = null
    private var mTouchDragging: Boolean = false
    private var mTouchStart: Float = 0.toFloat()
    private var mTouchInitialOffset: Int = 0
    private var mTouchInitialStartPos: Int = 0
    private var mTouchInitialEndPos: Int = 0
    private var mWaveformTouchStartMsec: Long = 0
    private var mDensity: Float = 0.toFloat()
    private var outputFile: File? = null
    private var mSound_AlbumArt_Path: String? = null
    private var marginvalue: Int = 0
    private var EdgeReached = false
    private var mSoundDuration = 0
    private var Maskhidden = true
    var audioManager: AudioManager? = null

    private var isEnable = false

    var type: Type = Type.TRIM
    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            if (mPlayer != null) {
                try {
                    if (mPlayer!!.isPlaying) {
                        pausePlayer()

                        if (viewModel.isPlay1.value) {
                            viewModel.isPlay1.value = false
                        }
                        if (viewModel.isPlay2.value) {
                            viewModel.isPlay2.value = false
                        }
                    }
                } catch (ex: java.lang.Exception) {
                }
            }
        }
    }
    private val mTimerRunnable = object : Runnable {
        override fun run() {
            if (mStartPos != mLastDisplayedStartPos && !mStartText!!.hasFocus()) {
                mStartText!!.text = getTimeFormat(formatTime(mStartPos))
                mLastDisplayedStartPos = mStartPos

                /*val formatStartTime = formatTime1(mStartPos)
                val formatEndTime = formatTime1(mEndPos)
                mStartSplitSelected?.text = formatStartTime
                mEndSplitSelected?.text = formatEndTime*/
                if (type == Type.SPLIT) {
                    viewModel.setStart(0)
                } else {
                    viewModel.setStart(mStartPos)
                }
            }

            if (mEndPos != mLastDisplayedEndPos && !mEndText!!.hasFocus()) {
                mEndText!!.text = getTimeFormat(formatTime(mEndPos))
                mLastDisplayedEndPos = mEndPos
                viewModel.setEnd(mEndPos)
                /*val formatEndTime = formatTime1(mMaxPos - mStartPos)*/
                /* mEndSplitSelected?.text = formatEndTime*/

            }

            mHandler?.postDelayed(this, 100)
        }
    }

    private var audio: Audio? = null
    override val viewModel: TrimViewmodel by viewModels()

    private var rangeTrimDurationSelected = 0
    private var job: Job? = null

    private var currentSeconds = 0

    private var totalProgress1 = 0

    private var totalProgress2 = 0


    @OptIn(FlowPreview::class)
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        setStatusBarColor(R.color.color_background)

        type = intent.extras?.parcelable(TYPE_ARG) ?: Type.TRIM
        Log.d("TYPE", "$type")

        lifecycleScope.launch {
            showProgress(true)
            delay(1500)
            showProgress(false)
        }

        Log.d("WAVE_INIT", "${binding.waveform.isInitialized}")
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager


        binding.zoomIn.setOnClickListener(this@ActivityEditorAudio)
        binding.zoomOut.setOnClickListener(this@ActivityEditorAudio)
        binding.btnSetRingTone.setOnClickListener(this@ActivityEditorAudio)
        binding.icPlay.setOnClickListener(this@ActivityEditorAudio)
        // temporary solution to fix the delay between initial pause to play animation

        marginvalue = Pixels.pxtodp(this@ActivityEditorAudio, 12)
        mPlayer = null
        mIsPlaying = false
        mSoundFile = null
        mKeyDown = false
        mHandler = Handler()
        mHandler?.postDelayed(mTimerRunnable, 100)

        audio = intent.extras?.parcelable(AUDIO_ARG)
        binding.baseTextview.isSelected = true
        audio?.let {
            binding.baseTextview.text = it.title
            binding.timeEnd.text = it.duration.formatSecondsToTime()
            audioDuration = it.duration.durationToSecond()
            Log.d("AUDIO_DURATION", "${it.duration}")
        }

        mFilename = audio?.path
        if (mSoundFile == null) loadFromFile() else mHandler?.post { finishOpeningSoundFile() }
        loadGui()

        binding.btnBack.setOnSingleClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback { finish() }


        binding.icDownload.setOnSingleClickListener {
            downLoadType(type)
        }

        lifecycleScope.launch {
            combine(viewModel.selectedStart, viewModel.selectedEnd) { end, start ->
                end to start
            }.collectLatest {
                if (mSoundFile != null) {
                    checkType(type)


                    delay(100)

                    isEnable = true
                    if (binding.waveform.maxPos() != null) {
                        Log.d("MAX_POS", "${binding.waveform.maxPos()}")
                    }

                    val duration = convertWaveSelected(it.first, it.second)
                    rangeTrimDurationSelected = (duration * 1000).toInt()
                    binding.progressCircular.max = (duration * 1000).toInt()
                    val formatSelected = formatDuration(duration.toInt())

                    Log.d("DURATION_SELECTED", "$duration")
                    binding.txtSelected.text =
                        getString(
                            R.string.selected_time,
                            formatSelected
                        )
                    binding.timeEnd.text = formatSelected

                    if (type == Type.SPLIT) {
                        binding.layoutPart.txtSelectedPart1.text = formatDuration(duration.toInt())
                        binding.previewSplit.txtEnd1.text = formatDuration(duration.toInt())
                        binding.previewSplit.progressCircular.max = (duration * 1000).toInt()
                        totalProgress1 = (duration * 1000).toInt()


                        val durationRemaining = audioDuration - duration
                        totalProgress2 = (durationRemaining * 1000).toInt()
                        binding.previewSplit.progressCircular2.max = (duration * 1000).toInt()
                        Log.d("DURATION_REMAINING", "$durationRemaining")
                        binding.layoutPart.txtSelectedPart2.text =
                            formatDuration(durationRemaining.toInt())
                        binding.previewSplit.txtEnd2.text =
                            formatDuration(durationRemaining.toInt())

                    }
                }
            }
        }

        previewAudio()
    }

    fun startSeekBarProgress(totalDuration: Int, seekBar: SeekBar, timeStartTxt: TextView) {
        val startTime = System.currentTimeMillis()
        var lastSecondMark = 0
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                seekBar.progress = min(elapsed.toInt(), totalDuration)

                val elapsedSeconds = elapsed
                if (elapsedSeconds > lastSecondMark) {
                    lastSecondMark = elapsedSeconds.toInt()
                    currentSeconds = elapsedSeconds.toInt()

                    timeStartTxt.text = currentSeconds.formatSecondsToTime()
                }

                if (elapsed >= totalDuration) {
                    if (viewModel.isPlay1.value) {
                        viewModel.isPlay1.value = false
                    }

                    if (viewModel.isPlay2.value) {
                        viewModel.isPlay2.value = false
                    }
                    break
                }
                delay(16)
            }
        }
    }

    private fun stopSeekbarProgress() {
        job?.cancel()
    }

    private fun downLoadType(type: Type) {
        when (type) {
            Type.TRIM -> {
                val endTime: Float = (binding.waveform.pixelsToMillisecs(this.mEndPos)) / 1000.0f
                val startTime: Float =
                    (binding.waveform.pixelsToMillisecs(this.mStartPos)) / 1000.0f
                val duration = endTime - startTime
                Log.d("DURATION_SELECTED", "$duration")

                if (endTime >= 0.0f) {
                    if (startTime == endTime) {
                        Toast.makeText(
                            this@ActivityEditorAudio,
                            "Start time = End Time",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    DialogSaveFile.show(supportFragmentManager) {
                        showLoading(true)
                        viewModel.trim(
                            audio = audio ?: return@show,
                            fileName = it,
                            startTime = startTime,
                            duration = duration,
                            onStart = {
                                showLoading(true)
                            },
                            onFail = {
                                showLoading(false)
                                Log.d("TRIM_PROGRESS", "FAIL")
                            },
                            onSuccess = {
                                showLoading(false)
                                MyFolderActivity.onStart(FolderType.TRIM_DIR, this)
                            }, onProgress = { i ->
                                Log.d("TRIM_PROGRESS", "$i")
                                showLoading(true)
                            }
                        )
                    }

                }
            }

            Type.SPLIT -> {
                val endTime: Float = (binding.waveform.pixelsToMillisecs(this.mEndPos)) / 1000.0f
                DialogSaveSplitFile.show(supportFragmentManager) { path_1, path_2 ->
                    Log.d("MSTART_POS", "$mStartPos")
                    showLoading(true)
                    viewModel.split(
                        audio = audio ?: return@show,
                        fileName1 = path_1,
                        fileName2 = path_2,
                        splitTime = endTime,
                        onStart = {

                        },
                        onFail = {
                            Log.d("SPLIT_STATUS", it)
                            showLoading(false)
                        },
                        onSuccess = {
                            Log.d("SPLIT_STATUS", "SUCCESS")
                            showLoading(false)
                            MyFolderActivity.onStart(FolderType.SPLIT, this)
                        },
                        onProgress = {}
                    )
                }
            }

            Type.REMOVE_PART -> TODO()
            Type.MUTE_PART -> TODO()
            else -> {}
        }
    }

    private fun previewAudio() {
        binding.previewSplit.icPlay1.setOnClickListener {
            viewModel.isPlay1.value = !viewModel.isPlay1.value
        }

        binding.previewSplit.icPlay2.setOnClickListener {
            viewModel.isPlay2.value = !viewModel.isPlay2.value
        }

        binding.icPlay.setOnClickListener {
            viewModel.isPlayTrim.value = !viewModel.isPlayTrim.value
        }

        lifecycleScope.launch {
            viewModel.isPlayTrim.collect {
                if (it) {
                    onPlay(mStartPos)
                    Log.d("RANGE_TRIM", "$rangeTrimDurationSelected")
                    startSeekBarProgress(
                        rangeTrimDurationSelected,
                        binding.progressCircular,
                        binding.timeStart
                    )
                } else {
                    if (mIsPlaying) {
                        pausePlayer()
                    }
                    stopSeekbarProgress()
                }

                binding.icPlay.setImageResource(if (it) R.drawable.ic_pause else R.drawable.ic_play)
            }
        }

        lifecycleScope.launch {
            viewModel.isPlay1.collect {
                if (it) {
                    viewModel.isPlay2.value = false
                    startSeekBarProgress(
                        totalProgress1,
                        binding.previewSplit.progressCircular,
                        binding.previewSplit.txtStart1
                    )
                    onPlay(mStartPos)
                    binding.previewSplit.progressCircular2.progress = 0
                } else {
                    if (mIsPlaying) {
                        pausePlayer()
                    }
                    stopSeekbarProgress()
                }
                binding.previewSplit.icPlay1.setImageResource(if (it) R.drawable.ic_pause else R.drawable.ic_play)
            }
        }

        lifecycleScope.launch {
            viewModel.isPlay2.collect {
                if (it) {
                    viewModel.isPlay1.value = false
                    onPlay(mEndPos)
                    startSeekBarProgress(
                        totalProgress1,
                        binding.previewSplit.progressCircular2,
                        binding.previewSplit.txtStart2
                    )
                    Log.d("END_POS", "$mMaxPos - $mStartPos - $mEndPos")
                    binding.previewSplit.progressCircular.progress = 0
                } else {
                    if (mIsPlaying) {
                        pausePlayer()
                        binding.previewSplit.progressCircular2.progress = 0
                    }
                    stopSeekbarProgress()
                }
                binding.previewSplit.icPlay2.setImageResource(if (it) R.drawable.ic_pause else R.drawable.ic_play)
            }
        }
    }

    fun convertWaveSelected(start: Int?, end: Int?): Float {
        val endTime: Float =
            (end?.let { binding.waveform.pixelsToMillisecs(it) })?.div(1000.0f) ?: 0f
        val startTime: Float =
            (start?.let { binding.waveform.pixelsToMillisecs(it) })?.div(1000.0f) ?: 0f
        return endTime - startTime
    }

    fun checkType(type: Type) {
        when (type) {
            Type.TRIM -> {
                binding.titleScreen.text = getString(R.string.trim_audio)
            }

            Type.SPLIT -> {
                binding.titleScreen.text = getString(R.string.split)
                binding.startmarker.gone()
                binding.endmarker.rotation = 180f
                binding.layoutTrim.gone()
                binding.frameSplit.show()
                binding.constraintLayout4.hide()
                binding.framePart.show()
                binding.waveform.split(true)
                viewModel.setStart(0)
                updateDisplay()
            }

            Type.REMOVE_PART -> {
                binding.titleScreen.text = getString(R.string.remove_part)
            }

            Type.MUTE_PART -> {
                binding.titleScreen.text = getString(R.string.mute_path)
            }

            else -> {}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            var mSoundTitle: String
            val dataUri = data?.data
            val proj = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Albums.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Artists.ARTIST
            )
            val tempCursor = managedQuery(dataUri, proj, null, null, null)
            tempCursor.moveToFirst() //reset the cursor
            var col_index: Int
            var AlbumID_index: Int
            do {
                col_index = tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                mSoundTitle = tempCursor.getString(col_index)
                AlbumID_index = tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID)
                val albumid = tempCursor.getLong(AlbumID_index)
                val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                val uri = ContentUris.withAppendedId(sArtworkUri, albumid)
                mSound_AlbumArt_Path = uri.toString()
            } while (tempCursor.moveToNext())
            try {
                assert(dataUri != null)
                var path: String? = dataUri!!.path

                if (!path!!.startsWith("/storage/")) {
                    path = MediaStoreHelper.getRealPathFromURI(applicationContext, data.data!!)
                }
                assert(path != null)
                val file = File(path!!)
                if (mSoundTitle.contains(EXTENSION_MP3)) {

                }

                mFilename = file.absolutePath

                if (mSoundFile == null) loadFromFile() else mHandler?.post { this.finishOpeningSoundFile() }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    private fun pausePlayer() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer!!.pause()
        }
        mWaveformView?.setPlayback(-1)
        mIsPlaying = false
        if (viewModel.isPlay1.value) {
            viewModel.isPlay1.value = false
        }
        if (viewModel.isPlay2.value) {
            viewModel.isPlay2.value = false
        }
        enableDisableButtons()
    }

    override fun onPause() {
        super.onPause()
        if (mPlayer != null) {
            if (mPlayer!!.isPlaying) {
                mPlayer!!.pause()
                mWaveformView?.setPlayback(-1)
                mIsPlaying = false
                enableDisableButtons()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer!!.stop()
            mPlayer!!.release()
            mPlayer = null
        }
        mProgressDialog = null
        finish()

        mSoundFile = null
        mWaveformView = null
        mHandler?.removeCallbacks(mTimerRunnable)
    }


    override fun waveformDraw() {
        if (mWaveformView != null) {
            mWidth = mWaveformView?.measuredWidth ?: 0
        }
        if (mOffsetGoal != mOffset && !mKeyDown && !EdgeReached) {
            updateDisplay()
        } else if (mIsPlaying) {
            updateDisplay()
        } else if (mFlingVelocity != 0) {
            updateDisplay()
        }
    }

    override fun waveformTouchStart(x: Float) {
        mTouchDragging = true
        mTouchStart = x
        mTouchInitialOffset = mOffset
        mFlingVelocity = 0
        mWaveformTouchStartMsec = System.currentTimeMillis()
    }

    override fun waveformTouchMove(x: Float) {
        mOffset = trap((mTouchInitialOffset + (mTouchStart - x)).toInt())
        updateDisplay()
    }

    override fun waveformTouchEnd() {
        mTouchDragging = false
        mOffsetGoal = mOffset
        val elapsedMsec = System.currentTimeMillis() - mWaveformTouchStartMsec
        if (elapsedMsec < 300) {
            if (mIsPlaying) {
                val seekMsec = mWaveformView?.pixelsToMillisecs((mTouchStart + mOffset).toInt())
                if (seekMsec != null) {
                    if (seekMsec >= mPlayStartMsec && seekMsec < mPlayEndMsec) {
                        mPlayer!!.seekTo(seekMsec - mPlayStartOffset)
                    } else {
                        handlePause()
                    }
                }
            } else {
                onPlay((mTouchStart + mOffset).toInt())
            }
        }
    }

    override fun waveformFling(vx: Float) {
        mTouchDragging = false
        mOffsetGoal = mOffset
        mFlingVelocity = (-vx).toInt()
        updateDisplay()
    }

    override fun waveformZoomIn() {
        if (mWaveformView?.canZoomOut() == true) {
            marginvalue = Pixels.pxtodp(this, 12)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(
                Pixels.pxtodp(this@ActivityEditorAudio, 12),
                0,
                Pixels.pxtodp(this@ActivityEditorAudio, 12),
                Pixels.pxtodp(this@ActivityEditorAudio, 20)
            )
            mWaveformView?.layoutParams = params
        }
        mWaveformView?.zoomIn()

        mStartPos = mWaveformView?.start ?: 0
        mEndPos = mWaveformView?.end ?: 0
        mMaxPos = mWaveformView?.maxPos() ?: 0
        mOffset = mWaveformView?.offset ?: 0
        mOffsetGoal = mOffset
        updateDisplay()

    }

    override fun waveformZoomOut() {
        if (mWaveformView?.canZoomOut() == true) {
            marginvalue = Pixels.pxtodp(this, 12)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(
                Pixels.pxtodp(this@ActivityEditorAudio, 12),
                0,
                Pixels.pxtodp(this@ActivityEditorAudio, 12),
                Pixels.pxtodp(this@ActivityEditorAudio, 20)
            )
            mWaveformView?.layoutParams = params

        }
        mWaveformView?.zoomOut()
        mStartPos = mWaveformView?.start ?: 0
        mEndPos = mWaveformView?.end ?: 0
        mMaxPos = mWaveformView?.maxPos() ?: 0
        mOffset = mWaveformView?.offset ?: 0
        mOffsetGoal = mOffset
        updateDisplay()
    }

    override fun markerDraw() {}

    override fun markerTouchStart(marker: MarkerView, pos: Float) {
        mTouchDragging = true
        mTouchStart = pos
        mTouchInitialStartPos = mStartPos
        mTouchInitialEndPos = mEndPos
    }

    override fun markerTouchMove(marker: MarkerView, pos: Float) {
        val delta: Float = pos - mTouchStart

        if (marker == mStartMarker) {
            mStartPos = trap((mTouchInitialStartPos + delta).toInt())
            if (mStartPos + mStartMarker!!.width >= mEndPos) {
                mStartPos = mEndPos - mStartMarker!!.width
            }
        } else {
            mEndPos = trap((mTouchInitialEndPos + delta).toInt())
            if (mEndPos < mStartPos + mStartMarker!!.width) mEndPos =
                mStartPos + mStartMarker!!.width
        }
        /*mEndSplitSelected?.text = formatTime1(mMaxPos - mStartPos)*/
        updateDisplay()
    }

    override fun markerTouchEnd(marker: MarkerView) {
        mTouchDragging = false
        if (mIsPlaying && type == Type.SPLIT) {
            binding.waveform.setPlayback(-1)
            mIsPlaying = false
            pausePlayer()
            if (viewModel.isPlay1.value) {
                viewModel.isPlay1.value = false
            }

            if (viewModel.isPlay2.value) {
                viewModel.isPlay2.value = false
            }
        }
        if (marker == mStartMarker) {
            setOffsetGoalStart()
        } else {
            setOffsetGoalEnd()
        }
    }

    override fun markerLeft(marker: MarkerView, velocity: Int) {
        mKeyDown = true
        if (marker == mStartMarker) {
            val saveStart = mStartPos
            mStartPos = trap(mStartPos - velocity)
            mEndPos = trap(mEndPos - (saveStart - mStartPos))
            setOffsetGoalStart()
        }

        if (marker == mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity)
                mEndPos = mStartPos
            } else {
                mEndPos = trap(mEndPos - velocity)
            }
            setOffsetGoalEnd()
        }
        updateDisplay()
    }

    override fun markerRight(marker: MarkerView, velocity: Int) {
        mKeyDown = true
        if (marker == mStartMarker) {
            val saveStart = mStartPos
            mStartPos += velocity
            if (mStartPos > mMaxPos) mStartPos = mMaxPos
            mEndPos += mStartPos - saveStart
            if (mEndPos > mMaxPos) mEndPos = mMaxPos
            setOffsetGoalStart()
        }
        if (marker == mEndMarker) {
            mEndPos += velocity
            if (mEndPos > mMaxPos) mEndPos = mMaxPos
            setOffsetGoalEnd()
        }
        updateDisplay()
    }

    override fun markerEnter(marker: MarkerView) {}

    override fun markerKeyUp() {
        mKeyDown = false
        updateDisplay()
    }

    override fun markerFocus(marker: MarkerView) {
        mKeyDown = false
        if (marker == mStartMarker) {
            setOffsetGoalStartNoUpdate()
        } else {
            setOffsetGoalEndNoUpdate()
        }
        mHandler?.postDelayed({ this.updateDisplay() }, 100)
    }

    private fun loadGui() {
        val metrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(metrics)
        mDensity = metrics.density

        mMarkerLeftInset = (13 * mDensity).toInt()
        mMarkerRightInset = (13 * mDensity).toInt()


        mStartText = binding.starttext
        mEndText = binding.endtext
        mStartSplitSelected = binding.layoutPart.txtSelectedPart1
        mEndSplitSelected = binding.layoutPart.txtSelectedPart2

        binding.markStart.setOnClickListener(this)
        binding.markEnd.setOnClickListener(this)

        enableDisableButtons()

        mWaveformView = binding.waveform
        mWaveformView?.setListener(this)
        mMaxPos = 0
        mLastDisplayedStartPos = -1
        mLastDisplayedEndPos = -1

        if (mSoundFile != null && mWaveformView?.hasSoundFile() == true) {
            mWaveformView?.setSoundFile(mSoundFile)
            mWaveformView?.recomputeHeights(mDensity)
            mMaxPos = mWaveformView?.maxPos() ?: 0
        }

        mStartMarker = binding.startmarker
        mStartMarker!!.setListener(this)
        mStartMarker!!.alpha = 1f
        mStartMarker!!.isFocusable = true
        mStartMarker!!.isFocusableInTouchMode = true
        mStartVisible = true

        mEndMarker = binding.endmarker
        mEndMarker!!.setListener(this)
        mEndMarker!!.alpha = 1f
        mEndMarker!!.isFocusable = true
        mEndMarker!!.isFocusableInTouchMode = true
        mEndVisible = true

        updateDisplay()
    }

    private fun loadFromFile() {
        mFile = File(mFilename!!)
        val mFileName = mFile!!.name
        var FileSupported = false
        for (aSupported_Format in Supported_Format) if (mFileName.contains(aSupported_Format)) {
            FileSupported = true
            break
        }

        if (!FileSupported) {
            Toast.makeText(this, "No support", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        mLoadingLastUpdateTime = System.currentTimeMillis()
        mLoadingKeepGoing = true
        mProgressDialog =
            ProgressDialog(this@ActivityEditorAudio, R.style.AppCompatAlertDialogStyle)
        mProgressDialog?.setCancelable(false)
        mProgressDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog?.setTitle("Loading")
        mProgressDialog?.setButton(
            DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel)
        ) { _, _ ->
            finish() //dismiss dialog
        }
        mProgressDialog?.show()
        mProgressDialog!!.setOnDismissListener {
            runOnUiThread {
                mEndMarker!!.visibility = View.VISIBLE
                mStartMarker!!.visibility = View.VISIBLE
            }
        }
        // Create the MediaPlayer in a background thread
        thread1 = object : Thread() {
            override fun run() {
                try {
                    mPlayer = MediaPlayer()
                    mPlayer?.setDataSource(this@ActivityEditorAudio, Uri.fromFile(mFile))
                    mPlayer?.prepare()
                    thread1?.interrupt()
                } catch (ignored: java.lang.Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ActivityEditorAudio,
                            getString(R.string.file_name),
                            Toast.LENGTH_LONG
                        ).show()
                        AlertDialog.Builder(this@ActivityEditorAudio).setTitle(R.string.error)
                            .setMessage(R.string.error.toString())
                            .setPositiveButton(R.string.save) { _, _ -> finish() }.show()
                    }
                    try {
                        val filePath = mFile!!.absolutePath
                        val file = File(filePath)
                        val inputStream = FileInputStream(file)
                        mPlayer = MediaPlayer()
                        mPlayer?.setDataSource(inputStream.fd)
                        mPlayer?.prepare()
                        thread1?.interrupt()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        thread1?.start()
        // Load the sound file in a background thread
        val listener = CheapSoundFile.ProgressListener { fractionComplete ->
            val now = System.currentTimeMillis()
            if (now - mLoadingLastUpdateTime > 100) {
                mProgressDialog!!.progress = (mProgressDialog!!.max * fractionComplete).toInt()
                mLoadingLastUpdateTime = now
            }
            mLoadingKeepGoing
        }


        thread2 = object : Thread() {
            override fun run() {
                try {
                    mSoundFile = CheapSoundFile.create(mFile!!.absolutePath, listener)
                } catch (e: Exception) {
                    mProgressDialog?.dismiss()
                    return
                }

                if (mLoadingKeepGoing) {
                    mHandler?.post {
                        if (mSoundFile != null) {
                            runOnUiThread {
                                finishOpeningSoundFile()
                            }
                        } else {
                            mProgressDialog?.dismiss()
                            AlertDialog.Builder(this@ActivityEditorAudio).setTitle(R.string.error)
                                .setMessage(R.string.error)
                                .setPositiveButton(R.string.save) { _, _ -> finish() }.show()
                        }
                    }
                }
                thread2?.interrupt()
            }
        }
        thread2?.start()
    }

    private fun finishOpeningSoundFile() {
        try {
            mWaveformView?.setSoundFile(mSoundFile)
            mWaveformView?.recomputeHeights(mDensity)
            mMaxPos = mWaveformView?.maxPos()!!
            mLastDisplayedStartPos = -1
            mLastDisplayedEndPos = -1
            mTouchDragging = false
            mOffset = 0
            mOffsetGoal = 0
            mFlingVelocity = 0
            checkType(type)
            resetPositions()
            /*binding.layoutPart.txtSelectedPart2.text = formatTime1(mStartPos)*/
            binding.endmarker.show()
            binding.startmarker.show()
            updateDisplay()
            mProgressDialog?.dismiss()
        } catch (ex: java.lang.Exception) {
        }
    }

    @SuppressLint("NewApi")
    @Synchronized
    private fun updateDisplay() {
        if (mPlayer != null) {
            mSoundDuration = mPlayer!!.duration / 1000
        }

        // ====== khi đang play ======
        if (mIsPlaying) {
            var now = (mPlayer?.currentPosition ?: 0) + mPlayStartOffset
            val frames = mWaveformView?.millisecsToPixels(now)
            if (frames != null) {
                mWaveformView?.setPlayback(frames)
                setOffsetGoalNoUpdate(frames - mWidth / 2)
            }
            if (now >= mPlayEndMsec) {
                handlePause()
            }
        }

        // ====== fling / scroll ======
        if (!mTouchDragging) {
            var offsetDelta: Int
            if (mFlingVelocity != 0) {
                offsetDelta = mFlingVelocity / 30
                if (mFlingVelocity > 80) mFlingVelocity -= 80
                else if (mFlingVelocity < -80) mFlingVelocity += 80
                else mFlingVelocity = 0

                mOffset += offsetDelta
                if (mOffset + mWidth / 2 > mMaxPos) {
                    mOffset = mMaxPos - mWidth / 2
                    mFlingVelocity = 0
                }
                if (mOffset < 0) {
                    mOffset = 0
                    mFlingVelocity = 0
                }
                mOffsetGoal = mOffset
            } else {
                offsetDelta = mOffsetGoal - mOffset
                when {
                    offsetDelta > 10 -> offsetDelta /= 10
                    offsetDelta > 0 -> offsetDelta = 1
                    offsetDelta < -10 -> offsetDelta /= 10
                    offsetDelta < 0 -> offsetDelta = -1
                    else -> offsetDelta = 0
                }
                mOffset += offsetDelta
            }
        }

        // ====== kiểm tra edge ======
        if (mWaveformView != null) {
            if (mWaveformView?.getcurrentmLevel() != 0) {
                if ((mWaveformView?.measuredWidth?.plus(mOffset)
                        ?: 0) >= (mWaveformView?.getcurrentmLevel() ?: 0)
                ) {
                    mOffset =
                        (mWaveformView?.getcurrentmLevel() ?: 0) - (mWaveformView?.measuredWidth
                            ?: 0)
                    EdgeReached = true
                } else {
                    EdgeReached = false
                }
            }
        }

        if (type == Type.SPLIT) {
            mStartPos = 0
        }

        if (type == Type.TRIM) {
            val midPos = (mStartPos + mEndPos) / 2
            setOffsetGoalNoUpdate(midPos - mWidth / 2)
        }
        mWaveformView?.setParameters(mStartPos, mEndPos, mOffset, mSoundDuration)
        mWaveformView?.invalidate()

        if (type != Type.SPLIT) {
            var startX = mStartPos - mOffset - mMarkerLeftInset
            if (startX + mStartMarker!!.width >= 0) {
                if (!mStartVisible) {
                    mHandler?.postDelayed({
                        mStartVisible = true
                        mStartMarker!!.alpha = 1f
                    }, 0)
                }
            } else {
                if (mStartVisible) {
                    mStartMarker!!.alpha = 0f
                    mStartVisible = false
                }
                startX = 0
            }
            val layoutParamsStart = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            mWaveformView?.measuredHeight?.let {
                layoutParamsStart.setMargins(startX + marginvalue, it, 0, 0)
            }
            mStartMarker!!.layoutParams = layoutParamsStart
        }

        // END marker (luôn có)
        var endX = mEndPos - mOffset - mEndMarker!!.width + mMarkerRightInset
        if (endX + mEndMarker!!.width >= 0) {
            if (!mEndVisible) {
                mHandler?.postDelayed({
                    mEndVisible = true
                    mEndMarker!!.alpha = 1f
                }, 0)
            }
        } else {
            if (mEndVisible) {
                mEndMarker!!.alpha = 0f
                mEndVisible = false
            }
            endX = 0
        }
        val layoutParamsEnd = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        if (endX + marginvalue <= (mWaveformView?.measuredWidth ?: 0)) {
            layoutParamsEnd.setMargins(endX + marginvalue, mWaveformView?.measuredHeight ?: 0, 0, 0)
        } else {
            if (endX <= (mWaveformView?.measuredWidth ?: 0)) {
                layoutParamsEnd.setMargins(
                    mWaveformView?.measuredWidth ?: 0,
                    mWaveformView?.measuredHeight ?: 0,
                    0, 0
                )
            } else {
                mWaveformView?.measuredHeight?.let { layoutParamsEnd.setMargins(endX, it, 0, 0) }
            }
        }
        mEndMarker!!.layoutParams = layoutParamsEnd
        Log.d("M_START_POS", "$mEndPos")

    }


    private fun enableDisableButtons() {
        runOnUiThread {
            if (mIsPlaying) {
//                binding.icPlay.setImageResource(R.drawable.ic_pause)
//                binding.baseTextview4.show()
            } else {
                viewModel.isPlayTrim.value = false
            }
        }
    }

    private fun resetPositions() {
        if (type == Type.SPLIT) {
            mStartPos = 0
            mEndPos = mMaxPos
        } else {
            mStartPos = 0
            mEndPos = mMaxPos
        }
    }

    private fun trap(pos: Int): Int {
        if (pos < 0) return 0
        return if (pos > mMaxPos) mMaxPos else pos
    }

    private fun setOffsetGoalStart() = setOffsetGoal(mStartPos - mWidth / 2)
    private fun setOffsetGoalStartNoUpdate() {
        val m = if (type == Type.SPLIT) 0 else mStartPos - mWidth / 2
        setOffsetGoalNoUpdate(m)
    }

    private fun setOffsetGoalEnd() = setOffsetGoal(mEndPos - mWidth / 2)
    private fun setOffsetGoalEndNoUpdate() = setOffsetGoalNoUpdate(mEndPos - mWidth / 2)

    private fun setOffsetGoal(offset: Int) {
        setOffsetGoalNoUpdate(offset)
        updateDisplay()
    }

    private fun setOffsetGoalNoUpdate(offset: Int) {
        if (mTouchDragging) {
            return
        }

        mOffsetGoal = offset
        if (mOffsetGoal + mWidth / 2 > mMaxPos) mOffsetGoal = mMaxPos - mWidth / 2
        if (mOffsetGoal < 0) mOffsetGoal = 0
    }

    private fun formatTime(pixels: Int): String {
        return if (mWaveformView != null && mWaveformView?.isInitialized == true) {
            formatDecimal(mWaveformView?.pixelsToSeconds(pixels) ?: 0.toDouble())
        } else {
            ""
        }
    }

    private fun formatTime1(i: Int): String {
        return DurationFormatter.format((binding.waveform.pixelsToSeconds(i) * 1000.0).toInt())
            ?: ""
    }

    private fun formatDecimal(x: Double): String {
        var xWhole = x.toInt()
        var xFrac = (100 * (x - xWhole) + 0.5).toInt()

        if (xFrac >= 100) {
            xWhole++ //Round up
            xFrac -= 100 //Now we need the remainder after the round up
            if (xFrac < 10) {
                xFrac *= 10 //we need a fraction that is 2 digits long
            }
        }

        return if (xFrac < 10) "$xWhole.0$xFrac"
        else "$xWhole.$xFrac"
    }

    @Synchronized
    private fun handlePause() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer!!.pause()
        }
        mWaveformView?.setPlayback(-1)
        mIsPlaying = false
        job?.cancel()
        binding.progressCircular.progress = 0
        if (viewModel.isPlay1.value) {
            viewModel.isPlay1.value = false
        }

        if (viewModel.isPlay2.value) {
            viewModel.isPlay2.value = false
        }
        enableDisableButtons()

    }

    @Synchronized
    private fun onPlay(startPosition: Int) {
        if (mIsPlaying) {
            handlePause()
            return
        }

        if (mPlayer == null) {
            // Not initialized yet
            return
        }

        try {
            mPlayStartMsec = mWaveformView?.pixelsToMillisecs(startPosition) ?: 0

            if (type == Type.SPLIT) {
                if (startPosition == 0) {
                    mPlayEndMsec = mWaveformView?.pixelsToMillisecs(mEndPos) ?: 0
                } else if (startPosition == mEndPos) {
                    mPlayEndMsec = mWaveformView?.pixelsToMillisecs(mMaxPos) ?: 0
                }
            } else {
                mPlayEndMsec = if (startPosition < mStartPos) {
                    mWaveformView?.pixelsToMillisecs(mStartPos) ?: 0
                } else if (startPosition > mEndPos) {
                    mWaveformView?.pixelsToMillisecs(mMaxPos) ?: 0
                } else {
                    mWaveformView?.pixelsToMillisecs(mEndPos) ?: 0
                }
            }


            mPlayStartOffset = 0
            val startFrame = mWaveformView?.secondsToFrames(mPlayStartMsec * 0.001)
            val endFrame = mWaveformView?.secondsToFrames(mPlayEndMsec * 0.001)
            val startByte = mSoundFile!!.getSeekableFrameOffset(startFrame ?: 0)
            val endByte = mSoundFile!!.getSeekableFrameOffset(endFrame ?: 0)
            if (startByte >= 0 && endByte >= 0) {
                mPlayStartOffset = try {
                    mPlayer!!.reset()
                    val subsetInputStream = FileInputStream(mFile!!.absolutePath)
                    mPlayer!!.setDataSource(
                        subsetInputStream.fd, startByte.toLong(), (endByte - startByte).toLong()
                    )
                    mPlayer!!.prepare()
                    mPlayStartMsec
                } catch (e: Exception) {
                    mPlayer!!.reset()
                    mPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    mPlayer!!.setDataSource(mFile!!.absolutePath)
                    mPlayer!!.prepare()
                    0
                }

            }

            mPlayer!!.setOnCompletionListener { handlePause() }
            mIsPlaying = true
            if (mPlayStartOffset == 0) {
                mPlayer!!.seekTo(mPlayStartMsec)
            }
            val res = audioManager!!.requestAudioFocus(
                afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
            )
            if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mPlayer!!.start()
            }
            updateDisplay()
            enableDisableButtons()
        } catch (e: Exception) {
        }

    }


    @SuppressLint("SetTextI18n")
    override fun CreateSelection(startpoint: Double, endpoint: Double) {
        if (mEndPos != -1 || mStartPos != -1) {
            if (mWaveformView != null) {
                val endpointbefore =
                    java.lang.Float.valueOf(mWaveformView?.pixelsToSeconds(mEndPos).toString())
                val endpointafter = java.lang.Float.valueOf(endpoint.toString())
                val propertyValuesHolder =
                    PropertyValuesHolder.ofFloat("phase", endpointbefore, endpointafter)
                val startpointBefore =
                    java.lang.Float.valueOf(mWaveformView?.pixelsToSeconds(mStartPos).toString())
                val startpointAFter = java.lang.Float.valueOf(startpoint.toString())
                val propertyValuesHolder2 =
                    PropertyValuesHolder.ofFloat("phase2", startpointBefore, startpointAFter)
                val mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                    this, propertyValuesHolder, propertyValuesHolder2
                )

                mObjectAnimator.addUpdateListener { valueAnimator ->
                    val newEndpos = java.lang.Float.valueOf(
                        valueAnimator.getAnimatedValue(propertyValuesHolder.propertyName).toString()
                    )
                    if (mWaveformView != null) {
                        mEndPos = mWaveformView?.secondsToPixels(newEndpos.toDouble()) ?: 0
                        val NewStartpos = java.lang.Float.valueOf(
                            valueAnimator.getAnimatedValue(propertyValuesHolder2.propertyName)
                                .toString()
                        )

                        mStartPos = mWaveformView?.secondsToPixels(NewStartpos.toDouble()) ?: 0
                        viewModel.setEnd(mEndPos)
                        if (type == Type.SPLIT) {
                            viewModel.setStart(0)
                        } else {
                            viewModel.setStart(mStartPos)
                        }

                        mStartText!!.text = (newEndpos % 3600 / 60).toInt()
                            .toString() + ":" + (newEndpos % 60).toInt().toString()
                        mEndText!!.text = (NewStartpos % 3600 / 60).toInt()
                            .toString() + ":" + (NewStartpos % 60).toInt().toString()
                        updateDisplay()
                    }
                }
                mObjectAnimator.start()
                mStartText!!.text =
                    (startpoint % 3600 / 60).toInt().toString() + ":" + (startpoint % 60).toInt()
                        .toString()
                mEndText!!.text =
                    (endpoint % 3600 / 60).toInt().toString() + ":" + (endpoint % 60).toInt()
                        .toString()
                mEndPos = mWaveformView?.secondsToPixels(endpoint) ?: 0
                mStartPos = mWaveformView?.secondsToPixels(startpoint) ?: 0
                viewModel.setEnd(mEndPos)
                viewModel.setStart(mStartPos)

                Log.d("POSITION_POINT", "$mEndPos - $mStartPos")
                updateDisplay()
            }
        }

    }

    fun setPhase(phase: Float) {}
    fun setPhase2(phase2: Float) {}

    override fun onClick(view: View) {
        when {
            view === binding.zoomIn -> waveformZoomIn()
            view === binding.zoomOut -> waveformZoomOut()

            view == binding.markStart -> if (mIsPlaying) {
                mStartPos =
                    mWaveformView?.millisecsToPixels(mPlayer!!.currentPosition + mPlayStartOffset)
                        ?: 0
                updateDisplay()
            }

            view == binding.markEnd -> if (mIsPlaying) {
                mEndPos =
                    mWaveformView?.millisecsToPixels(mPlayer!!.currentPosition + mPlayStartOffset)
                        ?: 0
                updateDisplay()
                handlePause()
            }

            else -> Cutselection(view.id)
        }
    }

    private fun Cutselection(which: Int) {
        when (which) {
            R.id.btnSetRingTone -> {
                /* if (!AppUtils.isGrantSettingPermission(this)) {
                     AppUtils.requestSettingPermission(this)
                 } else {
                     showInterAdsSingleGlobal(AppConstants.InterAdsType.InterRingtone,
                         actionFinishAds = {
                             SaveRingTone()
                             mNewFileKind = FILE_KIND_RINGTONE
                         })
                 }*/
            }
        }
    }

    companion object {
        private val TAG = "ActivityEditorAudio"
        private const val EXTENSION_MP3 = ".mp3"
        const val KEY_SOUND_COLUMN_title = "title"
        const val KEY_SOUND_COLUMN_artist = "artist"
        const val KEY_SOUND_COLUMN_path = "path"
        const val FILE_KIND_RINGTONE = 1

        private fun getTimeFormat(time: String): String {
            return if (time.isNotEmpty() && !time.contains("-")) {
                val Displayedmins: String
                val DisplayedSecs: String
                val mins = java.lang.Double.parseDouble(time) % 3600 / 60
                Displayedmins =
                    if (mins < 10) "0" + mins.toInt().toString() else mins.toInt().toString()
                val secs = java.lang.Double.parseDouble(time) % 60
                DisplayedSecs =
                    if (secs < 10) "0" + secs.toInt().toString() else secs.toInt().toString()
                "$Displayedmins:$DisplayedSecs"
            } else ""
        }

        @SuppressLint("DefaultLocale")
        fun formatDuration(seconds: Int): String {
            val minutes = seconds / 60
            val secs = seconds % 60

            return if (minutes > 0) {
                String.format("%02d:%02d", minutes, secs)
            } else {
                String.format("%02d:%02d", minutes, secs)
            }
        }
    }
}