package br.com.finalcraft.quickshop.integration.listener;

import br.com.finalcraft.quickshop.integration.fakeitem.FakeItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.maxgamer.quickshop.Shop.ShopCreateEvent;

public class QuickShopRenderListener implements Listener{

    @EventHandler(priority = EventPriority.MONITOR)
    public void onShopCreate(final ShopCreateEvent event){
        if (!event.isCancelled()){
            FakeItemManager.sendFakeItemsToThemAll(event.getShop(), false);
        }
    }

}
