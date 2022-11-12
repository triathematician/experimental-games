package asher.greek.components

/** A level-wave configuration. */
class LevelWave(val level: Level, val num: Int) {
    var order = AttackOrder()
    var defenders = mutableListOf<Defender>()

    val lastSpawnTime
        get() = order.lastSpawnTime
}

