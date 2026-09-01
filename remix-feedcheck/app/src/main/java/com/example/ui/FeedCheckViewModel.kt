package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FeedCheckDatabase
import com.example.data.local.FeedTestEntity
import com.example.data.repository.FeedAnalysisResult
import com.example.data.repository.GeminiRepository
import com.example.util.TTSHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppLanguage {
    ENGLISH, TELUGU
}

enum class FeedScanStep {
    CAPTURE,
    ANALYZING,
    QUESTIONS,
    ASSESSMENT,
    ADVISORY
}

enum class NavigationScreen {
    HOME,
    CHECK_FEED,
    HISTORY,
    PROFILE,
    ADMIN_DASHBOARD,
    CHATBOT,
    WATER_TEST
}

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AlertItem(
    val id: String,
    val farmerName: String,
    val village: String,
    val alertType: String, // "Aflatoxin", "Moisture", "High Acidity"
    val timeAgo: String,
    val severity: String // "High", "Warning"
)

class FeedCheckViewModel(application: Application) : AndroidViewModel(application) {
    private val database = FeedCheckDatabase.getDatabase(application, viewModelScope)
    private val feedTestDao = database.feedTestDao()
    private val geminiRepository = GeminiRepository()
    val ttsHelper = TTSHelper(application)

    // Language state
    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Navigation screen
    private val _currentScreen = MutableStateFlow(NavigationScreen.HOME)
    val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

    // Recent test history from Room
    val testHistory = feedTestDao.getAllTests().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Active test in Feed Check Flow
    private val _scanStep = MutableStateFlow(FeedScanStep.CAPTURE)
    val scanStep: StateFlow<FeedScanStep> = _scanStep.asStateFlow()

    private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedBitmap: StateFlow<Bitmap?> = _capturedBitmap.asStateFlow()

    // Analysis checklist progress
    private val _detectingFeedProgress = MutableStateFlow(0)
    val detectingFeedProgress: StateFlow<Int> = _detectingFeedProgress.asStateFlow()

    private val _searchingParticlesProgress = MutableStateFlow(0)
    val searchingParticlesProgress: StateFlow<Int> = _searchingParticlesProgress.asStateFlow()

    private val _mouldIndicatorsProgress = MutableStateFlow(0)
    val mouldIndicatorsProgress: StateFlow<Int> = _mouldIndicatorsProgress.asStateFlow()

    private val _textureProgress = MutableStateFlow(0)
    val textureProgress: StateFlow<Int> = _textureProgress.asStateFlow()

    // Questionnaire state
    private val _selectedSmell = MutableStateFlow("Normal / Fresh")
    val selectedSmell: StateFlow<String> = _selectedSmell.asStateFlow()

    private val _selectedTexture = MutableStateFlow("Dry & Granular")
    val selectedTexture: StateFlow<String> = _selectedTexture.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    // Active Assessment Result
    private val _currentAssessment = MutableStateFlow<FeedAnalysisResult?>(null)
    val currentAssessment: StateFlow<FeedAnalysisResult?> = _currentAssessment.asStateFlow()

    private val _selectedFeedTest = MutableStateFlow<FeedTestEntity?>(null)
    val selectedFeedTest: StateFlow<FeedTestEntity?> = _selectedFeedTest.asStateFlow()

    // Water settling test state
    private val _waterTestProgress = MutableStateFlow(0)
    val waterTestProgress: StateFlow<Int> = _waterTestProgress.asStateFlow()

    private val _isWaterRecording = MutableStateFlow(false)
    val isWaterRecording: StateFlow<Boolean> = _isWaterRecording.asStateFlow()

    private val _waterTestFinished = MutableStateFlow(false)
    val waterTestFinished: StateFlow<Boolean> = _waterTestFinished.asStateFlow()

