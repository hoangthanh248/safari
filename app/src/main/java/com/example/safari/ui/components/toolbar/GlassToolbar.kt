package com.example.safari.ui.components.toolbar

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.safari.ui.components.CupertinoIcon
import com.example.safari.ui.components.CupertinoIcons
import com.example.safari.ui.components.glass.*
import com.example.safari.ui.theme.*
import com.kyant.backdrop.Backdrop

// ── Start Page Toolbar ────────────────────────────────────────────────────────

@Composable
fun SafariGlassToolbar(
    backdrop: Backdrop,
    currentUrl: String,
    displayUrl: String,
    isLoading: Boolean,
    canGoBack: Boolean,
    isPrivateMode: Boolean,
    progress: Float,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    onSearchFocus: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }
    var isFocused  by remember { mutableStateOf(false) }
    val iconColor  = if (isPrivateMode) IOSColors.labelDark else IOSColors.label

    LaunchedEffect(displayUrl) {
        if (!isFocused) searchText = displayUrl
    }

    // FIX: LiquidGlassToolbar now takes BoxScope → put Row inside
    LiquidGlassToolbar(
        backdrop = backdrop,
        isDark   = isPrivateMode,
        modifier = modifier
    ) {
        // Progress bar
        if (isLoading && progress in 0.01f..0.99f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(2.dp)
                    .background(IOSColors.iosBlue)
                    .align(Alignment.TopStart)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = IOSSpacing.sm, vertical = IOSSpacing.sm),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(IOSSpacing.sm)
        ) {
            GlassIconButton(
                icon    = CupertinoIcons.ArrowLeft,
                tint    = if (canGoBack) iconColor else iconColor.copy(0.3f),
                backdrop = backdrop,
                isDark  = isPrivateMode,
                onClick = { if (canGoBack) onBack() }
            )
            GlassSearchField(
                backdrop      = backdrop,
                text          = searchText,
                isFocused     = isFocused,
                displayUrl    = displayUrl,
                isPrivateMode = isPrivateMode,
                modifier      = Modifier.weight(1f),
                onTextChange  = { searchText = it },
                onFocusChange = { focused ->
                    isFocused = focused
                    if (focused) { searchText = currentUrl; onSearchFocus() }
                },
                onSubmit = { onSearch(searchText); isFocused = false }
            )
            GlassIconButton(
                icon    = CupertinoIcons.Ellipsis,
                tint    = iconColor,
                backdrop = backdrop,
                isDark  = isPrivateMode,
                onClick = onMore
            )
        }
    }
}

// ── Browsing Toolbar (page loaded) ────────────────────────────────────────────

@Composable
fun BrowsingGlassToolbar(
    backdrop: Backdrop,
    displayUrl: String,
    isLoading: Boolean,
    canGoBack: Boolean,
    canGoForward: Boolean,
    isPrivateMode: Boolean,
    progress: Float,
    tabCount: Int,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onUrlClick: () -> Unit,
    onTabs: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = if (isPrivateMode) IOSColors.labelDark else IOSColors.label

    LiquidGlassToolbar(
        backdrop = backdrop,
        isDark   = isPrivateMode,
        modifier = modifier
    ) {
        // Progress bar
        if (isLoading && progress in 0.01f..0.99f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(2.dp)
                    .background(IOSColors.iosBlue)
                    .align(Alignment.TopStart)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassIconButton(
                icon    = CupertinoIcons.ArrowLeft,
                tint    = if (canGoBack) iconColor else iconColor.copy(0.3f),
                backdrop = backdrop,
                isDark  = isPrivateMode,
                onClick = { if (canGoBack) onBack() }
            )
            GlassAddressPill(
                backdrop      = backdrop,
                displayUrl    = displayUrl,
                isLoading     = isLoading,
                isPrivateMode = isPrivateMode,
                modifier      = Modifier.weight(1f),
                onClick       = onUrlClick
            )
            GlassTabCountButton(
                count    = tabCount,
                backdrop = backdrop,
                isPrivate = isPrivateMode,
                onClick  = onTabs
            )
            GlassIconButton(
                icon    = CupertinoIcons.Ellipsis,
                tint    = iconColor,
                backdrop = backdrop,
                isDark  = isPrivateMode,
                onClick = onMore
            )
        }
    }
}

