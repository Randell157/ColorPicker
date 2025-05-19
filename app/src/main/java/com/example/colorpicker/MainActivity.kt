package com.example.colorpicker

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
//import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    // Initialize DataStore
    private lateinit var colorDataStore: ColorPickerDataStore

    // Initialize ViewModel
    private lateinit var viewModel: ColorPickerViewModel

    // UI Components
    private lateinit var colorDisplayBox: View

    private lateinit var redSwitch: SwitchCompat
    private lateinit var redSeekBar: SeekBar
    private lateinit var redValueEditText: EditText

    private lateinit var greenSwitch: SwitchCompat
    private lateinit var greenSeekBar: SeekBar
    private lateinit var greenValueEditText: EditText

    private lateinit var blueSwitch: SwitchCompat
    private lateinit var blueSeekBar: SeekBar
    private lateinit var blueValueEditText: EditText

    private lateinit var resetButton: Button

    // Flags to prevent infinite loops when updating UI
    private var isUpdatingRed = false
    private var isUpdatingGreen = false
    private var isUpdatingBlue = false

    // Restores last value from textbox
    private var lastValidRedValue = 0.0f
    private var lastValidGreenValue = 0.0f
    private var lastValidBlueValue = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize DataStore
        colorDataStore = ColorPickerDataStore(this)

        // Initialize ViewModel with SavedStateHandle
        viewModel = ViewModelProvider(this, SavedStateViewModelFactory(application, this))[ColorPickerViewModel::class.java]

        // Load saved state from DataStore
        viewModel.loadState(colorDataStore)

        // Initialize UI components
        initializeViews()

        // Set up listeners
        setupListeners()

        // Update UI from saved values from ViewModel
        updateUIFromViewModel()

        // Initialize the last color values
        lastValidRedValue = viewModel.redValue.value
        lastValidGreenValue = viewModel.greenValue.value
        lastValidBlueValue = viewModel.blueValue.value
    }

    override fun onPause() {
        super.onPause()
        // Save state to DataStore when app is paused
        viewModel.saveState(colorDataStore)
    }

    private fun initializeViews() {
        colorDisplayBox = findViewById(R.id.colorDisplayBox)

        redSwitch = findViewById(R.id.redSwitch)
        redSeekBar = findViewById(R.id.redSeekBar)
        redValueEditText = findViewById(R.id.redValueEditText)

        greenSwitch = findViewById(R.id.greenSwitch)
        greenSeekBar = findViewById(R.id.greenSeekBar)
        greenValueEditText = findViewById(R.id.greenValueEditText)

        blueSwitch = findViewById(R.id.blueSwitch)
        blueSeekBar = findViewById(R.id.blueSeekBar)
        blueValueEditText = findViewById(R.id.blueValueEditText)

        resetButton = findViewById(R.id.resetButton)
    }

    private fun updateUIFromViewModel() {
        // Update UI with current values from ViewModel
        updateRedUI(viewModel.redValue.value)
        updateGreenUI(viewModel.greenValue.value)
        updateBlueUI(viewModel.blueValue.value)

        // Update switch states
        redSwitch.isChecked = viewModel.redEnabled.value
        redSeekBar.isEnabled = viewModel.redEnabled.value
        redValueEditText.isEnabled = viewModel.redEnabled.value

        greenSwitch.isChecked = viewModel.greenEnabled.value
        greenSeekBar.isEnabled = viewModel.greenEnabled.value
        greenValueEditText.isEnabled = viewModel.greenEnabled.value

        blueSwitch.isChecked = viewModel.blueEnabled.value
        blueSeekBar.isEnabled = viewModel.blueEnabled.value
        blueValueEditText.isEnabled = viewModel.blueEnabled.value

        // Update color display
        updateColorDisplay()
    }

    // Functions that update UI components
    private fun updateRedUI(value: Float) {
        isUpdatingRed = true
        redSeekBar.progress = (value * 100).toInt()
        redValueEditText.setText(value.toString())
        isUpdatingRed = false
    }

    private fun updateGreenUI(value: Float) {
        isUpdatingGreen = true
        greenSeekBar.progress = (value * 100).toInt()
        greenValueEditText.setText(value.toString())
        isUpdatingGreen = false
    }

    private fun updateBlueUI(value: Float) {
        isUpdatingBlue = true
        blueSeekBar.progress = (value * 100).toInt()
        blueValueEditText.setText(value.toString())
        isUpdatingBlue = false
    }

    // Functions to revert to last valid value
    private fun revertToLastValidRed() {
        isUpdatingRed = true
        redValueEditText.setText(lastValidRedValue.toString())
        redSeekBar.progress = (lastValidRedValue * 100).toInt()
        isUpdatingRed = false
    }
    private fun revertToLastValidGreen() {
        isUpdatingGreen = true
        greenValueEditText.setText(lastValidGreenValue.toString())
        greenSeekBar.progress = (lastValidGreenValue * 100).toInt()
        isUpdatingGreen = false
    }
    private fun revertToLastValidBlue() {
        isUpdatingBlue = true
        blueValueEditText.setText(lastValidBlueValue.toString())
        blueSeekBar.progress = (lastValidBlueValue * 100).toInt()
        isUpdatingBlue = false
    }

    private fun validateRedValue() {
        val textValue = redValueEditText.text.toString()

        try {
            // Handle empty input
            if (textValue.isEmpty() || textValue == "." || textValue == "0.") {
                Toast.makeText(
                    this,
                    "Invalid red value. Reverting to previous value.",
                    Toast.LENGTH_SHORT
                ).show()
                revertToLastValidRed()
                return
            }

            val inputValue = textValue.toFloat()

            // Validate range
            if (inputValue < 0f || inputValue > 1f) {
                Toast.makeText(
                    this,
                    "Red value must be between 0.0 and 1.0",
                    Toast.LENGTH_SHORT
                ).show()
                revertToLastValidRed()
            } else {
                // Update with valid value
                lastValidRedValue = inputValue
                viewModel.updateRedValue((inputValue))
                updateColorDisplay()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(
                this,
                "Invalid number format for Red value",
                Toast.LENGTH_SHORT
            ).show()
            revertToLastValidRed()
        }
    }

    private fun validateGreenValue() {
        val textValue = greenValueEditText.text.toString()

        try {
            // Handle empty input
            if (textValue.isEmpty() || textValue == "." || textValue == "0.") {
                Toast.makeText(
                    this,
                    "Invalid green value. Reverting to previous value.",
                    Toast.LENGTH_SHORT
                ).show()
                revertToLastValidGreen()
                return
            }

            val inputValue = textValue.toFloat()

            // Validate range
            if (inputValue < 0f || inputValue > 1f) {
                Toast.makeText(
                    this,
                    "Green value must be between 0.0 and 1.0",
                    Toast.LENGTH_SHORT
                ).show()
                revertToLastValidGreen()
            } else {
                // Update with valid value
                lastValidGreenValue = inputValue
                viewModel.updateGreenValue(inputValue)
                updateColorDisplay()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(
                this,
                "Invalid number format for Green value",
                Toast.LENGTH_SHORT
            ).show()
            revertToLastValidGreen()
        }
    }

    private fun validateBlueValue() {
        val textValue = blueValueEditText.text.toString()

        try {
            // Handle empty input
            if (textValue.isEmpty() || textValue == "." || textValue == "0.") {
                Toast.makeText(
                    this,
                    "Invalid blue value. Reverting to previous value.",
                    Toast.LENGTH_SHORT
                ).show()
                revertToLastValidBlue()
                return
            }

            val inputValue = textValue.toFloat()

            // Validate range
            if (inputValue < 0f || inputValue > 1f) {
                Toast.makeText(
                    this,
                    "Blue value must be between 0.0 and 1.0",
                    Toast.LENGTH_SHORT
                ).show()
                revertToLastValidBlue()
            } else {
                // Update with valid value
                lastValidBlueValue = inputValue
                viewModel.updateBlueValue(inputValue)
                updateColorDisplay()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(
                this,
                "Invalid number format for Blue value",
                Toast.LENGTH_SHORT
            ).show()
            revertToLastValidBlue()
        }
    }

    // Setting up event listeners on UI
    private fun setupListeners() {

    // Red controls
        redSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked ->
            viewModel.toggleRed(isChecked)
            updateUIFromViewModel()
        }

        redSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isUpdatingRed) {
                    val value = progress / 100f
                    viewModel.updateRedValue(value)
                    updateRedUI(value)
                    updateColorDisplay()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        redValueEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdatingRed) {
                    val textValue = s.toString()

                    // Allow empty text, single decimal point, or starting with decimal
                    if (textValue.isEmpty() || textValue == "." || textValue == "0.") {
                        return // Allow intermediate states during typing
                    }

                    try {
                        val inputValue = textValue.toFloat()

                        // Only update the display during typing, don't update ViewModel yet
                        if (inputValue >= 0f && inputValue <= 1f) {
                            // Update UI but don't update ViewModel yet
                            isUpdatingRed = true
                            redSeekBar.progress = (inputValue * 100).toInt()
                            isUpdatingRed = false
                        }
                    } catch (e: NumberFormatException) {
                        // Ignore parsing errors during typing
                    }
                }
            }
        })

        // Add focus change listener to validate when focus is lost
        redValueEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateRedValue()
            }
        }

        // Green controls
        greenSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked ->
            viewModel.toggleGreen(isChecked)
            updateUIFromViewModel()
        }

        greenSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isUpdatingGreen) {
                    val value = progress / 100f
                    viewModel.updateGreenValue(value)
                    updateGreenUI(value)
                    updateColorDisplay()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        greenValueEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdatingGreen) {
                    val textValue = s.toString()

                    // Allow empty text, single decimal point, or starting with decimal
                    if (textValue.isEmpty() || textValue == "." || textValue == "0.") {
                        return // Allow intermediate states during typing
                    }

                    try {
                        val inputValue = textValue.toFloat()

                        // Only update the display during typing, don't update ViewModel yet
                        if (inputValue >= 0f && inputValue <= 1f) {
                            // Update UI but don't update ViewModel yet
                            isUpdatingGreen = true
                            greenSeekBar.progress = (inputValue * 100).toInt()
                            isUpdatingGreen = false
                        }
                    } catch (e: NumberFormatException) {
                        // Ignore parsing errors during typing
                    }
                }
            }
        })

        // Add focus change listener to validate when focus is lost
        greenValueEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateGreenValue()
            }
        }

        // Blue controls
        blueSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked ->
            viewModel.toggleBlue(isChecked)
            updateUIFromViewModel()
        }

        blueSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isUpdatingBlue) {
                    val value = progress / 100f
                    viewModel.updateBlueValue(value)
                    updateBlueUI(value)
                    updateColorDisplay()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        blueValueEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdatingBlue) {
                    val textValue = s.toString()

                    // Allow empty text, single decimal point, or starting with decimal
                    if (textValue.isEmpty() || textValue == "." || textValue == "0.") {
                        return // Allow intermediate states during typing
                    }

                    try {
                        val inputValue = textValue.toFloat()

                        // Only update the display during typing, don't update ViewModel yet
                        if (inputValue >= 0f && inputValue <= 1f) {
                            // Update UI but don't update ViewModel yet
                            isUpdatingBlue = true
                            blueSeekBar.progress = (inputValue * 100).toInt()
                            isUpdatingBlue = false
                        }
                    } catch (e: NumberFormatException) {
                        // Ignore parsing errors during typing
                    }
                }
            }
        })

        // Add focus change listener to validate when focus is lost
        blueValueEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateBlueValue()
            }
        }

        // Reset button
        resetButton.setOnClickListener {
            viewModel.resetToDefault()

            // Update last valid values
            lastValidRedValue = viewModel.redValue.value
            lastValidGreenValue = viewModel.greenValue.value
            lastValidBlueValue = viewModel.blueValue.value

            updateUIFromViewModel()
        }
    }

    private fun updateColorDisplay() {
        // Convert float values (0-1) to int values (0-255)
        val red = (viewModel.redValue.value * 255).toInt()
        val green = (viewModel.greenValue.value * 255).toInt()
        val blue = (viewModel.blueValue.value * 255).toInt()

        // Get the current background drawable
        val drawable = colorDisplayBox.background.mutate() as GradientDrawable

        // Set the color of the drawable
        drawable.setColor(Color.rgb(red, green, blue))
    }
}