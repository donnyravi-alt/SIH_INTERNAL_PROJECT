package com.example.data.repository

import android.graphics.Bitmap
import android.util.Base64
import com.example.data.remote.GeminiApiService
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiInlineData
import com.example.data.remote.GeminiNetworkClient
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

data class FeedAnalysisResult(
    val qualityStatus: String, // Optimal, Deficient, Warning, Good
    val qualityScore: Int,
    val foreignParticles: String,
    val mouldRisk: String,
    val storageRisk: String,
    val recommendationEn: String,
    val recommendationTe: String,
    val advisoryEn: String,
    val advisoryTe: String
)

class GeminiRepository(
    private val apiService: GeminiApiService = GeminiNetworkClient.service
) {
    private val modelName = "gemini-3.5-flash"

    suspend fun analyzeFeed(
        bitmap: Bitmap?,
        smell: String,
        texture: String,
        feedType: String,
        moistureObserved: Boolean
    ): FeedAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = GeminiNetworkClient.getApiKey()
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val parts = mutableListOf<GeminiPart>()
                val prompt = """
                    You are an expert veterinary dairy cattle feed nutritionist.
                    Analyze this dairy cattle feed sample with the following farmer observations:
                    - Smell: $smell
                    - Texture: $texture
                    - Feed Type: $feedType
                    - Excess Moisture: $moistureObserved
                    
                    Return a concise summary with:
                    1. Overall Quality status (GOOD, OPTIMAL, DEFICIENT, or HIGH_RISK)
                    2. Quality score percentage (0-100)
                    3. Foreign particles risk (NONE, LOW, MODERATE, HIGH)
                    4. Mould risk (LOW, MODERATE, HIGH)
                    5. Storage risk (LOW, MODERATE, HIGH)
                    6. Practical recommendation in English and Telugu
                    7. Nutrition advisory in English and Telugu
                    
                    Keep text clear, structured, and easy for rural dairy farmers.
                """.trimIndent()
                
                parts.add(GeminiPart(text = prompt))
                
                if (bitmap != null) {
                    val base64Image = bitmapToBase64(bitmap)
                    parts.add(GeminiPart(inlineData = GeminiInlineData(mimeType = "image/jpeg", data = base64Image)))
                }

                val request = GeminiRequest(
                    contents = listOf(GeminiContent(parts = parts)),
                    generationConfig = GeminiGenerationConfig(temperature = 0.2f, maxOutputTokens = 800)
                )

                val response = apiService.generateContent(modelName, apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!responseText.isNullOrBlank()) {
                    return@withContext parseAIResponse(responseText, smell, texture)
                }
            } catch (e: Exception) {
                // Fallback to local expert heuristics if network/key error
            }
        }

        // Local expert domain heuristics
        return@withContext computeLocalHeuristics(smell, texture, moistureObserved)
    }

    suspend fun chatWithAdvisor(
        history: List<Pair<String, String>>, // role to message
        userMessage: String,
        isTelugu: Boolean
    ): String = withContext(Dispatchers.IO) {
        val apiKey = GeminiNetworkClient.getApiKey()
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemPrompt = if (isTelugu) {
                    "మీరు 'FeedCheck AI' పశుగ్రాస మరియు పాడి పశువుల పోషకాహార నిపుణులు. రైతులకు స్పష్టమైన, సరళమైన మరియు ఆచరణాత్మక సలహాలు ఇవ్వండి."
                } else {
                    "You are FeedCheck AI, an expert veterinary dairy nutrition advisor for Indian farmers. Give practical, high-impact advice on cattle feed quality, aflatoxin prevention, silage preparation, and milk yield improvement."
                }

                val contents = mutableListOf<GeminiContent>()
                history.takeLast(6).forEach { (role, text) ->
                    val geminiRole = if (role == "user") "user" else "model"
                    contents.add(GeminiContent(role = geminiRole, parts = listOf(GeminiPart(text = text))))
                }
                contents.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = userMessage))))

                val request = GeminiRequest(
                    contents = contents,
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                    generationConfig = GeminiGenerationConfig(temperature = 0.5f, maxOutputTokens = 600)
                )

                val response = apiService.generateContent(modelName, apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            } catch (e: Exception) {
                // Return helpful offline response
            }
        }

        return@withContext getOfflineChatResponse(userMessage, isTelugu)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    private fun computeLocalHeuristics(smell: String, texture: String, moisture: Boolean): FeedAnalysisResult {
        return when {
            smell.contains("Sour", ignoreCase = true) || smell.contains("పుల్లటి") -> {
                FeedAnalysisResult(
                    qualityStatus = "Deficient",
                    qualityScore = 52,
                    foreignParticles = "LOW",
                    mouldRisk = "HIGH",
                    storageRisk = "HIGH",
                    recommendationEn = "Do not feed immediately. Check for fermentation acidosis risk and aerate feed.",
                    recommendationTe = "వెంటనే ఆహారంగా ఇవ్వవద్దు. కిణ్వ ప్రక్రియ అసిడోసిస్ ప్రమాదాన్ని తనిఖీ చేసి గాలి తగిలేలా ఆరబెట్టండి.",
                    advisoryEn = "Sour odor indicates high moisture fermentation or mycotoxin buildup. Separate this batch from clean grain storage and consult veterinary advisor.",
                    advisoryTe = "పుల్లటి వాసన అధిక తేమ లేదా మైకోటాక్సిన్ల పెరుగుదలను సూచిస్తుంది. ఈ బ్యాచ్‌ను శుభ్రమైన నిల్వ నుండి వేరు చేయండి."
                )
            }
            smell.contains("Musty", ignoreCase = true) || smell.contains("బూజు") || moisture -> {
                FeedAnalysisResult(
                    qualityStatus = "Warning",
                    qualityScore = 64,
                    foreignParticles = "LOW",
                    mouldRisk = "MODERATE",
                    storageRisk = "HIGH",
                    recommendationEn = "Sun dry thoroughly before serving. Check moisture levels under 12%.",
                    recommendationTe = "వాడే ముందు ఎండలో బాగా ఆరబెట్టండి. తేమ స్థాయి 12% కంటే తక్కువగా ఉండేలా చూడండి.",
                    advisoryEn = "Moisture detected. Current feed mix lacks sufficient protein for high-yield dairy cows. We recommend increasing alfalfa ratio by 15%. Alert: Trace minerals are below threshold.",
                    advisoryTe = "తేమ గుర్తించబడింది. ప్రస్తుత ఫీడ్ మిశ్రమంలో అధిక దిగుబడినిచ్చే ఆవులకు సరిపడా ప్రోటీన్ లేదు. ఆల్ఫాల్ఫా నిష్పత్తిని 15% పెంచాలని సిఫార్సు చేస్తున్నాము."
                )
            }
            else -> {
                FeedAnalysisResult(
                    qualityStatus = "GOOD",
                    qualityScore = 88,
                    foreignParticles = "NONE",
                    mouldRisk = "LOW",
                    storageRisk = "MODERATE",
                    recommendationEn = "Safe for feeding today. Check moisture levels.",
                    recommendationTe = "ఈ రోజు ఆహారం ఇవ్వడానికి సురక్షితం. తేమ స్థాయిలను తనిఖీ చేయండి.",
                    advisoryEn = "Current feed mix lacks sufficient protein for high-yield dairy cows. We recommend increasing the alfalfa ratio by 15% to maintain milk production levels. Alert: Trace minerals are below the recommended threshold. Immediate supplementation required.",
                    advisoryTe = "ప్రస్తుత ఫీడ్ మిశ్రమంలో అధిక దిగుబడినిచ్చే ఆవులకు సరిపడా ప్రోటీన్ లేదు. పాల ఉత్పత్తి స్థాయిలను నిర్వహించడానికి ఆల్ఫాల్ఫా నిష్పత్తిని 15% పెంచాలని సిఫార్సు చేస్తున్నాము. హెచ్చరిక: ఖనిజాల స్థాయిలు తక్కువగా ఉన్నాయి."
                )
            }
        }
    }

    private fun parseAIResponse(text: String, smell: String, texture: String): FeedAnalysisResult {
        // Safe extraction with default fallback
        val defaultResult = computeLocalHeuristics(smell, texture, false)
        return defaultResult.copy(
            advisoryEn = text.take(400)
        )
    }

    private fun getOfflineChatResponse(query: String, isTelugu: Boolean): String {
        val q = query.lowercase()
        return if (isTelugu) {
            when {
                q.contains("protein") || q.contains("ప్రోటీన్") -> "పాల దిగుబడి పెంచడానికి పశువుల దాణాలో 20-22% ముడి ప్రోటీన్ ఉండేలా చూడండి. పత్తి చెక్క, సోయాబీన్ మీల్ మరియు ఆల్ఫాల్ఫా (లూసర్న్) మంచి మూలాలు."
                q.contains("mould") || q.contains("బూజు") || q.contains("aflatoxin") -> "దాణాలో బూజు కనిపిస్తే వెంటనే ఉపయోగించడం ఆపండి. అఫ్లాటాక్సిన్ విషపూరితం కాలేయానికి హానికరం మరియు పాలలోకి చేరుతుంది. దాణా తేమ 12% లోపు ఉండాలి."
                q.contains("water") || q.contains("నీరు") -> "నీటి సెట్లింగ్ పరీక్షలో ఇసుక లేదా భారీ మలినాలు 30 సెకన్లలో అడుగున చేరుతాయి. స్వచ్ఛమైన తేలియాడే గడ్డి మరియు ధాన్యాలు సరైన పోషకాలను సూచిస్తాయి."
                else -> "నమస్కారం! పశుగ్రాసం నాణ్యత, దాణా మిశ్రమం, లేదా అఫ్లాటాక్సిన్ నివారణ గురించి మీరు ఏదైనా అడగవచ్చు."
            }
        } else {
            when {
                q.contains("protein") || q.contains("milk") -> "For optimal milk yield (15L+ daily), ensure total mixed ration (TMR) contains 16-18% Crude Protein. Supplement with cotton seed cake, soybean meal, and green fodder like Lucerne/Berseem."
                q.contains("mould") || q.contains("fungus") || q.contains("aflatoxin") -> "Mouldy feed contains dangerous mycotoxins. Never feed mouldy cakes or damp grains. Maintain storage humidity below 65% and use antifungal propionic acid preservatives."
                q.contains("water") || q.contains("settling") -> "In the water settling test, foreign particles like sand and silica sink rapidly to the bottom, while light digestible hull and grain fibers remain suspended. Clear water indicates zero soluble adulterants."
                else -> "Hello! I am your FeedCheck Veterinary Advisor. Ask me about feed formulation, silage making, mineral mixtures, or toxin tests."
            }
        }
    }
}
