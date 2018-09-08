package com.booksaw.automine.commands.autosell;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.automine.Mine;

import net.md_5.bungee.api.ChatColor;

public class AutoMineFortune implements Sub {

	@Override
	public void command(CommandSender sender, String[] args, String label) {

		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "/" + label + " fortune <player> [amount]");
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

		if (args.length < 3) {
			m.upgradeFortune();

			sender.sendMessage(ChatColor.RED + "The fortune level has been upgraded");
			return;
		} else {
			int amount = 0;
			try {
				amount = Integer.parseInt(args[2]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "/" + label + " fortune <player> [amount]");
				return;
			}

			m.setFortune(amount);
			sender.sendMessage(ChatColor.GOLD + "The players fortune level has been set to " + amount);
			return;
		}

	}

}
