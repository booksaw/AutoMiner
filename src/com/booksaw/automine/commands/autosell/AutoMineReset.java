package com.booksaw.automine.commands.autosell;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.automine.Mine;

import net.md_5.bungee.api.ChatColor;

public class AutoMineReset implements Sub {

	@Override
	public void command(CommandSender sender, String[] args, String label) {

		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "/" + label + " info <player>");
			return;
		}

		Player p = Bukkit.getPlayer(args[1]);

		if (p == null) {
			sender.sendMessage(ChatColor.RED + "That is not a player");
			return;
		}

		Mine m = Mine.mines.get(p);

		if (m == null) {
			sender.sendMessage(ChatColor.RED + "That player does not have a mine");
			return;
		}

		m.reset();

		sender.sendMessage(ChatColor.GOLD + "That mine has been reset");

	}

}
