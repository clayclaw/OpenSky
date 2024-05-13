package io.github.clayclaw.opensky.compatibility

import org.bukkit.entity.Player

interface EconomyFacade {

    fun getBalance(player: Player): Double

    fun deposit(player: Player, amount: Double): Boolean
    fun withdraw(player: Player, amount: Double): Boolean

}