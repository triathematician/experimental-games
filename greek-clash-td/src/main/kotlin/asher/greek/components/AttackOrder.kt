package asher.greek.components

/** Sequence of attackers for a level, by time. */
class AttackOrder(var order: List<Pair<Int, Attacker>> = listOf()) {

    constructor(vararg order: Pair<Int, Attacker>) : this(order.toList())

    operator fun get(clock: Int) = order.filter { it.first == clock }.map { it.second }

    val lastSpawnTime: Int
        get() = order.maxOfOrNull { it.first } ?: 0
}