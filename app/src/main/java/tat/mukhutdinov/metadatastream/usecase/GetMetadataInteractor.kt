package tat.mukhutdinov.metadatastream.usecase

import tat.mukhutdinov.metadatastream.usecase.boundary.MetadataGateway
import tat.mukhutdinov.metadatastream.viewmodel.boundary.GetMetadataUseCase

class GetMetadataInteractor(private val metadataGateway: MetadataGateway) : GetMetadataUseCase {

    override suspend fun execute(key: String): String =
        metadataGateway.getMetadata(key)
}