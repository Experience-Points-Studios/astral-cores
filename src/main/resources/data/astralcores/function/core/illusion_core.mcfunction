execute if score illusion_core core_amount matches 0 run core give @s illusion_core
execute if score illusion_core core_amount matches 0 run title @a title {"text":"Illusion Core Earned!","color":"#FFAA00","bold":true}
execute if score illusion_core core_amount matches 0 run title @a subtitle [{"selector":"@s","color":"#FFFF55","bold":true},{"text":" just got the ","color":"#E0E0E0"},{"text":"Illusion Core!","color":"#FF55FF","bold":true}]
execute if score illusion_core core_amount matches 0 run scoreboard players add illusion_core core_amount 1