package com.example.safari.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.safari.model.HistoryItem
import com.example.safari.ui.components.CupertinoIcon
import com.example.safari.ui.components.CupertinoIcons
import com.example.safari.ui.components.glass.FrostedCard
import com.example.safari.ui.components.glass.GlassContextMenu
import com.example.safari.ui.theme.*
import com.kyant.backdrop.Backdrop
import java.text.SimpleDateFormat
import java.util.*

// ── History Screen ────────────────────────────────────────────────────────────
// Matches Image 16 — grouped by time-of-day, "···" menu with Select/Clear

@Composable
fun HistoryScreen(
    backdrop: Backdrop,
    history: List<HistoryItem>,
    onClose: () -> Unit,
    onItemClick: (HistoryItem) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val grouped = remember(history) { groupHistoryByTime(history) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IOSColors.systemBackground)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(IOSColors.systemBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // ··· menu button (top left)
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E5EA))
                    .clickable { showMenu = true }
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center
            ) {
                CupertinoIcon(CupertinoIcons.Ellipsis, tint = IOSColors.label, size = 14.dp)
            }

            // X close (top right)
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E5EA))
                    .clickable(onClick = onClose)
                    .align(Alignment.CenterEnd),
                contentAlignment = Alignment.Center
            ) {
                CupertinoIcon(CupertinoIcons.XMark, tint = IOSColors.label, size = 12.dp)
            }
        }

        // "Recently Saved >" header link
        Row(
            modifier = Modifier
                .clickable {}
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BasicText(
                "Recently Saved",
                style = IOSTypography.headline.copy(color = IOSColors.secondaryLabel)
            )
            CupertinoIcon(
                CupertinoIcons.ArrowRight,
                tint = IOSColors.secondaryLabel,
                size = 12.dp
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── History list ──────────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            grouped.forEach { (groupLabel, items) ->
                item {
                    HistoryGroupHeader(label = groupLabel)
                }
                item {
                    FrostedCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 14.dp
                    ) {
                        Column {
                            items.forEachIndexed { i, histItem ->
                                HistoryItemRow(
                                    item = histItem,
                                    onClick = { onItemClick(histItem) }
                                )
                                if (i < items.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 52.dp)
                                            .height(0.5.dp)
                                            .background(IOSColors.separator)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (history.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            "No History",
                            style = IOSTypography.body.copy(color = IOSColors.secondaryLabel)
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }

    // ── Context menu popup ────────────────────────────────────────────────────
    if (showMenu) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showMenu = false }
        ) {
            GlassContextMenu(
                backdrop = backdrop,
                isDark = false,
                modifier = Modifier
                    .padding(top = 52.dp, start = 12.dp)
                    .width(200.dp)
            ) {
                // Select Websites
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMenu = false }
                        .padding(horizontal = 14.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CupertinoIcon(CupertinoIcons.CheckCircle, tint = IOSColors.label, size = 20.dp)
                    BasicText("Select Websites", style = IOSTypography.body.copy(color = IOSColors.label))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(IOSColors.separator)
                )
                // Clear — red destructive
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClearHistory(); showMenu = false }
                        .padding(horizontal = 14.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CupertinoIcon(CupertinoIcons.Clock, tint = IOSColors.systemRed, size = 20.dp)
                    BasicText(
                        "Clear",
                        style = IOSTypography.body.copy(color = IOSColors.systemRed)
                    )
                }
            }
        }
    }
}

// ── History Group Header ──────────────────────────────────────────────────────

@Composable
private fun HistoryGroupHeader(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(
            label,
            style = IOSTypography.headline.copy(color = IOSColors.label)
        )
        // Collapse chevron
        CupertinoIcon(
            CupertinoIcons.ArrowRight,
            tint = IOSColors.iosBlue,
            size = 14.dp
        )
    }
}

// ── History Item Row ──────────────────────────────────────────────────────────

@Composable
private fun HistoryItemRow(item: HistoryItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Favicon
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF0F0F5)),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                item.title.firstOrNull()?.toString() ?: "?",
                style = IOSTypography.footnote.copy(color = IOSColors.secondaryLabel)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                item.title,
                style = IOSTypography.callout.copy(color = IOSColors.label),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            BasicText(
                item.url,
                style = IOSTypography.caption1.copy(color = IOSColors.secondaryLabel),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ── Group history by time-of-day label ───────────────────────────────────────

private fun groupHistoryByTime(history: List<HistoryItem>): List<Pair<String, List<HistoryItem>>> {
    if (history.isEmpty()) return emptyList()
    val now = System.currentTimeMillis()
    val cal = Calendar.getInstance()

    fun label(ts: Long): String {
        val diff = now - ts
        val diffH = diff / 3_600_000
        val diffD = diff / 86_400_000
        return when {
            diffH < 1 -> "Just Now"
            diffH < 6 -> {
                cal.timeInMillis = ts
                val h = cal.get(Calendar.HOUR_OF_DAY)
                when {
                    h >= 18 -> "This Evening"
                    h >= 12 -> "This Afternoon"
                    else -> "This Morning"
                }
            }
            diffD < 1 -> "Earlier Today"
            diffD < 2 -> "Yesterday"
            diffD < 7 -> "Earlier This Week"
            else -> SimpleDateFormat("MMMM d", Locale.getDefault()).format(Date(ts))
        }
    }

    return history
        .groupBy { label(it.visitedAt) }
        .entries
        .toList()
        .map { it.key to it.value }
}
