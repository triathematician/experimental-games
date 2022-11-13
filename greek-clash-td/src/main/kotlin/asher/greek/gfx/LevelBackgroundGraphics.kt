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

import asher.greek.components.Level
import asher.greek.ui.DefenderDraggerTool
import asher.greek.util.point
import asher.greek.util.rectangle
import javafx.scene.Group

/** Background for a level/wave. Supports mouse interaction for adding defenders. */
class LevelBackgroundGraphics(level: Level, toolDragger: DefenderDraggerTool): Group() {
    init {
        children.add(rectangle(level.bounds, GraphicsFactory.LEVEL_COLOR))

        setOnMouseMoved { toolDragger.pos = point(it.x, it.y) }
        setOnMouseEntered {
            toolDragger.isVisible = true
            toolDragger.updateTool()
        }
        setOnMouseExited {
            toolDragger.isVisible = false
            toolDragger.defenderGraphic = null
        }
    }
}
