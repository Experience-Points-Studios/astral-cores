execute if score berserker_core core_amount matches 0 run core give @s berserker_core
execute if score berserker_core core_amount matches 0 run title @a title {"text":"Berserker Core Earned!","color":"#FFAA00","bold":true}
execute if score berserker_core core_amount matches 0 run title @a subtitle [{"selector":"@s","color":"#FFFF55","bold":true},{"text":" just got the ","color":"#E0E0E0"},{"text":"Berserker Core!","color":"#AA0000","bold":true}]
execute if score berserker_core core_amount matches 0 run scoreboard players add berserker_core core_amount 1