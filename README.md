# Download
- You can download the plugin in compiled form on [Modrinth](https://modrinth.com/plugin/simple_baguette)

# Description
### En
>This plugin adds a Baguette with its own recipe. The plugin has a configuration in which you can configure the plugin in detail, change the name of the baguette, description, etc.

### Ru
>Данный плагин добавляет Багет со своим рецептом. Плагин имеет конфигурацию, в которой вы можете подробно настроить плагин, изменить название багета, описание и так далее


# Config
### config.yml
```
crafting-enabled: true

item:
  name: "&6Baguette"
  lore:
    - "" # Empty line - no description added
  custom-model-data: 1
  stack-size: 64

crafting:
  ingredients:
    - BREAD
    - BREAD
  result-amount: 1

crafting-types:
  workbench: true
  furnace: false
```
