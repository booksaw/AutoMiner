package com.booksaw.automine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.koletar.jj.mineresetlite.Mine.CompositionEntry;
import com.koletar.jj.mineresetlite.SerializableBlock;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.World;

@SuppressWarnings("deprecation")
public class Mine {

	static CuboidClipboard mine;
	static int defDelay, reduceDelay;

	public static void enable() {

		File f = new File(Main.pl.getDataFolder() + File.separator + "mine.schematic");

		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Bukkit.getLogger().warning("Could not find schematic file, will create one where it is expected");
			Bukkit.getLogger().warning("Could not find schematic file, will create one where it is expected");
			Bukkit.getLogger().warning("Could not find schematic file, will create one where it is expected");
			Bukkit.getLogger().warning("Could not find schematic file, will create one where it is expected");
		}

		try {
			mine = SchematicFormat.MCEDIT.load(f);
		} catch (DataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		defDelay = Main.pl.getConfig().getInt("defaultDelay");
		reduceDelay = Main.pl.getConfig().getInt("levelDelay");
	}

	public static HashMap<Player, Mine> mines = new HashMap<>();

	public static void join(Player p) {
		new Mine(p);
	}

	public static void leave(Player p) {
		Mine m = mines.get(p);
		m.unload();
	}

	Location pos1, pos2, tp;
	int level, fortune;

	Player p;
	YamlConfiguration mineDetails;
	File f;
	int tickTracker;

	public boolean isGenerated = true;

	public Mine(Player p) {
		this.p = p;
		// load that players mine
		loadMine();
		tickTracker = 0;
		reset();
		mines.put(p, this);
	}

	public void createNewMine() {

		int xmain = Main.pl.getConfig().getInt("x");
		int zmain = Main.pl.getConfig().getInt("z");
		World world = new BukkitWorld(Bukkit.getWorld(Main.pl.getConfig().getString("world")));

		int x = xmain * mine.getWidth();
		int z = zmain * mine.getLength();

		xmain++;
		if (xmain > 15) {
			x = 0;
			Main.pl.getConfig().set("z", zmain + 1);
		}
		Main.pl.getConfig().set("x", xmain);
		Main.pl.saveConfig();

		Vector v = new Vector(x, 30, z);
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
		try {
			mine.paste(editSession, v, false);
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("COULD NOT PASTE MINE");
		}

		pos1 = new Location(Bukkit.getWorld(Main.pl.getConfig().getString("world")), x + 5, 30, z + 5);
		pos2 = new Location(Bukkit.getWorld(Main.pl.getConfig().getString("world")), x + 9, 33, z + 9);
		tp = new Location(Bukkit.getWorld(Main.pl.getConfig().getString("world")), x + 7.5, 34, z + 3.5, 0, 0);

		// saving the locations for this player
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("COULD NOT CREATE MINE FOR PLAYER (create file error)");
		}

		mineDetails = YamlConfiguration.loadConfiguration(f);

		setFortune(0);
		setLevel(0);

		mineDetails.set("locations.pos1", MineAPI.getString(pos1));
		mineDetails.set("locations.pos2", MineAPI.getString(pos2));
		mineDetails.set("locations.tp", MineAPI.getString(tp));

