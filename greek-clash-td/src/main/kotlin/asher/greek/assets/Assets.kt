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
package asher.greek.assets

import asher.greek.util.Point2
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

/** Manages lists of assets/content for the game. */
object Assets {
    val assetConfigUrl = Assets::class.java.getResource("resources/assets.yaml")!!
    val assetConfig = YAMLMapper().registerKotlinModule().readValue<AssetsConfig>(assetConfigUrl)

    val levelConfigUrl = Assets::class.java.getResource("resources/levels.yaml")!!
    val levelConfig = YAMLMapper().registerKotlinModule().readValue<LevelsConfig>(levelConfigUrl)

    val attackerTypes = assetConfig.attackers.associateBy { it.name }
    val defenderTypes = assetConfig.defenders.associateBy { it.name }

    fun attacker(name: String) = attackerTypes[name]!!.copy()
    fun defender(name: String) = defenderTypes[name]!!.copy()
}

internal fun Array<Int>.toPoint2() = Point2(this[0].toDouble(), this[1].toDouble())
