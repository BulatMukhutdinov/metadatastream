package tat.mukhutdinov.metadatastream.gateway.util

import java.io.IOException
import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Possible alternative to get stream metadata if we don't want to use any external libs
 */

class IcyStreamMetadata {

    var streamUrl: URL?
        get() = _streamUrl
        set(value) {
            _streamUrl = value
            metadata = mutableMapOf()
            isError = false
        }

    var isError: Boolean = false
        private set

    val streamTitle: String
        @Throws(IOException::class)
        get() = getMetadata()["StreamTitle"].orEmpty()

    private var _streamUrl: URL? = null

    private var metadata: Map<String, String> = mutableMapOf()

    @Throws(IOException::class)
    fun getMetadata(): Map<String, String> {
        if (metadata.isEmpty()) {
            refreshMeta()
        }

        return metadata
    }

    @Synchronized
    @Throws(IOException::class)
    fun refreshMeta() {
        retrieveMetadata()
    }

    @Synchronized
    @Throws(IOException::class)
    private fun retrieveMetadata() {
        val streamUrl = _streamUrl ?: return

        val urlConnection = streamUrl.openConnection()

        urlConnection.setRequestProperty("Icy-MetaData", "1")
        urlConnection.setRequestProperty("Connection", "close")
        urlConnection.setRequestProperty("Accept", null)
        urlConnection.connect()

        var metaDataOffset = 0
        val headers = urlConnection.headerFields

        urlConnection.getInputStream().use {
            if (headers.containsKey("icy-metaint")) {
                val metaint = headers["icy-metaint"] ?: return
                // Headers are sent via HTTP
                metaDataOffset = Integer.parseInt(metaint[0])
            } else {
                // Headers are sent within a stream
                val strHeaders = StringBuilder()

                do {
                    val i = it.read().toChar().toInt()
                    strHeaders.append(i)
                    if (strHeaders.length > 5 && strHeaders.substring(strHeaders.length - 4, strHeaders.length) == "\r\n\r\n") {
                        // end of headers
                        break
                    }
                } while (i != -1)

                // Match headers to get metadata offset within a stream
                val pattern = Pattern.compile("\\r\\n(icy-metaint):\\s*(.*)\\r\\n")
                val matcher = pattern.matcher(strHeaders.toString())

                if (matcher.find()) {
                    val group = matcher.group(2) ?: return
                    metaDataOffset = Integer.parseInt(group)
                }
            }

            // In case no data was sent
            if (metaDataOffset == 0) {
                isError = true
                return
            }

            // Read metadata
            var count = 0
            var metaDataLength = 4080 // 4080 is the max length
            var inData: Boolean
            val metaData = StringBuilder()

            // Stream position should be either at the beginning or right after headers
            do {
                val i = it.read().toChar().toInt()
                count++

                // Length of the metadata
                if (count == metaDataOffset + 1) {
                    metaDataLength = i * 16
                }

                inData = count > metaDataOffset + 1 && count < metaDataOffset + metaDataLength

                if (inData && i != 0) {
                    metaData.append(i.toChar())
                }

                if (count > metaDataOffset + metaDataLength) {
                    break
                }
            } while (i != -1)

            // Set the data
            metadata = parseMetadata(metaData.toString())
        }
    }

    companion object {
        fun parseMetadata(metaString: String): Map<String, String> {
            val metadata = mutableMapOf<String, String>()
            val metaParts = metaString.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val pattern = Pattern.compile("^([a-zA-Z]+)='([^']*)'$")
            var matcher: Matcher

            metaParts.indices.forEach {
                matcher = pattern.matcher(metaParts[it])

                if (matcher.find()) {
                    metadata[matcher.group(1) as String] = matcher.group(2) as String
                }
            }

            return metadata
        }
    }
}