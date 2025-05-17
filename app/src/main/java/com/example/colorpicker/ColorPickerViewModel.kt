package com.example.colorpicker
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ColorPickerViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    // Keys for SavedStateHandle
    companion object {
        const val RED_VALUE = "red_value"
        const val GREEN_VALUE = "green_value"
        const val BLUE_VALUE = "blue_value"
        const val RED_ENABLED = "red_enabled"
        const val GREEN_ENABLED = "green_enabled"
        const val BLUE_ENABLED = "blue_enabled"
        const val PREVIOUS_RED_VALUE = "previous_red_value"
        const val PREVIOUS_GREEN_VALUE = "previous_green_value"
        const val PREVIOUS_BLUE_VALUE = "previous_blue_value"
    }

    // Current color values
    private val _redValue = MutableStateFlow(savedStateHandle.get<Float>(RED_VALUE) ?: 0.0f)
    val redValue: StateFlow<Float> = _redValue.asStateFlow()

    private val _greenValue = MutableStateFlow(savedStateHandle.get<Float>(GREEN_VALUE) ?: 0.0f)
    val greenValue: StateFlow<Float> = _greenValue.asStateFlow()

    private val _blueValue = MutableStateFlow(savedStateHandle.get<Float>(BLUE_VALUE) ?: 0.0f)
    val blueValue: StateFlow<Float> = _blueValue.asStateFlow()

    // Switch states
    private val _redEnabled = MutableStateFlow(savedStateHandle.get<Boolean>(RED_ENABLED) ?: true)
    val redEnabled: StateFlow<Boolean> = _redEnabled.asStateFlow()

    private val _greenEnabled = MutableStateFlow(savedStateHandle.get<Boolean>(GREEN_ENABLED) ?: true)
    val greenEnabled: StateFlow<Boolean> = _greenEnabled.asStateFlow()

    private val _blueEnabled = MutableStateFlow(savedStateHandle.get<Boolean>(BLUE_ENABLED) ?: true)
    val blueEnabled: StateFlow<Boolean> = _blueEnabled.asStateFlow()

    // Previous values (for when re-enabling)
    private var previousRedValue = savedStateHandle.get<Float>(PREVIOUS_RED_VALUE) ?: 0.0f
    private var previousGreenValue = savedStateHandle.get<Float>(PREVIOUS_GREEN_VALUE) ?: 0.0f
    private var previousBlueValue = savedStateHandle.get<Float>(PREVIOUS_BLUE_VALUE) ?: 0.0f

    // Update functions
    fun updateRedValue(value: Float) {
        _redValue.value = value
        savedStateHandle[RED_VALUE] = value
    }

    fun updateGreenValue(value: Float) {
        _greenValue.value = value
        savedStateHandle[GREEN_VALUE] = value
    }

    fun updateBlueValue(value: Float) {
        _blueValue.value = value
        savedStateHandle[BLUE_VALUE] = value
    }

    fun toggleRed(enabled: Boolean) {
        if (enabled) {
            _redValue.value = previousRedValue
            savedStateHandle[RED_VALUE] = previousRedValue
        } else {
            previousRedValue = _redValue.value
            savedStateHandle[PREVIOUS_RED_VALUE] = previousRedValue
            _redValue.value = 0.0f
            savedStateHandle[RED_VALUE] = 0.0f
        }
        _redEnabled.value = enabled
        savedStateHandle[RED_ENABLED] = enabled
    }

    fun toggleGreen(enabled: Boolean) {
        if (enabled) {
            _greenValue.value = previousGreenValue
            savedStateHandle[GREEN_VALUE] = previousGreenValue
        } else {
            previousGreenValue = _greenValue.value
            savedStateHandle[PREVIOUS_GREEN_VALUE] = previousGreenValue
            _greenValue.value = 0.0f
            savedStateHandle[GREEN_VALUE] = 0.0f
        }
        _greenEnabled.value = enabled
        savedStateHandle[GREEN_ENABLED] = enabled
    }

    fun toggleBlue(enabled: Boolean) {
        if (enabled) {
            _blueValue.value = previousBlueValue
            savedStateHandle[BLUE_VALUE] = previousBlueValue
        } else {
            previousBlueValue = _blueValue.value
            savedStateHandle[PREVIOUS_BLUE_VALUE] = previousBlueValue
            _blueValue.value = 0.0f
            savedStateHandle[BLUE_VALUE] = 0.0f
        }
        _blueEnabled.value = enabled
        savedStateHandle[BLUE_ENABLED] = enabled
    }

    fun resetToDefault() {
        // Reset to default values (0.0f)
        _redValue.value = 0.0f
        _greenValue.value = 0.0f
        _blueValue.value = 0.0f

        previousRedValue = 0.0f
        previousGreenValue = 0.0f
        previousBlueValue = 0.0f

        _redEnabled.value = true
        _greenEnabled.value = true
        _blueEnabled.value = true

        // Update SavedStateHandle
        savedStateHandle[RED_VALUE] = 0.0f
        savedStateHandle[GREEN_VALUE] = 0.0f
        savedStateHandle[BLUE_VALUE] = 0.0f
        savedStateHandle[PREVIOUS_RED_VALUE] = 0.0f
        savedStateHandle[PREVIOUS_GREEN_VALUE] = 0.0f
        savedStateHandle[PREVIOUS_BLUE_VALUE] = 0.0f
        savedStateHandle[RED_ENABLED] = true
        savedStateHandle[GREEN_ENABLED] = true
        savedStateHandle[BLUE_ENABLED] = true
    }

    // Save current state to be restored later
    fun saveState(colorDataStore: ColorPickerDataStore) {
        viewModelScope.launch {
            colorDataStore.saveColorState(
                _redValue.value,
                _greenValue.value,
                _blueValue.value,
                _redEnabled.value,
                _greenEnabled.value,
                _blueEnabled.value,
                previousRedValue,
                previousGreenValue,
                previousBlueValue
            )
        }
    }

    // Load saved state
    fun loadState(colorDataStore: ColorPickerDataStore) {
        viewModelScope.launch {
            colorDataStore.getColorState().collect { state ->
                _redValue.value = state.redValue
                _greenValue.value = state.greenValue
                _blueValue.value = state.blueValue
                _redEnabled.value = state.redEnabled
                _greenEnabled.value = state.greenEnabled
                _blueEnabled.value = state.blueEnabled
                previousRedValue = state.previousRedValue
                previousGreenValue = state.previousGreenValue
                previousBlueValue = state.previousBlueValue

                // Update SavedStateHandle
                savedStateHandle[RED_VALUE] = state.redValue
                savedStateHandle[GREEN_VALUE] = state.greenValue
                savedStateHandle[BLUE_VALUE] = state.blueValue
                savedStateHandle[RED_ENABLED] = state.redEnabled
                savedStateHandle[GREEN_ENABLED] = state.greenEnabled
                savedStateHandle[BLUE_ENABLED] = state.blueEnabled
                savedStateHandle[PREVIOUS_RED_VALUE] = state.previousRedValue
                savedStateHandle[PREVIOUS_GREEN_VALUE] = state.previousGreenValue
                savedStateHandle[PREVIOUS_BLUE_VALUE] = state.previousBlueValue
            }
        }
    }
}