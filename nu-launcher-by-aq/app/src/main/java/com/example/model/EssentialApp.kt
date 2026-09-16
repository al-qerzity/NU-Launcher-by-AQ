package com.example.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class EssentialAppType {
    PHONE,
    MESSAGING,
    WHATSAPP
}

data class EssentialAppInfo(
    val type: EssentialAppType,
    val title: String,
    val subtitle: String,
    val packageName: String?,
    val isAvailable: Boolean,
    val iconVector: ImageVector,
    val accentColorHex: Long
)
