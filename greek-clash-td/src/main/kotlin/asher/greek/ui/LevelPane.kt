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
package asher.greek.ui

import asher.greek.components.*
import asher.greek.gfx.DefenderGraphic
import asher.greek.gfx.GraphicsFactory.LEVEL_BOUNDS
import asher.greek.gfx.GraphicsFactory.PALETTE_BOUNDS
import asher.greek.gfx.GraphicsFactory.PLAY_AREA_BOUNDS
import asher.greek.gfx.GraphicsFactory.VIEW_BOUNDS
import asher.greek.gfx.GraphicsFactory.createGraphics
import asher.greek.gfx.GraphicsFactory.hitAnimation
import asher.greek.gfx.GraphicsFactory.updateGraphics
import asher.greek.gfx.LevelBackgroundGraphics
import asher.greek.gfx.LevelStatsGraphics
import asher.greek.util.rectangle
import asher.greek.util.scaleToFit
import javafx.animation.AnimationTimer
import javafx.scene.Group
import javafx.scene.input.MouseButton
import javafx.scene.layout.Pane
import javafx.scene.paint.Color.*
import javafx.scene.shape.Path
import javafx.scene.shape.Shape
import tornadofx.onLeftClick
import tornadofx.onRightClick

/** JavaFX scene for a level. */
class LevelPane(val game: GameState, val controller: GameController) : Pane() {

    val resizableGroup = Group().apply {
        clip = rectangle(VIEW_BOUNDS)
    }
    val levelGroup = Group()
    val defPalette = DefenderPalette(controller, PALETTE_BOUNDS)
    val toolDragger = DefenderDraggerTool(controller, this::spaceAvailable)
    val stats = LevelStatsGraphics(LEVEL_BOUNDS)
    lateinit var bgGraphics: LevelBackgroundGraphics
    lateinit var pathGraphics: Shape
    val attackers = mutableMapOf<Attacker, Group>()
    val defenders = mutableMapOf<Defender, DefenderGraphic>()
    val bullets = mutableMapOf<Bullet, Shape>()

    var waveState = WaveState(game, game.curWave)
    var timer: AnimationTimer? = null

    init {
        children += resizableGroup
        with (resizableGroup.children) {
            add(defPalette)
            add(rectangle(PLAY_AREA_BOUNDS, fill = LIGHTGOLDENRODYELLOW, stroke = DARKGOLDENROD))
            add(stats)
            add(levelGroup)
            add(toolDragger)
        }

        layoutBoundsProperty().addListener { _, _, lb ->
            resizableGroup.scaleToFit(source = VIEW_BOUNDS, target = lb)
        }

        initWave(keepDefenders = false)
    }

    /** Shapes comprising occupied area. */
    fun spaceAvailable(shape: Shape): Boolean {
        if (!bgGraphics.boundsInParent.contains(shape.boundsInParent)) {
            return false
        }
        // tried to compute a union of areas and use that result, but jfx incorporates scene transforms into the result
        // making it more difficult to compare
        return !(listOf(pathGraphics) + defenders.values.map { it.defenderShape }).any {
            (Shape.intersect(it, shape) as Path).elements.isNotEmpty()
        }
    }

    /** Initializes the wave. */
    fun initWave(keepDefenders: Boolean) {
        pauseWaveTimer()

        // create new wave
        val defs = if (keepDefenders) waveState.defenders else listOf()
        waveState = WaveState(game, game.curWave).also {
            it.setup(defs)
        }
        game.isWaveStarted = false
        game.isWavePaused = false
        game.isPassedWave = false
        game.isWaveOver = false

        // reset graphics
        levelGroup.children.clear()

        // set up graphics
        bgGraphics = LevelBackgroundGraphics(game.curLevel, toolDragger).apply {
            setOnMouseClicked {
                if (it.clickCount == 1 && it.button == MouseButton.PRIMARY)
                    maybePlaceDefender(waveState, it.x, it.y)
                else if (it.clickCount == 1 && it.button == MouseButton.SECONDARY)
                    controller.selectNone()
            }
        }
        pathGraphics = game.curLevel.path.createGraphics()
        levelGroup.children.add(bgGraphics)
        levelGroup.children.add(pathGraphics)
        waveState.defenders.forEach { createDefenderGraphics(it) }

        stats.update(waveState, game.player)
        defPalette.update(game.player)
    }

