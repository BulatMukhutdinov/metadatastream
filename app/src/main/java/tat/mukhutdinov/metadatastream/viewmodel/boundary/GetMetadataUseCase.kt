package tat.mukhutdinov.metadatastream.viewmodel.boundary

interface GetMetadataUseCase {

    suspend fun execute(key: String): String
}