		try {
			mineDetails.save(f);
		} catch (IOException e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("CANNOT SAVE MINE FILE, REASON ABOVE");
		}

	}

	private void loadMine() {
		f = new File(Main.pl.getDataFolder() + File.separator + "mines" + File.separator + p.getUniqueId() + ".yml");

		if (!f.exists()) {
			isGenerated = false;
			return;
		}

		mineDetails = YamlConfiguration.loadConfiguration(f);

		// loading the mine locations
		pos1 = MineAPI.getLocation(mineDetails.getString("locations.pos1"));
		pos2 = MineAPI.getLocation(mineDetails.getString("locations.pos2"));
		tp = MineAPI.getLocation(mineDetails.getString("locations.tp"));

		level = mineDetails.getInt("statistics.level");
		fortune = mineDetails.getInt("statistics.fortune");
	}

	public void setFortune(int fortune) {
		this.fortune = fortune;
		mineDetails.set("statistics.fortune", fortune);

		for (int i = 0; i <= 3; i++)
			try {
				mineDetails.save(f);
				return;
			} catch (IOException e) {
				if (i > 2) {
					e.printStackTrace();
				}
			}

		Bukkit.getLogger().warning("CANNOT SAVE MINE FILE, REASON ABOVE");
	}

	public void setLevel(int level) {
		this.level = level;
		mineDetails.set("statistics.level", level);

		for (int i = 0; i <= 3; i++)
			try {
				mineDetails.save(f);
				return;
			} catch (IOException e) {
				if (i > 2) {
					e.printStackTrace();
				}
			}

		Bukkit.getLogger().warning("CANNOT SAVE MINE FILE, REASON ABOVE");
	}

	public void upgradeLevel() {
		setLevel(level + 1);
	}

	public void upgradeFortune() {
		setFortune(fortune + 1);
	}

	public void teleportPlayer() {
		p.teleport(tp);
	}

	public void unload() {

		mines.remove(p);

	}

	public void reset() {

		List<CompositionEntry> probabilityMap = mapComposition(getConfiguration().blocks);

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (isInsideMine(p.getLocation())) {
				p.teleport(tp);
			}

		}

		Random rand = new Random();
		for (int x = pos1.getBlockX(); x <= this.pos2.getBlockX(); x++) {
			for (int y = this.pos1.getBlockY(); y <= this.pos2.getBlockY(); y++) {
				double r;
				for (int z = this.pos1.getBlockZ(); z <= this.pos2.getBlockZ(); z++) {

					r = rand.nextDouble();
					for (CompositionEntry ce : probabilityMap) {
						if (r <= ce.getChance()) {
							this.pos1.getWorld().getBlockAt(x, y, z).setTypeIdAndData(ce.getBlock().getBlockId(),
									ce.getBlock().getData(), false);
							break;
						}
					}

				}
			}
		}
	}

	public static ArrayList<CompositionEntry> mapComposition(Map<SerializableBlock, Double> compositionIn) {
		ArrayList<CompositionEntry> probabilityMap = new ArrayList<>();
		Map<SerializableBlock, Double> composition = new HashMap<>(compositionIn);
		double max = 0.0D;
		for (Map.Entry<SerializableBlock, Double> entry : composition.entrySet()) {
			max += ((Double) entry.getValue()).doubleValue();
		}
		if (max < 1.0D) {
			composition.put(new SerializableBlock(0), Double.valueOf(1.0D - max));
			max = 1.0D;
		}
		double i = 0.0D;
		for (Map.Entry<SerializableBlock, Double> entry : composition.entrySet()) {
			double v = ((Double) entry.getValue()).doubleValue() / max;
			i += v;
			probabilityMap.add(new CompositionEntry((SerializableBlock) entry.getKey(), i));
		}
		return probabilityMap;
	}

	public MineConfiguration getConfiguration() {

		List<String> ranks = Main.pl.getConfig().getStringList("ranks");

		for (String temp : ranks) {
			if (p.hasPermission("automine.rank." + temp)) {
				return MineConfiguration.ranks.get(temp);
			}
		}
		return null;

	}

	public void tick() {
		tickTracker++;

		if (defDelay - (reduceDelay * level) < tickTracker) {
			mine();
			tickTracker = 0;
		}
	}

	public void mine() {
		Location loc = getNextBlock();

		loc.getWorld().getBlockAt(loc).setType(Material.AIR);

	}

	public Location getNextBlock() {

		for (int y = this.pos2.getBlockY(); y >= this.pos1.getBlockY(); y--) {
			for (int x = pos2.getBlockX(); x >= this.pos1.getBlockX(); x--) {
				for (int z = this.pos2.getBlockZ(); z >= this.pos1.getBlockZ(); z--) {

					Location loc = new Location(pos2.getWorld(), x, y, z);
					if (pos2.getWorld().getBlockAt(loc).getType() != Material.AIR) {
						return loc;
					}
				}
			}
		}
		reset();
		return pos2;
	}

	public boolean isInsideMine(Location loc) {

		if (pos1 == null) {
			if (loc.getWorld() == Bukkit.getWorld(Main.pl.getConfig().getString("world"))) {
				return false;
			}
			return true;
		}

		if (loc.getWorld() != pos1.getWorld()) {
			return true;
		}

		if (loc.getBlockX() >= pos1.getBlockX() && loc.getBlockX() <= pos2.getBlockX()) {
			if (loc.getBlockY() >= pos1.getBlockY() && loc.getBlockY() <= pos2.getBlockY()) {
				if (loc.getBlockZ() >= pos1.getBlockZ() && loc.getBlockZ() <= pos2.getBlockZ()) {
					return true;
				}
			}
		}
		return false;
	}

	public void sell(Block b, int amount) {
		// TODO
	}

	public void calculateSale(Block b) {
		Random rnd = new Random();
		int amount = rnd.nextInt(fortune) + 1;

		sell(b, amount);
	}

	public int getFortune() {
		return fortune;
	}

	public int getLevel() {
		return level;
	}

}
