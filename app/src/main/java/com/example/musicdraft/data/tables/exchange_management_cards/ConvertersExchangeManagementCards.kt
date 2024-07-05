package com.example.musicdraft.data.tables.exchange_management_cards

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * Classe che si preoccupa di eseguire la conversione dei dati di tipo List<String> per le tabelle 'ExchangeManagementCards'.
 *
 */
class ConvertersExchangeManagementCards {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }
}