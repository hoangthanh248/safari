package com.example.safari.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.BasicText
import com.example.safari.ui.components.*
import com.example.safari.ui.theme.*

// ── Private Mode Onboarding Screen ───────────────────────────────────────────

@Composable
fun PrivateModeScreen(
    onTurnOn: () -> Unit,
    onNotNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = IOSSpacing.xl, vertical = IOSSpacing.xxl),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(IOSSpacing.lg)
        ) {
            Spacer(Modifier.height(40.dp))

            // Hand + Face ID icons
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.size(52.dp)) {
                    CupertinoIcon(
                        CupertinoIcons.Hand,
                        tint = Color.White,
                        size = 52.dp,
                        strokeWidth = 1.5f
                    )
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .offset(x = (-8).dp)
                ) {
                    // FaceID approximation
                    FaceIdIcon()
                }
            }

            Spacer(Modifier.height(IOSSpacing.lg))

            BasicText(
                text = "Locked Private Browsing",
                style = IOSTypography.title2.copy(color = Color.White)
            )

            BasicText(
                text = "Private Browsing will lock when you leave Safari, leave Private Browsing, or lock your iPhone.",
                style = IOSTypography.body.copy(color = IOSColors.secondaryLabelDark)
            )

            BasicText(
                text = "You can unlock Private Browsing with Face ID or your passcode.",
                style = IOSTypography.body.copy(color = IOSColors.secondaryLabelDark)
            )

            BasicText(
                text = "You can change this later in Safari Settings.",
                style = IOSTypography.body.copy(color = IOSColors.secondaryLabelDark)
            )
        }

        // Bottom buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = IOSSpacing.xl, vertical = IOSSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(IOSSpacing.sm)
        ) {
            // Turn on button
            TurnOnButton(onClick = onTurnOn)

            // Not Now
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF2C2C2E))
                    .clickable(onClick = onNotNow)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    "Not Now",
                    style = IOSTypography.callout.copy(color = Color.White)
                )
            }
        }
    }
}

@Composable
private fun TurnOnButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "btn_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(IOSColors.iosBlue)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            "Turn On Locked Private Browsing",
            style = IOSTypography.callout.copy(color = Color.White)
        )
    }
}

@Composable
private fun FaceIdIcon() {
    // Simplified FaceID icon
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = 2f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        val c = Color.White

        // Corner markers
        val cornerLen = w * 0.25f
        val cornerR = w * 0.12f
        val pad = w * 0.08f

        // Top-left
        drawLine(c, androidx.compose.ui.geometry.Offset(pad, pad + cornerLen), androidx.compose.ui.geometry.Offset(pad, pad + cornerR), 2f)
        drawLine(c, androidx.compose.ui.geometry.Offset(pad + cornerR, pad), androidx.compose.ui.geometry.Offset(pad + cornerLen, pad), 2f)
        // Top-right
        drawLine(c, androidx.compose.ui.geometry.Offset(w - pad, pad + cornerR), androidx.compose.ui.geometry.Offset(w - pad, pad + cornerLen), 2f)
        drawLine(c, androidx.compose.ui.geometry.Offset(w - pad - cornerR, pad), androidx.compose.ui.geometry.Offset(w - pad - cornerLen, pad), 2f)
        // Bottom-left
        drawLine(c, androidx.compose.ui.geometry.Offset(pad, h - pad - cornerR), androidx.compose.ui.geometry.Offset(pad, h - pad - cornerLen), 2f)
        drawLine(c, androidx.compose.ui.geometry.Offset(pad + cornerR, h - pad), androidx.compose.ui.geometry.Offset(pad + cornerLen, h - pad), 2f)
        // Bottom-right
        drawLine(c, androidx.compose.ui.geometry.Offset(w - pad, h - pad - cornerLen), androidx.compose.ui.geometry.Offset(w - pad, h - pad - cornerR), 2f)
        drawLine(c, androidx.compose.ui.geometry.Offset(w - pad - cornerLen, h - pad), androidx.compose.ui.geometry.Offset(w - pad - cornerR, h - pad), 2f)

        // Face lines
        val cx = w / 2f
        val cy = h / 2f
        // Eyes
        drawLine(c, androidx.compose.ui.geometry.Offset(cx - w * 0.15f, cy - h * 0.1f), androidx.compose.ui.geometry.Offset(cx - w * 0.15f, cy - h * 0.05f), 2f, androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(c, androidx.compose.ui.geometry.Offset(cx + w * 0.15f, cy - h * 0.1f), androidx.compose.ui.geometry.Offset(cx + w * 0.15f, cy - h * 0.05f), 2f, androidx.compose.ui.graphics.StrokeCap.Round)
        // Nose
        drawLine(c, androidx.compose.ui.geometry.Offset(cx, cy - h * 0.05f), androidx.compose.ui.geometry.Offset(cx, cy + h * 0.05f), 2f, androidx.compose.ui.graphics.StrokeCap.Round)
        // Mouth arc
        val mouthPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx - w * 0.18f, cy + h * 0.08f)
            cubicTo(
                cx - w * 0.05f, cy + h * 0.2f,
                cx + w * 0.05f, cy + h * 0.2f,
                cx + w * 0.18f, cy + h * 0.08f
            )
        }
        drawPath(mouthPath, c, style = stroke)
    }
}
