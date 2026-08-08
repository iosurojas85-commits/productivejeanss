package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateAiResponse(
        prompt: String,
        userName: String = "Bunnie",
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val effectiveName = userName.ifBlank { "Bunnie" }
        val apiKey = try {
            val keyField = BuildConfig::class.java.getField("GEMINI_API_KEY")
            keyField.get(null) as? String ?: ""
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank()) {
            return@withContext generateFallbackResponse(prompt, effectiveName)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val contentsArray = JSONArray()

            // System prompt setup
            val systemObj = JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", 
                    "You are Productive Jeans AI, a smart, clear, and encouraging productivity assistant. The user's name is '$effectiveName'. Always address the user directly as '$effectiveName' in your responses. Provide concise, direct, helpful responses without using excessive emojis or unnecessary fluff."
                )))
            }
            contentsArray.put(systemObj)

            val systemAck = JSONObject().apply {
                put("role", "model")
                put("parts", JSONArray().put(JSONObject().put("text", "Understood. I am Productive Jeans AI, ready to assist $effectiveName with focus and organization.")))
            }
            contentsArray.put(systemAck)

            // Conversation history
            conversationHistory.takeLast(6).forEach { (sender, text) ->
                val role = if (sender == "Productive Jeans AI" || sender == "Gemini AI") "model" else "user"
                val obj = JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().put(JSONObject().put("text", text)))
                }
                contentsArray.put(obj)
            }

            // Current user prompt
            val userObj = JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", prompt)))
            }
            contentsArray.put(userObj)

            val requestBodyJson = JSONObject().apply {
                put("contents", contentsArray)
            }

            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string()

            if (response.isSuccessful && !responseString.isNullOrBlank()) {
                val jsonResp = JSONObject(responseString)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text", "")
                        if (text.isNotBlank()) {
                            return@withContext text.trim()
                        }
                    }
                }
            }

            return@withContext generateFallbackResponse(prompt, effectiveName)
        } catch (e: Exception) {
            return@withContext generateFallbackResponse(prompt, effectiveName)
        }
    }

    private fun generateFallbackResponse(prompt: String, userName: String = "Bunnie"): String {
        val effectiveName = userName.ifBlank { "Bunnie" }
        val lower = prompt.lowercase()
        return when {
            lower.contains("task") || lower.contains("todo") || lower.contains("remind") || lower.contains("tarea") ->
                "Anotado, $effectiveName. Te sugiero agregar esta tarea a tus notas para mantener tus metas al día."
            lower.contains("motivat") || lower.contains("cansad") || lower.contains("tired") || lower.contains("focus") ->
                "Cada minuto de concentración es un avance, $effectiveName. ¡Sigue adelante, lo estás haciendo genial!"
            lower.contains("hello") || lower.contains("hi") || lower.contains("hola") ->
                "¡Hola, $effectiveName! ¿En qué puedo ayudarte a enfocar hoy?"
            else ->
                "Hola $effectiveName, estoy aquí para ayudarte a organizar tus tareas y mantener la concentración."
        }
    }
}
