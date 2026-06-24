package com.example.safari.ui.components.toolbar

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.BasicText
import com.example.safari.ui.components.*
import com.example.safari.ui.theme.*

// ── Safari Toolbar ────────────────────────────────────────────────────────────

@Composable
fun SafariToolbar(
    currentUrl: String,
    displayUrl: String,
    isLoading: Boolean,
    canGoBack: Boolean,
    canGoForward: Boolean,
    isPrivateMode: Boolean,
    progress: Float,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onSearch: (String) -> Unit,
    onSearchFocus: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }

    val toolbarBg = if (isPrivateMode) IOSColors.privateBackground else IOSColors.systemBackground
    val textColor = if (isPrivateMode) IOSColors.labelDark else IOSColors.label
    val iconColor = if (isPrivateMode) IOSColors.labelDark else IOSColors.label

    // Update search text when url changes externally
    LaunchedEffect(displayUrl) {
        if (!isFocused) searchText = displayUrl
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(toolbarBg.copy(alpha = 0.92f))
    ) {
        // Progress bar
        if (isLoading && progress > 0f && progress < 1f) {
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(IOSSpacing.sm)
        ) {
            // ← Back button
            ToolbarIconButton(
                icon = CupertinoIcons.ArrowLeft,
                tint = if (canGoBack) iconColor else iconColor.copy(alpha = 0.3f),
                onClick = { if (canGoBack) onBack() }
            )

            // Search Bar
            SearchBarField(
                text = searchText,
                isFocused = isFocused,
                displayUrl = displayUrl,
                isPrivateMode = isPrivateMode,
                isLoading = isLoading,
                modifier = Modifier.weight(1f),
                onTextChange = { searchText = it },
                onFocusChange = { focused ->
                    isFocused = focused
                    if (focused) {
                        searchText = currentUrl
                        onSearchFocus()
                    }
                },
                onSubmit = {
                    onSearch(searchText)
                    isFocused = false
                }
            )

            // ··· More button
            ToolbarIconButton(
                icon = CupertinoIcons.Ellipsis,
                tint = iconColor,
                onClick = onMore
            )
        }
    }
}

// ── Search Bar ────────────────────────────────────────────────────────────────

@Composable
private fun SearchBarField(
    text: String,
    isFocused: Boolean,
    displayUrl: String,
    isPrivateMode: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onSubmit: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    val searchBg = if (isPrivateMode) {
        Color(0xFF3A3A3C)
    } else {
        Color(0xFFE5E5EA)
    }

    val textColor = if (isPrivateMode) IOSColors.labelDark else IOSColors.label
    val placeholderColor = if (isPrivateMode) IOSColors.secondaryLabelDark else IOSColors.secondaryLabel

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(searchBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isFocused) {
                    focusRequester.requestFocus()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (!isFocused) {
                CupertinoIcon(
                    icon = CupertinoIcons.Search,
                    tint = placeholderColor,
                    size = 14.dp,
                    strokeWidth = 2f
                )
            }

            if (isFocused) {
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusChanged { onFocusChange(it.isFocused) },
                    singleLine = true,
                    textStyle = IOSTypography.subheadline.copy(color = textColor),
                    cursorBrush = SolidColor(IOSColors.iosBlue),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onGo = { onSubmit() },
                        onSearch = { onSubmit() }
                    ),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Go
                    )
                )
            } else {
                // Display mode: show domain or placeholder
                if (displayUrl.isEmpty()) {
                    BasicText(
                        text = "Search or enter website name",
                        modifier = Modifier.weight(1f),
                        style = IOSTypography.subheadline.copy(
                            color = placeholderColor,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    BasicText(
                        text = displayUrl,
                        modifier = Modifier.weight(1f),
                        style = IOSTypography.subheadline.copy(
                            color = textColor,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Microphone
                CupertinoIcon(
                    icon = CupertinoIcons.Microphone,
                    tint = placeholderColor,
                    size = 16.dp,
                    strokeWidth = 1.8f
                )
            }
        }
    }
}

// ── Toolbar Icon Button ───────────────────────────────────────────────────────

@Composable
fun ToolbarIconButton(
    icon: CupertinoIcons,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "icon_scale"
    )

    val bgColor = if (isDark) Color(0xFF3A3A3C) else Color(0xFFE5E5EA)

    Box(
        modifier = modifier
            .size(36.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        CupertinoIcon(icon = icon, tint = tint, size = 16.dp, strokeWidth = 2f)
    }
}

// ── Browsing Toolbar (shown when page is loaded) ──────────────────────────────

@Composable
fun BrowsingToolbar(
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
    val bg = if (isPrivateMode) Color(0xE51C1C1E) else Color(0xE5F2F2F7)
    val iconColor = if (isPrivateMode) IOSColors.labelDark else IOSColors.label

    Box(modifier = modifier.fillMaxWidth().background(bg)) {
        // Progress
        if (isLoading && progress in 0.01f..0.99f) {
            Box(
                Modifier
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Back
            ToolbarIconButton(
                icon = CupertinoIcons.ArrowLeft,
                tint = if (canGoBack) iconColor else iconColor.copy(alpha = 0.3f),
                onClick = { if (canGoBack) onBack() },
                isDark = isPrivateMode
            )

            // Address pill (center)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isPrivateMode) Color(0xFF3A3A3C) else Color(0xFFE5E5EA))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onUrlClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (!isLoading) {
                        CupertinoIcon(
                            CupertinoIcons.Lock,
                            tint = if (isPrivateMode) IOSColors.secondaryLabelDark else IOSColors.secondaryLabel,
                            size = 11.dp, strokeWidth = 1.8f
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    BasicText(
                        text = displayUrl.ifEmpty { "Search or enter website name" },
                        style = IOSTypography.subheadline.copy(
                            color = if (isPrivateMode) IOSColors.labelDark else IOSColors.label,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Tabs
            TabCountButton(
                count = tabCount,
                isPrivate = isPrivateMode,
                onClick = onTabs
            )

            // More
            ToolbarIconButton(
                icon = CupertinoIcons.Ellipsis,
                tint = iconColor,
                onClick = onMore,
                isDark = isPrivateMode
            )
        }
    }
}

// ── Tab Count Button ──────────────────────────────────────────────────────────

@Composable
fun TabCountButton(
    count: Int,
    isPrivate: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "tab_btn_scale"
    )

    Box(
        modifier = Modifier
            .size(36.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(if (isPrivate) Color(0xFF3A3A3C) else Color(0xFFE5E5EA))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = count.toString(),
            style = IOSTypography.footnote.copy(
                color = if (isPrivate) IOSColors.labelDark else IOSColors.label,
                textAlign = TextAlign.Center
            )
        )
    }
}
