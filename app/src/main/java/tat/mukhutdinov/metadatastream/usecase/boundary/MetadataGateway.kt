package tat.mukhutdinov.metadatastream.usecase.boundary

interface MetadataGateway {

    suspend fun getMetadata(key: String) :String
}