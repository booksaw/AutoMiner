package com.booksaw.automine.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.automine.Mine;
import com.booksaw.automine.commands.autosell.AutoMineFortune;
import com.booksaw.automine.commands.autosell.AutoMineHelp;
import com.booksaw.automine.commands.autosell.AutoMineInfo;
import com.booksaw.automine.commands.autosell.AutoMineReset;
import com.booksaw.automine.commands.autosell.AutoMineUpgrade;
import com.booksaw.automine.commands.autosell.Sub;

public class CommandAutoMine implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {

		if ((args.length == 0 || !sender.hasPermission("automine.admin")) && sender instanceof Player) {
			Mine m = Mine.mines.get((Player) sender);
			if (!m.isGenerated) {
				m.createNewMine();
			}
			m.teleportPlayer();
			return true;
		}

		Sub cmd;
		if (args.length == 0) {
			cmd = new AutoMineHelp();
			cmd.command(sender, args, label);

			return true;
		}

		switch (args[0].toLowerCase()) {
		case "info":
			cmd = new AutoMineInfo();
			break;
		case "upgrade":
			cmd = new AutoMineUpgrade();
			break;
		case "fortune":
			cmd = new AutoMineFortune();
			break;
		case "reset":
			cmd = new AutoMineReset();
			break;
		default:
			cmd = new AutoMineHelp();
		}

		cmd.command(sender, args, label);
		return true;

	}

}
