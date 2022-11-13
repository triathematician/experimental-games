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
package asher.greek.gfx

import asher.greek.components.*
import asher.greek.util.*
import com.sun.javafx.geom.Area
import com.sun.javafx.tk.Toolkit
import javafx.animation.FadeTransition
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.paint.Color.*
import javafx.scene.shape.*
import javafx.scene.text.Text
import javafx.util.Duration
import tornadofx.doubleBinding
import java.awt.BasicStroke
import java.awt.geom.Rectangle2D

/** Creates visual elements for display. */
object GraphicsFactory {

    val LEVEL_BOUNDS = bounds(0, 0, 400, 400)
    val VIEW_BOUNDS = bounds(LEVEL_BOUNDS.minX - 50, LEVEL_BOUNDS.minY - 120, LEVEL_BOUNDS.width + 100, LEVEL_BOUNDS.height + 170)

    val PALETTE_BOUNDS = bounds(LEVEL_BOUNDS.minX - 50, LEVEL_BOUNDS.minY - 120, LEVEL_BOUNDS.width + 100, 70)
    val PLAY_AREA_BOUNDS = bounds(LEVEL_BOUNDS.minX - 50, LEVEL_BOUNDS.minY - 50, LEVEL_BOUNDS.width + 100, LEVEL_BOUNDS.height + 100)

    val LEVEL_COLOR = LIGHTBLUE
    val PATH_COLOR = DARKGRAY

    //region FACTORIES

    fun AttackPath.createGraphics(): Shape {
        val path = path.toAwtPath().createAwtOutline(pathWidth)
        val background = Rectangle2D.Double(LEVEL_BOUNDS.minX, LEVEL_BOUNDS.minY, LEVEL_BOUNDS.width, LEVEL_BOUNDS.height)
        val clippedPath = java.awt.geom.Area(path).also { it.intersect(java.awt.geom.Area(background)) }
        return clippedPath.toJfxPath().apply {
            fill = PATH_COLOR
            stroke = null
            isMouseTransparent = true
        }
    }

    fun Defender.createGraphics(size: Double = 15.0) = DefenderGraphic(this, size, palette = false)

    fun Defender.createPaletteGraphics(size: Double = 15.0) = DefenderGraphic(this, size, palette = true)

    fun Attacker.createGraphics() = Group().apply {
        val circle = circle(position.point!!, 8.0, color)
        val shortName = name.split(" ").joinToString("") { it.substring(0, 2) }
        val text = text(position.point!!, shortName, 8.0, WHITE)
        val w = text.prefWidth(0.0)
        val h = text.prefHeight(0.0)
        text.xProperty().bind(circle.centerXProperty().doubleBinding { it!!.toDouble() - .5 * w })
        text.yProperty().bind(circle.centerYProperty().doubleBinding { it!!.toDouble() + .2 * h })
        children.add(circle)
        children.add(text)
    }

    fun Bullet.createGraphics() = shape.apply {
        stroke = BLACK
        strokeWidth = if (damage >= 400) 4.0 else if (damage >= 100) 2.0 else 1.0
        if (this is Circle) opacity = .2
    }

    //endregion

    //region UPDATERS

    fun Attacker.updateGraphics(c: Group) {
        (c.children.first { it is Circle } as Circle).center = position.point!!
    }

    fun Bullet.updateGraphics(s: Shape) {
        when (s) {
            is Line -> {
                val shape = shape as Line
                s.startX = shape.startX
                s.startY = shape.startY
                s.endX = shape.endX
                s.endY = shape.endY
            }
            is Circle -> {
                val shape = shape as Circle
                s.center = shape.center
                s.radius = shape.radius
            }
        }
    }

    //endregion

    //region ANIMATIONS

    /** Animates a "hit" on this shape. */
    fun Group.hitAnimation() {
        FadeTransition(Duration.seconds(0.1), this).apply {
            fromValue = 1.0
            toValue = 0.6
            cycleCount = 2
            isAutoReverse = true
        }.playFromStart()
    }

    //endregion
}

internal fun Text.alignAbove(node: Node) {
    val w = prefWidth(0.0)
    xProperty().bind(node.boundsInLocalProperty().doubleBinding { it!!.centerX - .5 * w })
    yProperty().bind(node.boundsInLocalProperty().doubleBinding { it!!.minY - 0.3 * it.height })
}

internal fun Text.alignAbove2(node: Node) {
    val w = prefWidth(0.0)
    xProperty().bind(node.boundsInLocalProperty().doubleBinding { it!!.centerX - .5 * w })
    yProperty().bind(node.boundsInLocalProperty().doubleBinding { it!!.minY - 0.06 * it.height })
}

internal fun Text.alignBelow(node: Node) {
    val w = prefWidth(0.0)
    xProperty().bind(node.boundsInLocalProperty().doubleBinding { it!!.centerX - .5 * w })
    yProperty().bind(node.boundsInLocalProperty().doubleBinding { it!!.maxY + 0.3 * it.height })
}

internal fun Text.alignBelow2(node: Node) {
    val w = prefWidth(0.0)
    xProperty().bind(node.boundsInLocalProperty().doubleBinding { it!!.centerX - .5 * w })
    yProperty().bind(node.boundsInLocalProperty().doubleBinding { it!!.maxY + 0.54 * it.height })
}

internal fun String.beforeSpace() = if (" " in this) substringBefore(" ") else ""
internal fun String.afterSpace() = if (" " in this) substringAfter(" ") else this
