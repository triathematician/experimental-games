package asher.greek.assets

import asher.greek.components.AttackOrder
import asher.greek.components.Level
import asher.greek.components.LevelWave

class WaveConfig {
    var attackers = mutableMapOf<String, String>()
    var defenders = mutableMapOf<String, List<Array<Int>>>()

    fun createWave(it: Level, num: Int) = LevelWave(it, num).apply {
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