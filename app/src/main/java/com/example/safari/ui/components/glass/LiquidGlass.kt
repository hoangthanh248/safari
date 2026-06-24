package com.example.safari.ui.components.glass

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
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
// Wrap the root screen content with this so all glass children can sample from it.

@Composable
fun LiquidGlassRoot(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(backdrop: Backdrop) -> Unit
) {
    val backdrop = rememberLayerBackdrop()
    Box(
        modifier = modifier.layerBackdrop(backdrop)
    ) {
        content(backdrop)
    }
}

// ── Liquid Glass Surface ──────────────────────────────────────────────────────
// General-purpose glass panel. Uses vibrancy + blur + subtle lens.

@Composable
fun LiquidGlassSurface(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = IOSShapes.cardRounded,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = remember(cornerRadius) {
        RoundedCornerShape(cornerRadius)
    }
    val tintColor = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.55f)
                    else Color.White.copy(alpha = 0.55f)

    Box(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape.toOutline(size, layoutDirection, this) },
                effects = {
                    vibrancy()
                    blur(18f.dp.toPx())
                    colorControls(brightness = 0.02f, saturation = 1.15f)
                },
                highlight = { Highlight.Default },
                onDrawSurface = {
                    drawRect(tintColor)
                    // Subtle top specular sheen
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDark) 0.07f else 0.22f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.38f
                        )
                    )
                }
            )
    ) {
        content()
    }
}

// ── Liquid Glass Container ────────────────────────────────────────────────────
// Heavier card with more blur — used for main cards (Start Page card, etc.)

@Composable
fun LiquidGlassContainer(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = IOSShapes.largeRounded,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val tintColor = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.7f)
                    else Color.White.copy(alpha = 0.78f)

    Box(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape.toOutline(size, layoutDirection, this) },
                effects = {
                    vibrancy()
                    blur(28f.dp.toPx())
                    colorControls(saturation = 1.1f)
                },
                highlight = { Highlight.Default },
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
    ) {
        content()
    }
}

// ── Liquid Glass Button (Capsule) ─────────────────────────────────────────────
// Capsule/pill shaped glass — used for toolbar buttons, search bar.

@Composable
fun LiquidGlassButton(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val tintColor = if (isDark) Color(0xFF3A3A3C).copy(alpha = 0.6f)
                    else Color.White.copy(alpha = 0.65f)

    Box(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { Capsule().toOutline(size, layoutDirection, this) },
                effects = {
                    vibrancy()
                    blur(16f.dp.toPx())
                    lens(8f.dp.toPx(), 12f.dp.toPx())
                },
                highlight = { Highlight.Default },
                onDrawSurface = { drawRect(tintColor) }
            )
    ) {
        content()
    }
}

// ── Liquid Glass Toolbar ──────────────────────────────────────────────────────
// Horizontal bar at the bottom — vibrancy + heavy blur + no lens.

@Composable
fun LiquidGlassToolbar(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val tintColor = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.82f)
                    else Color(0xFFF2F2F7).copy(alpha = 0.82f)

    Row(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { RoundedCornerShape(0.dp).toOutline(size, layoutDirection, this) },
                effects = {
                    vibrancy()
                    blur(24f.dp.toPx())
                    colorControls(saturation = 1.05f)
                },
                onDrawSurface = {
                    drawRect(tintColor)
                    // Top hairline separator
                    drawLine(
                        color = if (isDark) Color.White.copy(0.12f) else Color.Black.copy(0.10f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 0.5.dp.toPx()
                    )
                }
            )
    ) {
        content()
    }
}

// ── Liquid Glass Sheet ────────────────────────────────────────────────────────
// Bottom sheet / modal — rounded top corners, heavy blur.

@Composable
fun LiquidGlassSheet(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetShape = remember {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    }
    val tintColor = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.88f)
                    else Color(0xFFF2F2F7).copy(alpha = 0.88f)

    Column(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { sheetShape.toOutline(size, layoutDirection, this) },
                effects = {
                    vibrancy()
                    blur(32f.dp.toPx())
                    colorControls(saturation = 1.08f)
                },
                highlight = {
                    Highlight.Default.copy(alpha = 0.5f)
                },
                onDrawSurface = { drawRect(tintColor) }
            )
    ) {
        content()
    }
}

// ── Frosted Card (fallback / solid variant) ───────────────────────────────────
// Used where no backdrop is available or for small cards that don't need full glass.

@Composable
fun FrostedCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = IOSShapes.largeRounded,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val bg = if (isDark) Color(0xFF2C2C2E) else Color.White.copy(alpha = 0.92f)
    Box(
        modifier = modifier
            .drawWithContent {
                drawRoundRect(bg, cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx()))
                drawContent()
            }
    ) {
        content()
    }
}

// ── Search Bar Glass ──────────────────────────────────────────────────────────
// Capsule search bar with real backdrop blur.

@Composable
fun GlassSearchBar(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val tintColor = if (isDark) Color(0xFF3A3A3C).copy(alpha = 0.7f)
                    else Color(0xFFE5E5EA).copy(alpha = 0.72f)

    Box(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { Capsule().toOutline(size, layoutDirection, this) },
                effects = {
                    vibrancy()
                    blur(20f.dp.toPx())
                    colorControls(saturation = 1.1f)
                },
                highlight = { Highlight.Default.copy(alpha = 0.6f) },
                onDrawSurface = { drawRect(tintColor) }
            )
    ) {
        content()
    }
}

// ── Context Menu Glass ────────────────────────────────────────────────────────
// Popup menu with vibrancy + lens distortion.

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
            .drawBackdrop(
                backdrop = backdrop,
                shape = { menuShape.toOutline(size, layoutDirection, this) },
                effects = {
                    vibrancy()
                    blur(24f.dp.toPx())
                    lens(6f.dp.toPx(), 10f.dp.toPx())
                    colorControls(saturation = 1.12f)
                },
                highlight = { Highlight.Default },
                shadow = { Shadow(alpha = 0.18f, radius = 12.dp) },
                onDrawSurface = { drawRect(tintColor) }
            )
    ) {
        content()
    }
}

// ── Tab Card Glass ────────────────────────────────────────────────────────────
// Individual tab preview card in tab switcher.

@Composable
fun GlassTabCard(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val cardShape = remember { RoundedCornerShape(14.dp) }
    val tintColor = if (isDark) Color(0xFF2C2C2E).copy(alpha = 0.85f)
                    else Color.White.copy(alpha = 0.85f)
    val activeColor = if (isDark) Color(0xFF0A84FF).copy(alpha = 0.25f)
                      else Color(0xFF007AFF).copy(alpha = 0.18f)

    Box(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { cardShape.toOutline(size, layoutDirection, this) },
                effects = {
                    vibrancy()
                    blur(14f.dp.toPx())
                    if (isActive) colorControls(brightness = 0.03f, saturation = 1.2f)
                },
                highlight = { if (isActive) Highlight.Default else Highlight.Default.copy(alpha = 0.4f) },
                onDrawSurface = {
                    drawRect(tintColor)
                    if (isActive) drawRect(activeColor)
                }
            )
    ) {
        content()
    }
}

// ── Shape Extension ───────────────────────────────────────────────────────────
// Convert Compose Shape → Outline for drawBackdrop shape lambda.

private fun androidx.compose.ui.graphics.Shape.toOutline(
    size: androidx.compose.ui.geometry.Size,
    layoutDirection: androidx.compose.ui.unit.LayoutDirection,
    density: androidx.compose.ui.unit.Density
): androidx.compose.ui.graphics.Outline {
    return createOutline(size, layoutDirection, density)
}
