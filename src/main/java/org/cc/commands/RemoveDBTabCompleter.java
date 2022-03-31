package org.cc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.cc.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoveDBTabCompleter implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> commands = new ArrayList();
        List<String> completions = new ArrayList();
        if (args.length == 1) {
            try {
                Statement statement = Main.getInstance().getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT username FROM login_data WHERE username=username");
                while (resultSet.next()) {
                    commands.add(resultSet.getString(1));
                }
                statement.closeOnCompletion();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
