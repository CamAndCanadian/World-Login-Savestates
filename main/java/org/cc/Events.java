package org.cc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Events implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {

            Player p = e.getPlayer();
            Statement statement = Main.getInstance().getConnection().createStatement();
            String sql = "SELECT * FROM login_data WHERE uuid=\"" + p.getUniqueId() + "\"";
            ResultSet uuids = statement.executeQuery(sql);
            try {
                while (!uuids.next()) {
                    statement.executeUpdate("INSERT INTO login_data (uuid,username) VALUES (\"" + p.getUniqueId() + "\",\"" + p.getName() + "\");");
                }
            } catch (Exception ex) {
                System.out.println("Possible constraint failure, ignore if user is placed in DB");
                System.out.println(ex.getMessage());
            }

    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws SQLException {
            Player p = e.getPlayer();
            Statement statement = Main.getInstance().getConnection().createStatement();
            String sql = "SELECT uuid FROM login_data WHERE uuid=\"" + p.getUniqueId() + "\"";
            ResultSet uuids = statement.executeQuery(sql);

            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);

            while (uuids.next()) {
                statement.executeUpdate("UPDATE login_data SET exit_date=\"" + strDate + "\",x=" + p.getLocation().getX() + ",y=" + p.getLocation().getY() + ",z=" + p.getLocation().getZ() + ",world=\"" + p.getLocation().getWorld().getName() + "\" WHERE uuid=\"" + p.getUniqueId() + "\"");
            }

    }
    @EventHandler
    public void onWorldEnter(PlayerChangedWorldEvent e) throws SQLException {

            String world = Main.getInstance().getConfig().getString("login.world");
            if (world == null) {
                Main.getInstance().getConfig().set("login.world", "SMP");
                Main.getInstance().saveConfig();
            }
            Player p = e.getPlayer();
            String loc = "SELECT x,y,z,world FROM login_data WHERE uuid=\"" + p.getUniqueId() + "\"";
            Statement statement = Main.getInstance().getConnection().createStatement();
            ResultSet locSet = statement.executeQuery(loc);
            while (locSet.next()) {
                if (p.getWorld().getName().equals(world)) {
                    if (Bukkit.getServer().getWorld(world) != null) {
                        Location location = new Location(Bukkit.getServer().getWorld(world), locSet.getDouble(1), locSet.getDouble(2), locSet.getDouble(3));
                        if (location.getX() != 0.0 && location.getY() != 0.0 && location.getZ() != 0.0) {
                            p.teleport(location);
                            String parsed = location.getX() + ", " + location.getY() + ", " + location.getZ() + " in world " + world;
                            Bukkit.getLogger().info("teleported player to " + parsed);
                        } else {
                            p.teleport(p.getWorld().getSpawnLocation());
                            Bukkit.getLogger().info("teleported player to world spawn because value of coordinates are invalid");
                        }
                    } else {
                        Bukkit.getLogger().severe("World " + world + " does not exist");

                    }

            }
        }
    }

}
