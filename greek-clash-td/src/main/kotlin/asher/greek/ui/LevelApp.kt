package asher.greek.ui

import asher.greek.components.Game
import asher.greek.gfx.GraphicsFactory.PALETTE_BOUNDS
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage

class LevelApp: Application() {
    override fun start(stage: Stage) {
        val levelGraphics = LevelPane(Game())
        val gamePane = BorderPane().apply {
            top = DefenderPalette(PALETTE_BOUNDS)
            center = levelGraphics
        }
        stage.title = "Greek Clash TD Level Tester"
        stage.scene = Scene(gamePane, 1000.0, 1000.0, Color.LIGHTGOLDENRODYELLOW)
        stage.show()

        levelGraphics.initWave(keepDefenders = false)
        levelGraphics.startWaveTimer()
    }
}

fun main(args: Array<String>) = Application.launch(LevelApp::class.java, *args)