execute if score chrono_core core_amount matches 0 run core give @s chrono_core
execute if score chrono_core core_amount matches 0 run title @a title {"text":"Chrono Core Earned!","color":"#FFAA00","bold":true}
execute if score chrono_core core_amount matches 0 run title @a subtitle [{"selector":"@s","color":"#FFFF55","bold":true},{"text":" just got the ","color":"#E0E0E0"},{"text":"Chrono Core!","color":"#FFAA00","bold":true}]
execute if score chrono_core core_amount matches 0 run scoreboard players add chrono_core core_amount 1