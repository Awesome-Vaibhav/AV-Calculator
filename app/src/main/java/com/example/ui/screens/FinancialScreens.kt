package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmiCalculatorScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val emiPrincipal by viewModel.emiPrincipal.collectAsState()
    val emiInterestRate by viewModel.emiInterestRate.collectAsState()
    val emiTenureYears by viewModel.emiTenureYears.collectAsState()

    val monthlyPayment by viewModel.emiMonthlyPayment.collectAsState()
    val totalInterest by viewModel.emiTotalInterest.collectAsState()
    val totalRepayment by viewModel.emiTotalRepayment.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EMI (Loan) Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Enter Loan Parameters", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = emiPrincipal,
                        onValueChange = { viewModel.updateEmiParams(it, emiInterestRate, emiTenureYears) },
                        label = { Text("Loan Amount (Principal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = emiInterestRate,
                        onValueChange = { viewModel.updateEmiParams(emiPrincipal, it, emiTenureYears) },
                        label = { Text("Annual Interest Rate (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = emiTenureYears,
                        onValueChange = { viewModel.updateEmiParams(emiPrincipal, emiInterestRate, it) },
                        label = { Text("Loan Tenure (Years)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Repayment Summary", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, monthlyPayment) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Monthly EMI Payment", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(monthlyPayment, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, totalInterest) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Interest Payable", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(totalInterest, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, totalRepayment) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Repayment Amount", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(totalRepayment, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Tip: Tap any row to Copy its value", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GstCalculatorScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gstPrice by viewModel.gstPrice.collectAsState()
    val gstRate by viewModel.gstRate.collectAsState()
    val gstModeAdd by viewModel.gstModeAdd.collectAsState()

    val gstAmount by viewModel.gstAmount.collectAsState()
    val gstNetPrice by viewModel.gstNetPrice.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GST Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("GST Mode Selection", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.updateGstParams(gstPrice, gstRate, true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (gstModeAdd) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add GST", color = if (gstModeAdd) Color.White else MaterialTheme.colorScheme.onSurface)
                        }

                        Button(
                            onClick = { viewModel.updateGstParams(gstPrice, gstRate, false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!gstModeAdd) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Remove GST", color = if (!gstModeAdd) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = gstPrice,
                        onValueChange = { viewModel.updateGstParams(it, gstRate, gstModeAdd) },
                        label = { Text("Base Amount (Price)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = gstRate,
                        onValueChange = { viewModel.updateGstParams(gstPrice, it, gstModeAdd) },
                        label = { Text("GST Rate (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Calculation Result", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, gstAmount) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("GST Content Amount", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(gstAmount, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, gstNetPrice) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (gstModeAdd) "Net Price (With GST)" else "Original Price (Without GST)", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(gstNetPrice, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountCalculatorScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val discountPrice by viewModel.discountPrice.collectAsState()
    val discountPct by viewModel.discountPercentage.collectAsState()

    val savedAmount by viewModel.discountAmountSaved.collectAsState()
    val finalPrice by viewModel.discountFinalPrice.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discount Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Enter Shopping Info", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = discountPrice,
                        onValueChange = { viewModel.updateDiscountParams(it, discountPct) },
                        label = { Text("Original price") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = discountPct,
                        onValueChange = { viewModel.updateDiscountParams(discountPrice, it) },
                        label = { Text("Discount Percentage (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Savings Breakdown", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, savedAmount) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Amount Saved", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(savedAmount, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, finalPrice) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Final Purchase Price", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(finalPrice, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MortgageCalculatorScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mortgageHome by viewModel.mortgageHomeValue.collectAsState()
    val mortgageDown by viewModel.mortgageDownPayment.collectAsState()
    val mortgageRate by viewModel.mortgageRate.collectAsState()
    val mortgageYears by viewModel.mortgageYears.collectAsState()

    val mortgageMonthly by viewModel.mortgageMonthlyPayment.collectAsState()
    val mortgageTotalInterest by viewModel.mortgageTotalInterest.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mortgage Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Property Details", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = mortgageHome,
                        onValueChange = { viewModel.updateMortgageParams(it, mortgageDown, mortgageRate, mortgageYears) },
                        label = { Text("Home Value") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = mortgageDown,
                        onValueChange = { viewModel.updateMortgageParams(mortgageHome, it, mortgageRate, mortgageYears) },
                        label = { Text("Down Payment") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = mortgageRate,
                        onValueChange = { viewModel.updateMortgageParams(mortgageHome, mortgageDown, it, mortgageYears) },
                        label = { Text("Annual Rate (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = mortgageYears,
                        onValueChange = { viewModel.updateMortgageParams(mortgageHome, mortgageDown, mortgageRate, it) },
                        label = { Text("Term Length (Years)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Payment Schedule", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, mortgageMonthly) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Monthly Payment", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(mortgageMonthly, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, mortgageTotalInterest) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Interest Amount", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(mortgageTotalInterest, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillCalculatorScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalBill by viewModel.splitTotalBill.collectAsState()
    val tipPct by viewModel.splitTipPct.collectAsState()
    val people by viewModel.splitPeopleCount.collectAsState()

    val amountPerPerson by viewModel.splitPerPersonBill.collectAsState()
    val totalTip by viewModel.splitTotalTipAmount.collectAsState()
    val grandTotal by viewModel.splitTotalPayable.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Split Bill Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dinner Expenses", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = totalBill,
                        onValueChange = { viewModel.updateSplitParams(it, tipPct, people) },
                        label = { Text("Total Bill") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = tipPct,
                        onValueChange = { viewModel.updateSplitParams(totalBill, it, people) },
                        label = { Text("Tip Percentage (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = people,
                        onValueChange = { viewModel.updateSplitParams(totalBill, tipPct, it) },
                        label = { Text("Number of Persons") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Fair Share Breakdown", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, amountPerPerson) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Per Person Rate", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(amountPerPerson, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, totalTip) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Calculated Tip", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(totalTip, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, grandTotal) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Payable Sum", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(grandTotal, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelCalculatorScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fuelDistance by viewModel.fuelDistance.collectAsState()
    val fuelEfficiency by viewModel.fuelEfficiency.collectAsState()
    val fuelPricePerLiter by viewModel.fuelPricePerLiter.collectAsState()
    val fuelTotalCost by viewModel.fuelTotalCost.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Fuel Cost", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Trip Details", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = fuelDistance,
                        onValueChange = { viewModel.updateFuelParams(it, fuelEfficiency, fuelPricePerLiter) },
                        label = { Text("Total Distance (km)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fuelEfficiency,
                        onValueChange = { viewModel.updateFuelParams(fuelDistance, it, fuelPricePerLiter) },
                        label = { Text("Fuel Mileage (km/L)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fuelPricePerLiter,
                        onValueChange = { viewModel.updateFuelParams(fuelDistance, fuelEfficiency, it) },
                        label = { Text("Fuel Price (Per Liter / Gallon)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Estimated fuel price", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = fuelTotalCost,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { copyToClipboard(context, fuelTotalCost) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tap to Copy Value", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipCalculatorScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tipBillAmt by viewModel.tipBillAmt.collectAsState()
    val tipPercentage by viewModel.tipPercentage.collectAsState()
    val tipPeopleCount by viewModel.tipPeopleCount.collectAsState()

    val tipSingleTip by viewModel.tipSingleTip.collectAsState()
    val tipSplitCostPerPerson by viewModel.tipSplitCostPerPerson.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tip Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Gratuity parameters", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = tipBillAmt,
                        onValueChange = { viewModel.updateTipParams(it, tipPercentage, tipPeopleCount) },
                        label = { Text("Base cost") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = tipPercentage,
                        onValueChange = { viewModel.updateTipParams(tipBillAmt, it, tipPeopleCount) },
                        label = { Text("Tip rate (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = tipPeopleCount,
                        onValueChange = { viewModel.updateTipParams(tipBillAmt, tipPercentage, it) },
                        label = { Text("Person count") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Finances Details", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, tipSingleTip) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Calculated Tip Amount", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(tipSingleTip, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(context, tipSplitCostPerPerson) }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Share (Per Person)", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(tipSplitCostPerPerson, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
