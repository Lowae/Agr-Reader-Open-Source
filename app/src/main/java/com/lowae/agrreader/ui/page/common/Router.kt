package com.lowae.agrreader.ui.page.common

import android.net.Uri
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.lowae.agrreader.data.model.entities.ImageSrcEntity
import com.lowae.agrreader.data.model.general.FilterState

sealed class Router(
    private val route: String,
) {

    open val arguments: List<NamedNavArgument> = emptyList()

    val routeUri: String
        get() {
            val route = StringBuilder(this.route)
            arguments.forEach {
                when (it.argument.type) {
                    is NavType.ParcelableType,
                    is NavType.ParcelableArrayType<*>,
                    is NavType.EnumType,
                    is NavType.SerializableType,
                    is NavType.SerializableArrayType<*> -> {
                        return@forEach
                    }
                }
                if (it.argument.isNullable) {
                    route.append("?${it.name}={${it.name}}")
                } else {
                    route.append("/{${it.name}}")
                }
            }
            return route.toString()
        }


    fun navigate(
        navHostController: NavHostController,
        vararg args: String?,
        builder: (NavOptionsBuilder.() -> Unit)? = null
    ) {
        if (builder == null) {
            navHostController.navigate(getNavigationUri(*args))
        } else {
            navHostController.navigate(getNavigationUri(*args), builder)
        }
    }

    fun navigate(
        navHostController: NavHostController,
        parcelable: List<Parcelable>,
        builder: (NavOptionsBuilder.() -> Unit)? = null
    ) {
        val destination = navHostController.graph.findNode(this.route)
        if (destination != null) {
            navHostController.navigate(
                destination.id,
                bundleOf(*arguments.mapIndexed { index, namedNavArgument -> namedNavArgument.name to parcelable[index] }
                    .toTypedArray()),
                if (builder == null) null else navOptions(builder)
            )
        }
    }

    private fun getNavigationUri(vararg args: String?): String {
        val route = StringBuilder(this.route)
        arguments.forEachIndexed { index, argument ->
            val argValues = Uri.encode(args[index])
            if (argument.argument.isNullable) {
                route.append("?${argument.name}=${argValues}")
            } else {
                route.append("/${argValues}")
            }
        }
        return route.toString()
    }
}

data object ReadingRouter : Router("reading") {

    override val arguments: List<NamedNavArgument> =
        listOf(navArgument("article_id") { type = NavType.StringType })
}

data object ReadingPagerRouter : Router("reading_pager") {

    override val arguments: List<NamedNavArgument> =
        listOf(navArgument("article_id") { type = NavType.StringType })
}

data object FlowRouter : Router("flow") {

    const val ARGUMENT_FLOW_KEY_FILTER_STATE = "filter_state"

    override val arguments: List<NamedNavArgument> =
        listOf(navArgument(ARGUMENT_FLOW_KEY_FILTER_STATE) {
            type = NavType.ParcelableType(FilterState::class.java)
        })

}

data object TodayOfUnreadFlowRouter : Router("today_of_unread")

data object CheckProDialogRouter : Router("check_pro") {

    override val arguments: List<NamedNavArgument> = listOf(navArgument("arg_description") {
        type = NavType.StringType
        nullable = true
    })

}

data object FeedInfoRouter : Router("feed_info") {
    override val arguments: List<NamedNavArgument> = listOf(navArgument("feed_id") {
        type = NavType.StringType
    })
}

data object GroupInfoRouter : Router("group_info") {
    override val arguments: List<NamedNavArgument> = listOf(navArgument("group_id") {
        type = NavType.StringType
    })
}

data object RssServerSettingRouter : Router("rss_server_setting")

data object NavigationAndFeedBackRouter : Router("navigation_feedback")

data object BackupSettingRouter : Router("backup_setting")
data object InteractiveSettingRouter : Router("interactive_setting")

data object InteractiveTranslatorSettingRouter : Router("interactive_translator_setting")

data object ReadingImageViewerRouter : Router("reading_image_viewer") {

    const val ARGUMENT_IMAGES = "argument_images"

    override val arguments: List<NamedNavArgument> = listOf(navArgument(ARGUMENT_IMAGES) {
        type = NavType.ParcelableType(ImageSrcEntity::class.java)
    })
}

data object FeedManagementRouter : Router("feed_management")

