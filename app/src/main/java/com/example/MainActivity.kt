package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.db.AppDatabase
import com.example.db.HistoryRepository
import com.example.ui.Screen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Setup Room DB and Repository
    val database = AppDatabase.getDatabase(applicationContext)
    val repository = HistoryRepository(database.historyDao())

    // Factory inline to instantiate CalculatorViewModel
    val factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalculatorViewModel(repository) as T
      }
    }
    val viewModel = ViewModelProvider(this, factory)[CalculatorViewModel::class.java]

    setContent {
      MyApplicationTheme {
        val navigationStack = remember { mutableStateListOf<Screen>(Screen.BasicCalc) }
        var lastBackPressTime by remember { mutableStateOf(0L) }
        val context = LocalContext.current

        // Handle back button gestures on device
        BackHandler {
          if (navigationStack.size > 1) {
            // Pop the last screen off the custom navigation stack
            navigationStack.removeAt(navigationStack.lastIndex)
          } else {
            // Double tap logic to close application when on the landing screen
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressTime < 2000) {
              finish()
            } else {
              lastBackPressTime = currentTime
              Toast.makeText(context, "Press BACK again to exit", Toast.LENGTH_SHORT).show()
            }
          }
        }

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          val currentScreen = navigationStack.last()

          when (currentScreen) {
            is Screen.Home -> {
              HomeScreen(
                onNavigate = { screen ->
                  navigationStack.add(screen)
                },
                onBack = {
                  navigationStack.removeAt(navigationStack.lastIndex)
                },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.BasicCalc -> {
              BasicCalculatorScreen(
                viewModel = viewModel,
                onNavigateToMenu = {
                  navigationStack.add(Screen.Home)
                },
                onNavigateToHistory = {
                  navigationStack.add(Screen.History)
                },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.ScientificCalc -> {
              ScientificCalculatorScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.UnitConverter -> {
              UnitConverterScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.CurrencyConverter -> {
              CurrencyConverterScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.AgeCalc -> {
              AgeCalculatorScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.EmiCalc -> {
              EmiCalculatorScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.GstCalc -> {
              GstCalculatorScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.DiscountCalc -> {
              DiscountCalculatorScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.MortgageCalc -> {
              MortgageCalculatorScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.SplitBillCalc -> {
              SplitBillCalculatorScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.BmiCalc -> {
              BmiCalculatorScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.FuelCalc -> {
              FuelCalculatorScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.TipCalc -> {
              TipCalculatorScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.History -> {
              HistoryLogsScreen(
                viewModel = viewModel,
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
            is Screen.About -> {
              AboutScreen(
                onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                modifier = Modifier.padding(innerPadding)
              )
            }
          }
        }
      }
    }
  }
}
