package com.example.calculatorkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.calculatorkotlin.R.id.resultsTV



class MainActivity : AppCompatActivity() {

    private lateinit var resultsTV: TextView
    private lateinit var workingsTV: TextView

    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultsTV = findViewById(R.id.resultsTV)
        workingsTV = findViewById(R.id.workingsTV)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            val currentNumber = workingsTV.text.toString()
            if (currentNumber == "0") {
                workingsTV.text = view.text
            } else {
                if (view.text == ".") {
                    if (canAddDecimal) {
                        workingsTV.append(view.text)
                    }
                    canAddDecimal = false
                } else {
                    workingsTV.append(view.text)
                }
            }
            canAddOperation = true
        }
    }

    fun operationAction(view: View) {
        if(view is Button && canAddOperation) {
            workingsTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun backSpaceAction(view: View) {
        val workingsText = workingsTV.text.toString()
        if (workingsText.isNotEmpty()) {
            val updatedText = workingsText.dropLast(1)
            if (updatedText.isEmpty()) {
                workingsTV.text = "0"
            } else {
                val lastDigit = updatedText.last()
                if (lastDigit.isDigit() && updatedText.length == 1) {
                    workingsTV.text = "0"
                } else {
                    workingsTV.text = updatedText
                }
            }
        }
    }

    fun clearEntryAction(view: View) {
        val workingsText = workingsTV.text.toString()
        val lastOperatorIndex = workingsText.lastIndexOfAny(listOf("+", "-", "*", "/"))
        if (lastOperatorIndex != -1) {
            val updatedText = workingsText.substring(0, lastOperatorIndex + 1)
            workingsTV.text = updatedText + "0"
        } else {
            workingsTV.text = "0"
        }
    }

    fun clearAction(view: View) {
        workingsTV.text = "0"
        resultsTV.text = ""
    }

    fun equalsAction(view: View) {
        resultsTV.text = calculateResults()
        
    }
    
    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()
        if(digitsOperators.isEmpty()) return ""
        
        val timesDivision = timesDivisionCalculate(digitsOperators)
        if(timesDivision.isEmpty()) return ""


        val result = addSubtractCalculate(timesDivision)
        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if(passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if(operator == '+') {
                    result += nextDigit
                }
                if(operator == '-') {
                    result -= nextDigit
                }
            }
        }

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while(list.contains('x') || list.contains('/')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for(i in passedList.indices) {
            if(passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when(operator) {
                    'x' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' -> {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if(i > restartIndex) {
                newList.add(passedList[i])
            }
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for(character in workingsTV.text) {
            if(character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }
        
        if(currentDigit != "") {
            list.add(currentDigit.toFloat())
        }
        
        return list
    }
}