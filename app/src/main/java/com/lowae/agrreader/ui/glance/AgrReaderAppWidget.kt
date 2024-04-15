package com.lowae.agrreader.ui.glance

import android.content.Context
import android.graphics.Bitmap
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults.defaultTextStyle
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Size
import com.lowae.agrreader.MainActivity
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.source.RYDatabase
import com.lowae.agrreader.ui.page.common.ExtraName
import com.lowae.agrreader.utils.ext.collectAsStateValue
import com.lowae.agrreader.utils.ext.formatAsString
import com.lowae.agrreader.utils.ifNullOrBlank
import kotlin.math.roundToInt

class AgrReaderAppWidget : GlanceAppWidget() {

    private val articleIdKey = ActionParameters.Key<String>(ExtraName.ARTICLE_ID)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                AgrReaderContent()
            }
        }
    }

    @Preview
    @Composable
    @GlanceComposable
    private fun AgrReaderContent() {
        val context = LocalContext.current
        val repository =
            remember { AgrReaderWidgetRepository(RYDatabase.getInstance(context.applicationContext)) }
        val articleList =
            repository.unreadOfTodayArticlesFlow.collectAsStateValue(initial = emptyList())
        Column(
            modifier = GlanceModifier.padding(12.dp).fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = GlanceModifier.size(24.dp),
                    provider = ImageProvider(R.mipmap.ic_launcher_round),
                    contentDescription = ""
                )
                Text(
                    modifier = GlanceModifier.padding(horizontal = 4.dp),
                    text = context.getString(R.string.unread_of_today),
                    style = defaultTextStyle.copy(
                        fontSize = 12.sp,
                        color = GlanceTheme.colors.inverseSurface
                    ),
                )
            }
            articleList.forEach {
                ArticleItem(
                    it,
                    actionStartActivity<MainActivity>(actionParametersOf(articleIdKey to it.article.id))
                )
            }
        }
    }

    @Composable
    @GlanceComposable
    private fun ArticleItem(
        articleWithFeed: ArticleWithFeed,
        onArticleClickAction: Action
    ) {
        val context = LocalContext.current
        Row(
            modifier = GlanceModifier.padding(vertical = 6.dp)
                .height(56.dp)
                .fillMaxWidth()
                .cornerRadius(8.dp)
                .clickable(onArticleClickAction)
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    text = articleWithFeed.article.title, style = defaultTextStyle.copy(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 15.sp,
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val feedIcon by rememberAsyncImageProvider(
                        context = context,
                        placeholder = R.mipmap.ic_launcher_round,
                        Size(dp2Px(context, 16), dp2Px(context, 16)),
                        articleWithFeed.feed.icon
                    )
                    Image(
                        modifier = GlanceModifier.size(16.dp).cornerRadius(12.dp),
                        provider = feedIcon,
                        contentDescription = ""
                    )
                    Text(
                        modifier = GlanceModifier.padding(horizontal = 6.dp),
                        text = "${articleWithFeed.article.author.ifNullOrBlank { articleWithFeed.feed.name }} | ${
                            articleWithFeed.article.date.formatAsString(context, true)
                        }",
                        style = defaultTextStyle.copy(
                            color = GlanceTheme.colors.secondary,
                            fontSize = 12.sp
                        ),
                        maxLines = 1
                    )
                }
            }
            Spacer(modifier = GlanceModifier.width(8.dp))
            if (articleWithFeed.article.img != null) {
                val articleImage by rememberAsyncImageProvider(
                    context = context,
                    placeholder = R.mipmap.ic_launcher_round,
                    Size(dp2Px(context, 64), dp2Px(context, 36)),
                    articleWithFeed.article.img
                )
                Image(
                    modifier = GlanceModifier.width(64.dp).height(36.dp).cornerRadius(8.dp),
                    provider = articleImage,
                    contentDescription = ""
                )
            }
        }
    }

    private suspend fun Context.loadImage(url: String, size: Size): Bitmap? {
        val request =
            ImageRequest.Builder(this).data(url)
                .allowRgb565(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .size(size)
                .build()
        return imageLoader.execute(request).drawable?.toBitmap()
    }

    @Composable
    @GlanceComposable
    private fun rememberAsyncImageProvider(
        context: Context,
        @DrawableRes placeholder: Int,
        size: Size,
        url: String? = null
    ): State<ImageProvider> {
        val image = remember {
            mutableStateOf(ImageProvider(placeholder))
        }
        LaunchedEffect(url) {
            url?.also { imageUrl ->
                val bitmap = context.loadImage(imageUrl, size) ?: return@also
                image.value = ImageProvider(bitmap)
            }
        }
        return image
    }

    private fun dp2Px(context: Context, dpValue: Int) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpValue.toFloat(),
        context.resources.displayMetrics
    ).roundToInt()
}