package com.lowae.agrreader.data.repository

sealed interface SyncProgress {

    data object Start : SyncProgress

    data class Syncing(val progress: Int, val total: Int, val name: String) : SyncProgress

    data object End : SyncProgress
}

inline val SyncProgress.isSyncing: Boolean
    get() = this !is SyncProgress.End

inline val SyncProgress.progress: String?
    get() {
        return if (this is SyncProgress.Syncing) {
            "[$progress/$total]: $name"
        } else null
    }