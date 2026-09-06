execute if score gravity_core core_amount matches 0 run core give @s gravity_core
execute if score gravity_core core_amount matches 0 run title @a title {"text":"Gravity Core Earned!","color":"#FFAA00","bold":true}
execute if score gravity_core core_amount matches 0 run title @a subtitle [{"selector":"@s","color":"#FFFF55","bold":true},{"text":" just got the ","color":"#E0E0E0"},{"text":"Gravity Core!","color":"#AA00AA","bold":true}]
execute if score gravity_core core_amount matches 0 run scoreboard players add gravity_core core_amount 1