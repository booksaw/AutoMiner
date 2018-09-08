package com.booksaw.automine.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.booksaw.automine.Mine;

public class JoinAndLeave implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Mine.join(e.getPlayer());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Mine.leave(e.getPlayer());
	}
}
