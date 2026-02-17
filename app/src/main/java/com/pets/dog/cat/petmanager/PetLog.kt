package com.pets.dog.cat.petmanager

data class PetLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val imagePath: String,     // Where the photo is saved on the phone
    val aiDescription: String, // The text from Grok/Gemini
    val foodAlert: String?,    // e.g., "Feed 2 cups"
    val medAlert: String?      // e.g., "Give Pill X"
)