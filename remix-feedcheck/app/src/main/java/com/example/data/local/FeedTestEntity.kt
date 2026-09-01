package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_tests")
data class FeedTestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sampleName: String,
    val date: String,
    val qualityStatus: String, // Optimal, Deficient, Pending, Warning
    val qualityScore: Int, // e.g. 85
    val smellCategory: String, // Normal/Fresh, Sour/Bad, Musty/Damp
    val foreignParticles: String, // NONE, LOW, HIGH
    val mouldRisk: String, // LOW, MEDIUM, HIGH
    val storageRisk: String, // LOW, MODERATE, HIGH
    val recommendationEn: String,
    val recommendationTe: String,
    val advisoryTextEn: String,
    val advisoryTextTe: String,
    val imageUrl: String? = null,
    val testType: String = "CATTLE_FEED" // CATTLE_FEED or WATER_TEST
)
