package org.freekode.tp2intervals.integration.fit

import com.garmin.fit.Decode
import com.garmin.fit.FileIdMesgListener
import com.garmin.fit.MesgBroadcaster
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

/**
 * Utility object for reading metadata from Garmin FIT files.
 * Supports both plain .fit and .fit.gz (gzip-compressed) byte arrays.
 */
object FitFileReader {

    private val log = LoggerFactory.getLogger(FitFileReader::class.java)

    /**
     * Reads the product name from a FIT file's FileId message.
     * Returns null if the file is not a valid FIT file or has no product name.
     *
     * @param fitBytes raw bytes of a .fit or .fit.gz file
     * @param isGzipped whether the bytes are gzip-compressed
     */
    fun readProductName(fitBytes: ByteArray, isGzipped: Boolean = true): String? {
        return try {
            val inputStream = if (isGzipped) {
                GZIPInputStream(ByteArrayInputStream(fitBytes))
            } else {
                ByteArrayInputStream(fitBytes)
            }

            var productName: String? = null

            val decode = Decode()
            val broadcaster = MesgBroadcaster(decode)

            broadcaster.addListener(FileIdMesgListener { mesg ->
                if (mesg.productName != null) {
                    productName = mesg.productName
                }
            })

            inputStream.use { decode.read(it, broadcaster) }

            log.debug("FIT file product name: $productName")
            productName
        } catch (e: Exception) {
            log.warn("Could not read product name from FIT file: ${e.message}")
            null
        }
    }
}
