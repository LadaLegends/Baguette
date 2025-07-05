package com.ladalegends.baguette;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baguette extends JavaPlugin {

    private boolean craftingEnabled = true;
    private List<String> craftIngredients = new ArrayList<>();
    private String itemName = "Baguette";
    private List<String> itemLore = new ArrayList<>();
    private boolean workbenchCrafting = true;
    private boolean furnaceCrafting = false;
    private int resultAmount = 1;
    private int customModelData = 0;
    private int stackSize = 64; // Note: stack size is limited by Material.BREAD (64)

    private final List<NamespacedKey> registeredRecipes = new ArrayList<>();

    @Override
    public void onEnable() {
        try {
            getLogger().info("Initializing Baguette plugin...");
            saveDefaultConfig();
            loadConfig();

            ItemStack baguette = new ItemStack(Material.BREAD, resultAmount);
            ItemMeta meta = baguette.getItemMeta();
            if (meta != null) {
                // First try to process ampersand codes (e.g., &6)
                String displayName = ChatColor.translateAlternateColorCodes('&', itemName);
                // If no ampersand code, check for hex
                if (displayName.equals(itemName) && itemName.startsWith("#") && itemName.length() >= 7) {
                    String hexColor = itemName.substring(0, 7);
                    String text = itemName.replaceFirst("#[0-9A-Fa-f]{6}", "");
                    displayName = ChatColor.translateAlternateColorCodes('&', hexColor + text);
                }
                meta.setDisplayName(displayName);
                List<String> translatedLore = new ArrayList<>();
                for (String line : itemLore) {
                    if (!line.isEmpty() && !"".equals(line.trim())) {
                        String lineText = ChatColor.translateAlternateColorCodes('&', line);
                        if (lineText.equals(line) && line.startsWith("#") && line.length() >= 7) {
                            String lineHex = line.substring(0, 7);
                            String lineContent = line.replaceFirst("#[0-9A-Fa-f]{6}", "");
                            lineText = ChatColor.translateAlternateColorCodes('&', lineHex + lineContent);
                        }
                        translatedLore.add(lineText);
                    }
                }
                if (!translatedLore.isEmpty()) {
                    meta.setLore(translatedLore);
                }
                if (customModelData > 0) {
                    meta.setCustomModelData(customModelData);
                }
                baguette.setItemMeta(meta);
            } else {
                getLogger().warning("ItemMeta is null, skipping metadata setup.");
            }

            if (craftingEnabled) {
                if (workbenchCrafting && !craftIngredients.isEmpty()) {
                    NamespacedKey key = new NamespacedKey(this, "baguette_workbench");
                    ShapedRecipe recipe = new ShapedRecipe(key, baguette);
                    recipe.shape("BB ", "   ", "   ");
                    boolean validIngredients = false;
                    for (String ingredient : craftIngredients) {
                        try {
                            Material mat = Material.valueOf(ingredient.toUpperCase());
                            recipe.setIngredient('B', new RecipeChoice.MaterialChoice(mat));
                            validIngredients = true;
                        } catch (IllegalArgumentException e) {
                            getLogger().warning("Invalid material in config: " + ingredient + ". Using BREAD as default.");
                            recipe.setIngredient('B', new RecipeChoice.MaterialChoice(Material.BREAD));
                            break;
                        }
                    }
                    if (validIngredients) {
                        if (getServer().addRecipe(recipe)) {
                            registeredRecipes.add(key);
                            getLogger().info("Workbench recipe successfully registered for key: " + key.getKey());
                        } else {
                            getLogger().severe("Failed to register workbench recipe for key: " + key.getKey());
                        }
                    } else {
                        getLogger().warning("Workbench recipe not registered due to invalid ingredients.");
                    }
                }
                if (furnaceCrafting) {
                    NamespacedKey key = new NamespacedKey(this, "baguette_furnace");
                    FurnaceRecipe recipe = new FurnaceRecipe(key, baguette, Material.BREAD, 0.0f, 200);
                    if (getServer().addRecipe(recipe)) {
                        registeredRecipes.add(key);
                        getLogger().info("Furnace recipe successfully registered for key: " + key.getKey());
                    } else {
                        getLogger().severe("Failed to register furnace recipe for key: " + key.getKey());
                    }
                }
            }

            getLogger().info(ChatColor.GREEN + "=========================");
            getLogger().info(ChatColor.GREEN + "Baguette plugin version " + getDescription().getVersion() + " successfully enabled!");
            getLogger().info(ChatColor.GREEN + "=========================");
        } catch (Exception e) {
            getLogger().severe("Error enabling plugin: " + e.getMessage());
            e.printStackTrace();
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        for (NamespacedKey key : registeredRecipes) {
            if (getServer().getRecipe(key) != null) {
                getServer().removeRecipe(key);
                getLogger().info("Recipe " + key.getKey() + " removed.");
            }
        }
        registeredRecipes.clear();

        getLogger().info(ChatColor.RED + "=========================");
        getLogger().info(ChatColor.RED + "Baguette plugin version " + getDescription().getVersion() + " disabled.");
        getLogger().info(ChatColor.RED + "=========================");
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        craftingEnabled = config.getBoolean("crafting-enabled", true);
        itemName = config.getString("item.name", "Baguette");
        itemLore = config.getStringList("item.lore");
        craftIngredients = config.getStringList("crafting.ingredients");
        workbenchCrafting = config.getBoolean("crafting-types.workbench", true);
        furnaceCrafting = config.getBoolean("crafting-types.furnace", false);
        resultAmount = config.getInt("crafting.result-amount", 1);
        customModelData = config.getInt("item.custom-model-data", 0);
        stackSize = config.getInt("item.stack-size", 64);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("baguette")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("baguette.reload")) {
                    reloadConfig();
                    loadConfig();
                    for (NamespacedKey key : registeredRecipes) {
                        if (getServer().getRecipe(key) != null) {
                            getServer().removeRecipe(key);
                        }
                    }
                    registeredRecipes.clear();
                    onEnable();
                    sender.sendMessage(ChatColor.GREEN + "Baguette configuration reloaded!");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission for this command!");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("baguette") && args.length == 1) {
            List<String> completions = new ArrayList<>();
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
            return completions;
        }
        return Collections.emptyList();
    }
}