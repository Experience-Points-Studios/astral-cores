execute if score nature_core core_amount matches 0 run core give @s nature_core
execute if score nature_core core_amount matches 0 run title @a title {"text":"Nature Core Earned!","color":"#FFAA00","bold":true}
execute if score nature_core core_amount matches 0 run title @a subtitle [{"selector":"@s","color":"#FFFF55","bold":true},{"text":" just got the ","color":"#E0E0E0"},{"text":"Nature Core!","color":"#55FF55","bold":true}]
execute if score nature_core core_amount matches 0 run scoreboard players add nature_core core_amount 1