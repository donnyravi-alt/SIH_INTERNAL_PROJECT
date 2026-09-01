package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [FeedTestEntity::class], version = 1, exportSchema = false)
abstract class FeedCheckDatabase : RoomDatabase() {
    abstract fun feedTestDao(): FeedTestDao

    companion object {
        @Volatile
        private var INSTANCE: FeedCheckDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FeedCheckDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FeedCheckDatabase::class.java,
                    "feedcheck_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.feedTestDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: FeedTestDao) {
                dao.insertTest(
                    FeedTestEntity(
                        sampleName = "Silage Batch A",
                        date = "Oct 12, 2023",
                        qualityStatus = "Optimal",
                        qualityScore = 92,
                        smellCategory = "Normal / Fresh",
                        foreignParticles = "NONE",
                        mouldRisk = "LOW",
                        storageRisk = "LOW",
                        recommendationEn = "Safe for feeding today. High nutrient density observed.",
                        recommendationTe = "ఈ రోజు ఆహారం ఇవ్వడానికి సురక్షితం. అధిక పోషక సాంద్రత గమనించబడింది.",
                        advisoryTextEn = "Silage fermentation is optimal with desirable lactic acid aroma and low pH. Protein balance is well suited for lactating cows.",
                        advisoryTextTe = "సైలేజ్ కిణ్వ ప్రక్రియ లాక్టిక్ యాసిడ్ వాసన మరియు తక్కువ pH తో సరైనదిగా ఉంది. పాలిచ్చే ఆవులకు ప్రోటీన్ సమతుల్యత సరిపోతుంది.",
                        testType = "CATTLE_FEED"
                    )
                )
                dao.insertTest(
                    FeedTestEntity(
                        sampleName = "Dry Fodder Mix",
                        date = "Oct 05, 2023",
                        qualityStatus = "Deficient",
                        qualityScore = 58,
                        smellCategory = "Musty / Damp",
                        foreignParticles = "LOW",
                        mouldRisk = "HIGH",
                        storageRisk = "HIGH",
                        recommendationEn = "Current feed mix lacks sufficient protein for high-yield dairy cows. We recommend increasing alfalfa ratio by 15%.",
                        recommendationTe = "ప్రస్తుత ఫీడ్ మిశ్రమంలో అధిక దిగుబడినిచ్చే ఆవులకు సరిపడా ప్రోటీన్ లేదు. ఆల్ఫాల్ఫా నిష్పత్తిని 15% పెంచాలని సిఫార్సు చేస్తున్నాము.",
                        advisoryTextEn = "Current feed mix lacks sufficient protein for high-yield dairy cows. We recommend increasing the alfalfa ratio by 15% to maintain milk production levels. Alert: Trace minerals are below the recommended threshold. Immediate supplementation required.",
                        advisoryTextTe = "ప్రస్తుత ఫీడ్ మిశ్రమంలో అధిక దిగుబడినిచ్చే ఆవులకు సరిపడా ప్రోటీన్ లేదు. పాల ఉత్పత్తి స్థాయిలను నిర్వహించడానికి ఆల్ఫాల్ఫా నిష్పత్తిని 15% పెంచాలని సిఫార్సు చేస్తున్నాము. హెచ్చరిక: ఖనిజాల స్థాయిలు తక్కువగా ఉన్నాయి.",
                        testType = "CATTLE_FEED"
                    )
                )
                dao.insertTest(
                    FeedTestEntity(
                        sampleName = "Concentrate Blend",
                        date = "Sep 28, 2023",
                        qualityStatus = "Pending",
                        qualityScore = 75,
                        smellCategory = "Normal / Fresh",
                        foreignParticles = "NONE",
                        mouldRisk = "LOW",
                        storageRisk = "MODERATE",
                        recommendationEn = "Moisture settling test completed. Secondary lab assay in progress.",
                        recommendationTe = "తేమ సెట్లింగ్ పరీక్ష పూర్తయింది. ద్వితీయ ప్రయోగశాల విశ్లేషణ జరుగుతోంది.",
                        advisoryTextEn = "Grain composition is balanced. Awaiting final aflatoxin assay results from the cooperative central lab.",
                        advisoryTextTe = "ధాన్యాల కూర్పు సమతుల్యంగా ఉంది. సహకార కేంద్ర ప్రయోగశాల నుండి అఫ్లాటాక్సిన్ ఫలితాల కోసం వేచి ఉన్నాము.",
                        testType = "WATER_TEST"
                    )
                )
            }
        }
    }
}
