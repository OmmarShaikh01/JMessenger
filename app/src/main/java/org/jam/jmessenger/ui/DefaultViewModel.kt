package org.jam.jmessenger.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.jam.jmessenger.data.db.Event
import org.jam.jmessenger.data.db.Result

abstract class DefaultViewModel : ViewModel() {
    private val mDataLoading = MutableLiveData<Event<Boolean>>()
    val dataLoading: LiveData<Event<Boolean>> = mDataLoading

    protected fun <T> onResult(mutableLiveData: MutableLiveData<T>? = null, result: Result<T>) {
        when (result) {
            is Result.Loading -> mDataLoading.value = Event(true)

            is Result.Error -> {
                mDataLoading.value = Event(false)
            }

            is Result.Success -> {
                mDataLoading.value = Event(false)
                result.data?.let { mutableLiveData?.value = it }
            }
        }
    }
}