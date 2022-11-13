package asher.greek.util

import javafx.beans.property.StringProperty
import javafx.event.EventTarget
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.FillRule
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.awt.BasicStroke
import java.awt.geom.Path2D
import java.awt.geom.PathIterator
import java.awt.geom.Point2D

typealias Point2 = Point2D.Double
typealias Polyline = List<Point2>

//region CONVERSIONS

fun Polyline.toAwtPath() = Path2D.Double().apply {
    forEach {
        if (it == first())
            moveTo(it.x, it.y)
        else
            lineTo(it.x, it.y)
    }
}
fun Polyline.toJfxPolyline() = javafx.scene.shape.Polyline(*flatMap { listOf(it.x, it.y) }.toDoubleArray())

/** Generates outline of the shape, as an AWT shape. */
fun java.awt.Shape.createAwtOutline(width: Number) =
    BasicStroke(width.toFloat()).createStrokedShape(this)

/** Generates outline of the shape, as a JFX shape. */
fun java.awt.Shape.createJfxOutline(width: Number) =
    createAwtOutline(width).toJfxPath()

/** Converts AWT Shape to JFX Path */
fun java.awt.Shape.toJfxPath() = Path().apply {
    val pi = getPathIterator(null)
    fillRule = when (pi.windingRule) {
        PathIterator.WIND_EVEN_ODD -> FillRule.EVEN_ODD
        PathIterator.WIND_NON_ZERO -> FillRule.NON_ZERO
        else -> throw IllegalStateException()
    }
    val coords = FloatArray(6) { 0f }
    while (!pi.isDone) {
        when (pi.currentSegment(coords)) {
            PathIterator.SEG_MOVETO -> moveTo(coords[0], coords[1])
            PathIterator.SEG_LINETO -> lineTo(coords[0], coords[1])
            PathIterator.SEG_QUADTO -> quadcurveTo(coords[0], coords[1], coords[2], coords[3])
            PathIterator.SEG_CUBICTO -> cubiccurveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5])
            PathIterator.SEG_CLOSE -> closepath()
        }
        pi.next()
    }
}

//endregion

//region FACTORIES

fun point(x: Number, y: Number) = Point2(x.toDouble(), y.toDouble())

fun bounds(x: Number, y: Number, width: Number, height: Number) =
    BoundingBox(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
fun rectangle(b: Bounds, fillColor: Color? = null, strokeColor: Color? = null) =
    Rectangle(b.minX, b.minY, b.width, b.height).apply {
        fillColor?.let { fill = it }
        strokeColor?.let { stroke = it }
    }
fun rectangle(x: Number, y: Number, width: Number, height: Number, fillColor: Color? = null, strokeColor: Color? = null) =
    Rectangle(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble()).apply {
        fillColor?.let { fill = it }
        strokeColor?.let { stroke = it }
    }
fun circle(center: Point2, radius: Double, fillColor: Color? = null) = Circle(center.x, center.y, radius).apply {
    fillColor?.let { fill = it }
}
fun squareByCenter(center: Point2, halfWidth: Double, fillColor: Color? = null) = Rectangle(center.x - halfWidth, center.y - halfWidth, 2*halfWidth, 2*halfWidth).apply {
    fillColor?.let { fill = it }
}
fun Rectangle.recenter(x: Number, y: Number) {
    this.x = x.toDouble() - .5 * width
    this.y = y.toDouble() - .5 * height
}
fun Circle.recenter(x: Number, y: Number) {
    this.centerX = x.toDouble()
    this.centerY = y.toDouble()
}
fun text(loc: Point2, text: String, fontSize: Double? = null, fillColor: Color? = null) = Text(loc.x, loc.y, text).apply {
    fillColor?.let { fill = it }
    fontSize?.let { font = Font.font(it) }
}

//endregion

//region BUILDERS

fun EventTarget.text(prop: StringProperty, xx: Number, yy: Number,
                     f: Font? = null, fc: Color? = null, op:Text.() -> Unit = {}) = text(prop) {
    x = xx.toDouble()
    y = yy.toDouble()
    fc?.let { fill = it }
    f?.let { font = it }
    op()
}

fun EventTarget.righttext(prop: StringProperty, xx: Number, yy: Number, width: Number,
                     f: Font? = null, fc: Color? = null, op:Text.() -> Unit = {}) = text(prop) {
    x = xx.toDouble() - width.toDouble()
    y = yy.toDouble()
    textAlignment = TextAlignment.RIGHT
    wrappingWidth = width.toDouble()
    fc?.let { fill = it }
    f?.let { font = it }
    op()
}

//endregion

//region XF

fun Point2.copy() = Point2(x, y)

operator fun Point2.plus(p: Point2) = Point2(x + p.x, y + p.y)
operator fun Point2.minus(p: Point2) = Point2(x - p.x, y - p.y)
operator fun Point2.times(a: Number) = Point2(x * a.toDouble(), y * a.toDouble())
operator fun Point2.div(a: Number) = Point2(x / a.toDouble(), y / a.toDouble())

val Point2.length
    get() = distance(0.0, 0.0)
val Point2.magnitude
    get() = distance(0.0, 0.0)

/** Get point on segment from p1 to p2, a given distance from p1. */
fun pointOnSegment(p1: Point2, p2: Point2, dist: Double) = p1 + (p2 - p1) * dist / (p2 - p1).length

val Polyline.length
    get() = (1..size).sumOf { get(it).distance(get(it - 1)) }

/** Gets point by absolute distance from start. */
fun Polyline.pointAt(pos: Double): Point2 {
    if (pos <= 0) {
        return first()
    }
    var dist = 0.0
    for (i in 1 until size) {
        val segmentDist = get(i).distance(get(i - 1))
        if (dist + segmentDist > pos) {
            return get(i - 1) + (get(i) - get(i - 1)) * ((pos - dist) / segmentDist)
        }
        dist += segmentDist
    }
    return last()
}

operator fun Node.contains(p: Point2) = contains(javafx.geometry.Point2D(p.x, p.y))

var Circle.center: Point2
    get() = Point2(centerX, centerY)
    set(value) { centerX = value.x;
/*-
 * #%L
 * greek-clash-td
 * --
 * Copyright (C) 2020 - 2022 Elisha Peterson and Asher Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
 centerY = value.y }

var Text.anchor: Point2
    get() = Point2(x, y)
    set(value) { x = value.x; y = value.y }

//endregion
