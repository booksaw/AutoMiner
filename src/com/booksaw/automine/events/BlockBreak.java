package com.booksaw.automine.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.booksaw.automine.Mine;

public class BlockBreak implements Listener {

	@EventHandler
	public void onBreak(BlockBreakEvent e) {

		Mine m = Mine.mines.get(e.getPlayer());

		if (m.isInsideMine(e.getBlock().getLocation())) {
			return;
		} else {
			e.setCancelled(true);
			return;
		}

	}
}
