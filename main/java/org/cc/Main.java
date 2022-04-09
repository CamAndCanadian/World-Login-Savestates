package org.cc;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.cc.commands.RemoveDB;
import org.cc.commands.RemoveDBTabCompleter;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Main extends JavaPlugin {
    Connection connection;
    private static Main instance;
    public static String prefix = "&6[&9CC SQL&6] ";
    public static Main getInstance() {
        return instance;
    }

    @Override
    @SuppressWarnings("all")
    public void onEnable() {
        createNewDatabase("logins.db");
        instance = this;
        try {
            this.getConnection().createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS login_data (\n" +
                    "\tuuid PRIMARY KEY default NULL,\n" +
                    "\tusername varchar(255) default NULL,\n" +
                    "\texit_date varchar(255),\n" +
                    "\tx double default 0,\n" +
                    "\ty double default 0,\n" +
                    "\tz double default 0,\n" +
                    "\tworld varchar(25) default NULL\n" +
                    ");");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        this.getServer().getPluginManager().registerEvents(new Events(), this);
        this.getCommand("removedb").setExecutor(new RemoveDB());
        this.getCommand("removedb").setTabCompleter(new RemoveDBTabCompleter());
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public void createNewDatabase(String fileName) {
        String url = "jdbc:sqlite:" + getDataFolder() + "\\databases\\" + fileName;

        try {
            connection = DriverManager.getConnection(url);
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        synchronized (this) {
            if (connection == null) {
                try {
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                connection = DriverManager.getConnection("jdbc:sqlite:D:" + getDataFolder() + "\\databases\\" + "logins.db");
            }
        }
        return connection;
    }

    public static String format(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}

