package tat.mukhutdinov.metadatastream.ui.boundary

import androidx.lifecycle.LiveData

interface MetaStreamBinding {

    val streamTitle: LiveData<String>

    val isStreaming: LiveData<Boolean>

    fun onStartClicked()

    fun onPauseClicked()
}