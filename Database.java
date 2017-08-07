package com.fnordo.PlayerScatter;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

/**
 * Created by KZ5EE on 6/6/2017.
 */
public class Database {
    //private static FileConfiguration config = Main.instance.getConfig();

    private static final String INSERT = "INSERT INTO ps_spawn (uuid, spawnX, spawnY, spawnZ) VALUES(?,?,?,?)";

    public static void addSavedSpawn(Player p, int spawnx, int spawny, int spawnz) {

        Connection conn = null;
        PreparedStatement preparedStatement;

        //getLogger().log(Level.WARNING, ChatColor.RED + INSERT);
        try {
            conn = Main.instance.getHikari().getConnection();

            preparedStatement = conn.prepareStatement(INSERT);
            preparedStatement.setString(1, p.getUniqueId().toString());
            preparedStatement.setInt(2, spawnx);
            preparedStatement.setInt(3, spawny);
            preparedStatement.setInt(4, spawnz);

            preparedStatement.execute();

            preparedStatement.close();
            conn.close();

        } catch (SQLException ex) {
            getLogger().log(Level.WARNING, ChatColor.RED + ex.toString());

        }
    }

    private static final String SELECT = "SELECT spawnX, spawnY, spawnZ FROM ps_spawn WHERE uuid=?";

    public static Location getSavedSpawn(Player p) {

        int[] sLoc;

        World w = p.getWorld();
        Connection conn = null;
        ResultSet resultSet;

        sLoc = new int[3];

        //Location sLocation = new Location(w, x, y, z);

        try {

            conn = Main.instance.getHikari().getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement(SELECT);
            preparedStatement.setString(1, p.getUniqueId().toString());

            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();

            int i = 0;

            while (resultSet.next()) {

                sLoc[0] = resultSet.getInt(1);
                sLoc[1] = resultSet.getInt(2);
                sLoc[2] = resultSet.getInt(3);
                //p.sendMessage(ChatColor.AQUA + Integer.toString(sLoc[0]) + " " + Integer.toString(sLoc[1]) + " " + Integer.toString(sLoc[2]));
            }

            preparedStatement.close();
            conn.close();

        } catch (Exception ex) {
            getLogger().log(Level.WARNING, ChatColor.RED + ex.toString());
        }

        Location sLocation = new Location(w, sLoc[0], sLoc[1], sLoc[2]);

        return sLocation;
    }
}
