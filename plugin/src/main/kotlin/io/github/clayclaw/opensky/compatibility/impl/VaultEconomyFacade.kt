package io.github.clayclaw.opensky.compatibility.impl

import io.github.clayclaw.opensky.compatibility.EconomyFacade
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class VaultEconomyFacade: EconomyFacade {

    private val vaultAPI by lazy {
        Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)?.provider!!
    }

    override fun getBalance(player: Player): Double {
        return vaultAPI.getBalance(player)
    }

    override fun deposit(player: Player, amount: Double): Boolean {
        return vaultAPI.depositPlayer(player, amount).transactionSuccess()
    }

    override fun withdraw(player: Player, amount: Double): Boolean {
        return vaultAPI.withdrawPlayer(player, amount).transactionSuccess()
    }

}