package com.example.bmr_app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageException
import com.amplifyframework.storage.options.StorageDownloadFileOptions
import com.amplifyframework.storage.result.StorageDownloadFileResult
import com.amplifyframework.storage.result.StorageTransferProgress
import com.amplifyframework.storage.result.StorageUploadFileResult
import com.example.bmr_app.weatherData.weatherApplication
import java.io.File


object AWSHelper {
    fun uploadFile() {
        var files: Array<String> = arrayOf(
            "/data/data/com.example.bmr_app/databases/user_database",
            "/data/data/com.example.bmr_app/databases/weather.db"
        )

//    var out = ZipOutputStream(BufferedOutputStream(FileOutputStream("/data/data/com.example.bmr_app/databases/databases.zip")))
//    var data = ByteArray(1024)
//
//    for (file in files){
//        var fileinput = FileInputStream(file)
//        var bufferedinput = BufferedInputStream(fileinput)
//        var entry = ZipEntry(file.substring(file.lastIndexOf("/")))
//        out.putNextEntry(entry)
//        bufferedinput.buffered(1024).reader().forEachLine {
//            out.write(data)
//        }
//        bufferedinput.close()
//        out.closeEntry()
//    }
//    out.close()

//    val file = File("/data/data/com.example.bmr_app/databases/databases.zip")
        for (file in files) {
            val fullFile = File(file)
            if (fullFile.exists()) {
                val key =
                    Amplify.Auth.currentUser.userId + file.substring(file.lastIndexOf("/") + 1)
                Log.d("Key confirmation", key)

                Amplify.Storage.uploadFile(
                    key,
                    File(file),
                    { result: StorageUploadFileResult ->
                        Log.i("Amplify", "Successful upload:" + result.key)
                    },
                    { storageFailure: StorageException? ->
                        Log.e("Amplify", "Upload Failed: $key", storageFailure)
                    }

                )
            } else {
                continue
            }
        }

    }

    fun downloadFile() {
        var files: Array<String> = arrayOf(
            "/data/data/com.example.bmr_app/databases/user_database",
            "/data/data/com.example.bmr_app/databases/weather.db"
        )


        for (file in files) {
            var fileName =
                Amplify.Auth.currentUser.userId + file.substring(file.lastIndexOf("/") + 1)
            Log.d("Key confirmation", fileName)

            Amplify.Storage.downloadFile(
                fileName,
                File(file),
                StorageDownloadFileOptions.defaultInstance(),
                { progress: StorageTransferProgress ->
                    Log.i(
                        "AmplifyDownload",
                        "Fraction completed: " + progress.fractionCompleted
                    )
                },
                { result: StorageDownloadFileResult ->
                    Log.i(
                        "AmplifyDownload",
                        "Successfully downloaded: " + result.file.name
                    )
//                    triggerRebirth(weatherApplication().applicationContext)
                },
                { error: StorageException? ->
                    Log.e(
                        "AmplifyDownload",
                        "Download Failure $fileName",
                        error
                    )
                }
            )
        }
    }

    fun triggerRebirth(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
}

