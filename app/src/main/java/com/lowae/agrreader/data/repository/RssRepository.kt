package com.lowae.agrreader.data.repository

import android.content.Context
import com.lowae.agrreader.data.model.account.AccountType
import com.lowae.agrreader.data.provider.FreshRssRepository
import com.lowae.agrreader.data.provider.fever.FeverRssRepository
import com.lowae.agrreader.data.provider.greader.GReaderRssRepository
import com.lowae.agrreader.utils.ext.CurrentAccountType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RssRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val localRssRepository: LocalRssRepository,
    private val freshRssRepository: FreshRssRepository,
    private val gReaderRssRepository: GReaderRssRepository,
    private val feverRssRepository: FeverRssRepository,
) {

    fun get() = get(CurrentAccountType)

    fun get(accountTypeId: Int) = when (accountTypeId) {
        AccountType.Local.id -> localRssRepository
        AccountType.FreshRSS.id -> freshRssRepository
        AccountType.GoogleReader.id -> gReaderRssRepository
        AccountType.Fever.id -> feverRssRepository
        AccountType.Inoreader.id -> localRssRepository
        AccountType.Feedly.id -> localRssRepository
        else -> localRssRepository
    }
}
