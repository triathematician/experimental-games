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
package asher.greek.components

import asher.greek.util.Point2
import java.util.*
import kotlin.math.pow

/**
 * The primary game loop where attackers advance and defenders fire.
 * All movement is synchronized with integer clock "ticks".
 * All speed parameters reference this unit of time.
 */
class WaveState(val game: GameState, val levelWave: LevelWave) {
    val attackers = mutableListOf<Attacker>()
    val defenders = mutableListOf<Defender>()
    val bullets = mutableListOf<Bullet>()
    var hits = mutableListOf<Pair<Bullet, Attacker>>()

    var clock = 0

    val items: List<Any>
        get() = attackers + defenders + bullets
    val waveOver: Boolean
        get() = (attackers.isEmpty() && clock > levelWave.lastSpawnTime) || game.player.lives == 0
    val playerLost: Boolean
        get() = game.player.lives == 0

    //region WAVE LIFECYCLE

    /** Sets up the wave. */
    fun setup(defs: List<Defender>) {
        println("Wave setup")
        defenders.addAll(defs)
        defenders.addAll(levelWave.defenders)
    }

    /** Starts the game for the given wave. */
    fun start(updater: (List<Any>) -> Unit) {
        while (!waveOver) {
            tick()
            updater(items)
        }
    }

    /** Runs a single iteration. */
    fun tick() {
        levelWave.order[clock].forEach { it.spawn() }
        attackers.toList().forEach {
            it.advance(clock)
            if (it.position.point == levelWave.level.path.defensePoint) {
                it.reachesDefensePoint()
            }
        }
        hits = mutableListOf()
        bullets.forEach {
            it.advance()
            attackers.forEach { a -> if (it.hit(a)) hits.add(it to a) }
        }
        hits.forEach { it.first.handleHit(it.second) }
        bullets.filter { it.attackComplete() }.toList().forEach { it.despawn() }
        defenders.forEach {
            it.timeSinceLastFire++
            it.fireAtNearest()
        }
        clock++
    }

    /** Finishes the current wave. */
    fun finishWave() {
        println("Wave over")
    }

    //endregion

    //region ATTACKERS

    /** When attacker despawns. */
    fun Attacker.spawn() {
        println("Spawning $name at time $clock")
        with (copy(uid = UUID.randomUUID())) {
            position.path = levelWave.level.path
            attackers.add(this)
        }
    }

    /** When attacker reaches end of path. */
    fun Attacker.reachesDefensePoint() {
        println("$name reached end of path at time $clock with $health health")

        // lose 1 life minimum, 2 lives at 500, 3 at 1000, etc., but don't go below 0
        game.player.lives = maxOf(0, game.player.lives - 1 - (hitPoints/500))
        if (game.player.lives == 0) {
            playerLoses()
        }
        despawn()
    }

    /** When attacker loses all health and dies. */
    fun Attacker.die() {
        println("$name died")
        attackers -= this

        // gain funds based on hit points
        game.player.funds += (hitPoints.toDouble().pow(.75).toInt() / 5) * 5
    }

    /** When attacker despawns. */
    fun Attacker.despawn() {
        println("Removing $name")
        attackers -= this
    }

    /** When bullet hits and is removed. */
    fun Bullet.despawn() {
        bullets -= this
    }

    /** Find nearest attacker to given location. */
    fun nearestAttackerTo(point: Point2): Attacker? {
        return attackers.minByOrNull { point.distanceSq(it.position.point) }
    }

    //endregion

    //region DEFENDERS

    /** Upgrade defender. */
    fun upgrade(def: Defender) {
        val nue = def.upgrade()
        if (nue == null) {
            println("Unexpected attempt to upgrade defender without defined upgrades.")
            return
        }
        defenders -= def
        defenders += nue
        game.player.funds -= def.cost // TODO - variable cost per upgrade
        println("Upgraded ${def.name} to ${nue.name} for \$${def.cost}")
    }

    /** Sell defender. */
    fun sell(def: Defender) {
        game.player.funds += def.cost // TODO - variable cost
        defenders.remove(def)
        println("Sold $def for \$${def.cost}")
    }

    /** Defender fires at nearest target. */
    fun Defender.fireAtNearest() {
        if (timeSinceLastFire >= fireRate) {
            val target = nearestAttackerTo(position)
            if (target != null && target.position.point!!.distance(position) <= range) {
                fireAt(target)
            }
        }
    }

    /** Defender fires at a target in range. */
    fun Defender.fireAt(target: Attacker) {
        println("  $name firing at ${target.name}")
        bullets += Bullet(this, target, attackPower, if (areaAttack) range else null, tracking = false)
        timeSinceLastFire = 0
    }

    //endregion

    //region BULLETS

    /** Attacker gets hit by an attack. */
    fun Bullet.handleHit(target: Attacker) {
        target.health = maxOf(0.0, target.health - damage)
        source.slowPower?.let { target.slowDown(it, clock) }
        val killed = target.health <= 0.0
        println("    hit ${target.name} " + if(killed) "KO" else "${target.health}")
        if (!areaAttack) {
            despawn()
        }
        if (killed) {
            target.die()
        }
    }

    //endregion

    //region PLAYERS

    fun playerLoses() {
        println("You lost your last life!")
    }

    //endregion

}
