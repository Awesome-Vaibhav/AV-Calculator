package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AVLogoIcon(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    innerPadding: Dp = 3.dp,
    tileGap: Dp = 1.5.dp,
    symbolSize: TextUnit = 12.sp
) {
    Box(
        modifier = modifier
            .drawBehind {
                // Subtle custom accent neon flares matching the original image design
                drawCircle(
                    color = Color(0x2E1E88E5),
                    radius = size.minDimension * 0.55f,
                    center = androidx.compose.ui.geometry.Offset(0f, size.height)
                )
                drawCircle(
                    color = Color(0x2EFF6D00),
                    radius = size.minDimension * 0.55f,
                    center = androidx.compose.ui.geometry.Offset(size.width, size.height)
                )
            }
            .background(Color(0xFF141517), RoundedCornerShape(cornerRadius))
            .border(1.dp, Color(0xFF262930), RoundedCornerShape(cornerRadius))
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(tileGap)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(tileGap)
            ) {
                // Top-Left: Dark grey tile with '+'
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            Color(0xFF1F2125), 
                            RoundedCornerShape(
                                topStart = cornerRadius - 1.5.dp, 
                                topEnd = 2.dp, 
                                bottomStart = 2.dp, 
                                bottomEnd = 2.dp
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = Color.White,
                        fontSize = symbolSize,
                        fontWeight = FontWeight.Bold
                    )
                }
                // Top-Right: Dark grey tile with '−' (minus)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            Color(0xFF1F2125), 
                            RoundedCornerShape(
                                topStart = 2.dp, 
                                topEnd = cornerRadius - 1.5.dp, 
                                bottomStart = 2.dp, 
                                bottomEnd = 2.dp
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "−",
                        color = Color.White,
                        fontSize = symbolSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(tileGap)
            ) {
                // Bottom-Left: Dark grey tile with '×'
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            Color(0xFF1F2125), 
                            RoundedCornerShape(
                                topStart = 2.dp, 
                                topEnd = 2.dp, 
                                bottomStart = cornerRadius - 1.5.dp, 
                                bottomEnd = 2.dp
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "×",
                        color = Color.White,
                        fontSize = symbolSize,
                        fontWeight = FontWeight.Bold
                    )
                }
                // Bottom-Right: Glowing accent orange tile with '='
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFF7D00), Color(0xFFE65100))
                            ),
                            shape = RoundedCornerShape(
                                topStart = 2.dp, 
                                topEnd = 2.dp, 
                                bottomStart = 2.dp, 
                                bottomEnd = cornerRadius - 1.5.dp
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "=",
                        color = Color.White,
                        fontSize = symbolSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
