package org.freekode.tp2intervals.integration.platform.intervalsicu.activity

import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Wraps a raw ByteArray as a Spring MultipartFile for Feign upload.
 */
class ByteArrayMultipartFile(
    private val bytes: ByteArray,
    private val fileName: String,
    private val contentType: String = "application/octet-stream"
) : MultipartFile {
    override fun getName(): String = "file"
    override fun getOriginalFilename(): String = fileName
    override fun getContentType(): String = contentType
    override fun isEmpty(): Boolean = bytes.isEmpty()
    override fun getSize(): Long = bytes.size.toLong()
    override fun getBytes(): ByteArray = bytes
    override fun getInputStream(): InputStream = ByteArrayInputStream(bytes)
    override fun transferTo(dest: java.io.File) {
        dest.writeBytes(bytes)
    }
}
