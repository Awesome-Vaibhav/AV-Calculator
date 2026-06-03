package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.components.AVLogoIcon

data class GridItem(
    val title: String,
    val screen: Screen,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        GridItem("Scientific", Screen.ScientificCalc, "Trigonometry & logs", Icons.Default.Star, Color(0xFFFF9100)),
        GridItem("Unit Covert", Screen.UnitConverter, "Length, weight, data etc.", Icons.Default.List, Color(0xFF2979FF)),
        GridItem("Currency", Screen.CurrencyConverter, "Live global currency exchange", Icons.Default.Refresh, Color(0xFF00E676)),
        GridItem("Age Calc", Screen.AgeCalc, "Date diff & birthday tracking", Icons.Default.Person, Color(0xFFD500F9)),
        GridItem("EMI", Screen.EmiCalc, "Vehicle & personal loan emi", Icons.Default.Check, Color(0xFFFF1744)),
        GridItem("GST", Screen.GstCalc, "Add or remove GST amount", Icons.Default.PlayArrow, Color(0xFFFF3D00)),
        GridItem("Discount", Screen.DiscountCalc, "Calculate savings in shopping", Icons.Default.Close, Color(0xFF00B0FF)),
        GridItem("Mortgage", Screen.MortgageCalc, "Home loan planning", Icons.Default.Home, Color(0xFF651FFF)),
        GridItem("Split Bill", Screen.SplitBillCalc, "Divide restaurant expenses", Icons.Default.Share, Color(0xFF1DE9B6)),
        GridItem("BMI Calc", Screen.BmiCalc, "Body mass index & category", Icons.Default.AccountBox, Color(0xFFC6FF00)),
        GridItem("Fuel Calc", Screen.FuelCalc, "Trip fuel estimation", Icons.Default.LocationOn, Color(0xFF3D5AFE)),
        GridItem("Tip Calc", Screen.TipCalc, "Gratuity splitting", Icons.Default.Favorite, Color(0xFFF50057)),
        GridItem("History Logs", Screen.History, "Calculation records", Icons.Default.Edit, Color(0xFF757575)),
        GridItem("Developer Details", Screen.About, "Developed by Vaibhav", Icons.Default.Info, Color(0xFF3F51B5))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AVLogoIcon(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(28.dp),
                            cornerRadius = 6.dp,
                            innerPadding = 2.dp,
                            tileGap = 1.dp,
                            symbolSize = 10.sp
                        )
                        Text(
                            text = "AV Toolkit",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Calculator"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Calculation Toolkit",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            
            Text(
                text = "Mi-inspired multi-tool utility panel",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.61f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clickable { onNavigate(item.screen) }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = item.color.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = item.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.description,
                                fontSize = 10.sp,
                                maxLines = 2,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
