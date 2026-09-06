execute if score phoenix_core core_amount matches 0 run core give @s phoenix_core
execute if score phoenix_core core_amount matches 0 run title @a title {"text":"Phoenix Core Earned!","color":"#FFAA00","bold":true}
execute if score phoenix_core core_amount matches 0 run title @a subtitle [{"selector":"@s","color":"#FFFF55","bold":true},{"text":" just got the ","color":"#E0E0E0"},{"text":"Phoenix Core!","color":"#FF5555","bold":true}]
execute if score phoenix_core core_amount matches 0 run scoreboard players add phoenix_core core_amount 1