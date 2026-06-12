package com.undef.superahorro.haronsignorini.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun createTicketImageUri(context: Context): Uri {
    val ticketDirectory = File(context.filesDir, "tickets").apply {
        mkdirs()
    }
    val ticketFile = File.createTempFile(
        "ticket_${System.currentTimeMillis()}_",
        ".jpg",
        ticketDirectory
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        ticketFile
    )
}

fun persistTicketImageUri(context: Context, uriString: String): String {
    val sourceUri = Uri.parse(uriString)
    if (sourceUri.authority == "${context.packageName}.fileprovider") {
        return uriString
    }

    val ticketDirectory = File(context.filesDir, "tickets").apply {
        mkdirs()
    }
    val ticketFile = File(
        ticketDirectory,
        "ticket_${System.currentTimeMillis()}.jpg"
    )

    return runCatching {
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(ticketFile).use { output ->
                input.copyTo(output)
            }
        } ?: return uriString

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            ticketFile
        ).toString()
    }.getOrElse {
        uriString
    }
}
