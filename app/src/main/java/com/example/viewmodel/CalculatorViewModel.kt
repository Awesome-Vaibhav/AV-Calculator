package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.CurrencyService
import com.example.db.HistoryEntity
import com.example.db.HistoryRepository
import com.example.util.MathEvaluator
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.pow
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class CalculatorViewModel(private val repository: HistoryRepository) : ViewModel() {

    // Helper formatter
    private val decimalFormat = DecimalFormat("#.#######", DecimalFormatSymbols(Locale.US))

    // Calculator History Flow
    val historyState: StateFlow<List<HistoryEntity>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Basic & Scientific Calculator Core State ---
    private val _expressionValue = MutableStateFlow(TextFieldValue(""))
    val expressionValue: StateFlow<TextFieldValue> = _expressionValue.asStateFlow()

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _calcResult = MutableStateFlow("")
    val calcResult: StateFlow<String> = _calcResult.asStateFlow()

    private val _previewResult = MutableStateFlow("")
    val previewResult: StateFlow<String> = _previewResult.asStateFlow()

    private val _isDegreeMode = MutableStateFlow(false)
    val isDegreeMode: StateFlow<Boolean> = _isDegreeMode.asStateFlow()

    // History deletion helper
    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAll()
        }
    }

    // --- Basic / Scientific Calculator Inputs ---
    fun updateExpressionValue(newValue: TextFieldValue) {
        _expressionValue.value = newValue
        _expression.value = newValue.text
        _calcResult.value = "" // Clear result when typing
        updateLivePreview()
    }

    private fun insertToken(expressionVal: TextFieldValue, token: String): TextFieldValue {
        val text = expressionVal.text
        val selection = expressionVal.selection
        
        val start = selection.min
        val end = selection.max
        
        val newText = StringBuilder(text)
            .replace(start, end, token)
            .toString()
            
        val newCursorPos = start + token.length
        return TextFieldValue(
            text = newText,
            selection = TextRange(newCursorPos)
        )
    }

    private fun deleteToken(expressionVal: TextFieldValue): TextFieldValue {
        val text = expressionVal.text
        val selection = expressionVal.selection
        
        val start = selection.min
        val end = selection.max
        
        if (start != end) {
            val newText = StringBuilder(text)
                .delete(start, end)
                .toString()
            return TextFieldValue(
                text = newText,
                selection = TextRange(start)
            )
        } else {
            if (start > 0) {
                val newText = StringBuilder(text)
                    .deleteAt(start - 1)
                    .toString()
                return TextFieldValue(
                    text = newText,
                    selection = TextRange(start - 1)
                )
            }
        }
        return expressionVal
    }

    fun onCalcInput(token: String) {
        val currentVal = _expressionValue.value
        if (token != "=") {
            _calcResult.value = "" // Typing clears committed final result
        }
        when (token) {
            "AC" -> {
                _expressionValue.value = TextFieldValue("")
                _expression.value = ""
                _calcResult.value = ""
                _previewResult.value = ""
            }
            "C" -> {
                val newVal = deleteToken(currentVal)
                _expressionValue.value = newVal
                _expression.value = newVal.text
                updateLivePreview()
            }
            "( )" -> {
                val openCount = currentVal.text.count { it == '(' }
                val closeCount = currentVal.text.count { it == ')' }
                val tokenToInsert = if (openCount > closeCount) ")" else "("
                val newVal = insertToken(currentVal, tokenToInsert)
                _expressionValue.value = newVal
                _expression.value = newVal.text
                updateLivePreview()
            }
            "=" -> {
                evaluateExpression()
            }
            "deg/rad" -> {
                _isDegreeMode.value = !_isDegreeMode.value
                updateLivePreview()
            }
            "sin", "cos", "tan", "log", "ln", "sqrt", "cbrt" -> {
                val newVal = insertToken(currentVal, "$token(")
                _expressionValue.value = newVal
                _expression.value = newVal.text
                updateLivePreview()
            }
            else -> {
                val newVal = insertToken(currentVal, token)
                _expressionValue.value = newVal
                _expression.value = newVal.text
                updateLivePreview()
            }
        }
    }

    private fun updateLivePreview() {
        val currentExpr = _expression.value.trim()
        if (currentExpr.isEmpty()) {
            _previewResult.value = ""
            return
        }

        // We only want to show preview if the expression is actually a formula or can be evaluated.
        val containsOperator = currentExpr.any { it in listOf('+', '−', '×', '÷', '*', '/', '-', '%', '^', '!') } ||
                listOf("sin", "cos", "tan", "log", "ln", "sqrt", "cbrt").any { currentExpr.contains(it) }

        if (!containsOperator) {
            _previewResult.value = ""
            return
        }

        try {
            val res = MathEvaluator.evaluate(currentExpr, isDegreeMode.value)
            if (res.isNaN() || res.isInfinite()) {
                _previewResult.value = ""
            } else {
                val formatted = decimalFormat.format(res)
                if (formatted != currentExpr) {
                    _previewResult.value = formatted
                } else {
                    _previewResult.value = ""
                }
            }
        } catch (e: Exception) {
            _previewResult.value = "" // Swallow parsing/incomplete exceptions during live typing
        }
    }

    private fun evaluateExpression() {
        val currentExpr = _expression.value
        if (currentExpr.isEmpty()) return
        try {
            val res = MathEvaluator.evaluate(currentExpr, isDegreeMode.value)
            val formatted = if (res.isNaN()) "Error" else decimalFormat.format(res)
            _calcResult.value = formatted
            _previewResult.value = "" // Clear live preview when result is committed!

            // Record into database history
            viewModelScope.launch(Dispatchers.IO) {
                repository.insert(
                    HistoryEntity(
                        type = "Calculator",
                        expression = currentExpr,
                        result = formatted
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("CalculatorVM", "Evaluation failed", e)
            _calcResult.value = "Error"
            _previewResult.value = ""
        }
    }

    // --- Unit Converter State ---
    private val _selectedUnitType = MutableStateFlow("Length") // Length, Weight, Area, Volume, Temperature, Speed, Time, Data Storage
    val selectedUnitType: StateFlow<String> = _selectedUnitType.asStateFlow()

    private val _fromUnit = MutableStateFlow("Meter")
    val fromUnit: StateFlow<String> = _fromUnit.asStateFlow()

    private val _toUnit = MutableStateFlow("Kilometer")
    val toUnit: StateFlow<String> = _toUnit.asStateFlow()

    private val _unitFromValue = MutableStateFlow("1")
    val unitFromValue: StateFlow<String> = _unitFromValue.asStateFlow()

    private val _unitToValue = MutableStateFlow("0.001")
    val unitToValue: StateFlow<String> = _unitToValue.asStateFlow()

    fun updateUnitType(type: String) {
        _selectedUnitType.value = type
        // Reset defaults
        val defaultFrom = when (type) {
            "Length" -> "Meter"
            "Weight" -> "Kilogram"
            "Area" -> "Square Meter"
            "Volume" -> "Liter"
            "Temperature" -> "Celsius"
            "Speed" -> "Meter/Second"
            "Time" -> "Second"
            "Data Storage" -> "Megabyte (MB)"
            else -> "Unit"
        }
        val defaultTo = when (type) {
            "Length" -> "Kilometer"
            "Weight" -> "Gram"
            "Area" -> "Hectare"
            "Volume" -> "Milliliter"
            "Temperature" -> "Fahrenheit"
            "Speed" -> "Kilometer/Hour"
            "Time" -> "Minute"
            "Data Storage" -> "Gigabyte (GB)"
            else -> "Unit"
        }
        _fromUnit.value = defaultFrom
        _toUnit.value = defaultTo
        _unitFromValue.value = "1"
        performUnitConversion()
    }

    fun updateFromUnit(unit: String) {
        _fromUnit.value = unit
        performUnitConversion()
    }

    fun updateToUnit(unit: String) {
        _toUnit.value = unit
        performUnitConversion()
    }

    fun updateUnitFromValue(value: String) {
        _unitFromValue.value = value
        performUnitConversion()
    }

    private fun performUnitConversion() {
        val valueDouble = _unitFromValue.value.toDoubleOrNull() ?: 0.0
        val type = _selectedUnitType.value
        val from = _fromUnit.value
        val to = _toUnit.value

        val res = try {
            when (type) {
                "Length" -> convertLength(valueDouble, from, to)
                "Weight" -> convertWeight(valueDouble, from, to)
                "Area" -> convertArea(valueDouble, from, to)
                "Volume" -> convertVolume(valueDouble, from, to)
                "Temperature" -> convertTemperature(valueDouble, from, to)
                "Speed" -> convertSpeed(valueDouble, from, to)
                "Time" -> convertTime(valueDouble, from, to)
                "Data Storage" -> convertDataStorage(valueDouble, from, to)
                else -> 0.0
            }
        } catch (e: Exception) {
            0.0
        }
        _unitToValue.value = decimalFormat.format(res)
    }

    // Converters helper logic
    private fun convertLength(valIn: Double, from: String, to: String): Double {
        // Base Unit: Meter
        val meters = when (from) {
            "Meter" -> valIn
            "Kilometer" -> valIn * 1000.0
            "Mile" -> valIn * 1609.344
            "Inch" -> valIn * 0.0254
            "Foot" -> valIn * 0.3048
            "Yard" -> valIn * 0.9144
            "Centimeter" -> valIn * 0.01
            "Millimeter" -> valIn * 0.001
            else -> valIn
        }
        return when (to) {
            "Meter" -> meters
            "Kilometer" -> meters / 1000.0
            "Mile" -> meters / 1609.344
            "Inch" -> meters / 0.0254
            "Foot" -> meters / 0.3048
            "Yard" -> meters / 0.9144
            "Centimeter" -> meters / 0.01
            "Millimeter" -> meters / 0.001
            else -> meters
        }
    }

    private fun convertWeight(valIn: Double, from: String, to: String): Double {
        // Base Unit: Gram
        val grams = when (from) {
            "Gram" -> valIn
            "Kilogram" -> valIn * 1000.0
            "Pound (lb)" -> valIn * 453.59237
            "Ounce (oz)" -> valIn * 28.34952
            "Ton" -> valIn * 1000000.0
            else -> valIn
        }
        return when (to) {
            "Gram" -> grams
            "Kilogram" -> grams / 1000.0
            "Pound (lb)" -> grams / 453.59237
            "Ounce (oz)" -> grams / 28.34952
            "Ton" -> grams / 1000000.0
            else -> grams
        }
    }

    private fun convertArea(valIn: Double, from: String, to: String): Double {
        // Base Unit: Square Meter
        val sqMeters = when (from) {
            "Square Meter" -> valIn
            "Square Kilometer" -> valIn * 1_000_000.0
            "Square Mile" -> valIn * 2_589_988.11
            "Hectare" -> valIn * 10_000.0
            "Acre" -> valIn * 4046.8564
            else -> valIn
        }
        return when (to) {
            "Square Meter" -> sqMeters
            "Square Kilometer" -> sqMeters / 1_000_000.0
            "Square Mile" -> sqMeters / 2_589_988.11
            "Hectare" -> sqMeters / 10_000.0
            "Acre" -> sqMeters / 4046.8564
            else -> sqMeters
        }
    }

    private fun convertVolume(valIn: Double, from: String, to: String): Double {
        // Base Unit: Liter
        val liters = when (from) {
            "Liter" -> valIn
            "Milliliter" -> valIn * 0.001
            "Gallon (US)" -> valIn * 3.78541
            "Quart (US)" -> valIn * 0.946353
            "Cup" -> valIn * 0.24
            "Cubic Meter" -> valIn * 1000.0
            else -> valIn
        }
        return when (to) {
            "Liter" -> liters
            "Milliliter" -> liters / 0.001
            "Gallon (US)" -> liters / 3.78541
            "Quart (US)" -> liters / 0.946353
            "Cup" -> liters / 0.24
            "Cubic Meter" -> liters / 1000.0
            else -> liters
        }
    }

    private fun convertTemperature(valIn: Double, from: String, to: String): Double {
        val celsius = when (from) {
            "Celsius" -> valIn
            "Fahrenheit" -> (valIn - 32) * 5.0 / 9.0
            "Kelvin" -> valIn - 273.15
            else -> valIn
        }
        return when (to) {
            "Celsius" -> celsius
            "Fahrenheit" -> (celsius * 9.0 / 5.0) + 32
            "Kelvin" -> celsius + 273.15
            else -> celsius
        }
    }

    private fun convertSpeed(valIn: Double, from: String, to: String): Double {
        // Base Unit: Meter/Second
        val mps = when (from) {
            "Meter/Second" -> valIn
            "Kilometer/Hour" -> valIn / 3.6
            "Mile/Hour" -> valIn * 0.44704
            "Knot" -> valIn * 0.514444
            else -> valIn
        }
        return when (to) {
            "Meter/Second" -> mps
            "Kilometer/Hour" -> mps * 3.6
            "Mile/Hour" -> mps / 0.44704
            "Knot" -> mps / 0.514444
            else -> mps
        }
    }

    private fun convertTime(valIn: Double, from: String, to: String): Double {
        // Base Unit: Second
        val seconds = when (from) {
            "Second" -> valIn
            "Minute" -> valIn * 60.0
            "Hour" -> valIn * 3600.0
            "Day" -> valIn * 86400.0
            "Week" -> valIn * 604800.0
            "Month (Avg)" -> valIn * 2629746.0
            "Year" -> valIn * 31556952.0
            else -> valIn
        }
        return when (to) {
            "Second" -> seconds
            "Minute" -> seconds / 60.0
            "Hour" -> seconds / 3600.0
            "Day" -> seconds / 86400.0
            "Week" -> seconds / 604800.0
            "Month (Avg)" -> seconds / 2629746.0
            "Year" -> seconds / 31556952.0
            else -> seconds
        }
    }

    private fun convertDataStorage(valIn: Double, from: String, to: String): Double {
        // Base Unit: Kilobyte (KB)
        val kb = when (from) {
            "Kilobyte (KB)" -> valIn
            "Megabyte (MB)" -> valIn * 1024.0
            "Gigabyte (GB)" -> valIn * 1024.0 * 1024.0
            "Terabyte (TB)" -> valIn * 1024.0 * 1024.0 * 1024.0
            "Byte" -> valIn / 1024.0
            else -> valIn
        }
        return when (to) {
            "Kilobyte (KB)" -> kb
            "Megabyte (MB)" -> kb / 1024.0
            "Gigabyte (GB)" -> kb / (1024.0 * 1024.0)
            "Terabyte (TB)" -> kb / (1024.0 * 1024.0 * 1024.0)
            "Byte" -> kb * 1024.0
            else -> kb
        }
    }


    // --- Currency Converter State ---
    private val _currencyFromUnit = MutableStateFlow("USD")
    val currencyFromUnit: StateFlow<String> = _currencyFromUnit.asStateFlow()

    private val _currencyToUnit = MutableStateFlow("INR")
    val currencyToUnit: StateFlow<String> = _currencyToUnit.asStateFlow()

    private val _currencyFromValue = MutableStateFlow("100")
    val currencyFromValue: StateFlow<String> = _currencyFromValue.asStateFlow()

    private val _currencyToValue = MutableStateFlow("8350.0")
    val currencyToValue: StateFlow<String> = _currencyToValue.asStateFlow()

    private val _isCurrencyLoading = MutableStateFlow(false)
    val isCurrencyLoading: StateFlow<Boolean> = _isCurrencyLoading.asStateFlow()

    private val _currencyRefreshedTime = MutableStateFlow("Offline Cache")
    val currencyRefreshedTime: StateFlow<String> = _currencyRefreshedTime.asStateFlow()

    // Default static fallback rates based on 1 USD
    private val defaultRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "INR" to 83.5,
        "JPY" to 156.4,
        "CNY" to 7.25,
        "CAD" to 1.37,
        "AUD" to 1.51,
        "SGD" to 1.35,
        "CHF" to 0.90,
        "AED" to 3.67,
        "MYR" to 4.71,
        "RUB" to 89.1,
        "ZAR" to 18.8,
        "BRL" to 5.25
    )

    private val _exchangeRates = MutableStateFlow<Map<String, Double>>(defaultRates)
    val exchangeRates: StateFlow<Map<String, Double>> = _exchangeRates.asStateFlow()

    init {
        // Attempt first API load immediately
        fetchLatestExchangeRates()
    }

    fun fetchLatestExchangeRates() {
        _isCurrencyLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val service = CurrencyService.create()
                val response = service.getUsdRates()
                if (response.result == "success") {
                    _exchangeRates.value = response.rates
                    _currencyRefreshedTime.value = "Updated: Live"
                } else {
                    _currencyRefreshedTime.value = "Error loading. Using cached rates"
                }
            } catch (e: Exception) {
                Log.e("CalculatorVM", "Failed to load exchange rates", e)
                _currencyRefreshedTime.value = "Offline. Using cached rates"
            } finally {
                _isCurrencyLoading.value = false
                performCurrencyConversion()
            }
        }
    }

    fun updateCurrencyFromUnit(unit: String) {
        _currencyFromUnit.value = unit
        performCurrencyConversion()
    }

    fun updateCurrencyToUnit(unit: String) {
        _currencyToUnit.value = unit
        performCurrencyConversion()
    }

    fun updateCurrencyFromValue(value: String) {
        _currencyFromValue.value = value
        performCurrencyConversion()
    }

    private fun performCurrencyConversion() {
        val amount = _currencyFromValue.value.toDoubleOrNull() ?: 0.0
        val fromUnit = _currencyFromUnit.value
        val toUnit = _currencyToUnit.value

        val rates = _exchangeRates.value
        val rateFromUsd = rates[fromUnit] ?: defaultRates[fromUnit] ?: 1.0
        val rateToUsd = rates[toUnit] ?: defaultRates[toUnit] ?: 1.0

        // Amount in USD
        val inUsd = amount / rateFromUsd
        val result = inUsd * rateToUsd

        _currencyToValue.value = decimalFormat.format(result)
    }


    // --- Age Calculator State ---
    private val _dobYear = MutableStateFlow(2000)
    private val _dobMonth = MutableStateFlow(0) // 0-indexed (Jan = 0)
    private val _dobDay = MutableStateFlow(1)

    private val _targetYear = MutableStateFlow(2026)
    private val _targetMonth = MutableStateFlow(5) // June
    private val _targetDay = MutableStateFlow(3)

    private val _calculatedAge = MutableStateFlow("Years: 26, Months: 5, Days: 2")
    val calculatedAge: StateFlow<String> = _calculatedAge.asStateFlow()

    fun updateAgeCalculatorDOB(year: Int, month: Int, day: Int) {
        _dobYear.value = year
        _dobMonth.value = month
        _dobDay.value = day
        calculateAge()
    }

    fun updateAgeCalculatorTargetDate(year: Int, month: Int, day: Int) {
        _targetYear.value = year
        _targetMonth.value = month
        _targetDay.value = day
        calculateAge()
    }

    private fun calculateAge() {
        // Simple accurate dates difference calc
        val dobYr = _dobYear.value
        val dobMth = _dobMonth.value + 1 // convert to 1-indexed
        val dobDy = _dobDay.value

        val tarYr = _targetYear.value
        val tarMth = _targetMonth.value + 1
        val tarDy = _targetDay.value

        // Total birth days since 0 CE approx
        var ageYears = tarYr - dobYr
        var ageMonths = tarMth - dobMth
        var ageDays = tarDy - dobDy

        if (ageDays < 0) {
            ageMonths -= 1
            // add days of prev month
            ageDays += 30 // Approx 30 days
        }
        if (ageMonths < 0) {
            ageYears -= 1
            ageMonths += 12
        }

        if (ageYears < 0) {
            _calculatedAge.value = "Target date is before birth date!"
        } else {
            _calculatedAge.value = "Age: $ageYears Years, $ageMonths Months, $ageDays Days"
        }
    }


    // --- EMI (Loan) Calculator State ---
    private val _emiPrincipal = MutableStateFlow("100000") // $100k or Rs. 100k
    val emiPrincipal: StateFlow<String> = _emiPrincipal.asStateFlow()

    private val _emiInterestRate = MutableStateFlow("8.5")
    val emiInterestRate: StateFlow<String> = _emiInterestRate.asStateFlow()

    private val _emiTenureYears = MutableStateFlow("10")
    val emiTenureYears: StateFlow<String> = _emiTenureYears.asStateFlow()

    private val _emiMonthlyPayment = MutableStateFlow("0")
    val emiMonthlyPayment: StateFlow<String> = _emiMonthlyPayment.asStateFlow()

    private val _emiTotalInterest = MutableStateFlow("0")
    val emiTotalInterest: StateFlow<String> = _emiTotalInterest.asStateFlow()

    private val _emiTotalRepayment = MutableStateFlow("0")
    val emiTotalRepayment: StateFlow<String> = _emiTotalRepayment.asStateFlow()

    fun updateEmiParams(principal: String, rate: String, tenure: String) {
        _emiPrincipal.value = principal
        _emiInterestRate.value = rate
        _emiTenureYears.value = tenure
        calculateEmi()
    }

    private fun calculateEmi() {
        val p = _emiPrincipal.value.toDoubleOrNull() ?: 0.0
        val annualRate = _emiInterestRate.value.toDoubleOrNull() ?: 0.0
        val years = _emiTenureYears.value.toDoubleOrNull() ?: 0.0

        if (p <= 0.0 || annualRate <= 0.0 || years <= 0.0) {
            _emiMonthlyPayment.value = "0"
            _emiTotalInterest.value = "0"
            _emiTotalRepayment.value = "0"
            return
        }

        val monthlyRate = annualRate / (12.0 * 100.0)
        val totalMonths = years * 12.0

        // EMI = [P x R x (1+R)^N]/[((1+R)^N)-1]
        val emi = p * monthlyRate * (1 + monthlyRate).pow(totalMonths) / ((1 + monthlyRate).pow(totalMonths) - 1)
        val totalRepayment = emi * totalMonths
        val totalInterest = totalRepayment - p

        _emiMonthlyPayment.value = decimalFormat.format(emi)
        _emiTotalInterest.value = decimalFormat.format(totalInterest)
        _emiTotalRepayment.value = decimalFormat.format(totalRepayment)
    }


    // --- GST Calculator State ---
    private val _gstPrice = MutableStateFlow("1000")
    val gstPrice: StateFlow<String> = _gstPrice.asStateFlow()

    private val _gstRate = MutableStateFlow("18") // 5%, 12%, 18%, 28%
    val gstRate: StateFlow<String> = _gstRate.asStateFlow()

    private val _gstModeAdd = MutableStateFlow(true) // true for Add GST, false for Remove GST
    val gstModeAdd: StateFlow<Boolean> = _gstModeAdd.asStateFlow()

    private val _gstAmount = MutableStateFlow("0")
    val gstAmount: StateFlow<String> = _gstAmount.asStateFlow()

    private val _gstNetPrice = MutableStateFlow("0")
    val gstNetPrice: StateFlow<String> = _gstNetPrice.asStateFlow()

    fun updateGstParams(price: String, rate: String, modeAdd: Boolean) {
        _gstPrice.value = price
        _gstRate.value = rate
        _gstModeAdd.value = modeAdd
        calculateGst()
    }

    private fun calculateGst() {
        val price = _gstPrice.value.toDoubleOrNull() ?: 0.0
        val rate = _gstRate.value.toDoubleOrNull() ?: 0.0

        if (price <= 0.0 || rate < 0.0) {
            _gstAmount.value = "0"
            _gstNetPrice.value = "0"
            return
        }

        if (_gstModeAdd.value) {
            // Add GST
            val amount = price * (rate / 100.0)
            val net = price + amount
            _gstAmount.value = decimalFormat.format(amount)
            _gstNetPrice.value = decimalFormat.format(net)
        } else {
            // Remove GST
            // Price is inclusive of GST. Original Price = Price / (1 + Rate/100)
            val original = price / (1.0 + (rate / 100.0))
            val amount = price - original
            _gstAmount.value = decimalFormat.format(amount)
            _gstNetPrice.value = decimalFormat.format(original)
        }
    }


    // --- Discount Calculator State ---
    private val _discountPrice = MutableStateFlow("500")
    val discountPrice: StateFlow<String> = _discountPrice.asStateFlow()

    private val _discountPercentage = MutableStateFlow("20")
    val discountPercentage: StateFlow<String> = _discountPercentage.asStateFlow()

    private val _discountAmountSaved = MutableStateFlow("0")
    val discountAmountSaved: StateFlow<String> = _discountAmountSaved.asStateFlow()

    private val _discountFinalPrice = MutableStateFlow("0")
    val discountFinalPrice: StateFlow<String> = _discountFinalPrice.asStateFlow()

    fun updateDiscountParams(price: String, pct: String) {
        _discountPrice.value = price
        _discountPercentage.value = pct
        calculateDiscount()
    }

    private fun calculateDiscount() {
        val price = _discountPrice.value.toDoubleOrNull() ?: 0.0
        val pct = _discountPercentage.value.toDoubleOrNull() ?: 0.0

        if (price <= 0.0 || pct < 0.0) {
            _discountAmountSaved.value = "0"
            _discountFinalPrice.value = "0"
            return
        }

        val saved = price * (pct / 100.0)
        val finalP = price - saved

        _discountAmountSaved.value = decimalFormat.format(saved)
        _discountFinalPrice.value = decimalFormat.format(finalP)
    }


    // --- Mortgage Calculator State ---
    private val _mortgageHomeValue = MutableStateFlow("300000")
    val mortgageHomeValue: StateFlow<String> = _mortgageHomeValue.asStateFlow()

    private val _mortgageDownPayment = MutableStateFlow("60000")
    val mortgageDownPayment: StateFlow<String> = _mortgageDownPayment.asStateFlow()

    private val _mortgageRate = MutableStateFlow("6.5")
    val mortgageRate: StateFlow<String> = _mortgageRate.asStateFlow()

    private val _mortgageYears = MutableStateFlow("30")
    val mortgageYears: StateFlow<String> = _mortgageYears.asStateFlow()

    private val _mortgageMonthlyPayment = MutableStateFlow("0")
    val mortgageMonthlyPayment: StateFlow<String> = _mortgageMonthlyPayment.asStateFlow()

    private val _mortgageTotalInterest = MutableStateFlow("0")
    val mortgageTotalInterest: StateFlow<String> = _mortgageTotalInterest.asStateFlow()

    fun updateMortgageParams(home: String, down: String, rate: String, years: String) {
        _mortgageHomeValue.value = home
        _mortgageDownPayment.value = down
        _mortgageRate.value = rate
        _mortgageYears.value = years
        calculateMortgage()
    }

    private fun calculateMortgage() {
        val home = _mortgageHomeValue.value.toDoubleOrNull() ?: 0.0
        val down = _mortgageDownPayment.value.toDoubleOrNull() ?: 0.0
        val rate = _mortgageRate.value.toDoubleOrNull() ?: 0.0
        val years = _mortgageYears.value.toDoubleOrNull() ?: 0.0

        val loanAmount = home - down
        if (loanAmount <= 0.0 || rate <= 0.0 || years <= 0.0) {
            _mortgageMonthlyPayment.value = "0"
            _mortgageTotalInterest.value = "0"
            return
        }

        val mRate = rate / (12.0 * 100.0)
        val months = years * 12.0

        val mPayment = loanAmount * mRate * (1 + mRate).pow(months) / ((1 + mRate).pow(months) - 1)
        val totalRepayment = mPayment * months
        val totalInterest = totalRepayment - loanAmount

        _mortgageMonthlyPayment.value = decimalFormat.format(mPayment)
        _mortgageTotalInterest.value = decimalFormat.format(totalInterest)
    }


    // --- Split Bill Calculator State ---
    private val _splitTotalBill = MutableStateFlow("150")
    val splitTotalBill: StateFlow<String> = _splitTotalBill.asStateFlow()

    private val _splitTipPct = MutableStateFlow("15")
    val splitTipPct: StateFlow<String> = _splitTipPct.asStateFlow()

    private val _splitPeopleCount = MutableStateFlow("4")
    val splitPeopleCount: StateFlow<String> = _splitPeopleCount.asStateFlow()

    private val _splitPerPersonBill = MutableStateFlow("0")
    val splitPerPersonBill: StateFlow<String> = _splitPerPersonBill.asStateFlow()

    private val _splitTotalTipAmount = MutableStateFlow("0")
    val splitTotalTipAmount: StateFlow<String> = _splitTotalTipAmount.asStateFlow()

    private val _splitTotalPayable = MutableStateFlow("0")
    val splitTotalPayable: StateFlow<String> = _splitTotalPayable.asStateFlow()

    fun updateSplitParams(bill: String, tip: String, people: String) {
        _splitTotalBill.value = bill
        _splitTipPct.value = tip
        _splitPeopleCount.value = people
        calculateSplitBill()
    }

    private fun calculateSplitBill() {
        val bill = _splitTotalBill.value.toDoubleOrNull() ?: 0.0
        val tipPct = _splitTipPct.value.toDoubleOrNull() ?: 0.0
        val people = _splitPeopleCount.value.toIntOrNull() ?: 1

        if (bill <= 0.0 || people < 1) {
            _splitPerPersonBill.value = "0"
            _splitTotalTipAmount.value = "0"
            _splitTotalPayable.value = "0"
            return
        }

        val totalTip = bill * (tipPct / 100.0)
        val grandTotal = bill + totalTip
        val perPerson = grandTotal / people

        _splitTotalTipAmount.value = decimalFormat.format(totalTip)
        _splitTotalPayable.value = decimalFormat.format(grandTotal)
        _splitPerPersonBill.value = decimalFormat.format(perPerson)
    }


    // --- BMI Calculator State ---
    private val _bmiWeightKg = MutableStateFlow("70")
    val bmiWeightKg: StateFlow<String> = _bmiWeightKg.asStateFlow()

    private val _bmiHeightCm = MutableStateFlow("175")
    val bmiHeightCm: StateFlow<String> = _bmiHeightCm.asStateFlow()

    private val _bmiValue = MutableStateFlow("22.86")
    val bmiValue: StateFlow<String> = _bmiValue.asStateFlow()

    private val _bmiCategory = MutableStateFlow("Normal Weight")
    val bmiCategory: StateFlow<String> = _bmiCategory.asStateFlow()

    fun updateBmiParams(weight: String, height: String) {
        _bmiWeightKg.value = weight
        _bmiHeightCm.value = height
        calculateBmi()
    }

    private fun calculateBmi() {
        val w = _bmiWeightKg.value.toDoubleOrNull() ?: 0.0
        val h = _bmiHeightCm.value.toDoubleOrNull() ?: 0.0

        if (w <= 0.0 || h <= 0.0) {
            _bmiValue.value = "0.0"
            _bmiCategory.value = "N/A"
            return
        }

        val hMeters = h / 100.0
        val bmi = w / (hMeters * hMeters)
        _bmiValue.value = decimalFormat.format(bmi)

        _bmiCategory.value = when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal Weight"
            bmi < 30.0 -> "Overweight"
            else -> "Obese"
        }
    }


    // --- Fuel Calculator State ---
    private val _fuelDistance = MutableStateFlow("150")
    val fuelDistance: StateFlow<String> = _fuelDistance.asStateFlow()

    private val _fuelEfficiency = MutableStateFlow("15") // km per Liter
    val fuelEfficiency: StateFlow<String> = _fuelEfficiency.asStateFlow()

    private val _fuelPricePerLiter = MutableStateFlow("1.2")
    val fuelPricePerLiter: StateFlow<String> = _fuelPricePerLiter.asStateFlow()

    private val _fuelTotalCost = MutableStateFlow("12.0")
    val fuelTotalCost: StateFlow<String> = _fuelTotalCost.asStateFlow()

    fun updateFuelParams(dist: String, efficiency: String, price: String) {
        _fuelDistance.value = dist
        _fuelEfficiency.value = efficiency
        _fuelPricePerLiter.value = price
        calculateFuelCost()
    }

    private fun calculateFuelCost() {
        val dist = _fuelDistance.value.toDoubleOrNull() ?: 0.0
        val eff = _fuelEfficiency.value.toDoubleOrNull() ?: 0.0
        val pr = _fuelPricePerLiter.value.toDoubleOrNull() ?: 0.0

        if (dist <= 0.0 || eff <= 0.0 || pr <= 0.0) {
            _fuelTotalCost.value = "0"
            return
        }

        // Fuel needed: distance / efficiency (Liters)
        val liters = dist / eff
        val cost = liters * pr

        _fuelTotalCost.value = decimalFormat.format(cost)
    }


    // --- Tip Calculator State ---
    private val _tipBillAmt = MutableStateFlow("80")
    val tipBillAmt: StateFlow<String> = _tipBillAmt.asStateFlow()

    private val _tipPercentage = MutableStateFlow("15")
    val tipPercentage: StateFlow<String> = _tipPercentage.asStateFlow()

    private val _tipPeopleCount = MutableStateFlow("2")
    val tipPeopleCount: StateFlow<String> = _tipPeopleCount.asStateFlow()

    private val _tipSingleTip = MutableStateFlow("6")
    val tipSingleTip: StateFlow<String> = _tipSingleTip.asStateFlow()

    private val _tipSplitCostPerPerson = MutableStateFlow("46")
    val tipSplitCostPerPerson: StateFlow<String> = _tipSplitCostPerPerson.asStateFlow()

    fun updateTipParams(bill: String, pct: String, people: String) {
        _tipBillAmt.value = bill
        _tipPercentage.value = pct
        _tipPeopleCount.value = people
        calculateTip()
    }

    private fun calculateTip() {
        val bill = _tipBillAmt.value.toDoubleOrNull() ?: 0.0
        val pct = _tipPercentage.value.toDoubleOrNull() ?: 0.0
        val people = _tipPeopleCount.value.toIntOrNull() ?: 1

        if (bill <= 0.0 || people < 1) {
            _tipSingleTip.value = "0"
            _tipSplitCostPerPerson.value = "0"
            return
        }

        val totalTip = bill * (pct / 100.0)
        val perPersonTip = totalTip / people
        val perPersonTotal = (bill + totalTip) / people

        _tipSingleTip.value = decimalFormat.format(totalTip)
        _tipSplitCostPerPerson.value = decimalFormat.format(perPersonTotal)
    }
}
