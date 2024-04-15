package com.lowae.agrreader.ui.page.home.reading.viewer

import android.os.Environment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.lowae.agrreader.data.model.entities.ImageSrcEntity
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.module.CachedOkHttpClient
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.module.MainDispatcher
import com.lowae.agrreader.ui.page.common.ReadingImageViewerRouter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.executeAsync
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ReadingImageViewerViewModel @Inject constructor(
    @ApplicationScope
    private val scope: CoroutineScope,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher
    private val mainDispatcher: CoroutineDispatcher,
    @CachedOkHttpClient
    private val cachedOkHttpClient: OkHttpClient,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _articleImages =
        MutableStateFlow(
            savedStateHandle[ReadingImageViewerRouter.ARGUMENT_IMAGES] ?: ImageSrcEntity()
        )
    val articleImages = _articleImages.asStateFlow()

    fun downloadImage(url: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        scope.launch(ioDispatcher) {
            try {
                val request = Request.Builder().url(url).build()
                val response = cachedOkHttpClient.newCall(request).executeAsync()
                val pictureFile =
                    File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/AgrReader")

                pictureFile.mkdirs()
                response.body.byteStream().copyTo(
                    FileOutputStream(
                        File(pictureFile, "AgrReader-${System.currentTimeMillis()}.png")
                    )
                )
                withContext(mainDispatcher) { onSuccess() }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(mainDispatcher) { onFailure() }
            }
        }
    }
}