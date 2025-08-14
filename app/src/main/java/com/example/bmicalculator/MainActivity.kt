package com.example.bmicalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bmicalculator.ui.theme.BMICalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BMICalculatorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BMICalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun BMICalculatorScreen() {
    var weight by remember { mutableStateOf("") }
    var height1 by remember { mutableStateOf("") }
    var height2 by remember { mutableStateOf("") }

    var selectedUnit by remember { mutableStateOf("cm") }
    val units = listOf("cm", "m", "inch", "ft/in")

    var bmiResult by remember { mutableStateOf<Float?>(null) }
    var bmiCategory by remember { mutableStateOf("") }

    fun convertToMeters(): Float? {
        val h1 = height1.toFloatOrNull() ?: return null
        return when (selectedUnit) {
            "cm" -> h1 / 100f
            "m" -> h1
            "inch" -> h1 * 0.0254f
            "ft/in" -> {
                val h2 = height2.toFloatOrNull() ?: 0f
                val totalInches = h1 * 12 + h2
                totalInches * 0.0254f
            }
            else -> null
        }
    }

    fun calculateBMI(weight: Float, heightM: Float): Float {
        return weight / (heightM * heightM)
    }

    fun getBMICategory(bmi: Float): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 24.9 -> "Normal weight"
            bmi < 29.9 -> "Overweight"
            else -> "Obesity"
        }
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("BMI Calculator", style = MaterialTheme.typography.headlineMedium)

        // Weight Input
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Unit Dropdown
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedTextField(
                value = selectedUnit,
                onValueChange = {},
                readOnly = true,
                label = { Text("Height Unit") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = {
                            selectedUnit = unit
                            height1 = ""
                            height2 = ""
                            expanded = false
                        }
                    )
                }
            }
        }

        // Height Inputs
        when (selectedUnit) {
            "ft/in" -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = height1,
                        onValueChange = { height1 = it },
                        label = { Text("Feet") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = height2,
                        onValueChange = { height2 = it },
                        label = { Text("Inches") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            else -> {
                OutlinedTextField(
                    value = height1,
                    onValueChange = { height1 = it },
                    label = { Text("Height ($selectedUnit)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Calculate Button
        Button(
            onClick = {
                val w = weight.toFloatOrNull()
                val h = convertToMeters()
                if (w != null && w > 0 && h != null && h > 0) {
                    val bmi = calculateBMI(w, h)
                    bmiResult = bmi
                    bmiCategory = getBMICategory(bmi)
                } else {
                    bmiResult = null
                    bmiCategory = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate BMI")
        }

        // Result
        bmiResult?.let {
            Text("Your BMI: %.2f".format(it), style = MaterialTheme.typography.headlineSmall)
            Text(bmiCategory, style = MaterialTheme.typography.titleMedium)
        }
    }
}
