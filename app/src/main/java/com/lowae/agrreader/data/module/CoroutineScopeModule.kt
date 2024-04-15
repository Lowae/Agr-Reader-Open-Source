package com.lowae.agrreader.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * [CoroutineScope] for the application consisting of [SupervisorJob]
 * and [DefaultDispatcher] context.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideCoroutineScope(
        @IODispatcher ioDispatcher: CoroutineDispatcher,
    ): CoroutineScope =
        CoroutineScope(SupervisorJob() + ioDispatcher + CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        })
}
