package com.example.fairshare.ui.graphs

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

data class DailyExpense(val day: Int, val amount: Float)
data class MonthlyExpenseData(
    val monthName: String,
    val color: Color,
    val data: List<DailyExpense>
)

@Composable
fun MonthlyComparisonChart(
    modifier: Modifier = Modifier,
    monthsData: List<MonthlyExpenseData>
) {
    val maxExpense = monthsData.flatMap { it.data }.maxOfOrNull { it.amount } ?: 0f
    var selectedDay by remember { mutableStateOf<Int?>(null) }

    // Animate chart drawing
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = "Monthly Expense Comparison",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Map tap X to day (approx)
                        val widthPerDay = size.width / 30f
                        selectedDay = (offset.x / widthPerDay).roundToInt().coerceIn(1, 30)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val chartWidth = size.width
                val chartHeight = size.height
                val stepX = chartWidth / 30f

                // Draw grid lines
                for (i in 0..4) {
                    val y = chartHeight - (i / 4f) * chartHeight
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        start = Offset(0f, y),
                        end = Offset(chartWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw month lines
                monthsData.forEach { month ->
                    val points = month.data.sortedBy { it.day }
                    val path = Path()
                    val gradient = Brush.verticalGradient(
                        listOf(month.color.copy(alpha = 0.5f), Color.Transparent),
                        endY = chartHeight
                    )

                    points.forEachIndexed { index, daily ->
                        val x = daily.day * stepX
                        val y = chartHeight - (daily.amount / maxExpense) * chartHeight * animationProgress
                        if (index == 0) path.moveTo(x, y)
                        else path.lineTo(x, y)
                    }

                    // Gradient fill
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(points.last().day * stepX, chartHeight)
                        lineTo(points.first().day * stepX, chartHeight)
                        close()
                    }
                    drawPath(fillPath, gradient)
                    drawPath(path, month.color, style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    )
                }

                // Draw tooltip if selected
                selectedDay?.let { day ->
                    val x = day * stepX
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        start = Offset(x,0f),
                        end = Offset(x, chartWidth),
                        strokeWidth = 1.dp.toPx()
                    )
                    monthsData.forEach { month ->
                        val match = month.data.find { it.day == day }
                        match?.let {
                            val y = chartHeight - (it.amount / maxExpense) * chartHeight
                            drawCircle(month.color, radius = 6f, center = Offset(x, y))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Legend
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            monthsData.forEach { month ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(month.color, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = month.monthName, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        selectedDay?.let { day ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Day $day: ${
                    monthsData.joinToString(" | ") { m ->
                        val amt = m.data.find { it.day == day }?.amount ?: 0f
                        "${m.monthName}: ₹${amt.roundToInt()}"
                    }
                }",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}
