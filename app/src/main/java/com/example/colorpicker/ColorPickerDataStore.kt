package com.example.colorpicker
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.colorPickerDataStore: DataStore<Preferences> by preferencesDataStore(name = "color_preferences")

class ColorPickerDataStore(private val context: Context) {

    // Define keys for DataStore
    companion object {
        val RED_VALUE = floatPreferencesKey("red_value")
        val GREEN_VALUE = floatPreferencesKey("green_value")
        val BLUE_VALUE = floatPreferencesKey("blue_value")
        val RED_ENABLED = booleanPreferencesKey("red_enabled")
        val GREEN_ENABLED = booleanPreferencesKey("green_enabled")
        val BLUE_ENABLED = booleanPreferencesKey("blue_enabled")
        val PREVIOUS_RED_VALUE = floatPreferencesKey("previous_red_value")
        val PREVIOUS_GREEN_VALUE = floatPreferencesKey("previous_green_value")
        val PREVIOUS_BLUE_VALUE = floatPreferencesKey("previous_blue_value")
    }

    // Data class to hold all color states
    data class ColorState(
        val redValue: Float = 0.0f,
        val greenValue: Float = 0.0f,
        val blueValue: Float = 0.0f,
        val redEnabled: Boolean = true,
        val greenEnabled: Boolean = true,
        val blueEnabled: Boolean = true,
        val previousRedValue: Float = 0.0f,
        val previousGreenValue: Float = 0.0f,
        val previousBlueValue: Float = 0.0f
    )

    // Save color state to DataStore
    suspend fun saveColorState(
        redValue: Float,
        greenValue: Float,
        blueValue: Float,
        redEnabled: Boolean,
        greenEnabled: Boolean,
        blueEnabled: Boolean,
        previousRedValue: Float,
        previousGreenValue: Float,
        previousBlueValue: Float
    ) {
        context.colorPickerDataStore.edit { preferences ->
            preferences[RED_VALUE] = redValue
            preferences[GREEN_VALUE] = greenValue
            preferences[BLUE_VALUE] = blueValue
            preferences[RED_ENABLED] = redEnabled
            preferences[GREEN_ENABLED] = greenEnabled
            preferences[BLUE_ENABLED] = blueEnabled
            preferences[PREVIOUS_RED_VALUE] = previousRedValue
            preferences[PREVIOUS_GREEN_VALUE] = previousGreenValue
            preferences[PREVIOUS_BLUE_VALUE] = previousBlueValue
        }
    }

    // Get color state from DataStore
    fun getColorState(): Flow<ColorState> {
        return context.colorPickerDataStore.data.map { preferences ->
            ColorState(
                redValue = preferences[RED_VALUE] ?: 0.0f,
                greenValue = preferences[GREEN_VALUE] ?: 0.0f,
                blueValue = preferences[BLUE_VALUE] ?: 0.0f,
                redEnabled = preferences[RED_ENABLED] ?: true,
                greenEnabled = preferences[GREEN_ENABLED] ?: true,
                blueEnabled = preferences[BLUE_ENABLED] ?: true,
                previousRedValue = preferences[PREVIOUS_RED_VALUE] ?: 0.0f,
                previousGreenValue = preferences[PREVIOUS_GREEN_VALUE] ?: 0.0f,
                previousBlueValue = preferences[PREVIOUS_BLUE_VALUE] ?: 0.0f
            )
        }
    }
}