# Simple Villagers

A serverside fabric mod inspired by https://github.com/henkelmax/easy-villagers.



https://user-images.githubusercontent.com/34912839/155026528-754712a2-32f3-42a9-b14c-8fd027b7bab0.mp4



## Permissions
* `simplevillagers.reroll_buttons` - whether to enable reroll buttons
* `simplevillagers.villager_item.pickup` - whether to allow picking up the villager
* `simplevillagers.villager_item.spawn` - whether to allow spawning the villager from the item

*Hey, do you know that spawn eggs can be put in spawner?*
Don't worry, even though you get a villager item that looks like spawn egg, it actually isn't under the hood.
~~It just uses spawn egg texture as I was too lazy to draw my own.~~

## Blocks
* all delays are configurable
* note: can be installed **serverside** as well, but clients will see colored glass blocks if not having the mod
* Breeder block
    * 1 baby villager item / 5 minutes
    * <details>
      <summary>Images</summary>
      <br>
         <img src="https://user-images.githubusercontent.com/34912839/156937230-259415e2-cb94-40ea-abba-b2cf8c409942.png" />

         <img src="https://user-images.githubusercontent.com/34912839/156937311-7d3f66c6-b8c1-4bdd-ab3a-e88c330c5dda.png" />
      </details>



* Iron Farm Block
    * Summons golem every 4 minutes.
    * <details>
      <summary>Images</summary>
      <br>
         <img src="https://user-images.githubusercontent.com/34912839/156937202-6748d561-7aaf-4b29-9ebe-650528f407b3.png" />

         <img src="https://user-images.githubusercontent.com/34912839/156937284-bfa53a48-748e-43ed-b15e-473891f3e2bc.png" />
      </details>



## Why so big jar file?
SimpleVillagers wouldn't have been possible without the help of the following libraries:
* [SGUI](https://github.com/Patbox/sgui)
* [Polymer](https://github.com/Patbox/polymer)
