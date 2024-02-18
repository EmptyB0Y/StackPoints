package com.redsifter.stackpoints;

import com.redsifter.stackpoints.listeners.Listen;
import com.redsifter.stackpoints.utils.FileManager;
import com.redsifter.stackpoints.utils.Game;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public final class StackPoints extends JavaPlugin {
    public static String TEAMNAME1 = "PHANTOMS";
    public static String  TEAMNAME2 = "DRAGONS";
    public static int MAXPLAYERS = 10;
    public static int MAXNUMBER = 3;
    public static int MINTIME = 200;
    public static int MAXTIME = 1060;
    public static int MINLIMIT = 120;
    public static int MAXLIMIT = 300;
    public static Game[] games = new Game[MAXNUMBER];
    public static FileManager fm;

    public enum GAMEMODE {
        TDM (420, 120),
        CTF (420, 150),
        HUNT (240, 130);

        public final int time;
        public final int limit;
        GAMEMODE(int time, int limit){
            this.time = time;
            this.limit = limit;
        }
    }
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new Listen(this), this);
        getDataFolder().mkdir();
        try {
            fm =  new FileManager("inventories.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Initializing config
        this.getLogger().info("Initializing config");
        this.saveDefaultConfig();

        if(this.getConfig().contains("TEAMNAME1")){
            if(this.getConfig().get("TEAMNAME1") != null) {
                MAXPLAYERS = this.getConfig().getInt("TEAMNAME1");
            }
        }

        if(this.getConfig().contains("TEAMNAME2")){
            if(this.getConfig().get("TEAMNAME2") != null) {
                MAXPLAYERS = this.getConfig().getInt("TEAMNAME2");
            }
        }

        if(this.getConfig().contains("MAXPLAYERS")){
            if(this.getConfig().get("MAXPLAYERS") != null) {
                MAXPLAYERS = this.getConfig().getInt("MAXPLAYERS");
            }
        }
        this.getLogger().info("MAXPLAYERS : "+MAXPLAYERS);

        if(this.getConfig().contains("MAXNUMBER")){
            if(this.getConfig().get("MAXNUMBER") != null) {
                MAXNUMBER = this.getConfig().getInt("MAXNUMBER");
            }
        }
        this.getLogger().info("MAXNUMBER : "+MAXNUMBER);

        if(this.getConfig().contains("MINTIME")){
            if(this.getConfig().get("MINTIME") != null) {
                MINTIME = this.getConfig().getInt("MINTIME");
            }
        }
        this.getLogger().info("MINTIME : "+MINTIME);

        if(this.getConfig().contains("MAXTIME")){
            if(this.getConfig().get("MAXTIME") != null) {
                MAXTIME = this.getConfig().getInt("MAXTIME");
            }
        }
        this.getLogger().info("MAXTIME : "+MAXTIME);

        if(this.getConfig().contains("MINLIMIT")){
            if(this.getConfig().get("MINLIMIT") != null) {
                MINLIMIT = this.getConfig().getInt("MINLIMIT");
            }
        }
        this.getLogger().info("MINLIMIT: "+MINLIMIT);

        if(this.getConfig().contains("MAXLIMIT")){
            if(this.getConfig().get("MAXLIMIT") != null) {
                MAXLIMIT = this.getConfig().getInt("MAXLIMIT");
            }
        }
        this.getLogger().info("MAXLIMIT : "+MAXLIMIT);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (sender instanceof Player && label.equals("sp")) {
            switch (args[0]) {
                case("set"):
                    if (countGames() == MAXNUMBER) {
                        sender.sendMessage("Too much games set at this time...\n");
                        return true;
                    }
                    if(playerHasGame((Player) sender) != -1){
                        sender.sendMessage("You already own a game !\n");
                        return true;
                    }
                    int game = countGames() + 1;

                    games[game] = new Game(game,(Player) sender,this);
                    break;
                case("start"):
                    if(args.length < 2){
                        sender.sendMessage("You must at least specify a game mode !");
                        return true;
                    }

                    int nb = playerHasGame((Player) sender);
                    if(nb != -1){
                        try {
                            int time = 0;
                            int limit = 0;

                            switch(args[1].toUpperCase()){
                                case "TDM":
                                    time = GAMEMODE.TDM.time;
                                    limit = GAMEMODE.TDM.limit;
                                    break;
                                case "CTF":
                                    time = GAMEMODE.CTF.time;
                                    limit = GAMEMODE.CTF.limit;
                                    break;
                                case "HUNT":
                                    time = GAMEMODE.HUNT.time;
                                    limit = GAMEMODE.HUNT.limit;
                                    break;
                            }
                            if( args.length > 2){
                                time = Integer.parseInt(args[2]);
                            }

                            if( args.length > 3){
                                limit = Integer.parseInt(args[3]);
                            }

                            if (!games[nb].start(args[1], time, limit)) {
                                sender.sendMessage("Couldn't start game, difference between the two teams is too high !\n");
                                return true;
                            } else {
                                sender.sendMessage("Successfully started game n°" + (nb + 1) + " !\n");
                            }
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else{
                        sender.sendMessage("You don't own a game !");
                    }
                    break;
            }
         }
        return false;
    }

    public boolean areaAvailableFor(Location l, int size, Player p){
        for(Game g : games) {
            if(g != null) {
                if (l.distance(g.spawn) <= size + g.SIZE && g.owner != p) {
                    p.sendMessage("There is no room for a game here ! Try to get a bit further from " + g.spawn.getX() + " " + g.spawn.getY() + " " + g.spawn.getZ());
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean playerInGame(Player pl){
        for(Game g : games){
            if(g != null) {
                if (g.players.contains(pl)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int countGames(){
        int count = 0;
        for (Game game : games) {
            if (game != null) {
                count++;
            }
        }
        return count;
    }

    public int playerHasGame(Player p){
        for (Game game : games) {
            if (game != null) {
                if(game.owner == p) {
                    return game.nb;
                }
            }
        }
        return -1;
    }
    public static void cancelGame(int nb) throws FileNotFoundException {
        if(games[nb] != null){
            if(games[nb].hasStarted) {
                games[nb].cancel();
            }
            for(Player p : games[nb].players){

                p.getInventory().clear();
                for (PotionEffect effect : p.getActivePotionEffects()) {
                    p.removePotionEffect(effect.getType());
                }
                loadinv(p);

                p.setGameMode(GameMode.SURVIVAL);
                p.setInvulnerable(false);
                p.teleport(games[nb].spawn);
            }

            for(Player p : games[nb].specs){
                p.teleport(games[nb].spawn);
                p.setGameMode(GameMode.SURVIVAL);
            }

            for(Team t : games[nb].board.getTeams()) {
                t.unregister();
            }
            games[nb].t1.flush();
            games[nb].t2.flush();
            games[nb].delScoreBoard();
            games[nb] = null;
            ArrayUtils.removeElement(games, games[nb]);
            System.gc();
        }
    }

    public static void saveinv(Player p) throws FileNotFoundException {
        fm.reloadConfig();
        int count = 0;
        fm.getConfig().set(p.getUniqueId().toString() + ".inventory", null);
        for (ItemStack it : p.getInventory()) {
            if (it != null) {
                count++;
                fm.getConfig().set(p.getUniqueId().toString() + ".inventory." + count, it);
            }
        }
        fm.getConfig().set(p.getUniqueId().toString() + ".count", count);
        try {
            fm.saveConfig();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadinv(Player p) throws FileNotFoundException {
        if (fm.getConfig().contains(p.getUniqueId().toString())) {

            ArrayList<ItemStack> inv = new ArrayList<ItemStack>();
            fm.reloadConfig();
            int nbr = fm.getConfig().getInt(p.getUniqueId().toString() + ".count");
            for (int i = 1; i <= nbr; i++) {
                inv.add(fm.getConfig().getItemStack(p.getUniqueId().toString() + ".inventory." + i));
            }
            fm.getConfig().set(p.getUniqueId().toString() + ".inventory", null);
            try {
                fm.saveConfig();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
            for (ItemStack it : inv) {
                if (it != null) {
                    p.getInventory().addItem(it);

                }
            }
        }
    }
    public static double randDouble(double min, double max) {
        return min + Math.random() * ((max - min));
    }

}
