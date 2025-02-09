package com.example.base_project.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.base_project.applicattion.activeActivity
import com.example.base_project.ext.getInset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

typealias MyFragment = BaseFragment<*>
typealias Inflate2<B> = (LayoutInflater, ViewGroup?, Boolean) -> B

abstract class BaseFragment<B : ViewBinding>(private val inflate: Inflate2<B>) : Fragment() {
//    val fragmentScope: CoroutineLauncher by lazy {
//        return@lazy CoroutineLauncher()
//    }


    private var _binding: B? = null

    val binding: B
        get() = _binding ?: error("Not found binding")
    val mActivity: MyActivity?
        get() = (this.activity ?: activeActivity()) as? MyActivity

    private var view: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        initView()
        return _binding?.root
    }

    fun isDestroyed(): Boolean = _binding == null

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun reloadWhenLogin() {}

    open fun initView() {}

    open fun onPermissionGranted(permissions: Map<String, Boolean>) {}

    open fun onPermissionDenied(permissions: Map<String, Boolean>) {}
    override fun onDestroy() {
        super.onDestroy()
//        fragmentScope.cancelCoroutines()
        _binding = null
    }

    fun setupInset(bottomBar: View) {
        lifecycleScope.launch {
            val inset = mActivity?.getInset() ?: return@launch
            bottomBar.updateLayoutParams<ConstraintLayout.LayoutParams> {
                this.height = if (inset.bottom == 0) 1 else inset.bottom
                bottomBar.visibility =
                    if (inset.bottom == 0) View.INVISIBLE else View.VISIBLE
            }
        }
    }
}

abstract class BaseVMFragment<B : ViewBinding, VM : BaseViewModel>(inflate: Inflate2<B>) :
    BaseFragment<B>(inflate) {
    abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.progress.observe(this) {
            showProgress(it)
        }
    }
}

@MainThread
fun MyFragment.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launch(context, start, block)

@MainThread
fun MyFragment.showProgress(isShow: Boolean) {
    mActivity?.showProgress(isShow)
}

@MainThread
fun MyFragment.pushTo(@IdRes resId: Int, args: Bundle? = null) {
    mActivity?.pushTo(resId, args)
}

@MainThread
fun MyFragment.popTo(@IdRes destinationId: Int? = null, inclusive: Boolean = false) {
    mActivity?.popTo(destinationId, inclusive)
}

@MainThread
fun MyFragment.popToRoot() {
    mActivity?.popToRoot()
}