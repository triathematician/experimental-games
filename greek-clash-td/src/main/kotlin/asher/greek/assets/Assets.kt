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