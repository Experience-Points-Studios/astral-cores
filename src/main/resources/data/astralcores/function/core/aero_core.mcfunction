execute if score aero_core core_amount matches 0 run core give @s aero_core
execute if score aero_core core_amount matches 0 run title @a title {"text":"Aero Core Earned!","color":"#FFAA00","bold":true}
execute if score aero_core core_amount matches 0 run title @a subtitle [{"selector":"@s","color":"#FFFF55","bold":true},{"text":" just got the ","color":"#E0E0E0"},{"text":"Aero Core!","color":"#5555FF","bold":true}]
execute if score aero_core core_amount matches 0 run scoreboard players add aero_core core_amount 1