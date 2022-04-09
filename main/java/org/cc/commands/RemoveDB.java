package org.cc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cc.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.cc.Main.format;
import static org.cc.Main.prefix;

public class RemoveDB implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("removedb")) {
            if (sender instanceof Player) {
                if (sender.isOp()) {
                    if (args.length == 1) {
                        synchronized (this) {
                            String sql = "SELECT * FROM login_data WHERE username=\"" + args[0] + "\"";
                            try {
                                Statement statement = Main.getInstance().getConnection().createStatement();
                                ResultSet query = statement.executeQuery(sql);
                                if (query.next()) {
                                    statement.executeUpdate("DELETE FROM login_data WHERE username=\"" + args[0] + "\"");
                                    sender.sendMessage(format(prefix + "&aDeleted " + args[0] + " from the database successfully"));

                                } else {
                                    sender.sendMessage(format(prefix + "&cCould not find user " + args[0] + " in the database"));
                                }


                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                                sender.sendMessage(format(prefix + "&cDATABASE WAS NOT FOUND OR USER IN DATABASE IS NULL"));
                            }
                        }

                    }
                } else {
                    sender.sendMessage(format(prefix+"&cYou don't have any permissions to run this command"));
                }
            } else {
                if (args.length == 1) {
                    synchronized (this) {
                        String sql = "SELECT * FROM login_data WHERE username=\"" + args[0] + "\"";
                        try {
                            Statement statement = Main.getInstance().getConnection().createStatement();
                            ResultSet query = statement.executeQuery(sql);
                            while (query.next()) {
                                statement.executeUpdate("DELETE FROM login_data WHERE username=\"" + args[0] + "\"");
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            }
        }
        return true;
    }
}
