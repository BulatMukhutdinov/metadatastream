package tat.mukhutdinov.metadatastream.gateway

import tat.mukhutdinov.metadatastream.usecase.boundary.MetadataGateway
import wseemann.media.FFmpegMediaMetadataRetriever

class MetadataRemoteGateway(private val metadataRetriever: FFmpegMediaMetadataRetriever) : MetadataGateway {

    override suspend fun getMetadata(key: String): String {
        metadataRetriever.setDataSource(DATA_SOURCE)

        return metadataRetriever.metadata.getString(key)
    }

    companion object {
        private const val DATA_SOURCE = "http://stream.antenne.com/antenne-nds-80er/mp3-128/app/"
    }
}