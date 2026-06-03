package com.example.ui

sealed class Screen(val title: String) {
    object Home : Screen("AV Toolkit")
    object BasicCalc : Screen("Basic Calculator")
    object ScientificCalc : Screen("Scientific Calculator")
    object UnitConverter : Screen("Unit Converter")
    object CurrencyConverter : Screen("Currency Converter")
    object AgeCalc : Screen("Age Calculator")
    object EmiCalc : Screen("EMI Calculator")
    object GstCalc : Screen("GST Calculator")
    object DiscountCalc : Screen("Discount Calculator")
    object MortgageCalc : Screen("Mortgage Calculator")
    object SplitBillCalc : Screen("Split Bill")
    object BmiCalc : Screen("BMI Calculator")
    object FuelCalc : Screen("Fuel Calculator")
    object TipCalc : Screen("Tip Calculator")
    object History : Screen("History Logs")
    object About : Screen("About Developer")
}
