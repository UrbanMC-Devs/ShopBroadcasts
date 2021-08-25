# ShopBroadcasts
A plugin that allows players to buy custom messages that are broadcast
at a configurable interval. These messages can be clicked on an teleport
the player depending on what type the broadcast is.

## Dependencies
Dependencies are plugins and/or software that need to be separately installed
on your server.

### Required Dependencies
Dependencies that are required for this plugin to run.
* [PaperMC](https://papermc.io/) 1.17 (or greater)
* Java 16

### Optional Dependencies
Dependencies that are utilized by the plugin and add additional functionality,
but are not required for the plugin to function.
* [Vault](https://www.spigotmc.org/resources/vault.34315/): Vault allows for players to buy advertisements with in-game money.
* [VotingPlugin](https://www.spigotmc.org/resources/votingplugin.15358/): VotingPlugin allows for advertisements to cost vote points.
* PlayerWarps: Allows for players to have advertisements teleport players to their playerwarps.
* [Towny](https://www.spigotmc.org/resources/towny-advanced.72694/) 0.97 (or greater): Allows for players 
to have advertisements teleport other players to their town.

## Installing
Download the plugin from the releases page and install in your plugins directory.

## Basic Player Usage Info
Players can buy shop advertisements with `/shopad buy [message]` and provide a message to be displayed when their
advertisement is broadcast. This advertisement can either cost in-game money or vote points.

Players can also set their advertisement type. Depending on the plugins installed, the in-built types are
"town" or "playerwarp". The advertisement type correlates to what will happen when a player clicks on a shop
advertisement. For example, a type of "town" means that when other players click on the ad, they will be
teleported to the town which the player that owns the ad belongs to.

There are other player commands available as well. All commands have permissions associated with them
(see the `plugin.yml` for the permissions).

## Building
This project uses gradle. To build the project, execute `./gradlew shadowJar --rerun-tasks` in the project
directory.

### Developed by Silverwolfg11