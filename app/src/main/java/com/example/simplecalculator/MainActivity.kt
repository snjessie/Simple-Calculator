package com.example.simplecalculator

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simplecalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // All Clear
        binding.btnAC.setOnClickListener {
            allClearAction()
        }

        // Backspace
        binding.btnBackspace.setOnClickListener {
            backspaceAction()
        }

        // Equals
        binding.btnEquals.setOnClickListener {
            equalsAction()
        }

        // Operators
        val operationButtons = listOf(binding.btnPlus, binding.btnMinus, binding.btnDivide, binding.btnMultiply)
        for (button in operationButtons){
            button.setOnClickListener { 
                operationAction(button.text.toString())
            }
        }

        // Number Buttons
        val numberButtons = listOf(binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9, binding.btnDot)

        for (button in numberButtons){
            button.setOnClickListener {
                val currentText = binding.tvWorkings.text.toString()
                binding.tvWorkings.text = currentText + button.text
            }
        }
    }

    private fun operationAction(operator: String) {
        val currentText = binding.tvWorkings.text.toString()
        // preventing syntax like this, e.g: 5+3-
        if (currentText.isNotEmpty() && !"+-X/".contains(currentText.last())){
            binding.tvWorkings.text = currentText + operator
        }
    }

    private fun evaluateSimpleExpression(expression: String): Double {
        // e.g: [5 , + , 3, *, 2]
        val tokens = mutableListOf<String>()
        var number = ""

        // Tokenize input (numbers and operators)
        for (char in expression){
            if (char in "+-*/"){
                tokens.add(number)
                tokens.add(char.toString())
                number = ""
            }else{
                number += char
            }
        }
        tokens.add(number) // Adding last number (tokens filled with numbers/operators)

        // First handle * and / from left to right
        var index = 0
        while (index < tokens.size){
            if (tokens[index] == "*" || tokens[index] == "/"){
                val left = tokens[index - 1].toDouble()
                val right = tokens[index + 1].toDouble()
                val result = if(tokens[index] == "*") left * right else left / right

                tokens[index - 1] = result.toString()
                tokens.removeAt(index) // remove operator
                tokens.removeAt(index) // remove right number
                index -= 1
            }else{
                index ++
            }

        }

        // Now handle + and -
        var result = tokens[0].toDouble()
        index = 1

        while (index < tokens.size){
            val operator = tokens[index]
            val nextNumber = tokens[index + 1].toDouble()
            result = when (operator) {
                "+" -> result + nextNumber
                "-" -> result - nextNumber
                else -> result
            }
            index += 2
        }

        return result
    }

    private fun equalsAction() {
        val input = binding.tvWorkings.text.toString().replace("X", "*")

        if (input.isEmpty()){
            binding.tvResults.text = ""
            return
        }
        try {
            val result = evaluateSimpleExpression(input)
            binding.tvResults.text = result.toString()
        } catch (e: Exception){
            binding.tvResults.text = "Error"
        }
    }

    private fun backspaceAction() {
        //CharSequence to String to perform dropLast
        val currentText = binding.tvWorkings.text.toString()
        if (currentText.isNotEmpty()){
            val updatedText = currentText.dropLast(1)
            //Immutable
            binding.tvWorkings.text = updatedText
        }
    }

    private fun allClearAction() {
        binding.tvWorkings.text = ""
        binding.tvResults.text = ""
    }


}