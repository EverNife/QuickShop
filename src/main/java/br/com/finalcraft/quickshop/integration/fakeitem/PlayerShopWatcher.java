package br.com.finalcraft.quickshop.integration.fakeitem;

import org.bukkit.entity.Player;
import org.maxgamer.quickshop.Shop.Shop;

import java.util.ArrayList;
import java.util.List;

public class PlayerShopWatcher {

    private Player player;
    private List<Shop> shopList = new ArrayList<>();

    public PlayerShopWatcher(Player player) {
        this.player = player;
    }

    public void addShop(Shop shop){
        shopList.add(shop);
    }

    public Player getPlayer() {
        return player;
    }

    public List<Shop> getShopList() {
        return shopList;
    }
}
