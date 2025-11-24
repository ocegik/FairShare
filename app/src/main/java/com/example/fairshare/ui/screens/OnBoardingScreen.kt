package com.example.fairshare.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import com.example.fairshare.core.data.models.AppPreferences
import com.example.fairshare.navigation.Screen
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

// --- Data Model ---
data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color
)

// --- Main Screen ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit
) {

    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }

    // 1. Defined Data (Single Source of Truth)
    val pages = remember {
        listOf(
            OnboardingPageData(
                title = "SQUAD UP",
                subtitle = "Groups",
                description = "Stop using spreadsheets. Organize trips and house bills in a way that actually works.",
                icon = Icons.Rounded.Groups,
                primaryColor = Color(0xFF00E5FF), // Cyan
                secondaryColor = Color(0xFF2979FF) // Blue
            ),
            OnboardingPageData(
                title = "SPLIT IT",
                subtitle = "Expenses",
                description = "Percentages? Shares? Exact amounts? We handle the math so you don't kill each other.",
                icon = Icons.AutoMirrored.Rounded.ReceiptLong,
                primaryColor = Color(0xFF00E676), // Green
                secondaryColor = Color(0xFF00C853)
            ),
            OnboardingPageData(
                title = "PAY UP",
                subtitle = "Settlements",
                description = "Settle balances instantly. No awkward conversations required. Just tap and go.",
                icon = Icons.Rounded.AccountBalanceWallet,
                primaryColor = Color(0xFFFF4081), // Pink
                secondaryColor = Color(0xFFC51162)
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current // For Haptics

    // 2. Background Animation State
    // We blend colors smoothly based on the current page offset
    val currentPage = pages[pagerState.currentPage]
    val targetColor by animateColorAsState(
        targetValue = currentPage.primaryColor,
        animationSpec = tween(1000),
        label = "bgColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)) // Production Grade Dark Mode (Not pure black)
    ) {
        // 3. Optimized Background (No Canvas/Blur needed)
        // Uses gradients for performance on all devices
        AmbientBackground(targetColor)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars) // Handle Notches
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    // navigate to ProfileSetup when user taps SKIP
                    { onOnboardingComplete() },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "SKIP",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Pager Section
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 32.dp),
                pageSpacing = 16.dp
            ) { pageIndex ->
                GlassCard(
                    page = pages[pageIndex],
                    pagerState = pagerState,
                    pageIndex = pageIndex
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                ProgressFab(
                    currentStep = pagerState.currentPage,
                    totalSteps = pages.size,
                    color = targetColor,
                    onNext = {

                        if (pagerState.currentPage < pages.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onOnboardingComplete()
                        }
                    }
                )
            }
        }
    }
}

// --- Optimized Background ---
@Composable
fun AmbientBackground(color: Color) {
    // Top Right Blob
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(x = 1000f, y = 0f),
                    radius = 1500f
                )
            )
    )
    // Bottom Left Blob
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(x = 0f, y = 2000f),
                    radius = 1200f
                )
            )
    )
}

// --- The 3D Glass Card ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlassCard(
    page: OnboardingPageData,
    pagerState: PagerState,
    pageIndex: Int
) {
    val density = LocalDensity.current
    val offset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
    val absOffset = offset.absoluteValue

    // Subtle 3D calculations
    val scale = lerp(0.9f, 1f, 1f - absOffset)
    val alpha = lerp(0.5f, 1f, 1f - absOffset)
    val rotationY = offset * -10f // Less rotation is more professional

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f) // Consistent Shape
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                this.rotationY = rotationY
                cameraDistance = 12f * density.density // Perspective
            }
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.02f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Subtitle / Tag
        Text(
            text = page.subtitle.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = page.primaryColor,
            letterSpacing = 2.sp,
            modifier = Modifier
                .background(page.primaryColor.copy(alpha = 0.1f), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        // Icon with Glow
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(page.primaryColor.copy(alpha = 0.3f), Color.Transparent)
                        )
                    )
            )
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
        }

        // Typography Group
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

// --- The Interactive FAB ---
@Composable
fun ProgressFab(
    currentStep: Int,
    totalSteps: Int,
    color: Color,
    onNext: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = (currentStep + 1) / totalSteps.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(90.dp)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null, // Disable default ripple, we have custom animation
                onClick = onNext
            )
    ) {
        // Track
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color.White.copy(alpha = 0.1f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Button
        Surface(
            modifier = Modifier
                .size(64.dp)
                .scale(1f), // You can add a press animation here if desired
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 10.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                val isLast = currentStep == totalSteps - 1

                AnimatedVisibility(
                    visible = !isLast,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                AnimatedVisibility(
                    visible = isLast,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Done",
                        tint = color, // Tint the checkmark with the page color
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

