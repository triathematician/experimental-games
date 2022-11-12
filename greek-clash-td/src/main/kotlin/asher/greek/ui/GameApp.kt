package asher.greek.ui

import asher.greek.components.Game
import javafx.scene.Scene
import tornadofx.*

class GameView(val game: Game = Game()) : View() {

    val levelGraphics = LevelPane(game)

    override val root = borderpane {
        top = toolbar {
            button("Start Wave") {
                disableWhen { game._levelStarted }
                action { levelGraphics.startWaveTimer() }
            }
            button("Pause Wave") {
                enableWhen { game.levelActive }
                action { levelGraphics.pauseWaveTimer() }
            }
            button("Continue Wave") {
                enableWhen { game._levelPaused }
                action { levelGraphics.unpauseWaveTimer() }
            }
            button("Reset Wave") {
                disableWhen { game.levelActive }
                action { levelGraphics.initWave(keepDefenders = true) }
            }
            separator {  }
            button("Previous Wave") {
                visibleWhen { game._testMode }
                enableWhen { game.canProceedToPreviousWave }
                action { previousWave() }
            }
            button("Next Wave") {
                action { nextWave() }
                enableWhen { game.canProceedToNextWave }
            }
            button("Previous Level") {
                visibleWhen { game._testMode }
                enableWhen { game.canProceedToPreviousLevel }
                action { previousLevel() }
            }
            button("Next Level") {
                enableWhen { game.canProceedToNextLevel }
                action { nextLevel() }
            }
            button("Reset Level") {
                action { resetLevel() }
            }
            separator { }
            text(game.waveText)
            separator { }
            checkbox("Testing", game._testMode)
        }
        center = levelGraphics
    }

    //region change levels/waves

    fun previousWave() {
        levelGraphics.pauseWaveTimer()
        game.previousWave()
        levelGraphics.initWave(keepDefenders = true)
    }
    fun nextWave() {
        levelGraphics.pauseWaveTimer()
        game.nextWave()
        levelGraphics.initWave(keepDefenders = true)
    }

    fun previousLevel() {
        levelGraphics.pauseWaveTimer()
        game.previousLevel()
        levelGraphics.initWave(keepDefenders = false)
    }
    fun nextLevel() {
        levelGraphics.pauseWaveTimer()
        game.nextLevel()
        levelGraphics.initWave(keepDefenders = false)
    }
    fun resetLevel() {
        levelGraphics.pauseWaveTimer()
        game.initLevel()
        levelGraphics.initWave(keepDefenders = false)
    }

    //endregion
}

class GameApp : App() {
    override val primaryView = GameView::class

    override fun createPrimaryScene(view: UIComponent) = Scene(view.root, 1000.0, 1000.0)
}

fun main(args: Array<String>) = launch<GameApp>(args)