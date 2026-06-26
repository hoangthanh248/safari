package com.example.safari.ui.components.glass

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.safari.ui.theme.IOSColors
import com.example.safari.ui.theme.IOSShapes
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.Shadow
import com.kyant.shapes.Capsule

// ── Backdrop Root Wrapper ─────────────────────────────────────────────────────

@Composable
fun LiquidGlassRoot(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(backdrop: Backdrop) -> Unit
) {
    val backdrop = rememberLayerBackdrop()
    Box(modifier = modifier.layerBackdrop(backdrop)) {
        content(backdrop)
    }
}

// ── Liquid Glass Surface ──────────────────────────────────────────────────────

@Composable
fun LiquidGlassSurface(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = IOSShapes.cardRounded,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val shape     = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val tintColor = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.55f)
                    else Color.White.copy(alpha = 0.55f)

    Box(
        modifier = modifier
            .clip(shape)                     // FIX: clip before drawBackdrop
            .drawBackdrop(
                backdrop  = backdrop,
                shape     = { shape.createOutline(size, layoutDirection, this) },
                effects   = {
                    vibrancy()
                    blur(18f.dp.toPx())
                    colorControls(brightness = 0.02f, saturation = 1.15f)
                },
                highlight      = { Highlight.Default },
                onDrawSurface  = {
                    drawRect(tintColor)
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDark) 0.07f else 0.22f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY   = size.height * 0.38f
                        )
                    )
                }
            )
    ) { content() }
}

// ── Liquid Glass Container ────────────────────────────────────────────────────

@Composable
fun LiquidGlassContainer(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = IOSShapes.largeRounded,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val shape     = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val tintColor = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.7f)
                    else Color.White.copy(alpha = 0.78f)

    Box(
        modifier = modifier
            .clip(shape)
            .drawBackdrop(
                backdrop  = backdrop,
                shape     = { shape.createOutline(size, layoutDirection, this) },
                effects   = {
                    vibrancy()
                    blur(28f.dp.toPx())
                    colorControls(saturation = 1.1f)
                },
                highlight     = { Highlight.Default },
                onDrawSurface = {
                    drawRect(tintColor)
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(0.18f), Color.Transparent),
                            endY = size.height * 0.35f
                        )
                    )
                }
            )
    ) { content() }
}

// ── Liquid Glass Button (Capsule) ─────────────────────────────────────────────

@Composable
fun LiquidGlassButton(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val tintColor = if (isDark) Color(0xFF3A3A3C).copy(alpha = 0.6f)
                    else Color.White.copy(alpha = 0.65f)
    val capsule   = remember { Capsule() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))    // visual clip so ripple is capsule-shaped
            .drawBackdrop(
                backdrop  = backdrop,
                shape     = { capsule.createOutline(size, layoutDirection, this) },
                effects   = {
                    vibrancy()
                    blur(16f.dp.toPx())
                    lens(8f.dp.toPx(), 12f.dp.toPx())
                },
                highlight     = { Highlight.Default },
                onDrawSurface = { drawRect(tintColor) }
            )
    ) { content() }
}

// ── Liquid Glass Toolbar ──────────────────────────────────────────────────────
// NOTE: LiquidGlassToolbar wraps content in Box, NOT Row.
// The caller is responsible for putting a Row inside.

@Composable
fun LiquidGlassToolbar(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit   // FIX: BoxScope, was RowScope → crash
) {
    val tintColor = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.82f)
                    else Color(0xFFF2F2F7).copy(alpha = 0.82f)
    val rectShape = remember { RoundedCornerShape(0.dp) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBackdrop(
                backdrop  = backdrop,
                shape     = { rectShape.createOutline(size, layoutDirection, this) },
                effects   = {
                    vibrancy()
                    blur(24f.dp.toPx())
                    colorControls(saturation = 1.05f)
                },
                onDrawSurface = {
                    drawRect(tintColor)
                    drawLine(
                        color       = if (isDark) Color.White.copy(0.12f) else Color.Black.copy(0.10f),
                        start       = Offset(0f, 0f),
                        end         = Offset(size.width, 0f),
                        strokeWidth = 0.5.dp.toPx()
                    )
                }
            )
    ) { content() }
}

