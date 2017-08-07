package com.fnordo.PlayerScatter;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

/**
 * Created by KZ5EE on 5/31/2017.
 */
public class Main extends JavaPlugin {

    public static Main instance;
    private File configf, spawnf;
    private FileConfiguration config, spawn;
    public HikariDataSource hikari;

    @Override
    public void onEnable() {
        instance = this;
        createConfigs();
        setupHikari();
        new PlayerScatter(this);
        //registerListeners();

    }
    @Override
    public void onDisable() {
        instance = null;

        if (hikari != null) {
            hikari.close();
        }
    }

    private void createConfigs() {

        configf = new File(getDataFolder(), "config.yml");
        spawnf = new File(getDataFolder(), "spawns.yml");

        if (!configf.exists()) {
            getLogger().log(Level.WARNING, ChatColor.RED + "No config file found! Creating.");
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        if (!spawnf.exists()) {
            getLogger().log(Level.WARNING, ChatColor.RED + "No spawn file found! Creating.");
            spawnf.getParentFile().mkdirs();
            saveResource("spawns.yml", false);
        }

        config = new YamlConfiguration();
        spawn = new YamlConfiguration();
        try {
            config.load(configf);
            spawn.load(spawnf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getSpawnList() {
        return this.spawn;
    }

/*    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PlayerScatter(), this);
    }*/

    private void setupHikari() {

        String dbAddress = config.getString("mysql.host");
        String dbName = config.getString("mysql.database");
        String dbUsername = config.getString("mysql.username");
        String dbPasswd = config.getString("mysql.password");
        String dbPort = config.getString("mysql.port");

        hikari = new HikariDataSource();

        hikari.setMaximumPoolSize(10);
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", dbAddress);
        hikari.addDataSourceProperty("port", dbPort);  //Integer.parseInt(port));
        hikari.addDataSourceProperty("databaseName", dbName);
        hikari.addDataSourceProperty("user", dbUsername);
        hikari.addDataSourceProperty("password", dbPasswd);

    }

    public HikariDataSource getHikari() {
        return hikari;
    }
}