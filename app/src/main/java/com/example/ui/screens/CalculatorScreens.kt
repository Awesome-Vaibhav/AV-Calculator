package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AVLogoIcon
import com.example.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BasicCalculatorScreen(
    viewModel: CalculatorViewModel,
    onNavigateToMenu: () -> Unit,
    onNavigateToScientific: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expressionValue by viewModel.expressionValue.collectAsState()
    val calcResult by viewModel.calcResult.collectAsState()
    val previewResult by viewModel.previewResult.collectAsState()
    val context = LocalContext.current

    val keys = listOf(
        "AC", "⌫", "%", "÷",
        "7", "8", "9", "×",
        "4", "5", "6", "−",
        "1", "2", "3", "+",
        "toggle_sci", "0", ".", "="
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
                            text = "AV Calculator",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToMenu) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Toolkit Menu",
                            tint = MaterialTheme.colorScheme.primary
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
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Display Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Interactive Expression Input
                BasicTextField(
                    value = expressionValue,
                    onValueChange = { viewModel.updateExpressionValue(it) },
                    textStyle = TextStyle(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.End,
                        lineHeight = 44.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    readOnly = true,
                    visualTransformation = com.example.util.IndianGroupingVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { /* Allow selection/focus */ },
                                    onLongClick = {
                                        val text = getClipboardText(context)
                                        if (text != null) {
                                            val cleaned = text.filter { it.isDigit() || it in ".,+-*/÷×−%() " }
                                            if (cleaned.isNotEmpty()) {
                                                viewModel.pasteExpression(cleaned)
                                                Toast.makeText(context, "Pasted: $cleaned", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "No valid number in clipboard", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            if (expressionValue.text.isEmpty()) {
                                Text(
                                    text = "0",
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Light,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                    textAlign = TextAlign.End
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // History Menu and Live/Final Result
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // History and Paste Icon Buttons (on the left, above the result value)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateToHistory,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Calculation History",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = {
                                val text = getClipboardText(context)
                                if (text != null) {
                                    val cleaned = text.filter { it.isDigit() || it in ".,+-*/÷×−%() " }
                                    if (cleaned.isNotEmpty()) {
                                        viewModel.pasteExpression(cleaned)
                                        Toast.makeText(context, "Pasted: $cleaned", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "No valid number in clipboard", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "Paste from Clipboard",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Result (on the right)
                    if (calcResult.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    copyToClipboard(context, calcResult)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "= ${com.example.util.NumberFormatter.insertWordJoiners(calcResult)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(Copy)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        }
                    } else if (previewResult.isNotEmpty()) {
                        Text(
                            text = "= ${com.example.util.NumberFormatter.insertWordJoiners(previewResult)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            // Keypad
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(keys) { key ->
                    val isOperator = key in listOf("÷", "×", "−", "+", "=")
                    val isAction = key in listOf("AC", "⌫", "%", "toggle_sci")
                    val isNumber = !isOperator && !isAction

                    val containerColor = if (key == "=") MaterialTheme.colorScheme.primary else Color.Transparent
                    val contentColor = when {
                        key == "=" -> Color.White
                        isNumber -> MaterialTheme.colorScheme.onBackground
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(containerColor)
                            .combinedClickable(
                                onClick = {
                                    when (key) {
                                        "⌫" -> viewModel.onCalcInput("C")
                                        "toggle_sci" -> onNavigateToScientific()
                                        else -> viewModel.onCalcInput(key)
                                    }
                                },
                                onLongClick = {
                                    if (key == "⌫") {
                                        viewModel.onCalcInput("AC")
                                    }
                                }
                            )
                    ) {
                        when (key) {
                            "⌫" -> {
                                Icon(
                                    imageVector = Icons.Default.Backspace,
                                    contentDescription = "Backspace",
                                    tint = contentColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            "toggle_sci" -> {
                                Box(
                                    modifier = Modifier.size(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(13.dp)
                                            .align(Alignment.TopEnd)
                                            .border(1.5.dp, contentColor, RoundedCornerShape(3.dp))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(13.dp)
                                            .align(Alignment.BottomStart)
                                            .border(1.5.dp, contentColor, RoundedCornerShape(3.dp))
                                    )
                                }
                            }
                            else -> {
                                Text(
                                    text = key,
                                    fontSize = when {
                                        key == "=" -> 34.sp
                                        isNumber -> 32.sp
                                        key == "AC" -> 24.sp
                                        else -> 32.sp
                                    },
                                    fontWeight = when {
                                        key == "=" -> FontWeight.Normal
                                        isNumber -> FontWeight.Normal
                                        else -> FontWeight.Normal
                                    },
                                    color = contentColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScientificCalculatorScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expressionValue by viewModel.expressionValue.collectAsState()
    val calcResult by viewModel.calcResult.collectAsState()
    val previewResult by viewModel.previewResult.collectAsState()
    val isDegree by viewModel.isDegreeMode.collectAsState()
    val context = LocalContext.current

    val basicKeys = listOf(
        "AC", "⌫", "%", "÷",
        "7", "8", "9", "×",
        "4", "5", "6", "−",
        "1", "2", "3", "+",
        "toggle_basic", "0", ".", "="
    )

    val scientificKeys = listOf(
        "sin", "cos", "tan", "deg/rad",
        "log", "ln", "^", "√",
        "cbrt", "!", "π", "e"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scientific Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val text = getClipboardText(context)
                            if (text != null) {
                                val cleaned = text.filter { it.isDigit() || it in ".,+-*/÷×−%() " }
                                if (cleaned.isNotEmpty()) {
                                    viewModel.pasteExpression(cleaned)
                                    Toast.makeText(context, "Pasted: $cleaned", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "No valid number in clipboard", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Paste from Clipboard",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDegree) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { viewModel.onCalcInput("deg/rad") }
                    ) {
                        Text(
                            text = if (isDegree) "DEG" else "RAD",
                            color = if (isDegree) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
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
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Display Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Info line
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = if (isDegree) "Degrees Mode" else "Radians Mode",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Interactive Expression Input
                BasicTextField(
                    value = expressionValue,
                    onValueChange = { viewModel.updateExpressionValue(it) },
                    textStyle = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.End,
                        lineHeight = 38.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    readOnly = true,
                    visualTransformation = com.example.util.IndianGroupingVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { /* Allow selection/focus */ },
                                    onLongClick = {
                                        val text = getClipboardText(context)
                                        if (text != null) {
                                            val cleaned = text.filter { it.isDigit() || it in ".,+-*/÷×−%() " }
                                            if (cleaned.isNotEmpty()) {
                                                viewModel.pasteExpression(cleaned)
                                                Toast.makeText(context, "Pasted: $cleaned", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "No valid number in clipboard", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            if (expressionValue.text.isEmpty()) {
                                Text(
                                    text = "0",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Light,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                    textAlign = TextAlign.End
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Live/Final Result
                if (calcResult.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                copyToClipboard(context, calcResult)
                            },
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "= ${com.example.util.NumberFormatter.insertWordJoiners(calcResult)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(Copy)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                } else if (previewResult.isNotEmpty()) {
                    Text(
                        text = "= ${com.example.util.NumberFormatter.insertWordJoiners(previewResult)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f),
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Scientific Keys Grid (3 rows of 4 buttons)
            Text(
                text = "Scientific Functions",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(scientificKeys) { key ->
                    val isConst = key in listOf("π", "e")
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isConst) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { viewModel.onCalcInput(key) }
                    ) {
                        Text(
                            text = key,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isConst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Basic Keys Grid (5 rows of 4 buttons)
            Text(
                text = "Arithmetic Keys",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(basicKeys) { key ->
                    val isOperator = key in listOf("÷", "×", "−", "+", "=")
                    val isAction = key in listOf("AC", "⌫", "%", "toggle_basic")
                    val isNumber = !isOperator && !isAction

                    val containerColor = if (key == "=") MaterialTheme.colorScheme.primary else Color.Transparent
                    val contentColor = when {
                        key == "=" -> Color.White
                        isNumber -> MaterialTheme.colorScheme.onBackground
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(CircleShape)
                            .background(containerColor)
                            .combinedClickable(
                                onClick = {
                                    when (key) {
                                        "⌫" -> viewModel.onCalcInput("C")
                                        "toggle_basic" -> onBack()
                                        else -> viewModel.onCalcInput(key)
                                    }
                                },
                                onLongClick = {
                                    if (key == "⌫") {
                                        viewModel.onCalcInput("AC")
                                    }
                                }
                            )
                    ) {
                        when (key) {
                            "⌫" -> {
                                Icon(
                                    imageVector = Icons.Default.Backspace,
                                    contentDescription = "Backspace",
                                    tint = contentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            "toggle_basic" -> {
                                Box(
                                    modifier = Modifier.size(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(13.dp)
                                            .align(Alignment.TopEnd)
                                            .border(1.5.dp, contentColor, RoundedCornerShape(3.dp))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(13.dp)
                                            .align(Alignment.BottomStart)
                                            .border(1.5.dp, contentColor, RoundedCornerShape(3.dp))
                                    )
                                }
                            }
                            else -> {
                                Text(
                                    text = key,
                                    fontSize = when {
                                        key == "=" -> 26.sp
                                        isNumber -> 24.sp
                                        key == "AC" -> 18.sp
                                        else -> 24.sp
                                    },
                                    fontWeight = when {
                                        key == "=" -> FontWeight.Normal
                                        isNumber -> FontWeight.Normal
                                        else -> FontWeight.Normal
                                    },
                                    color = contentColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Utility Clipboard copy
fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("AV Calculator Result", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied: $text", Toast.LENGTH_SHORT).show()
}

// Utility Clipboard paste
fun getClipboardText(context: Context): String? {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    if (clipboard.hasPrimaryClip()) {
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text
            if (text != null) {
                return text.toString()
            }
        }
    }
    return null
}
