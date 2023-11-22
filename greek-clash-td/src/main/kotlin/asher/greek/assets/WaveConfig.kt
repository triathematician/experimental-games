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

import asher.greek.components.AttackOrder
import asher.greek.components.Level
import asher.greek.components.LevelWave
import com.fasterxml.jackson.annotation.JsonAnySetter

class WaveConfig {
    var attackers = mutableMapOf<String, String>()
    var defenders = mutableMapOf<String, List<Array<Int>>>()

    @JsonAnySetter
    fun putAttacker(name: String, timeList: String) {
        attackers[name] = timeList
    }

    fun createWave(it: Level, num: Int, total: Int) = LevelWave(it, num, total).apply {
        order = createAttackOrder()
        defenders = createDefenders()
    }

    fun createAttackOrder(): AttackOrder {
        val pairs = attackers
            .flatMap { (name, times) -> decodeTimeList(times).map { it to Assets.attacker(name) } }
        return AttackOrder(pairs)
    }

    fun createDefenders() = defenders
        .flatMap { (name, positions) -> positions.map { it to Assets.defender(name) } }
        .map { (pos, def) -> def.also { it.position = pos.toPoint2() } }
        .toMutableList()
}

internal fun decodeTimeList(code: String): List<Int> {

    fun String.substringBetween(c0: Char, c1: Char) = substringAfter(c0).substringBefore(c1)

    fun decodeNumAt(it: String): List<Int> {
        val n = it.substringBefore('@').toInt()
        val t0 = it.substringBetween('@', '+').toInt()
        val dt = it.substringAfter('+').toInt()
        return (1..n).map { t0 + dt * (it - 1) }
    }

    return code.split("[,\\s]+".toRegex()).flatMap {
        when {
            '@' in it && '+' in it -> decodeNumAt(it)
            else -> listOf(it.toInt())
        }
    }
}
