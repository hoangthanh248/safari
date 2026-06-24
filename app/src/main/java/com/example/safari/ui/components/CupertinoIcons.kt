package com.example.safari.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── Cupertino Icon Composable ─────────────────────────────────────────────────

@Composable
fun CupertinoIcon(
    icon: CupertinoIcons,
    tint: Color = Color.Black,
    size: Dp = 22.dp,
    strokeWidth: Float = 2f
) {
    Canvas(modifier = Modifier.size(size)) {
        when (icon) {
            CupertinoIcons.ArrowLeft -> drawArrowLeft(tint, strokeWidth)
            CupertinoIcons.ArrowRight -> drawArrowRight(tint, strokeWidth)
            CupertinoIcons.Search -> drawSearch(tint, strokeWidth)
            CupertinoIcons.Plus -> drawPlus(tint, strokeWidth)
            CupertinoIcons.Ellipsis -> drawEllipsis(tint)
            CupertinoIcons.Shield -> drawShield(tint, strokeWidth)
            CupertinoIcons.Bookmark -> drawBookmark(tint, strokeWidth)
            CupertinoIcons.Hand -> drawHand(tint, strokeWidth)
            CupertinoIcons.XMark -> drawXMark(tint, strokeWidth)
            CupertinoIcons.Checkmark -> drawCheckmark(tint, strokeWidth)
            CupertinoIcons.Star -> drawStar(tint, strokeWidth)
            CupertinoIcons.Globe -> drawGlobe(tint, strokeWidth)
            CupertinoIcons.Tabs -> drawTabs(tint, strokeWidth)
            CupertinoIcons.Microphone -> drawMicrophone(tint, strokeWidth)
            CupertinoIcons.Lock -> drawLock(tint, strokeWidth)
            CupertinoIcons.Reload -> drawReload(tint, strokeWidth)
            CupertinoIcons.Share -> drawShare(tint, strokeWidth)
            CupertinoIcons.List -> drawList(tint, strokeWidth)
            CupertinoIcons.Clock -> drawClock(tint, strokeWidth)
            CupertinoIcons.Glasses -> drawGlasses(tint, strokeWidth)
            CupertinoIcons.Person -> drawPerson(tint, strokeWidth)
            CupertinoIcons.SortUpDown -> drawSortUpDown(tint, strokeWidth)
            CupertinoIcons.CheckCircle -> drawCheckCircle(tint, strokeWidth)
        }
    }
}

enum class CupertinoIcons {
    ArrowLeft, ArrowRight, Search, Plus, Ellipsis,
    Shield, Bookmark, Hand, XMark, Checkmark,
    Star, Globe, Tabs, Microphone, Lock,
    Reload, Share, List, Clock, Glasses,
    Person, SortUpDown, CheckCircle
}

// ── Icon Drawers ──────────────────────────────────────────────────────────────

private fun DrawScope.drawArrowLeft(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val cx = size.width / 2f
    val cy = size.height / 2f
    val path = Path().apply {
        moveTo(cx + size.width * 0.3f, cy - size.height * 0.3f)
        lineTo(cx - size.width * 0.2f, cy)
        lineTo(cx + size.width * 0.3f, cy + size.height * 0.3f)
    }
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawArrowRight(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val cx = size.width / 2f
    val cy = size.height / 2f
    val path = Path().apply {
        moveTo(cx - size.width * 0.3f, cy - size.height * 0.3f)
        lineTo(cx + size.width * 0.2f, cy)
        lineTo(cx - size.width * 0.3f, cy + size.height * 0.3f)
    }
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawSearch(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round)
    val r = size.minDimension * 0.32f
    val cx = size.width * 0.4f
    val cy = size.height * 0.4f
    drawCircle(color = color, radius = r, center = Offset(cx, cy), style = stroke)
    val lineStart = Offset(cx + r * 0.7f, cy + r * 0.7f)
    val lineEnd = Offset(size.width * 0.85f, size.height * 0.85f)
    drawLine(color, lineStart, lineEnd, sw, StrokeCap.Round)
}

private fun DrawScope.drawPlus(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round)
    val cx = size.width / 2f
    val cy = size.height / 2f
    val arm = size.minDimension * 0.35f
    drawLine(color, Offset(cx - arm, cy), Offset(cx + arm, cy), sw, StrokeCap.Round)
    drawLine(color, Offset(cx, cy - arm), Offset(cx, cy + arm), sw, StrokeCap.Round)
}

