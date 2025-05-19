package com.sulav.chatgptclone.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.ds by preferencesDataStore("chat_settings")

@Singleton
class AppSettings @Inject constructor(@ApplicationContext ctx: Context) {
    private val store = ctx.ds
    private val KEY_USE_REAL = booleanPreferencesKey("use_real_ai")

    val useRealAi: Flow<Boolean> =
        store.data.map { it[KEY_USE_REAL] == true }

    suspend fun setUseRealAi(value: Boolean) =
        store.edit { it[KEY_USE_REAL] = value }
}
