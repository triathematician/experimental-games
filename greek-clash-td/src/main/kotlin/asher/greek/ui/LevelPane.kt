package asher.greek.ui

import asher.greek.components.*
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
import javafx.animation.AnimationTimer
import javafx.scene.Group
import javafx.scene.layout.Pane
import javafx.scene.paint.Color.LIGHTGOLDENRODYELLOW
import javafx.scene.paint.Color.RED
import javafx.scene.shape.Shape
import javafx.scene.transform.Transform

/** JavaFX scene for a level. */
class LevelPane(val game: Game) : Pane() {

    val resizableGroup = Group().apply {
        clip = rectangle(VIEW_BOUNDS)
    }
    val levelGroup = Group()
    val defPalette = DefenderPalette(PALETTE_BOUNDS)
    val toolDragger = DefenderDraggerTool(defPalette._selectedTool)
    val stats = LevelStatsGraphics(LEVEL_BOUNDS)
    val attackers = mutableMapOf<Attacker, Group>()
    val bullets = mutableMapOf<Bullet, Shape>()

    var waveState = WaveState(game, game.curWave)
    var timer: AnimationTimer? = null

    init {
        children += resizableGroup
        with (resizableGroup.children) {
            add(defPalette)
            add(rectangle(PLAY_AREA_BOUNDS, fillColor = LIGHTGOLDENRODYELLOW, strokeColor = RED))
            add(stats)
            add(levelGroup)
            add(toolDragger)
        }

        layoutBoundsProperty().addListener { _, _, lb ->
            val scale = minOf(lb.width / VIEW_BOUNDS.width, lb.height / VIEW_BOUNDS.height)
            resizableGroup.transforms.setAll(
                Transform.translate(.5 * lb.width - .5 * scale * VIEW_BOUNDS.width, .5 * lb.height - .5 * scale * VIEW_BOUNDS.height),
                Transform.scale(scale, scale),
                Transform.translate(-VIEW_BOUNDS.minX, -VIEW_BOUNDS.minY)
            )
        }

        initWave(keepDefenders = false)
    }

    /** Initializes the wave. */
    fun initWave(keepDefenders: Boolean) {
        pauseWaveTimer()

        // create new wave
        val defs = if (keepDefenders) waveState.defenders else listOf()
        waveState = WaveState(game, game.curWave).also {
            it.setup(defs)
        }
        game.isPassedWave = false
        game.isLevelStarted = false
        game.isLevelPaused = false

        // reset graphics
        levelGroup.children.clear()

        // set up graphics
        val bg = LevelBackgroundGraphics(game.curLevel, toolDragger).apply {
            setOnMouseClicked { maybeAddDefender(waveState, it.x, it.y) }
        }
        levelGroup.children.add(bg)
        levelGroup.children.add(game.curLevel.path.createGraphics())
        waveState.defenders.forEach { levelGroup.children.add(it.createGraphics()) }

        stats.update(waveState, game.player)
        defPalette.update(game.player)
    }

    /** Starts the wave. */
    fun startWaveTimer() {
        timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                // advance simulation
                waveState.tick()

                // update attackers
                (waveState.attackers - attackers.keys).forEach { createAttackerGraphics(it) }
                (attackers.keys - waveState.attackers).forEach { removeAttackerGraphics(it) }
                attackers.forEach { (a, s) -> a.updateGraphics(s) }

                // update defenders
                (waveState.bullets - bullets.keys).forEach { createBulletGraphics(it) }
                (bullets.keys - waveState.bullets).forEach { removeBulletGraphics(it) }
                bullets.forEach { (b, s) -> b.updateGraphics(s) }

                // animate hits
                waveState.hits.mapNotNull { attackers[it.second] }.forEach { it.hitAnimation() }

                // update stats
                stats.update(waveState, game.player)
                defPalette.update(game.player)

                if (waveState.waveOver) {
                    pauseWaveTimer()
                    game.isPassedWave = !waveState.playerLost
                    game.isLevelStarted = false
                }
            }
        }.also {
            it.start()
            game.isLevelStarted = true
        }
    }

    /** Stops the animation. */
    fun pauseWaveTimer() {
        timer?.stop()
        game.isLevelPaused = true
    }

    /** Starts the animation. */
    fun unpauseWaveTimer() {
        timer?.start()
        game.isLevelPaused = false
    }

    //region GRAPHIC CREATORS/DESTRUCTORS

    fun maybeAddDefender(waveState: WaveState, x: Double, y: Double) {
        toolDragger.defGfx?.let { dg ->
            val newDef = dg.defender.at(x, y)
            game.player.funds -= newDef.cost
            waveState.defenders.add(newDef)
            levelGroup.children.add(newDef.createGraphics())

            stats.update(waveState, game.player)
            defPalette.update(game.player)
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

    fun removeAttackerGraphics(a: Attacker) {
        levelGroup.children.remove(attackers[a])
    }

    fun removeBulletGraphics(b: Bullet) {
        levelGroup.children.remove(bullets[b])
    }

    //endregion

}

