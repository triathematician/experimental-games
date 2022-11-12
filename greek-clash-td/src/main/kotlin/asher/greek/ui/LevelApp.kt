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
