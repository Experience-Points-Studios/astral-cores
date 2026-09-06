execute if score frost_core core_amount matches 0 run core give @s frost_core
execute if score frost_core core_amount matches 0 run title @a title {"text":"Frost Core Earned!","color":"#FFAA00","bold":true}
execute if score frost_core core_amount matches 0 run title @a subtitle [{"selector":"@s","color":"#FFFF55","bold":true},{"text":" just got the ","color":"#E0E0E0"},{"text":"Frost Core!","color":"#55FFFF","bold":true}]
execute if score frost_core core_amount matches 0 run scoreboard players add frost_core core_amount 1