// ── Liquid Glass Sheet ────────────────────────────────────────────────────────

@Composable
fun LiquidGlassSheet(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetShape = remember { RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp) }
    val tintColor  = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.88f)
                     else Color(0xFFF2F2F7).copy(alpha = 0.88f)

    Column(
        modifier = modifier
            .clip(sheetShape)
            .drawBackdrop(
                backdrop  = backdrop,
                shape     = { sheetShape.createOutline(size, layoutDirection, this) },
                effects   = {
                    vibrancy()
                    blur(32f.dp.toPx())
                    colorControls(saturation = 1.08f)
                },
                highlight     = { Highlight.Default.copy(alpha = 0.5f) },
                onDrawSurface = { drawRect(tintColor) }
            )
    ) { content() }
}

// ── Frosted Card ──────────────────────────────────────────────────────────────
// Solid fallback — no backdrop needed.

@Composable
fun FrostedCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = IOSShapes.largeRounded,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val bg    = if (isDark) Color(0xFF2C2C2E) else Color.White.copy(alpha = 0.94f)
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind { drawRect(bg) }     // FIX: drawBehind so content renders on top
    ) { content() }
}

// ── Glass Search Bar ──────────────────────────────────────────────────────────

@Composable
fun GlassSearchBar(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val tintColor = if (isDark) Color(0xFF3A3A3C).copy(alpha = 0.7f)
                    else Color(0xFFE5E5EA).copy(alpha = 0.72f)
    val capsule   = remember { Capsule() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .drawBackdrop(
                backdrop  = backdrop,
                shape     = { capsule.createOutline(size, layoutDirection, this) },
                effects   = {
                    vibrancy()
                    blur(20f.dp.toPx())
                    colorControls(saturation = 1.1f)
                },
                highlight     = { Highlight.Default.copy(alpha = 0.6f) },
                onDrawSurface = { drawRect(tintColor) }
            )
    ) { content() }
}

// ── Glass Context Menu ────────────────────────────────────────────────────────

@Composable
fun GlassContextMenu(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val menuShape = remember { RoundedCornerShape(14.dp) }
    val tintColor = if (isDark) Color(0xFF2C2C2E).copy(alpha = 0.92f)
                    else Color.White.copy(alpha = 0.92f)

    Column(
        modifier = modifier
            .clip(menuShape)
            .drawBackdrop(
                backdrop  = backdrop,
                shape     = { menuShape.createOutline(size, layoutDirection, this) },
                effects   = {
                    vibrancy()
                    blur(24f.dp.toPx())
                    lens(6f.dp.toPx(), 10f.dp.toPx())
                    colorControls(saturation = 1.12f)
                },
                highlight     = { Highlight.Default },
                shadow        = { Shadow(alpha = 0.18f, radius = 12.dp) },
                onDrawSurface = { drawRect(tintColor) }
            )
    ) { content() }
}

// ── Glass Tab Card ────────────────────────────────────────────────────────────

@Composable
fun GlassTabCard(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val cardShape   = remember { RoundedCornerShape(14.dp) }
    val tintColor   = if (isDark) Color(0xFF2C2C2E).copy(alpha = 0.85f)
                      else Color.White.copy(alpha = 0.85f)
    val activeColor = if (isDark) Color(0xFF0A84FF).copy(alpha = 0.25f)
                      else Color(0xFF007AFF).copy(alpha = 0.18f)

    Box(
        modifier = modifier
            .clip(cardShape)
            .drawBackdrop(
                backdrop  = backdrop,
                shape     = { cardShape.createOutline(size, layoutDirection, this) },
                effects   = {
                    vibrancy()
                    blur(14f.dp.toPx())
                    if (isActive) colorControls(brightness = 0.03f, saturation = 1.2f)
                },
                highlight     = { if (isActive) Highlight.Default else Highlight.Default.copy(alpha = 0.4f) },
                onDrawSurface = {
                    drawRect(tintColor)
                    if (isActive) drawRect(activeColor)
                }
            )
    ) { content() }
}
