package com.shn.company.limited.shnwebbrowserapp.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val DATASTORE_FILE="datastore_file"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_FILE)



class PreferencesDataStore @Inject constructor(@ApplicationContext val context: Context){



    val FIRSTTIMERUN = booleanPreferencesKey("first_time_run")
    val firsttimerunFlow: Flow<Boolean> = context.dataStore.data.map { preferences -> preferences[FIRSTTIMERUN]?:false

    }


    suspend fun editFirsttimerun() {
        context.dataStore.edit { preferences ->
            preferences[FIRSTTIMERUN] = true
        }
    }








}