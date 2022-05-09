package org.maxgamer.quickshop.Util;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Permissions {
	private Permissions() {}
	private static Permission provider;
	
	public static boolean init() {
		RegisteredServiceProvider<Permission> registeredService = Bukkit.getServicesManager().getRegistration(Permission.class);
		if (registeredService!=null) {
			provider = registeredService.getProvider();
		}
		return provider != null;
	}
	
	public static boolean hasPermission(OfflinePlayer player, String permission) {
		try {
			return provider.playerHas(null, player, permission);
		} catch (Exception e) {
			return false;
		}
	}
}