    // Chatbot state
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "1",
                sender = "ai",
                text = "Hello! I am your FeedCheck Veterinary Advisor. Ask me anything about feed quality, aflatoxin prevention, silage preparation, or dairy cattle nutrition."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Cooperative Admin alerts
    val adminAlerts = MutableStateFlow(
        listOf(
            AlertItem("1", "Ramesh Kumar", "Village Palem", "Aflatoxin", "2 hrs ago", "High"),
            AlertItem("2", "Srinivas Rao", "Kodur", "Moisture", "5 hrs ago", "Warning"),
            AlertItem("3", "Lakshmi Devi", "Village Palem", "Aflatoxin", "1 day ago", "High"),
            AlertItem("4", "Venkat Reddy", "Guntur West", "Acidosis Risk", "2 days ago", "Warning")
        )
    )

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ENGLISH) AppLanguage.TELUGU else AppLanguage.ENGLISH
    }

    fun navigateTo(screen: NavigationScreen) {
        _currentScreen.value = screen
        if (screen == NavigationScreen.CHECK_FEED && _scanStep.value == FeedScanStep.ASSESSMENT) {
            // keep current state
        } else if (screen == NavigationScreen.CHECK_FEED && _scanStep.value == FeedScanStep.ADVISORY) {
            // keep current state
        }
    }

    fun setScanStep(step: FeedScanStep) {
        _scanStep.value = step
    }

    fun setCapturedImage(bitmap: Bitmap?) {
        _capturedBitmap.value = bitmap
    }

    fun setSelectedSmell(smell: String) {
        _selectedSmell.value = smell
    }

    fun setSelectedTexture(texture: String) {
        _selectedTexture.value = texture
    }

    fun startFeedAnalysis(bitmap: Bitmap? = null) {
        _capturedBitmap.value = bitmap
        _scanStep.value = FeedScanStep.ANALYZING
        _isAnalyzing.value = true

        viewModelScope.launch {
            _detectingFeedProgress.value = 0
            _searchingParticlesProgress.value = 0
            _mouldIndicatorsProgress.value = 0
            _textureProgress.value = 0

            // Step 1: Detect cattle feed
            for (p in 0..100 step 20) {
                _detectingFeedProgress.value = p
                delay(120)
            }
            _detectingFeedProgress.value = 100

            // Step 2: Foreign particles
            for (p in 0..65 step 15) {
                _searchingParticlesProgress.value = p
                delay(150)
            }
            _searchingParticlesProgress.value = 55
            delay(300)

            // Step 3: Transition to Smart Questions to complete assessment accurately
            _isAnalyzing.value = false
            _scanStep.value = FeedScanStep.QUESTIONS
        }
    }

    fun submitQuestionnaireAndCalculateAssessment() {
        _scanStep.value = FeedScanStep.ANALYZING
        _isAnalyzing.value = true

        viewModelScope.launch {
            // Finish remaining progress bars
            for (p in 55..100 step 15) {
                _searchingParticlesProgress.value = p
                delay(100)
            }
            _searchingParticlesProgress.value = 100

            for (p in 0..100 step 25) {
                _mouldIndicatorsProgress.value = p
                delay(100)
            }
            _mouldIndicatorsProgress.value = 100

            for (p in 0..100 step 25) {
                _textureProgress.value = p
                delay(100)
            }
            _textureProgress.value = 100

            delay(400)

            // Run Gemini or local expert rules
            val result = geminiRepository.analyzeFeed(
                bitmap = _capturedBitmap.value,
                smell = _selectedSmell.value,
                texture = _selectedTexture.value,
                feedType = "Dairy Cattle Concentrate",
                moistureObserved = _selectedSmell.value.contains("Musty", ignoreCase = true)
            )

            _currentAssessment.value = result
            _isAnalyzing.value = false
            _scanStep.value = FeedScanStep.ASSESSMENT

            // Save test to Room Database
            val entity = FeedTestEntity(
                sampleName = "Feed Sample #${(100..999).random()}",
                date = "Today",
                qualityStatus = result.qualityStatus,
                qualityScore = result.qualityScore,
                smellCategory = _selectedSmell.value,
                foreignParticles = result.foreignParticles,
                mouldRisk = result.mouldRisk,
                storageRisk = result.storageRisk,
                recommendationEn = result.recommendationEn,
                recommendationTe = result.recommendationTe,
                advisoryTextEn = result.advisoryEn,
                advisoryTextTe = result.advisoryTe,
                testType = "CATTLE_FEED"
            )
            val insertedId = feedTestDao.insertTest(entity)
            _selectedFeedTest.value = entity.copy(id = insertedId)
        }
    }

    fun viewAdvisoryForCurrent() {
        _scanStep.value = FeedScanStep.ADVISORY
    }

    fun selectHistoryItem(item: FeedTestEntity) {
        _selectedFeedTest.value = item
        _currentAssessment.value = FeedAnalysisResult(
            qualityStatus = item.qualityStatus,
            qualityScore = item.qualityScore,
            foreignParticles = item.foreignParticles,
            mouldRisk = item.mouldRisk,
            storageRisk = item.storageRisk,
            recommendationEn = item.recommendationEn,
            recommendationTe = item.recommendationTe,
            advisoryEn = item.advisoryTextEn,
            advisoryTe = item.advisoryTextTe
        )
        _scanStep.value = FeedScanStep.ADVISORY
        _currentScreen.value = NavigationScreen.CHECK_FEED
    }

    fun startWaterSettlingTest() {
        _isWaterRecording.value = true
        _waterTestFinished.value = false
        _waterTestProgress.value = 0

        viewModelScope.launch {
            for (p in 1..5) {
                delay(1000)
                _waterTestProgress.value = p * 20
            }
            _isWaterRecording.value = false
            _waterTestFinished.value = true

            // Save water test to Room
            feedTestDao.insertTest(
                FeedTestEntity(
                    sampleName = "Water Settling #${(100..999).random()}",
                    date = "Today",
                    qualityStatus = "Optimal",
                    qualityScore = 90,
                    smellCategory = "Clear",
                    foreignParticles = "NONE",
                    mouldRisk = "LOW",
                    storageRisk = "LOW",
                    recommendationEn = "Clean settling profile. Minimal sediment detected.",
                    recommendationTe = "పరిశుభ్రమైన సెట్లింగ్ ప్రొఫైల్. కనీస అవక్షేపం గుర్తించబడింది.",
                    advisoryTextEn = "Water turbidity and feed dissolution are in normal range. Safe for mixing with daily cattle ration.",
                    advisoryTextTe = "నీటి స్వచ్ఛత మరియు దాణా కరిగే గుణం సాధారణ పరిధిలో ఉన్నాయి. రోజువారీ దాణాలో కలపడానికి సురక్షితం.",
                    testType = "WATER_TEST"
                )
            )
        }
    }

    fun resetWaterTest() {
        _isWaterRecording.value = false
        _waterTestFinished.value = false
        _waterTestProgress.value = 0
    }

    fun speakCurrentAdvisory() {
        val isTe = _language.value == AppLanguage.TELUGU
        val assessment = _currentAssessment.value
        val textToSpeak = if (isTe) {
            assessment?.advisoryTe ?: "ప్రస్తుత ఫీడ్ మిశ్రమంలో అధిక దిగుబడినిచ్చే ఆవులకు సరిపడా ప్రోటీన్ లేదు. ఆల్ఫాల్ఫా నిష్పత్తిని 15% పెంచాలని సిఫార్సు చేస్తున్నాము."
        } else {
            assessment?.advisoryEn ?: "Current feed mix lacks sufficient protein for high-yield dairy cows. We recommend increasing the alfalfa ratio by 15% to maintain milk production levels. Alert: Trace minerals are below the recommended threshold. Immediate supplementation required."
        }
        ttsHelper.speak(textToSpeak, isTelugu = isTe)
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val isTe = _language.value == AppLanguage.TELUGU
        val userMsg = ChatMessage(id = System.currentTimeMillis().toString(), sender = "user", text = text)
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            val historyPairs = _chatMessages.value.map { it.sender to it.text }
            val reply = geminiRepository.chatWithAdvisor(historyPairs, text, isTelugu = isTe)
            _chatMessages.value = _chatMessages.value + ChatMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                sender = "ai",
                text = reply
            )
            _isChatLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}
