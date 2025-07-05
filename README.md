# En
This plugin adds a Baguette with its own recipe. The plugin has a configuration in which you can configure the plugin in detail, change the name of the baguette, description, etc.

# Ru
Данный плагин добавляет Багет со своим рецептом. Плагин имеет конфигурацию, в которой вы можете подробно настроить плагин, изменить название багета, описание и так далее

# config.yml
crafting-enabled: true

item:
  name: "&6Baguette" # Ampersand code &6 for gold color
  lore:
    - "" # Empty line - no description added
  custom-model-data: 1 # Unique model ID for resource pack
  stack-size: 64 # Maximum stack size (1-64)

crafting:
  ingredients:
    - BREAD
    - BREAD
  result-amount: 1 # Number of baguettes crafted

# Available crafting types
crafting-types:
  workbench: true
  furnace: false
