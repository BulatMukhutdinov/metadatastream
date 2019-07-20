package tat.mukhutdinov.metadatastream.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tat.mukhutdinov.metadatastream.ui.boundary.MetaStreamBinding
import tat.mukhutdinov.metadatastream.ui.boundary.MetaStreamViewModel
import tat.mukhutdinov.metadatastream.viewmodel.boundary.GetMetadataUseCase
import timber.log.Timber

class MetaStreamLifecycleAwareViewModel(
    private val getMetadataUseCase: GetMetadataUseCase
) : ViewModel(), MetaStreamViewModel, MetaStreamBinding {

    override val binding: MetaStreamBinding = this

    override val toastMessage = MutableLiveData<String>()

    override val streamTitle = MutableLiveData("")

    override val isStreaming = MutableLiveData<Boolean>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        toastMessage.postValue(throwable.localizedMessage.orEmpty())
        isStreaming.postValue(false)

        Timber.e(throwable)
    }

    init {
        onStartClicked()
    }

    override fun onStartClicked() {
        if (isStreaming.value == true) {
            return
        }

        isStreaming.value = true

        viewModelScope.launch(exceptionHandler + Dispatchers.IO) {
            while (isStreaming.value == true) {
                val title = getMetadataUseCase.execute(STREAM_TITLE_KEY)

                streamTitle.postValue(title)

                delay(UPDATE_FREQUENCY)
            }
        }
    }

    override fun onPauseClicked() {
        isStreaming.value = false
    }

    companion object {
        private const val STREAM_TITLE_KEY = "StreamTitle"
        private const val UPDATE_FREQUENCY = 2000L
    }
}