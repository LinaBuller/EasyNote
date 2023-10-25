package com.easynote.domain.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import org.koin.core.component.KoinComponent
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.system.exitProcess

class BackupManagerLocalDatabase(val context: Context) :
    KoinComponent {

    fun backupDatabase(databaseName: String):File? {
        val dbFile = context.getDatabasePath(databaseName)
        val bkpFile =
            File(context.cacheDir, BACKUP_DATABASE)
        if (bkpFile.exists()) bkpFile.delete()

        return try {
            dbFile.copyTo(bkpFile, true)
            bkpFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun createDBTempFile(): File {
        val rootPath = File(context.filesDir, "Temp")
        if (!rootPath.exists()) rootPath.mkdirs()
        val bkpFile = File(rootPath, BACKUP_DATABASE)
        bkpFile.createNewFile()
        return bkpFile
    }

    fun loadTempFile(inputStream: InputStream,tempFile: File): File? {
        return try {
            val dso = tempFile.outputStream()
            inputStream.use { input ->
                dso.use { output ->
                    input.copyTo(output)
                }
            }
            inputStream.close()
            dso.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    fun restoreTempFileToDatabase(pathLocalDatabase: String, tempFile: File) {
        val dbFile = File(pathLocalDatabase)
        upload(tempFile, dbFile)
        tempFile.delete()
    }

    fun restartApp() {
        val i = context.packageManager.getLaunchIntentForPackage(context.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(i)
        exitProcess(0)
    }

    private fun upload(inputFile: File, outputFile: File) {
        try {
            val src = FileInputStream(inputFile).channel
            val dst = FileOutputStream(outputFile).channel
            dst.transferFrom(src, 0, src.size())
            src.close()
            dst.close()
            Log.d("msg", "File ${inputFile.name}  copy successfully")
        } catch (e: Exception) {
            Log.d("msg", e.toString())
        }
    }

    companion object {
        const val BACKUP_DATABASE = "database-bkp"
    }
}