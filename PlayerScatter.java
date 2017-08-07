package com.fnordo.PlayerScatter;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Created by KZ5EE on 6/1/2017.
 */
public class PlayerScatter implements Listener {

    private final Main plugin;
    public PlayerScatter(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    private FileConfiguration config = Main.instance.getConfig();
    private FileConfiguration spawn = Main.instance.getSpawnList();

    public void scatterPlayers(Player p){

        int xRand, yRand, zRand;

        World world = p.getWorld();

        xRand = genPlayerTarget(config.getInt("MaxX")); //Get random X coordinate for scatter
        zRand = genPlayerTarget(config.getInt("MaxZ")); //Get random Z coordinate for scatter
        yRand = world.getHighestBlockYAt(xRand, zRand); //Set Y so player won't suffocate

        Location rLocation = new Location(world, xRand, yRand, zRand);
        /*p.sendMessage(ChatColor.DARK_PURPLE + "Teleporting " + p.getName() + " to " + xRand + " " + yRand + " "
                + zRand +".");*/

        if(config.getBoolean("SaveRandomSpawn")) {
            Database.addSavedSpawn(p, xRand, yRand, zRand);
            //p.sendMessage("Spawn location set.");
        }

        p.teleport(rLocation);
    }

    @EventHandler
    public void onPlayerJoin (final PlayerJoinEvent e) {  //

        Player p = e.getPlayer();

        p.sendMessage(ChatColor.RED + "Welcome to the server " + p.getName());

        if (!p.hasPlayedBefore() && p.getGameMode() != GameMode.SPECTATOR) { //Camera accounts shouldn't be scattered.

            scatterPlayers(p);

        }
    }

    @EventHandler
    public void onPlayerRespawn (final PlayerRespawnEvent e){

        Player p = e.getPlayer();

        if (config.getBoolean("ScatterOnDeath"))
        {
            scatterPlayers(p);
        } else if(p.getBedSpawnLocation() == null)
        {
            p.sendMessage(ChatColor.YELLOW + "Bed spawn point not found.  Respawning at default spawn point");
            Location rsLocation = Database.getSavedSpawn(p);

            new BukkitRunnable() {
                @Override
                public void run() {
                    p.teleport(rsLocation);
                }

            }.runTaskLater(this.plugin, 5);
        }
    }

    public int genPlayerTarget(int value) {
        int coord = 0;
        Random rand = new Random();

        coord = rand.nextInt(value) + 1;

        if (rand.nextBoolean() == true) {
            coord *= -1;
        }

        return coord;
    }
}
