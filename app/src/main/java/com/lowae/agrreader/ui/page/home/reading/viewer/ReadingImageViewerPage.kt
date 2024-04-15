package com.lowae.agrreader.ui.page.home.reading.viewer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.showProCheckDialog
import com.lowae.agrreader.utils.ext.collectAsStateValue
import com.lowae.agrreader.utils.ext.toast
import com.lowae.component.viewer.ImageGallery

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReadingImageViewerPage(
    navController: NavHostController,
    imageViewerViewModel: ReadingImageViewerViewModel = hiltViewModel()
) {
    val isActivePro = false
    val systemUiController = rememberSystemUiController()
    val imageSrcEntity = imageViewerViewModel.articleImages.collectAsStateValue()

    DisposableEffect(systemUiController) {
        systemUiController.isNavigationBarVisible = false

        onDispose {
            systemUiController.isNavigationBarVisible = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val pagerState = rememberPagerState(imageSrcEntity.index) { imageSrcEntity.images.size }
        ImageGallery(
            state = pagerState,
            beyondBoundsPageCount = 1,
            imageLoader = { index -> rememberCoilImagePainter(imageSrcEntity.images[index]) },
            detectGesture = {
                onTap = {
                    navController.popBackStack()
                }
            }
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
        ) {
            IconButton(onClick = {
                if (isActivePro.not()) {
                    showProCheckDialog(navController)
                } else {
                    imageViewerViewModel.downloadImage(
                        imageSrcEntity.images[pagerState.currentPage],
                        onSuccess = {
                            toast(R.string.download_image_success_toast)
                        },
                        onFailure = {
                            toast(R.string.download_image_fail_toast)
                        })
                }
            }) {
                Icon(
                    imageVector = Icons.Rounded.Download,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}

@Composable
private fun rememberCoilImagePainter(image: Any): AsyncImagePainter {
    // 加载图片
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(image)
        .crossfade(true)
        .size(coil.size.Size.ORIGINAL)
        .build()
    // 获取图片的初始大小
    return rememberAsyncImagePainter(imageRequest)
}