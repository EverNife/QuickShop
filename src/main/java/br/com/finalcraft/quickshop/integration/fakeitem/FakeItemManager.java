package br.com.finalcraft.quickshop.integration.fakeitem;

import br.com.finalcraft.evernifecore.nms.util.NMSUtils;
import br.com.finalcraft.evernifecore.scheduler.FCScheduler;
import br.com.finalcraft.irondome.common.network.api.FakeItemRenderPacket;
import br.com.finalcraft.irondome.common.network.api.NetworkAPI;
import br.com.finalcraft.quickshop.integration.listener.QuickShopRenderListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Shop.Shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FakeItemManager {

	private static Thread thread;
	public static void initialize(){
		if (thread != null) {
			thread.interrupt();
		} else {
			//Only register the listener on the First call of initialize()
			Bukkit.getServer().getPluginManager().registerEvents(new QuickShopRenderListener(), QuickShop.instance);
		}
		thread = new Thread(){
			@Override
			public void run() {
				try {
					while (true){
						Thread.sleep(5 * 1000);
						sendFakeItemsToThemAll(false);
					}
				}catch (Exception e){
					QuickShop.instance.getLogger().warning("Error at FakeItemManager Thread");
					e.printStackTrace();
				}
			}
		};
		thread.setName("FakeItemManager");
		thread.start();
	}

	private static boolean isNearEnough(Location origin, Location target){
		return origin.getWorld() == target.getWorld() && origin.distanceSquared(target) < 1600;// sqrt(1600) == 40
	}

	public static void sendFakeItemsToThemAll(final Shop shop, boolean wasDeleted){
		FCScheduler.runAssync(() -> {
			Location shopLocation = shop.getLocation();
			ItemStack itemStack = shop.getItem();
			Object mcItemStack = wasDeleted ? null : NMSUtils.get().asMinecraftItemStack(itemStack);
			new ArrayList<>(Bukkit.getOnlinePlayers())
					.stream()
					.filter(player -> isNearEnough(shopLocation, player.getLocation()))
					.forEach(player -> {
								NetworkAPI.renderItems(player.getPlayer().getName(), Arrays.asList(
										new FakeItemRenderPacket(
												mcItemStack,
												shop.getLocation().getBlockX(),
												shop.getLocation().getBlockY() + 1,
												shop.getLocation().getBlockZ()
										)
								), false);
							}
					);
		});
	}

	public static void sendFakeItemsToThemAll(boolean reload){
		FCScheduler.runAssync(() -> {

			if(QuickShop.debug)System.out.println("Starting Send of FakeItems to players");

			List<Shop> loadedShops = new ArrayList<>();
			QuickShop.instance.getShopManager().getShopIterator().forEachRemaining(shop -> {
				int xPos = shop.getLocation().getBlockX();
				int zPos = shop.getLocation().getBlockZ();

				if (shop.getLocation().getWorld().isChunkLoaded(xPos >> 4, zPos >> 4)){
					loadedShops.add(shop);
				}
			});

			List<PlayerShopWatcher> playerShopWatchers =
					new ArrayList<>(Bukkit.getOnlinePlayers())
							.stream()
							.map(player -> new PlayerShopWatcher(player))
							.collect(Collectors.toList());

			for (PlayerShopWatcher playerShopWatcher : playerShopWatchers) {
				for (Shop loadedShop : loadedShops) {
					Location shopLocation = loadedShop.getLocation();
					if (isNearEnough(shopLocation, playerShopWatcher.getPlayer().getLocation())){
						playerShopWatcher.addShop(loadedShop);
					}
				}
			}

			for (PlayerShopWatcher playerShopWatcher : playerShopWatchers) {
				if (playerShopWatcher.getShopList().size() == 0 || !playerShopWatcher.getPlayer().isOnline()){
					continue;
				}

				List<FakeItemRenderPacket> renderPacketList = new ArrayList<>();
				for (Shop shop : playerShopWatcher.getShopList()) {
					ItemStack itemStack = shop.getItem();

					renderPacketList.add(new FakeItemRenderPacket(
							NMSUtils.get().asMinecraftItemStack(itemStack),
							shop.getLocation().getBlockX(),
							shop.getLocation().getBlockY() + 1,
							shop.getLocation().getBlockZ()
					));

				}

				if(QuickShop.debug)System.out.println("Sending " + renderPacketList.size()  + " fakeitems to " + playerShopWatcher.getPlayer().getName());
				NetworkAPI.renderItems(playerShopWatcher.getPlayer().getName(), renderPacketList, reload);
			}

		});
	}


}