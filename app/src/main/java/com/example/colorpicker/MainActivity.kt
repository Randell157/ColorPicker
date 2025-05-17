package com.example.colorpicker

import android.graphics.Color
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
                    try {
                        val textValue = s.toString()
                        if (textValue.isNotEmpty()) {
                            var value = textValue.toFloat()
                            value = value.coerceIn(0f, 1f)
                            viewModel.updateRedValue(value)
                            updateRedUI(value)
                            updateColorDisplay()
                        }
                    } catch (_: NumberFormatException) {
                        // Invalid input, ignore
                    }
                }
            }
        })

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
                    try {
                        val textValue = s.toString()
                        if (textValue.isNotEmpty()) {
                            var value = textValue.toFloat()
                            value = value.coerceIn(0f, 1f)
                            viewModel.updateGreenValue(value)
                            updateGreenUI(value)
                            updateColorDisplay()
                        }
                    } catch (_: NumberFormatException) {
                        // Invalid input, ignore
                    }
                }
            }
        })

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
                    try {
                        val textValue = s.toString()
                        if (textValue.isNotEmpty()) {
                            var value = textValue.toFloat()
                            value = value.coerceIn(0f, 1f)
                            viewModel.updateBlueValue(value)
                            updateBlueUI(value)
                            updateColorDisplay()
                        }
                    } catch (_: NumberFormatException) {
                        // Invalid input, ignore
                    }
                }
            }
        })

        // Reset button
        resetButton.setOnClickListener {
            viewModel.resetToDefault()
            updateUIFromViewModel()
        }
    }

    private fun updateColorDisplay() {
        // Convert float values (0-1) to int values (0-255)
        val red = (viewModel.redValue.value * 255).toInt()
        val green = (viewModel.greenValue.value * 255).toInt()
        val blue = (viewModel.blueValue.value * 255).toInt()

        // Set background color of the display box
        colorDisplayBox.setBackgroundColor(Color.rgb(red, green, blue))
    }
}