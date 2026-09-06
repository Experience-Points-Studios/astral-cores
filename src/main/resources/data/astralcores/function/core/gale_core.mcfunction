execute if score gale_core core_amount matches 0 run core give @s gale_core
execute if score gale_core core_amount matches 0 run title @a title {"text":"Gale Core Earned!","color":"#FFAA00","bold":true}
execute if score gale_core core_amount matches 0 run title @a subtitle [{"selector":"@s","color":"#FFFF55","bold":true},{"text":" just got the ","color":"#E0E0E0"},{"text":"Gale Core!","color":"#55FFFF","bold":true}]
execute if score gale_core core_amount matches 0 run scoreboard players add gale_core core_amount 1