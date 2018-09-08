package com.booksaw.automine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import com.koletar.jj.mineresetlite.MineResetLite;
import com.koletar.jj.mineresetlite.SerializableBlock;

public class MineConfiguration {

	public static HashMap<String, MineConfiguration> ranks = new HashMap<>();

	public Map<SerializableBlock, Double> blocks;

	public MineConfiguration(String name) {
		ranks.put(name, this);
		System.out.println("creating rank " + name);
		blocks = new HashMap<>();

		File f = new File(Main.pl.getDataFolder().getParentFile() + File.separator + "MineResetLite" + File.separator
				+ "mines" + File.separator + name + ".mine.yml");

		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				Bukkit.getLogger().warning("COULD NOT LOAD MINE DETAILS (" + name + ")S");
			}

		}

		List<com.koletar.jj.mineresetlite.Mine> mines = MineResetLite.instance.mines;
		com.koletar.jj.mineresetlite.Mine mine = null;

		for (com.koletar.jj.mineresetlite.Mine m : mines) {
			if (m.getName().equals(name)) {
				mine = m;
			}
		}

		blocks = mine.getComposition();
	}

}
