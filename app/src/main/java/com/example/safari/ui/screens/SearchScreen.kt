package com.example.safari.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.BasicText
import com.example.safari.model.HistoryItem
import com.example.safari.ui.components.*
import com.example.safari.ui.theme.*

// ── Search Screen ─────────────────────────────────────────────────────────────

@Composable
fun SearchScreen(
    initialText: String = "",
    recentHistory: List<HistoryItem>,
    isPrivateMode: Boolean,
    onSearch: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf(initialText) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val bg = if (isPrivateMode) IOSColors.privateBackground else IOSColors.systemBackground
    val textColor = if (isPrivateMode) IOSColors.labelDark else IOSColors.label

    Box(modifier = modifier.fillMaxSize().background(bg)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Search categories
            SearchCategoryRow(isPrivateMode = isPrivateMode)

            Spacer(Modifier.height(IOSSpacing.lg))

            // Suggestions text
            BasicText(
                text = "Safari search now shows personalized suggestions from the web, the iTunes Store, the App Store, movie showtimes, nearby locations, and more.",
                modifier = Modifier.padding(horizontal = IOSSpacing.xl),
                style = IOSTypography.subheadline.copy(color = textColor, androidx.compose.ui.text.style.TextAlign.Center)
            )

            Spacer(Modifier.height(IOSSpacing.sm))

            BasicText(
                text = "You can adjust this in Settings.",
                modifier = Modifier.padding(horizontal = IOSSpacing.xl),
                style = IOSTypography.subheadline.copy(
                    color = textColor,
                    androidx.compose.ui.text.style.TextAlign.Center
                )
            )

            // Continue button
            Box(
                modifier = Modifier
                    .padding(horizontal = IOSSpacing.xl, vertical = IOSSpacing.lg)
                    .clip(RoundedCornerShape(50.dp))
                    .background(IOSColors.iosBlue)
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 32.dp, vertical = 12.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                BasicText("Continue", style = IOSTypography.callout.copy(color = Color.White))
            }

            // Recent searches
            if (recentHistory.isNotEmpty() && !isPrivateMode) {
                Spacer(Modifier.height(IOSSpacing.xl))
                BasicText(
                    "Recent",
                    modifier = Modifier.padding(horizontal = IOSSpacing.md),
                    style = IOSTypography.subheadline.copy(color = IOSColors.secondaryLabel)
                )
                Spacer(Modifier.height(IOSSpacing.sm))
                recentHistory.take(5).forEach { item ->
                    HistoryRow(item = item, onClick = { onSearch(item.url) })
                }
            }

            Spacer(Modifier.weight(1f))

            // Bottom search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = IOSSpacing.md, vertical = IOSSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(IOSSpacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isPrivateMode) Color(0xFF3A3A3C) else Color(0xFFE5E5EA))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BasicTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier.weight(1f).focusRequester(focusRequester),
                            singleLine = true,
                            textStyle = IOSTypography.body.copy(color = textColor),
                            cursorBrush = SolidColor(IOSColors.iosBlue),
                            decorationBox = { inner ->
                                if (searchText.isEmpty()) {
                                    BasicText("Search or enter website name", style = IOSTypography.body.copy(color = IOSColors.secondaryLabel))
                                }
                                inner()
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(onGo = { onSearch(searchText) })
                        )
                        CupertinoIcon(CupertinoIcons.Microphone, tint = IOSColors.secondaryLabel, size = 16.dp)
                    }
                }

                // X button
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                        .background(if (isPrivateMode) Color(0xFF3A3A3C) else Color(0xFFE5E5EA))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    CupertinoIcon(CupertinoIcons.XMark, tint = textColor, size = 14.dp)
                }
            }
        }
    }
}

// ── Search Category Row ───────────────────────────────────────────────────────

@Composable
private fun SearchCategoryRow(isPrivateMode: Boolean) {
    val categories = listOf(
        CupertinoIcons.Globe to "Web",
        CupertinoIcons.Search to "Music",     // approximation
        CupertinoIcons.Globe to "App Store",   // approximation
        CupertinoIcons.Globe to "Movies",      // approximation
        CupertinoIcons.Globe to "Food",        // approximation
        CupertinoIcons.Globe to "Travel"       // approximation
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = IOSSpacing.lg, vertical = IOSSpacing.lg),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        categories.take(6).forEach { (icon, _) ->
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isPrivateMode) Color(0xFF3A3A3C) else Color(0xFFE5E5EA)),
                contentAlignment = Alignment.Center
            ) {
                CupertinoIcon(icon, tint = IOSColors.secondaryLabel, size = 20.dp)
            }
        }
    }
}

// ── History Row ───────────────────────────────────────────────────────────────

@Composable
private fun HistoryRow(item: HistoryItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = IOSSpacing.md, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(CircleShape).background(Color(0xFFE5E5EA)),
            contentAlignment = Alignment.Center
        ) {
            CupertinoIcon(CupertinoIcons.Clock, tint = IOSColors.secondaryLabel, size = 14.dp)
        }
        Column(modifier = Modifier.weight(1f)) {
            BasicText(item.title, style = IOSTypography.callout.copy(color = IOSColors.label), maxLines = 1, overflow = TextOverflow.Ellipsis)
            BasicText(item.url, style = IOSTypography.caption1.copy(color = IOSColors.secondaryLabel), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
