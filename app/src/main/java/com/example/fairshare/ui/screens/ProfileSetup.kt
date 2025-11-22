package com.example.fairshare.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Reuse the Color Palette from the previous screen for consistency
val NeonCyan = Color(0xFF00E5FF)
val DeepDark = Color(0xFF0A0A0A)

@Composable
fun ProfileSetupScreen(
    onSetupComplete: (String, String, Uri?) -> Unit = { _, _, _ -> }
) {
    // State
    var name by remember { mutableStateOf("") }
    var handle by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Animation triggers
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    // Photo Picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepDark)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus() // Hide keyboard on tap outside
            }
    ) {

        ProfileBackground(color = NeonCyan)

        // 2. Main Content - Scrollable for keyboard safety
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .imePadding() // Pushes content up when keyboard opens
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header
            Spacer(modifier = Modifier.height(40.dp))
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically()
            ) {
                Text(
                    text = "WHO ARE YOU?",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    ),
                    color = Color.White
                )
            }

            Text(
                text = "Let's get your profile set up.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Holographic Avatar Picker
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 200)) +
                        slideInVertically(animationSpec = tween(500, delayMillis = 200))
            ) {
                HolographicAvatar(
                    imageUri = imageUri,
                    onClick = { launcher.launch("image/*") }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 4. Neon Inputs
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 400)) +
                        slideInVertically(animationSpec = tween(500, delayMillis = 400))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    NeonTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Display Name",
                        placeholder = "ex. Alex Carter",
                        icon = Icons.Rounded.Person,
                        imeAction = ImeAction.Next
                    )

                    NeonTextField(
                        value = handle,
                        onValueChange = { handle = it.replace(" ", "").lowercase() },
                        label = "Username",
                        placeholder = "ex. alex_c",
                        icon = Icons.Rounded.AlternateEmail,
                        prefix = "@",
                        imeAction = ImeAction.Done,
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 5. Action Button
            val isFormValid = name.isNotEmpty() && handle.isNotEmpty()

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 600))
            ) {
                Button(
                    onClick = { if (isFormValid) onSetupComplete(name, handle, imageUri) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) NeonCyan else Color.White.copy(alpha = 0.1f),
                        contentColor = if (isFormValid) Color.Black else Color.White.copy(alpha = 0.3f),
                        disabledContainerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    enabled = isFormValid
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CREATE PROFILE",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (isFormValid) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- Components ---

@Composable
fun HolographicAvatar(
    imageUri: Uri?,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(120.dp)
            .clickable { onClick() }
    ) {
        // Outer Glow Ring
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            Color.Transparent,
                            NeonCyan.copy(alpha = 0.5f),
                            NeonCyan,
                            NeonCyan.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Actual Image Container
        Surface(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            color = Color.White.copy(alpha = 0.05f)
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddAPhoto,
                        contentDescription = null,
                        tint = NeonCyan.copy(alpha = 0.8f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NeonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    prefix: String = "",
    imeAction: ImeAction = ImeAction.Next,
    onDone: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) NeonCyan else Color.White.copy(alpha = 0.1f),
        label = "border"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isFocused) NeonCyan else Color.White.copy(alpha = 0.4f),
        label = "icon"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            interactionSource = interactionSource,
            cursorBrush = SolidColor(NeonCyan),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.03f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            BorderStroke(1.dp, borderColor),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    if (prefix.isNotEmpty()) {
                        Text(
                            text = prefix,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color.White.copy(alpha = 0.2f),
                                fontSize = 18.sp
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}

// Reusing the optimized background from previous step for context
// Rename this function so it doesn't conflict with the Onboarding screen
@Composable
fun ProfileBackground(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.15f), // Top color
                        Color.Transparent          // Fade to black
                    )
                )
            )
    )
}

@Preview
@Composable
fun PreviewProfileSetup() {
    ProfileSetupScreen()
}