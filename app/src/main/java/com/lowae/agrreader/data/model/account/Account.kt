package com.lowae.agrreader.data.model.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lowae.agrreader.data.model.account.security.DESUtils
import com.lowae.agrreader.data.source.RYDatabase
import java.util.Date

/**
 * In the application, at least one account exists and different accounts
 * can have the same feeds and articles.
 */
@Entity(tableName = RYDatabase.TABLE_NAME_ACCOUNT)
data class Account(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo
    var name: String,
    @ColumnInfo
    var type: AccountType,
    @ColumnInfo
    var updateAt: Date? = null,
    @ColumnInfo(defaultValue = DESUtils.empty)
    var securityKey: String? = DESUtils.empty,
)