// ── Glass Icon Button ─────────────────────────────────────────────────────────

@Composable
fun GlassIconButton(
    icon: CupertinoIcons,
    tint: Color,
    backdrop: Backdrop,
    isDark: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue    = if (isPressed) 0.87f else 1f,
        animationSpec  = spring(stiffness = Spring.StiffnessHigh),
        label          = "icon_scale"
    )
    LiquidGlassButton(
        backdrop = backdrop,
        isDark   = isDark,
        modifier = modifier
            .size(36.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick
            )
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CupertinoIcon(icon = icon, tint = tint, size = 16.dp, strokeWidth = 2f)
        }
    }
}

// ── Glass Search Field ────────────────────────────────────────────────────────

@Composable
private fun GlassSearchField(
    backdrop: Backdrop,
    text: String,
    isFocused: Boolean,
    displayUrl: String,
    isPrivateMode: Boolean,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onSubmit: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val textColor  = if (isPrivateMode) IOSColors.labelDark else IOSColors.label
    val hintColor  = if (isPrivateMode) IOSColors.secondaryLabelDark else IOSColors.secondaryLabel

    GlassSearchBar(
        backdrop = backdrop,
        isDark   = isPrivateMode,
        modifier = modifier
            .height(36.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { if (!isFocused) focusRequester.requestFocus() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (!isFocused) {
                CupertinoIcon(CupertinoIcons.Search, tint = hintColor, size = 14.dp, strokeWidth = 2f)
            }
            if (isFocused) {
                BasicTextField(
                    value          = text,
                    onValueChange  = onTextChange,
                    modifier       = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusChanged { onFocusChange(it.isFocused) },
                    singleLine     = true,
                    textStyle      = IOSTypography.subheadline.copy(color = textColor),
                    cursorBrush    = SolidColor(IOSColors.iosBlue),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(onGo = { onSubmit() })
                )
            } else {
                BasicText(
                    text     = displayUrl.ifEmpty { "Search or enter website name" },
                    modifier = Modifier.weight(1f),
                    style    = IOSTypography.subheadline.copy(
                        color     = if (displayUrl.isEmpty()) hintColor else textColor,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                CupertinoIcon(CupertinoIcons.Microphone, tint = hintColor, size = 15.dp)
            }
        }
    }
}

// ── Glass Address Pill ────────────────────────────────────────────────────────

@Composable
private fun GlassAddressPill(
    backdrop: Backdrop,
    displayUrl: String,
    isLoading: Boolean,
    isPrivateMode: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val textColor = if (isPrivateMode) IOSColors.labelDark else IOSColors.label
    val hintColor = if (isPrivateMode) IOSColors.secondaryLabelDark else IOSColors.secondaryLabel

    GlassSearchBar(
        backdrop = backdrop,
        isDark   = isPrivateMode,
        modifier = modifier
            .height(36.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (!isLoading) {
                CupertinoIcon(CupertinoIcons.Lock, tint = hintColor, size = 11.dp, strokeWidth = 1.8f)
                Spacer(Modifier.width(4.dp))
            }
            BasicText(
                text     = displayUrl.ifEmpty { "Search or enter website name" },
                style    = IOSTypography.subheadline.copy(
                    color     = if (displayUrl.isEmpty()) hintColor else textColor,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ── Glass Tab Count Button ────────────────────────────────────────────────────

@Composable
private fun GlassTabCountButton(
    count: Int,
    backdrop: Backdrop,
    isPrivate: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.87f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label         = "tab_scale"
    )
    LiquidGlassButton(
        backdrop = backdrop,
        isDark   = isPrivate,
        modifier = Modifier
            .size(36.dp)
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            BasicText(
                text  = count.coerceAtLeast(1).toString(),
                style = IOSTypography.footnote.copy(
                    color     = if (isPrivate) IOSColors.labelDark else IOSColors.label,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
