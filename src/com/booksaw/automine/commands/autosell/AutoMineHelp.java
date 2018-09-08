package com.booksaw.automine.commands.autosell;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class AutoMineHelp implements Sub {

	@Override
	public void command(CommandSender sender, String[] args, String label) {
		sender.sendMessage(ChatColor.GOLD + "/" + label + "" + ChatColor.WHITE+ " - " + ChatColor.AQUA + "Teleports you to your mine");
		sender.sendMessage(ChatColor.GOLD + "/" + label + " reset <player>" + ChatColor.WHITE+ " - " + ChatColor.AQUA + "resets <player>'s mine");
		sender.sendMessage(ChatColor.GOLD + "/" + label + " fortune <player> [level]" + ChatColor.WHITE+ " - " + ChatColor.AQUA + "Either upgrades a players fortune level or sets it to [level]");
		sender.sendMessage(ChatColor.GOLD + "/" + label + " upgrade <player> [level]" + ChatColor.WHITE+ " - " + ChatColor.AQUA + "Either upgrades a players mine level or sets it to [level]");
		sender.sendMessage(ChatColor.GOLD + "/" + label + " info <player>" + ChatColor.WHITE+ " - " + ChatColor.AQUA + "Gives you information about that player");
	}

}
