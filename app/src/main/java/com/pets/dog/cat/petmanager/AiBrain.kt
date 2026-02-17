package com.pets.dog.cat.petmanager

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson

data class AiResponse(
    val summary: String,
    val food_advice: String,
    val medication_alert: String?
)

class AiBrain {
    // GET YOUR FREE KEY HERE: https://aistudio.google.com/
    private val apiKey = "PASTE_YOUR_API_KEY_HERE"

    suspend fun analyzeImage(bitmap: Bitmap): AiResponse {
        val model = GenerativeModel("gemini-1.5-flash", apiKey)

        val prompt = """
            Analyze this pet image. Return ONLY a JSON object with this format:
            {
               "summary": "One sentence about the pet's look/mood.",
               "food_advice": "Simple diet tip.",
               "medication_alert": "Any visible health issues? If none, say null."
            }
            Do not use markdown. Just raw JSON.
        """.trimIndent()

        return try {
            val response = model.generateContent(content {
                image(bitmap)
                text(prompt)
            })
            // Clean up the response to ensure it's valid JSON
            val cleanJson = response.text!!.replace("```json", "").replace("```", "").trim()
            Gson().fromJson(cleanJson, AiResponse::class.java)
        } catch (e: Exception) {
            AiResponse("Error analyzing image", "N/A", null)
        }
    }
}