# Simple Villagers

[![https://discord.gg/9PAesuHFnp](https://img.shields.io/discord/797713290545332235?logo=discord&style=flat-square)](https://discord.gg/9PAesuHFnp)
[![GitHub license](https://img.shields.io/github/license/samolego/SimpleVillagers?style=flat-square)](https://github.com/samolego/SimpleVillagers/blob/master/LICENSE)
[![Server environment](https://img.shields.io/badge/Environment-server-blue?style=flat-square)](https://github.com/samolego/SimpleVillagers)
[![Singleplayer environment](https://img.shields.io/badge/Environment-singleplayer-yellow?style=flat-square)](https://github.com/samolego/SimpleVillagers)

A serverside fabric mod inspired by https://github.com/henkelmax/easy-villagers.



https://user-images.githubusercontent.com/34912839/155026528-754712a2-32f3-42a9-b14c-8fd027b7bab0.mp4



## Permissions
* `simplevillagers.reroll_buttons` - whether to enable reroll buttons
* `simplevillagers.villager_item.pickup` - whether to allow picking up the villager
* `simplevillagers.villager_item.spawn` - whether to allow spawning the villager from the item

"Can't spawn eggs can be put in spawner?"

Don't worry, even though you get a villager item that looks like spawn egg, it actually isn't under the hood.
~~It just uses spawn egg texture as I was too lazy to draw my own.~~

## Blocks
* all delays are configurable
* note: can be installed **serverside** as well, but clients will see colored glass blocks if not having the mod
* Breeder block
    * 1 baby villager item / 5 minutes
    * <details>
      <summary>Recipe</summary>
      <br>
         <img src="https://user-images.githubusercontent.com/34912839/156937230-259415e2-cb94-40ea-abba-b2cf8c409942.png" />

         <img src="https://user-images.githubusercontent.com/34912839/156937311-7d3f66c6-b8c1-4bdd-ab3a-e88c330c5dda.png" />
      </details>
      
* Incubator block
    * Grows baby vilagers faster (configurable)
    * <details>
      <summary>Recipe</summary>
      <br>
         (Any type of wool can be used)
         <img src="https://user-images.githubusercontent.com/34912839/163707340-486f19b9-a12f-4f0f-a8cc-33fe2d259cc5.png" />

         <img src="https://user-images.githubusercontent.com/34912839/163707089-8187995a-2cc5-4c3b-95bf-2ece85c83ff9.png" />
      </details>
            
* Converter block
    * Converts villagers to zombies and cures them.
    * <details>
      <summary>Recipe</summary>
      <br>
         <img src="https://user-images.githubusercontent.com/34912839/163707304-5fa48094-16c0-4fb3-8036-f50dfb7a4526.png" />

         <img src="https://user-images.githubusercontent.com/34912839/163707189-642fd6b5-d480-425b-bc48-cb5931f128ad.png" />
         <img src="https://user-images.githubusercontent.com/34912839/163707217-1bd9f8e4-0cc6-42b1-a87c-d24dfbced68d.png" />
      </details>



* Iron Farm Block
    * Summons golem every 4 minutes.
    * <details>
      <summary>Recipe</summary>
      <br>
         <img src="https://user-images.githubusercontent.com/34912839/156937202-6748d561-7aaf-4b29-9ebe-650528f407b3.png" />

         <img src="https://user-images.githubusercontent.com/34912839/156937284-bfa53a48-748e-43ed-b15e-473891f3e2bc.png" />
      </details>



## Why so big jar file?
SimpleVillagers wouldn't have been possible without the help of the following libraries:
* [SGUI](https://github.com/Patbox/sgui)
* [Polymer](https://github.com/Patbox/polymer)
