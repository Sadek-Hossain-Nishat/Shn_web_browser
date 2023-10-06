package com.shn.company.limited.shnwebbrowserapp.db

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.shn.company.limited.shnwebbrowserapp.LocationData
import java.io.InputStream
import java.io.OutputStream

object LoationSerializer: Serializer<LocationData>{
    override val defaultValue: LocationData
        get() = LocationData.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): LocationData {
        try {

            return  LocationData.parseFrom(input)

        }catch (
            exception:InvalidProtocolBufferException
        ){
            throw CorruptionException("Cannot read proto.",exception)
        }
    }

    override suspend fun writeTo(t: LocationData, output: OutputStream) {
        t.writeTo(output)


    }

}

val Context.locationDataStore : DataStore<LocationData> by dataStore(
    fileName = "location.pb",
    serializer = LoationSerializer
)