    //region MAIN GAME TIMER

    /** Starts the wave. */
    fun startWaveTimer() {
        timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                gameTick()
            }
        }.also {
            it.start()
            game.isWaveStarted = true
        }
    }

    /** Runs one tick of the game. */
    private fun gameTick() {
        // advance simulation
        waveState.tick()

        refreshAttackerGraphics()
        refreshDefenderGraphics()
        refreshBulletGraphics()
        refreshGameInfo()

        animateEvents()

        if (waveState.waveOver) {
            stopWaveTimer()
            game.isPassedWave = !waveState.playerLost
            game.isWaveOver = true
        }
    }

    //endregion

    //region GRAPHICS UPDATERS

    private fun refreshAttackerGraphics() {
        (waveState.attackers - attackers.keys).forEach { createAttackerGraphics(it) }
        (attackers.keys - waveState.attackers).forEach { removeAttackerGraphics(it) }
        attackers.forEach { (a, s) -> a.updateGraphics(s) }
    }

    private fun refreshDefenderGraphics() {
        (waveState.defenders - defenders.keys).forEach { createDefenderGraphics(it) }
        (defenders.keys - waveState.defenders).forEach { removeDefenderGraphics(it) }
        // defenders.forEach { (a, s) -> a.updateGraphics(s) }
    }

    private fun refreshBulletGraphics() {
        (waveState.bullets - bullets.keys).forEach { createBulletGraphics(it) }
        (bullets.keys - waveState.bullets).forEach { removeBulletGraphics(it) }
        bullets.forEach { (b, s) -> b.updateGraphics(s) }
    }

    private fun animateEvents() {
        waveState.hits.mapNotNull { attackers[it.second] }.forEach { it.hitAnimation() }
    }

    private fun refreshGameInfo() {
        stats.update(waveState, game.player)
        defPalette.update(game.player)
    }

    //endregion

    //region TIMER ACTIONS

    /** Pauses the animation. */
    fun pauseWaveTimer() {
        timer?.stop()
        game.isWavePaused = true
    }

    /** Starts the animation. */
    fun unpauseWaveTimer() {
        timer?.start()
        game.isWavePaused = false
    }

    /** Stops the animation. */
    fun stopWaveTimer() {
        timer?.stop()
        timer = null
    }

    //endregion

    //region GRAPHIC CREATORS/DESTRUCTORS

    fun upgradeSelected() {
        when(val s = controller.selected) {
            is DefenderGraphic -> {
                waveState.upgrade(s.defender)
                refreshDefenderGraphics()
                refreshGameInfo()
                controller.selectNone()
            }
        }
    }

    fun sellSelected() {
        when(val s = controller.selected) {
            is DefenderGraphic -> {
                waveState.sell(s.defender)
                refreshDefenderGraphics()
                refreshGameInfo()
                controller.selectNone()
            }
        }
    }

    fun maybePlaceDefender(waveState: WaveState, x: Double, y: Double) {
        val dg = toolDragger.previewGraphic ?: return
        if (!dg.isValidPlacement)
            return

        val newDef = dg.defender.at(x, y)
        game.player.funds -= newDef.cost
        waveState.defenders.add(newDef)
        createDefenderGraphics(newDef)

        stats.update(waveState, game.player)
        defPalette.update(game.player)
    }

    fun createDefenderGraphics(d: Defender) = d.createGraphics().apply {
        defenders[d] = this
        levelGroup.children.add(this)
        if (isInPlay) {
            onLeftClick {
                isSelected = !isSelected
                if (isSelected)
                    controller.selected = this
                else
                    controller.selectNone()
            }
        }
    }

    fun createAttackerGraphics(a: Attacker) = a.createGraphics().also {
        attackers[a] = it
        levelGroup.children.add(it)
    }

    fun createBulletGraphics(b: Bullet) = b.createGraphics().also {
        bullets[b] = it
        levelGroup.children.add(it)
    }

    fun removeDefenderGraphics(d: Defender) {
        levelGroup.children.remove(defenders[d])
    }

    fun removeAttackerGraphics(a: Attacker) {
        levelGroup.children.remove(attackers[a])
    }

    fun removeBulletGraphics(b: Bullet) {
        levelGroup.children.remove(bullets[b])
    }

    //endregion

}