private fun DrawScope.drawEllipsis(color: Color) {
    val r = size.minDimension * 0.09f
    val cy = size.height / 2f
    val positions = listOf(
        size.width * 0.2f,
        size.width * 0.5f,
        size.width * 0.8f
    )
    positions.forEach { x ->
        drawCircle(color = color, radius = r, center = Offset(x, cy))
    }
}

private fun DrawScope.drawShield(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.5f, h * 0.06f)
        lineTo(w * 0.9f, h * 0.22f)
        lineTo(w * 0.9f, h * 0.5f)
        cubicTo(w * 0.9f, h * 0.75f, w * 0.72f, h * 0.88f, w * 0.5f, h * 0.96f)
        cubicTo(w * 0.28f, h * 0.88f, w * 0.1f, h * 0.75f, w * 0.1f, h * 0.5f)
        lineTo(w * 0.1f, h * 0.22f)
        close()
    }
    drawPath(path, color, style = stroke)
    // Checkmark inside
    val checkPath = Path().apply {
        moveTo(w * 0.32f, h * 0.54f)
        lineTo(w * 0.46f, h * 0.67f)
        lineTo(w * 0.68f, h * 0.42f)
    }
    drawPath(checkPath, color, style = Stroke(width = sw * 0.85f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawBookmark(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.2f, h * 0.05f)
        lineTo(w * 0.8f, h * 0.05f)
        lineTo(w * 0.8f, h * 0.92f)
        lineTo(w * 0.5f, h * 0.7f)
        lineTo(w * 0.2f, h * 0.92f)
        close()
    }
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawHand(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val w = size.width
    val h = size.height
    // Palm
    val palmPath = Path().apply {
        moveTo(w * 0.5f, h * 0.85f)
        lineTo(w * 0.5f, h * 0.3f)
        cubicTo(w * 0.5f, h * 0.2f, w * 0.65f, h * 0.2f, w * 0.65f, h * 0.3f)
        lineTo(w * 0.65f, h * 0.55f)
        cubicTo(w * 0.65f, h * 0.45f, w * 0.8f, h * 0.45f, w * 0.8f, h * 0.55f)
        lineTo(w * 0.8f, h * 0.7f)
        cubicTo(w * 0.85f, h * 0.7f, w * 0.9f, h * 0.75f, w * 0.9f, h * 0.8f)
        lineTo(w * 0.9f, h * 0.85f)
        cubicTo(w * 0.9f, h * 0.93f, w * 0.84f, h * 0.97f, w * 0.76f, h * 0.97f)
        lineTo(w * 0.24f, h * 0.97f)
        cubicTo(w * 0.16f, h * 0.97f, w * 0.1f, h * 0.91f, w * 0.1f, h * 0.83f)
        lineTo(w * 0.1f, h * 0.55f)
        cubicTo(w * 0.1f, h * 0.45f, w * 0.25f, h * 0.44f, w * 0.25f, h * 0.55f)
        lineTo(w * 0.25f, h * 0.3f)
        cubicTo(w * 0.25f, h * 0.2f, w * 0.38f, h * 0.2f, w * 0.38f, h * 0.3f)
        lineTo(w * 0.38f, h * 0.05f)
        cubicTo(w * 0.38f, h * -0.04f, w * 0.5f, h * -0.04f, w * 0.5f, h * 0.05f)
        close()
    }
    drawPath(palmPath, color, style = stroke)
}

private fun DrawScope.drawXMark(color: Color, sw: Float) {
    val pad = size.minDimension * 0.2f
    drawLine(color, Offset(pad, pad), Offset(size.width - pad, size.height - pad), sw, StrokeCap.Round)
    drawLine(color, Offset(size.width - pad, pad), Offset(pad, size.height - pad), sw, StrokeCap.Round)
}

private fun DrawScope.drawCheckmark(color: Color, sw: Float) {
    val path = Path().apply {
        moveTo(size.width * 0.15f, size.height * 0.5f)
        lineTo(size.width * 0.42f, size.height * 0.75f)
        lineTo(size.width * 0.85f, size.height * 0.25f)
    }
    drawPath(path, color, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawStar(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val cx = size.width / 2f
    val cy = size.height / 2f
    val outerR = size.minDimension * 0.42f
    val innerR = size.minDimension * 0.18f
    val path = Path()
    for (i in 0 until 10) {
        val angle = Math.PI * i / 5.0 - Math.PI / 2.0
        val r = if (i % 2 == 0) outerR else innerR
        val x = (cx + r * Math.cos(angle)).toFloat()
        val y = (cy + r * Math.sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawGlobe(color: Color, sw: Float) {
    val stroke = Stroke(width = sw)
    val cx = size.width / 2f
    val cy = size.height / 2f
    val r = size.minDimension * 0.42f
    drawCircle(color, r, Offset(cx, cy), style = stroke)
    drawLine(color, Offset(cx, cy - r), Offset(cx, cy + r), sw)
    drawLine(color, Offset(cx - r, cy), Offset(cx + r, cy), sw)
    drawArc(
        color = color,
        startAngle = 0f, sweepAngle = 360f,
        useCenter = false,
        topLeft = Offset(cx - r * 0.5f, cy - r),
        size = Size(r, r * 2),
        style = stroke
    )
}

private fun DrawScope.drawTabs(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val w = size.width
    val h = size.height
    // Back rect
    val backPath = Path().apply {
        val r = w * 0.12f
        addRoundRect(RoundRect(
            Rect(w * 0.18f, h * 0.18f, w * 0.88f, h * 0.88f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r)
        ))
    }
    drawPath(backPath, color, style = stroke)
    // Front rect (offset)
    val frontPath = Path().apply {
        val r = w * 0.12f
        addRoundRect(RoundRect(
            Rect(w * 0.08f, h * 0.08f, w * 0.78f, h * 0.78f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r)
        ))
    }
    drawPath(frontPath, color, style = stroke)
}

private fun DrawScope.drawMicrophone(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val w = size.width
    val h = size.height
    // Body
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.32f, h * 0.05f),
        size = Size(w * 0.36f, h * 0.5f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.18f),
        style = stroke
    )
    // Arc
    val arcPath = Path().apply {
        moveTo(w * 0.12f, h * 0.5f)
        cubicTo(w * 0.12f, h * 0.8f, w * 0.88f, h * 0.8f, w * 0.88f, h * 0.5f)
    }
    drawPath(arcPath, color, style = stroke)
    drawLine(color, Offset(w * 0.5f, h * 0.8f), Offset(w * 0.5f, h * 0.95f), sw, StrokeCap.Round)
    drawLine(color, Offset(w * 0.3f, h * 0.95f), Offset(w * 0.7f, h * 0.95f), sw, StrokeCap.Round)
}

private fun DrawScope.drawLock(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.15f, h * 0.45f),
        size = Size(w * 0.7f, h * 0.5f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.1f),
        style = stroke
    )
    val arcPath = Path().apply {
        moveTo(w * 0.28f, h * 0.45f)
        lineTo(w * 0.28f, h * 0.32f)
        addArc(Rect(w * 0.22f, h * 0.12f, w * 0.78f, h * 0.45f), 180f, 180f)
        lineTo(w * 0.72f, h * 0.45f)
    }
    drawPath(arcPath, color, style = stroke)
}

private fun DrawScope.drawReload(color: Color, sw: Float) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val r = size.minDimension * 0.36f
    drawArc(
        color = color,
        startAngle = -60f, sweepAngle = 270f,
        useCenter = false,
        topLeft = Offset(cx - r, cy - r),
        size = Size(r * 2, r * 2),
        style = Stroke(width = sw, cap = StrokeCap.Round)
    )
    // Arrow head
    val arrowPath = Path().apply {
        moveTo(cx + r * 0.5f, cy - r * 0.7f)
        lineTo(cx + r * 0.85f, cy - r * 0.5f)
        lineTo(cx + r * 1.0f, cy - r * 0.9f)
    }
    drawPath(arrowPath, color, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawShare(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val w = size.width
    val h = size.height
    val arrowPath = Path().apply {
        moveTo(w * 0.5f, h * 0.6f)
        lineTo(w * 0.5f, h * 0.08f)
        moveTo(w * 0.25f, h * 0.33f)
        lineTo(w * 0.5f, h * 0.08f)
        lineTo(w * 0.75f, h * 0.33f)
    }
    drawPath(arrowPath, color, style = stroke)
    val boxPath = Path().apply {
        moveTo(w * 0.22f, h * 0.5f)
        lineTo(w * 0.12f, h * 0.5f)
        lineTo(w * 0.12f, h * 0.95f)
        lineTo(w * 0.88f, h * 0.95f)
        lineTo(w * 0.88f, h * 0.5f)
        lineTo(w * 0.78f, h * 0.5f)
    }
    drawPath(boxPath, color, style = stroke)
}

private fun DrawScope.drawList(color: Color, sw: Float) {
    val w = size.width
    val h = size.height
    val lines = listOf(0.25f, 0.5f, 0.75f)
    lines.forEach { y ->
        drawLine(color, Offset(w * 0.12f, h * y), Offset(w * 0.88f, h * y), sw, StrokeCap.Round)
    }
}

private fun DrawScope.drawClock(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round)
    val cx = size.width / 2f
    val cy = size.height / 2f
    val r = size.minDimension * 0.42f
    drawCircle(color, r, Offset(cx, cy), style = stroke)
    drawLine(color, Offset(cx, cy), Offset(cx, cy - r * 0.55f), sw, StrokeCap.Round)
    drawLine(color, Offset(cx, cy), Offset(cx + r * 0.35f, cy + r * 0.25f), sw, StrokeCap.Round)
}

private fun DrawScope.drawGlasses(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round)
    val w = size.width
    val h = size.height
    val r = w * 0.18f
    drawCircle(color, r, Offset(w * 0.3f, h * 0.5f), style = stroke)
    drawCircle(color, r, Offset(w * 0.7f, h * 0.5f), style = stroke)
    drawLine(color, Offset(w * 0.48f, h * 0.5f), Offset(w * 0.52f, h * 0.5f), sw, StrokeCap.Round)
    drawLine(color, Offset(w * 0.12f, h * 0.5f), Offset(w * 0.12f, h * 0.38f), sw, StrokeCap.Round)
    drawLine(color, Offset(w * 0.88f, h * 0.5f), Offset(w * 0.88f, h * 0.38f), sw, StrokeCap.Round)
}

private fun DrawScope.drawPerson(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val cx = size.width / 2f
    drawCircle(color, size.width * 0.22f, Offset(cx, size.height * 0.28f), style = stroke)
    val bodyPath = Path().apply {
        moveTo(size.width * 0.1f, size.height * 0.95f)
        cubicTo(
            size.width * 0.1f, size.height * 0.65f,
            size.width * 0.9f, size.height * 0.65f,
            size.width * 0.9f, size.height * 0.95f
        )
    }
    drawPath(bodyPath, color, style = stroke)
}

private fun DrawScope.drawSortUpDown(color: Color, sw: Float) {
    val w = size.width
    val h = size.height
    val upPath = Path().apply {
        moveTo(w * 0.3f, h * 0.45f)
        lineTo(w * 0.5f, h * 0.15f)
        lineTo(w * 0.7f, h * 0.45f)
    }
    val downPath = Path().apply {
        moveTo(w * 0.3f, h * 0.55f)
        lineTo(w * 0.5f, h * 0.85f)
        lineTo(w * 0.7f, h * 0.55f)
    }
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    drawPath(upPath, color, style = stroke)
    drawPath(downPath, color, style = stroke)
}

private fun DrawScope.drawCheckCircle(color: Color, sw: Float) {
    val stroke = Stroke(width = sw, cap = StrokeCap.Round)
    val cx = size.width / 2f
    val cy = size.height / 2f
    val r = size.minDimension * 0.42f
    drawCircle(color, r, Offset(cx, cy), style = stroke)
    val checkPath = Path().apply {
        moveTo(cx - r * 0.4f, cy)
        lineTo(cx - r * 0.1f, cy + r * 0.35f)
        lineTo(cx + r * 0.45f, cy - r * 0.3f)
    }
    drawPath(checkPath, color, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
}
