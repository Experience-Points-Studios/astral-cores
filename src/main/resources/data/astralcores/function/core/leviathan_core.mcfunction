execute if score leviathan_core core_amount matches 0 run core give @s leviathan_core
execute if score leviathan_core core_amount matches 0 run title @a title {"text":"Leviathan Core Earned!","color":"#FFAA00","bold":true}
execute if score leviathan_core core_amount matches 0 run title @a subtitle [{"selector":"@s","color":"#FFFF55","bold":true},{"text":" just got the ","color":"#E0E0E0"},{"text":"Leviathan Core!","color":"#0055FF","bold":true}]
execute if score leviathan_core core_amount matches 0 run scoreboard players add leviathan_core core_amount 1