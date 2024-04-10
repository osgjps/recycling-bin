package io.github.emojiconmc.recyclingbin.block;

//import com.mojang.authlib.GameProfile;
//import com.mojang.authlib.properties.Property;
import io.github.emojiconmc.recyclingbin.RecyclingBinPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;

//import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;


import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.net.URL;



public class RecyclingBlock {

    private static RecyclingBlock instance;
    private ItemStack itemStack;

    private RecyclingBlock(RecyclingBinPlugin plugin) {

		PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(),ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("recycling-block-name", ChatColor.GRAY + "Recycling Bin"))); // Get a new player profile
		PlayerTextures textures = profile.getTextures();
		URL urlObject;
		try {
			String url = "http://textures.minecraft.net/texture/9ce9bfa474e280ab28536dde3a768f7be2c141a4658c99c5954222f86f4f413b";
			urlObject = new URL(url); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
		} catch (java.net.MalformedURLException exception) {
			throw new RuntimeException("Invalid URL", exception);
		}
		textures.setSkin(urlObject); // Set the skin of the player profile to the URL
		profile.setTextures(textures);
		
		
        itemStack = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("recycling-block-name", ChatColor.GRAY + "Recycling Bin")));
		meta.setOwnerProfile(profile); // Set the owning player of the head to the player profile
		itemStack.setItemMeta(meta);
		
    }

    public void initRecipe(RecyclingBinPlugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "recycling_block_recipe");
        ShapedRecipe recipe = new ShapedRecipe(key, itemStack);

        List<String> recipeShapeList = plugin.getConfig().getStringList("recipe-shape");
        if (recipeShapeList.size() != 3) {
            plugin.getLogger().warning("Improper recipe shape in config.yml!");
            return;
        }

        for (String item : recipeShapeList) {
            if (item.length() != 3) {
                plugin.getLogger().warning("Improper recipe length in config.yml!");
                return;
            }
        }

        recipe.shape(recipeShapeList.get(0), recipeShapeList.get(1), recipeShapeList.get(2));

        if (!plugin.getConfig().isConfigurationSection("recipe-ingredients")) {
            return;
        }

        for (String ingredientKeyValue : plugin.getConfig().getConfigurationSection("recipe-ingredients").getKeys(false)) {
            if (ingredientKeyValue.length() != 1) {
                plugin.getLogger().warning("Improper recipe ingredient keys in config.yml!");
                return;
            }

            char ingredientKey = ingredientKeyValue.charAt(0);
            String ingredient = plugin.getConfig().getString("recipe-ingredients." + ingredientKey);
            if (ingredient == null) {
                plugin.getLogger().warning("Invalid recipe ingredients in config.yml!");
                return;
            }

            Material type = Material.getMaterial(ingredient);
            if (type == null) {
                plugin.getLogger().warning("Invalid recipe ingredients in config.yml!");
                return;
            }

            recipe.setIngredient(ingredientKey, type);
        }

        Bukkit.addRecipe(recipe);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public static RecyclingBlock getInstance(RecyclingBinPlugin plugin) {
        if (instance == null) {
            instance = new RecyclingBlock(plugin);
        }

        return instance;
    }
}
