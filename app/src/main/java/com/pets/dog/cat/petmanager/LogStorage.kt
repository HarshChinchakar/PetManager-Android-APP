package com.pets.dog.cat.petmanager

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class LogStorage(private val context: Context) {
    private val gson = Gson()

    // Get all logs for a specific Pet ID
    fun getLogs(petId: Int): MutableList<PetLog> {
        val file = File(context.filesDir, "logs_$petId.json")
        if (!file.exists()) return mutableListOf()

        val json = file.readText()
        val type = object : TypeToken<MutableList<PetLog>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    // Save a new log for a specific Pet ID
    fun saveLog(petId: Int, newLog: PetLog) {
        val logs = getLogs(petId)
        logs.add(0, newLog) // Add to top of list

        val file = File(context.filesDir, "logs_$petId.json")
        file.writeText(gson.toJson(logs))
    }
}