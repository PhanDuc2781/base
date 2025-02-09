package com.example.base_project.ext

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicBoolean

inline fun <T> Flow<T>.bind(
    liveData: MutableLiveData<T>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    crossinline onCollect: (T) -> Unit = {}
) = scope.launch {
    this@bind.collect {
        liveData.postValue(it)
        onCollect(it)
    }
}

fun <T> LiveData<T>.debounce(
    timeMillis: Long = 1000L,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) =
    MediatorLiveData<T>().also { mld ->

        val source = this
        var job: Job? = null

        mld.addSource(source) {
            job?.cancel()
            job = coroutineScope.launch {
                delay(timeMillis)
                mld.value = source.value
            }
        }
    }

infix fun <A, B> LiveData<A>.zip(stream: LiveData<B>): LiveData<Pair<A?, B?>> {
    val result = MediatorLiveData<Pair<A?, B?>>()
    result.addSource(this) { a ->
        result.value = a to stream.value
    }
    result.addSource(stream) { b ->
        result.value = this.value to b
    }
    return result
}

class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val observers = CopyOnWriteArraySet<ObserverWrapper<in T>>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer)
        observers.add(wrapper)
        super.observe(owner, wrapper)
    }

    override fun removeObservers(owner: LifecycleOwner) {
        observers.clear()
        super.removeObservers(owner)
    }

    override fun removeObserver(observer: Observer<in T>) {
        observers.remove(observer)
        call()
        super.removeObserver(observer)
    }

    @MainThread
    override fun setValue(t: T?) {
        observers.forEach { it.newValue() }
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    private class ObserverWrapper<T>(private val observer: Observer<T>) : Observer<T> {

        private val pending = AtomicBoolean(false)

        override fun onChanged(value: T) {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(value)
            }
        }

        fun newValue() {
            pending.set(true)
        }
    }
}