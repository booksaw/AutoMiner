package com.booksaw.automine;

import java.io.File;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.booksaw.automine.commands.CommandAutoMine;
import com.booksaw.automine.events.BlockBreak;
import com.booksaw.automine.events.JoinAndLeave;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class Main extends JavaPlugin {

	public static Main pl;

	public static WorldEditPlugin we;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		pl = this;
		if (getConfig().getBoolean("setup")) {
			File f = new File(getDataFolder() + File.separator + "mines");
			f.mkdirs();
			getLogger()
					.warning("AUTOMINER HAS NOT BEEN FULLY ENABLED, please carry out setup process before reloading");
			return;
		}

		we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

		if (we == null) {
			Bukkit.getLogger().warning("CANNOT FIND WORLD EDIT, MOST OF PLUGIN WILL NOT FUNCITON AS EXEPECTED");
		}

		Mine.enable();

		getCommand("automine").setExecutor(new CommandAutoMine());

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new JoinAndLeave(), this);
		pm.registerEvents(new BlockBreak(), this);

		for (String s : getConfig().getStringList("ranks")) {

			new MineConfiguration(s);

		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			Mine.join(p);
		}

		sched();

	}

	@Override
	public void onDisable() {

		for (Entry<Player, Mine> m : Mine.mines.entrySet()) {
			m.getValue().unload();
		}

	}

	public void sched() {
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {

				for (Entry<Player, Mine> m : Mine.mines.entrySet()) {
					m.getValue().tick();
				}

			}
		}, 0L, 1L);
	}

}
