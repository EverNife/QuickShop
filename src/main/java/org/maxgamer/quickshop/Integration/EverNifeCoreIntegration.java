package org.maxgamer.quickshop.Integration;

import br.com.finalcraft.evernifecore.fcitemstack.FCItemStack;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class EverNifeCoreIntegration {

    private static Boolean present = null;

    public static boolean isPresent(){
        return present != null ? present : (present = Bukkit.getPluginManager().isPluginEnabled("EverNifeCore"));
    }

    public static String getMinecraftIdentifier(ItemStack itemStack){
        return new FCItemStack(itemStack).getMinecraftIdentifier();
    }

    public static ItemStack fromMinecraftIdentifier(String minecraftIdentifier){
        return FCItemStack.fromMinecraftIdentifier(minecraftIdentifier).getItemStack();
    }

}
