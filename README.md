# HuskycratesExtension

## What is it? 

Huskycrates extension is a plugin for minecraft which extends huskycrates using huskycrates and huskyui.  
The purpose of this plugin is to add certain features i would have liked to see and you might like aswell. 

## What does it contain? 

### Commands: 
#### give  
  Usage: hce give \<Player\> \<Crate\> (\<Amount\>)  
  Description: give away your virtual keys to someone else  
  Permission: huskycratesextension.give  

#### reload  
  Usage: hce reload  
  Description: reloads the huskycrates extension config and huskycrates crates  
  Permission: huskycratesextension.admin.reload  
  Note: will also execute /hc reload  
  
#### rightclick  
  Usage: hce rightclick \<add/remove\> \<Crate\>  
  Description: add or removes crate with the ability to be claimed by right clicking the key  
  Permission: huskycratesextension.admin.managerightclick  
  
#### convert  
  Usage: hce convert (\<Crate ID\>) (\<Amount\>)  
  Description: convert physical key to virtual key (if no crateid is given) or the other way around  
  Permission: huskycratesextension.convert  
  
#### test
  Usage: hce test \<Crate ID\>  
  Description: Opens a gui menu, on click executes slot  
  Permission: huskycratesextension.admin.test  
  
#### testall 
  Usage: hce testall \<Crate ID\>  
  Description: Executes all rewards in the crate after eachother  
  Permission: huskycratesextension.admin.test.all  
  
#### addreward
  Usage: hce addreward \<Crate ID\> \<Reward type\> (\<Data\>)  
  Description: add reward to given crate of given type and data  
  Permission: huskycratesextension.admin.addreward  

#### memory
  Usage: hce memory \<Crate ID\>  
  Description: Starts a memory game, find 2 of the same and receive a reward
  Permission: huskycratesextension.memory  
  Note: Only works if the crate has 18+ slots
  
#### forcegive 
  Usage: hce forcegive (\<v/virtual\>) \<Crate ID\> \<Player\> (\<Amount\>)  
  Description: gives a key without auto completion on player  
  Permission: huskycratesextension.admin.forcegive  
  
#### addcommanditem 
  Usage: hce addcommanditem \<Command item ID\> \<command\>  
  Description: Creates an item which on right click executes a command  
  Permission: huskycratesextension.admin.addcommanditem  
  
#### givecommanditem  
  Usage: hce givecommanditem \<player\> \<ID\> (\<amount\>)  
  Description: Gives a command item from a given ID  
  Permission: huskycratesextension.admin.givecommanditem  
  
#### giverandomreward 
  Usage: hce random \<Player\> \<CrateID\>  
  Description: takes a random reward and executes it.  
  Permission: huskycratesextension.admin.random  
  
### bal
  Usage: hce bal (\<Player\>)  
  Description: Show the balance of player's keys  
  Permissions:  
- huskycrates.bal.base (to see own balance ) 
- huskycratesextension.admin.checkbalanceothers (to see other players balances (can be used when the player offline))


### Others:

#### Open from distance 
  Usage: Hold key in main hand and right click with it  
  Requirements: Crate name in config  
  
#### Preview crate from distance
  Usage: Hold key in main hand and left click with it  
  Requirements: none  
  
#### Unplaceable
  Description: Makes huskycrates keys and item commands unplaceable 

#### Customizable configs
   * Customize most messages (remove)/change) 
   * Customize memory game  
      * How many chances  
      * Multiple prizes  
      * Which crates can play memory  
   * What crate can not be tested
   * Item command storage + edits

### Interactable text  
* When using /hce bal , on hover over text will print text to click to convert. on click will convert clicked key.  


## Notes
  * Beginner in writing plugins, so can't tell how good it is for server performance  
  * Only 1 author: Gamefreak_2302 
  * Contact me in the [huskycrates discord](https://discord.gg/MUXdDmHQ4a)  
  * following mods required: 
    * [Huskycrates (2.0.0RC2-2.0.0RC3 tested)](https://ore.spongepowered.org/codeHusky/HuskyCrates) 
    * [HuskyUI 0.6.0PRE4](https://ore.spongepowered.org/codeHusky/HuskyUI)
  * This plugin is FREE , you can use it as much as you like and share it with anyone.
  * Don't claim this plugin to be yours, copyright is a nasty law 
  * Feeling into donating? donate to [huskycrates](https://ore.spongepowered.org/codeHusky/HuskyCrates) , it's their plugin that makes this possible.
  
