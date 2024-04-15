package com.lowae.agrreader.data.repository

import android.content.Context
import com.lowae.agrreader.utils.ext.formatAsFlowString
import com.lowae.agrreader.utils.ext.formatAsString
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Date
import javax.inject.Inject

class StringsRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {

    fun getString(resId: Int, vararg formatArgs: Any) = context.getString(resId, *formatArgs)

    fun getQuantityString(resId: Int, quantity: Int, vararg formatArgs: Any) =
        context.resources.getQuantityString(resId, quantity, *formatArgs)

    fun formatAsString(
        date: Date?,
        onlyHourMinute: Boolean? = false,
        atHourMinute: Boolean? = false,
    ) = date?.formatAsString(context, onlyHourMinute, atHourMinute)

    fun formatAsFlowString(
        date: Date?,
    ) = date?.formatAsFlowString(context)
}
