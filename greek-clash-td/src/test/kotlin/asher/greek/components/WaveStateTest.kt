package asher.greek.components

import asher.greek.assets.Assets.attacker
import asher.greek.util.point

fun main() {
    WaveStateTest.testWave()
}

object WaveStateTest {

    fun testLevel() = Level().apply {
        path = AttackPath(listOf(point(0, 0),
                point(100, 100),
                point(100, 0),
                point(200, 100)))
    }

    fun testLevelWave() = LevelWave(testLevel(), 1).apply {
        order = AttackOrder(0 to attacker("Skeleton"),
                100 to attacker("Skeleton"),
                200 to attacker("Harpy"))
    }

    fun testWave() {
        val g = Game()
        with(WaveState(g, testLevelWave())) {
            setup()
            start {}
            finishWave()
        }
    }

}