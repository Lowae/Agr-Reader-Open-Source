package com.lowae.agrreader.data.source

import androidx.room.invalidationTrackerFlow
import com.lowae.agrreader.AgrReaderApp
import kotlinx.coroutines.flow.Flow

fun databaseInvalidationTrackerFlow(tables: Array<String> = RYDatabase.ALL_TABLE_NAMES): Flow<Set<String>> {
    return RYDatabase.getInstance(AgrReaderApp.application)
        .invalidationTrackerFlow(*tables)
}