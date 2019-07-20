package tat.mukhutdinov.metadatastream.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tat.mukhutdinov.metadatastream.gateway.MetadataRemoteGateway
import tat.mukhutdinov.metadatastream.usecase.GetMetadataInteractor
import tat.mukhutdinov.metadatastream.usecase.boundary.MetadataGateway
import tat.mukhutdinov.metadatastream.viewmodel.MetaStreamLifecycleAwareViewModel
import tat.mukhutdinov.metadatastream.viewmodel.boundary.GetMetadataUseCase
import wseemann.media.FFmpegMediaMetadataRetriever

object InjectionModule {

    val module = module {

        viewModel {
            MetaStreamLifecycleAwareViewModel(get())
        }

        factory<GetMetadataUseCase> {
            GetMetadataInteractor(get())
        }

        single<MetadataGateway> {
            MetadataRemoteGateway(FFmpegMediaMetadataRetriever())
        }
    }
}