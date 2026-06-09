package com.cyna.app.data.util

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission

/**
 * Helper de vibration centralisé.
 *
 * Fournit des patterns semantiques alignés avec les états UI :
 * - [error]   → double impulsion courte/forte  (erreur serveur, réseau)
 * - [warning] → impulsion unique modérée        (erreur client 4xx)
 * - [success] → impulsion douce courte          (opération réussie)
 * - [light]   → tick léger                      (feedback UI générique)
 *
 * Utilisation :
 * ```kotlin
 * val vibrationHelper: VibrationHelper by inject()
 * vibrationHelper.error()
 * ```
 */
class VibrationHelper(context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Double impulsion courte/forte — erreur serveur (5xx) ou réseau. */
    @RequiresPermission(Manifest.permission.VIBRATE)
    fun error() = vibratePattern(
        timings    = longArrayOf(0, 80, 60, 80),
        amplitudes = intArrayOf(0, 220, 0, 220),
        fallbackMs = 80L
    )

    /** Impulsion unique modérée — erreur client (4xx). */
    @RequiresPermission(Manifest.permission.VIBRATE)
    fun warning() = vibratePattern(
        timings    = longArrayOf(0, 120),
        amplitudes = intArrayOf(0, 160),
        fallbackMs = 120L
    )

    /** Impulsion douce courte — succès d'une opération. */
    @RequiresPermission(Manifest.permission.VIBRATE)
    fun success() = vibratePattern(
        timings    = longArrayOf(0, 60),
        amplitudes = intArrayOf(0, 100),
        fallbackMs = 60L
    )

    /** Tick léger — feedback générique. */
    @RequiresPermission(Manifest.permission.VIBRATE)
    fun light() = vibratePattern(
        timings    = longArrayOf(0, 30),
        amplitudes = intArrayOf(0, 60),
        fallbackMs = 30L
    )

    // ── Internal ──────────────────────────────────────────────────────────────

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun vibratePattern(
        timings: LongArray,
        amplitudes: IntArray,
        fallbackMs: Long
    ) {
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = if (vibrator.hasAmplitudeControl()) {
                VibrationEffect.createWaveform(timings, amplitudes, -1)
            } else {
                // Appareil sans contrôle d'amplitude : impulsion simple
                VibrationEffect.createOneShot(fallbackMs, VibrationEffect.DEFAULT_AMPLITUDE)
            }
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(fallbackMs)
        }
    }
}

