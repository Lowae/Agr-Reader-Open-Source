package com.lowae.agrreader.data.model.account

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RssFeed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.painterResource
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.R
import com.lowae.agrreader.data.provider.RssOperation
import com.lowae.agrreader.utils.ext.toast

/**
 * Each account will specify its local or third-party API type.
 */
data class AccountType(val id: Int) {

    /**
     * Make sure the constructed object is valid.
     */
    init {
        if (id < 1 || id > 6) {
            throw IllegalArgumentException("Account type id is not valid.")
        }
    }

    fun toDesc(context: Context = AgrReaderApp.application): String =
        when (this.id) {
            1 -> context.getString(R.string.local)
            2 -> context.getString(R.string.fever)
            3 -> context.getString(R.string.google_reader)
            4 -> context.getString(R.string.fresh_rss)
            5 -> context.getString(R.string.feedly)
            6 -> context.getString(R.string.inoreader)
            else -> context.getString(R.string.unknown)
        }

    @Stable
    @Composable
    fun toIcon(): Any =
        when (this.id) {
            1 -> Icons.Rounded.RssFeed
            2 -> painterResource(id = R.drawable.ic_fever)
            3 -> painterResource(id = R.drawable.ic_google_reader)
            4 -> painterResource(id = R.drawable.ic_freshrss)
            5 -> painterResource(id = R.drawable.ic_feedly)
            6 -> painterResource(id = R.drawable.ic_inoreader)
            else -> Icons.Rounded.RssFeed
        }

    suspend fun removePreferences() {
        when (this.id) {
            FreshRSS.id -> {
            }
        }
    }

    /**
     * Type of account currently supported.
     */
    companion object {
        val Local = AccountType(1)
        val Fever = AccountType(2)
        val GoogleReader = AccountType(3)
        val FreshRSS = AccountType(4)
        val Feedly = AccountType(5)
        val Inoreader = AccountType(6)

        fun checkOperation(accountTypeId: Int, operation: RssOperation): Boolean {
            val supports = when (accountTypeId) {
                Local.id -> RssOperation.Local
                Fever.id -> RssOperation.Fever
                GoogleReader.id -> RssOperation.GoogleReader
                FreshRSS.id -> RssOperation.FreshRSS
                Feedly.id -> RssOperation.Feedly
                Inoreader.id -> RssOperation.Inoreader
                else -> RssOperation.Local
            }
            return if (supports.contains(operation).not()) {
                toast(R.string.rss_server_operation_disable_toast)
                false
            } else {
                true
            }
        }

        fun valueOf(type: Int) = when (type) {
            Local.id -> Local
            Fever.id -> Fever
            GoogleReader.id -> GoogleReader
            FreshRSS.id -> FreshRSS
            Feedly.id -> Feedly
            Inoreader.id -> Inoreader
            else -> Local
        }
    }
}

/**
 * Provide [TypeConverter] of [AccountType] for [RoomDatabase].
 */
class AccountTypeConverters {

    @TypeConverter
    fun toAccountType(id: Int): AccountType {
        return AccountType(id)
    }

    @TypeConverter
    fun fromAccountType(accountType: AccountType): Int {
        return accountType.id
    }
}
