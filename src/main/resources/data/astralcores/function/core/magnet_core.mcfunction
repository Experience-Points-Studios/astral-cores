execute if score magnet_core core_amount matches 0 run core give @s magnet_core
execute if score magnet_core core_amount matches 0 run title @a title {"text":"Magnet Core Earned!","color":"#FFAA00","bold":true}
execute if score magnet_core core_amount matches 0 run title @a subtitle [{"selector":"@s","color":"#FFFF55","bold":true},{"text":" just got the ","color":"#E0E0E0"},{"text":"Magnet Core!","color":"#FF55AA","bold":true}]
execute if score magnet_core core_amount matches 0 run scoreboard players add magnet_core core_amount 1