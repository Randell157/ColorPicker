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

class MainActivity : AppCompatActivity() {

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

    // Color values
    private var redValue = 0.0f
    private var greenValue = 0.0f
    private var blueValue = 0.0f

    // Previous values (for when re-enabling)
    private var previousRedValue = 0.0f
    private var previousGreenValue = 0.0f
    private var previousBlueValue = 0.0f

    // Default color values
    private val defaultRedValue = 0.0f
    private val defaultGreenValue = 0.0f
    private val defaultBlueValue = 0.0f

    // Flags to prevent infinite loops when updating UI
    private var isUpdatingRed = false
    private var isUpdatingGreen = false
    private var isUpdatingBlue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        initializeViews()

        // Set up listeners
        setupListeners()

        // Initially update color display
        updateColorDisplay()
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

    // Setting up event listeners on UI
    private fun setupListeners() {
        // Red controls
        setupColorControls(
            redSwitch,
            redSeekBar,
            redValueEditText,
            { redValue },
            { value -> redValue = value },
            { previousRedValue },
            { value -> previousRedValue = value }
        )

        // Green controls
        setupColorControls(
            greenSwitch,
            greenSeekBar,
            greenValueEditText,
            { greenValue },
            { value -> greenValue = value },
            { previousGreenValue },
            { value -> previousGreenValue = value }
        )

        // Blue controls
        setupColorControls(
            blueSwitch,
            blueSeekBar,
            blueValueEditText,
            { blueValue },
            { value -> blueValue = value },
            { previousBlueValue },
            { value -> previousBlueValue = value }
        )

        // Reset button
        resetButton.setOnClickListener {
            resetToDefault()
        }
    }

    private fun setupColorControls(
        switch: SwitchCompat,
        seekBar: SeekBar,
        editText: EditText,
        getValue: () -> Float,
        setValue: (Float) -> Unit,
        getPreviousValue: () -> Float,
        setPreviousValue: (Float) -> Unit
    ) {
        // Switch listener to enable and disable a color
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Enable and restore previous value
                val previousValue = getPreviousValue()
                setValue(previousValue)

                // Update UI
                val progress = (previousValue * 100).toInt()
                seekBar.progress = progress
                editText.setText(previousValue.toString())

                // Enable controls
                seekBar.isEnabled = true
                editText.isEnabled = true
            } else {
                // Save current value before disabling
                setPreviousValue(getValue())

                // Set value to 0
                setValue(0f)

                // Update UI
                seekBar.progress = 0
                editText.setText("0.0")

                // Disable controls
                seekBar.isEnabled = false
                editText.isEnabled = false
            }

            // Update color display
            updateColorDisplay()
        }

        // SeekBar listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isUpdatingRed && !isUpdatingGreen && !isUpdatingBlue) {
                    val value = progress / 100f
                    setValue(value)

                    // Update EditText
                    when (seekBar) {
                        redSeekBar -> {
                            isUpdatingRed = true
                            redValueEditText.setText(value.toString())
                            isUpdatingRed = false
                        }
                        greenSeekBar -> {
                            isUpdatingGreen = true
                            greenValueEditText.setText(value.toString())
                            isUpdatingGreen = false
                        }
                        blueSeekBar -> {
                            isUpdatingBlue = true
                            blueValueEditText.setText(value.toString())
                            isUpdatingBlue = false
                        }
                    }

                    // Update color display
                    updateColorDisplay()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // EditText listener
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isUpdatingRed && !isUpdatingGreen && !isUpdatingBlue) {
                    try {
                        val textValue = s.toString()
                        if (textValue.isNotEmpty()) {
                            var value = textValue.toFloat()

                            // Clamp value between 0 and 1
                            value = value.coerceIn(0f, 1f)

                            setValue(value)

                            // Update SeekBar
                            val progress = (value * 100).toInt()
                            when (editText) {
                                redValueEditText -> {
                                    isUpdatingRed = true
                                    redSeekBar.progress = progress
                                    if (value != textValue.toFloat()) {
                                        editText.setText(value.toString())
                                        editText.setSelection(editText.text.length)
                                    }
                                    isUpdatingRed = false
                                }
                                greenValueEditText -> {
                                    isUpdatingGreen = true
                                    greenSeekBar.progress = progress
                                    if (value != textValue.toFloat()) {
                                        editText.setText(value.toString())
                                        editText.setSelection(editText.text.length)
                                    }
                                    isUpdatingGreen = false
                                }
                                blueValueEditText -> {
                                    isUpdatingBlue = true
                                    blueSeekBar.progress = progress
                                    if (value != textValue.toFloat()) {
                                        editText.setText(value.toString())
                                        editText.setSelection(editText.text.length)
                                    }
                                    isUpdatingBlue = false
                                }
                            }

                            // Update color display
                            updateColorDisplay()
                        }
                    } catch (e: NumberFormatException) {
                        // Invalid input, ignore
                    }
                }
            }
        })
    }

    private fun updateColorDisplay() {
        // Convert float values (0-1) to int values (0-255)
        val red = (redValue * 255).toInt()
        val green = (greenValue * 255).toInt()
        val blue = (blueValue * 255).toInt()

        // Set background color of the display box
        colorDisplayBox.setBackgroundColor(Color.rgb(red, green, blue))
    }

    // Reset button functionality
    private fun resetToDefault() {
        // Reset values to default
        redValue = defaultRedValue
        greenValue = defaultGreenValue
        blueValue = defaultBlueValue

        // Reset previous values
        previousRedValue = defaultRedValue
        previousGreenValue = defaultGreenValue
        previousBlueValue = defaultBlueValue

        // Update UI
        updateUIForColor(redSwitch, redSeekBar, redValueEditText, redValue)
        updateUIForColor(greenSwitch, greenSeekBar, greenValueEditText, greenValue)
        updateUIForColor(blueSwitch, blueSeekBar, blueValueEditText, blueValue)

        // Update color display
        updateColorDisplay()
    }

    private fun updateUIForColor(
        switch: SwitchCompat,
        seekBar: SeekBar,
        editText: EditText,
        value: Float
    ) {
        // Enable switch
        switch.isChecked = true

        // Enable controls
        seekBar.isEnabled = true
        editText.isEnabled = true

        // Update progress and text
        seekBar.progress = (value * 100).toInt()
        editText.setText(value.toString())
    }
}