execute if score shadow_core core_amount matches 0 run core give @s shadow_core
execute if score shadow_core core_amount matches 0 run title @a title {"text":"Shadow Core Earned!","color":"#FFAA00","bold":true}
execute if score shadow_core core_amount matches 0 run title @a subtitle [{"selector":"@s","color":"#FFFF55","bold":true},{"text":" just got the ","color":"#E0E0E0"},{"text":"Shadow Core!","color":"#5500AA","bold":true}]
execute if score shadow_core core_amount matches 0 run scoreboard players add shadow_core core_amount 1