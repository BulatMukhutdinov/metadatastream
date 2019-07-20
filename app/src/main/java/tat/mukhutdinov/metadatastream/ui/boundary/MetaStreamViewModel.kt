package tat.mukhutdinov.metadatastream.ui.boundary

import androidx.lifecycle.LiveData

interface MetaStreamViewModel {

    val binding: MetaStreamBinding

    val toastMessage: LiveData<String>
}