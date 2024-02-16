package com.redsifter.stackpoints.utils;

import com.redsifter.stackpoints.StackPoints;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Game extends BukkitRunnable {
    public CustomTeam t1;
    public CustomTeam t2;
    public int nb;
    public int time;
    public int timeset;
    private StackPoints main;
    public boolean hasStarted = false;
    public Player owner;
    public Location spawn;

    public boolean full = false;
    public String mode = "normal";
    private HashMap<Player,Location> limit = new HashMap<Player,Location>();
    public HashMap<ArmorStand,Boolean> chests = new HashMap<ArmorStand,Boolean>();
    public HashMap<Player,Integer> lastBonus = new HashMap<Player,Integer>();
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Player> specs = new ArrayList<>();
    public Channel spectator = new Channel("spectators",ChatColor.GRAY);

    public int SIZE;
    //SCOREBOARD
    private final ScoreboardManager manager = Bukkit.getScoreboardManager();
    public Scoreboard board = manager.getMainScoreboard();
    private final Objective timer = board.registerNewObjective("timerSP"+nb, "test", "TIMER");
    private int cursor = 0;
    public Game(int n, Player gameOwner, StackPoints sp){
        CustomTeam t1 = new CustomTeam(1, StackPoints.TEAMNAME1);
        CustomTeam t2 = new CustomTeam(2, StackPoints.TEAMNAME2);

        nb = n;
        owner = gameOwner;
        spawn = gameOwner.getLocation();
        this.main = sp;
        players.addAll(t1.players);
        players.addAll(t2.players);
    }

    public void run(){
        updateScoreBoard();
        pushBack(t1);
        pushBack(t2);
        cursor++;

        if(cursor == 5){
            playersLocations();
            cursor = 0;
        }
        if(time > timeset - 60){
            for(Player p : t2.players) {
                if (p.getLocation().distance(spawn) > 2) {
                    p.teleport(spawn);
                }
            }
        }
        if(time == timeset - 60){
            announcement(ChatColor.DARK_RED + "[!]SEEKERS ARE UNLEASHED[!]",false);
            if(mode.equals("normal")) {
                announcement(ChatColor.GOLD + "[!]THE MYSTERY CHESTS ARE AVAILABLE[!]", false);
            }
        }
        if(time == timeset/2){
            announcement(ChatColor.GOLD + "[!]THE TIMER IS HALFWAY DONE[!]",false);
        }
        if(time == (timeset*0.1)){
            announcement(ChatColor.RED + "[!]THE TIMER IS ALMOST DONE[!]",false);

        }
/*        if(!t1.players.isEmpty() && t2.players.isEmpty()){
            try {
                hidersVictory();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(t1.players.isEmpty() && !t2.players.isEmpty()){
            try {
                seekersVictory();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(t1.players.isEmpty() && t2.players.isEmpty() && time > 0){
            try {
                main.cancelGame(nb);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(time == 0 && !t1.players.isEmpty()){
            try {
                hidersVictory();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }*/
        time--;
    }

    public boolean start(String mode, int timer,int limit) throws FileNotFoundException {
        this.mode = mode;
        if(!t1.players.isEmpty() && !t2.players.isEmpty()) {
            this.time = timer;
            this.timeset = timer;
            this.runTaskTimer(this.main, 0L, 20L);
            this.hasStarted = true;
            this.SIZE = limit;
            if(mode.equals("tdm")){
                board.registerNewTeam("spt1"+String.valueOf(nb));
                Objects.requireNonNull(board.getTeam(String.valueOf(nb))).setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
                board.registerNewTeam("spt2"+String.valueOf(nb));
                Objects.requireNonNull(board.getTeam(String.valueOf(nb))).setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
            }
            board.registerNewTeam("spt1"+String.valueOf(nb));
            Objects.requireNonNull(board.getTeam(String.valueOf(nb))).setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
            board.registerNewTeam("spt2"+String.valueOf(nb));
            Objects.requireNonNull(board.getTeam(String.valueOf(nb))).setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
            for(Player p : t1.players){
                StackPoints.saveinv(p);
                p.getInventory().clear();
                Objects.requireNonNull(board.getTeam("spt1" + String.valueOf(nb))).addEntry(p.getName());
                p.teleport(spawn);
                p.setInvulnerable(true);
                p.setFoodLevel(20);
                p.setHealth(20);
                p.setGameMode(GameMode.ADVENTURE);
                setScoreBoard(p);
                /*if(mode.equals("predator")){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*time, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20*time, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*time, 1));
                    board.getTeam(String.valueOf(nb)).setColor(ChatColor.DARK_GREEN);
                }*/
            }
            for(Player p : t2.players){
                StackPoints.saveinv(p);
                p.getInventory().clear();
                Objects.requireNonNull(board.getTeam("spt2" + String.valueOf(nb))).addEntry(p.getName());
                p.teleport(spawn);
                p.setInvulnerable(true);
                p.setFoodLevel(20);
                p.setHealth(20);
                p.setGameMode(GameMode.ADVENTURE);
                setScoreBoard(p);
                /*if(mode.equals("predator")){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*time, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20*time, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*time, 1));
                }*/

            }
            return true;
        }
        return false;
    }

    public boolean addPlayer(Player p,String t){
        if(t1.full && t2.full){
            return false;
        }
        if(t.equals("p")){
            if(!t1.players.contains(p) && ((t2.players.size() - t1.players.size()) <= 3 || (t2.players.size() - t1.players.size() >= -3))){
                if(!t1.full) {
                    t1.addPlayer(p);
                    players.add(p);
                    announcement(ChatColor.BLUE + "[+" + StackPoints.TEAMNAME1 + "]"+p.getName(), false);
                    return true;
                }
            }
        }
        else if(t.equals("d")){
            if(!t2.players.contains(p) && ((t2.players.size() - t1.players.size()) <= 3 || (t2.players.size() - t1.players.size() >= -3))){
                if(!t2.full) {
                    t2.addPlayer(p);
                    players.add(p);
                    announcement(ChatColor.RED + "[+" + StackPoints.TEAMNAME2 + "]"+p.getName(),false);
                    return true;
                }
            }
        }
        else if(t.equals("r")){
            if(t2.players.size() >= t1.players.size()){
                if(!t1.full) {
                    t1.addPlayer(p);
                    players.add(p);
                    announcement(ChatColor.DARK_GREEN + "[+" + StackPoints.TEAMNAME1 + "]"+p.getName(), false);
                    return true;
                }
            }
            else{
                double r = StackPoints.randDouble(1, 10);
                if(r < 5.5) {
                    if (!t1.full) {
                        t1.addPlayer(p);
                        players.add(p);
                        announcement(ChatColor.DARK_GREEN + "[+" + StackPoints.TEAMNAME1 + "]" + p.getName(), false);
                        return true;
                    }
                }
                else {
                    if(!t2.full) {
                        t2.addPlayer(p);
                        players.add(p);
                        announcement(ChatColor.RED + "[+" + StackPoints.TEAMNAME2 + "]"+p.getName(),false);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean remPlayer(Player p,Boolean seeker) throws FileNotFoundException {
        if (players.contains(p)) {
            players.remove(p);
            if (t1.players.contains(p)) {
                t1.remPlayer(p.getName());
                if (board.getTeam("" + nb) != null) {
                    board.getTeam("" + nb).removeEntry(p.getName());
                }
                announcement(ChatColor.DARK_GREEN + "[-H]" + p.getName(),false);
                if(seeker) {
                    specs.add(p);
                    spectator.addPlayer(p);
                    p.sendTitle("[HS]","[!] SPECTATOR MODE [!]",1,60,1);
                    p.sendMessage(ChatColor.GRAY + "SPECTATOR MODE : Type /hsleave to leave the game");
                    p.setGameMode(GameMode.SPECTATOR);
                }else {
                    p.setGameMode(GameMode.SURVIVAL);
                }
                p.setInvulnerable(false);
                p.getInventory().clear();
                //p.getActivePotionEffects().clear();
                for (PotionEffect effect : p.getActivePotionEffects()) {
                    p.removePotionEffect(effect.getType());
                }
                StackPoints.loadinv(p);
                return true;

            } else if (t2.players.contains(p)) {
                t2.remPlayer(p.getName());
                announcement(ChatColor.RED + "[-S]" + p.getName(),false);
                p.setGameMode(GameMode.SURVIVAL);
                p.setInvulnerable(false);
                p.getInventory().clear();
                //p.getActivePotionEffects().clear();
                for (PotionEffect effect : p.getActivePotionEffects()) {
                    p.removePotionEffect(effect.getType());
                }
                StackPoints.loadinv(p);
                return true;
            }
        }
        else if(specs.contains(p)){
            specs.remove(p);
            spectator.remPlayer(p);
            p.teleport(spawn);
            p.setGameMode(GameMode.SURVIVAL);
        }
        return false;
    }

    public void announcement(String msg,boolean title){
        t1.chat("[ANNOUNCEMENT]",msg);
        t2.chat("[ANNOUNCEMENT]",msg);
        spectator.broadcast("[ANNOUNCEMENT] " + msg);
        if(title){
            for(Player p : players){
                p.sendTitle("[HS]",msg,1,60,1);
            }
        }
    }

    /*public void hidersVictory() throws FileNotFoundException {
        announcement(ChatColor.GOLD + "The hiders won ! Congratulations :\n",false);
        for(Player p : t1.players){
            announcement(ChatColor.DARK_GREEN + p.getName() + "\n",false);
            //Event called
            CustomEventHs ev = new CustomEventHs("TeamWin",p);
            Bukkit.getServer().getPluginManager().callEvent(ev);
        }
        main.cancelGame(nb+1);
    }

    public void seekersVictory() throws FileNotFoundException {
        announcement(ChatColor.GOLD + "The seekers won ! Congratulations :\n",false);
        for(Player p : t2.players){
            announcement(ChatColor.RED + p.getName() + "\n",false);
            //Event called
            CustomEventHs ev = new CustomEventHs("TeamWin",p);
            Bukkit.getServer().getPluginManager().callEvent(ev);
        }
        main.cancelGame(nb+1);
    }*/
    public void pushBack(CustomTeam t){
        for(Player p : t.players){
            if(p.getLocation().distance(spawn) >= SIZE){
                Location l = new Location(limit.get(p).getWorld(),limit.get(p).getX(),limit.get(p).getY(),limit.get(p).getZ(),p.getLocation().getYaw(),p.getLocation().getPitch());
                p.teleport(l);
                p.sendMessage(ChatColor.RED + "You went too far, stay in the game zone.\n");
            }
            else{
                if(!limit.containsKey(p)) {
                    limit.put(p,p.getLocation());
                }
                else{
                    if(p.getLocation().distance(spawn) < SIZE -10)
                    limit.replace(p,p.getLocation());
                }
            }
        }
    }

    public void playersLocations(){
        for(Player p1 : t1.players){
            if(p1.getInventory().getItemInOffHand().getType() == Material.COMPASS){
                p1.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            }
            for(Player p2 : t2.players){
                if(p1.getLocation().distance(p2.getLocation()) <= 20 && p1.getLocation().distance(p2.getLocation()) > 10){
                    p1.sendActionBar(ChatColor.AQUA + "[!]A seeker is nearby, careful !");
                    p2.sendActionBar(ChatColor.AQUA + "[!]A hider is nearby !");
                }
                else if(p1.getLocation().distance(p2.getLocation()) <= 10){
                    p1.sendActionBar(ChatColor.DARK_PURPLE + "[!]A seeker is very close, careful !");
                    p1.getInventory().setItemInOffHand(new ItemStack(Material.COMPASS));
                    p1.setCompassTarget(new Location(p2.getWorld(),p2.getLocation().getX(),p2.getLocation().getY(),p2.getLocation().getZ()));
                    p2.sendActionBar(ChatColor.DARK_PURPLE + "[!]A hider is very close !");
                }
            }
        }
        if(time <= timeset*0.1){
            Location l = t1.players.get(0).getLocation();
            for(Player s : t2.players) {
                if(s.getInventory().getItemInOffHand().getType() != Material.COMPASS){
                    s.getInventory().setItemInOffHand(new ItemStack(Material.COMPASS));
                }
                for(Player h : t1.players) {
                    if(s.getLocation().distance(h.getLocation()) < s.getLocation().distance(l)){
                        l = h.getLocation();
                    }
                    s.setCompassTarget(l);
                }
            }
        }
    }

    public void setScoreBoard(Player p) {
        this.timer.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.timer.setDisplayName(ChatColor.DARK_PURPLE + "H&S");
        Score score = this.timer.getScore(ChatColor.DARK_GREEN + "TIMER");
        score.setScore(this.time);
        p.setScoreboard(this.board);
    }

    public void updateScoreBoard() {
        for(Player p : t1.players) {
            Score score = this.timer.getScore(ChatColor.DARK_GREEN + "TIMER");
            score.setScore(this.time);
            p.setScoreboard(this.board);
        }
        for(Player p : t2.players) {
            Score score = this.timer.getScore(ChatColor.DARK_GREEN + "TIMER");
            score.setScore(this.time);
            p.setScoreboard(this.board);
        }
    }

    public void delScoreBoard() {
        this.timer.unregister();
        this.board.clearSlot(DisplaySlot.SIDEBAR);
    }
/*
    public void noSit(){
        for (Player p : players){
            if(p.isInsideVehicle()){
                p.leaveVehicle();
                p.sendActionBar(ChatColor.RED + "[!]You can't sit within a game");
            }
        }
    }

    public void radarBonus(){
        //RADAR BONUS
        for (Player p : players){
            if(p.getInventory().getItemInMainHand().getType().equals(Material.COMPASS) && p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()){
                Location closest = null;
                if(t1.players.contains(p)){
                    for (Player pl : t2.players){
                        if(p.getLocation().distance(pl.getLocation()) <= SIZE/2){
                            if (closest != null){
                                if (p.getLocation().distance(pl.getLocation()) < p.getLocation().distance(closest)){
                                    closest = pl.getLocation();
                                    closest.setYaw(0);
                                    closest.setPitch(0);
                                    p.setCompassTarget(closest);
                                }
                            }
                            else {
                                closest = pl.getLocation();
                                closest.setYaw(0);
                                closest.setPitch(0);
                                p.setCompassTarget(closest);
                            }
                            p.sendActionBar(ChatColor.DARK_PURPLE + "[!] SEEKER DETECTED AT "+((int)p.getLocation().distance(closest))+" BLOCKS [!]");
                        }
                    }
                }
                else{
                    for (Player pl : t1.players){
                        if(p.getLocation().distance(pl.getLocation()) <= SIZE/2){
                            if (closest != null){
                                if (p.getLocation().distance(pl.getLocation()) < p.getLocation().distance(closest)){
                                    closest = pl.getLocation();
                                    closest.setYaw(0);
                                    closest.setPitch(0);
                                    p.setCompassTarget(closest);
                                }
                            }
                            else {
                                closest = pl.getLocation();
                                closest.setYaw(0);
                                closest.setPitch(0);
                                p.setCompassTarget(closest);
                            }
                            p.sendActionBar(ChatColor.DARK_PURPLE + "[!] HIDER DETECTED AT "+((int)p.getLocation().distance(closest))+" BLOCKS [!]");
                        }
                    }
                }
            }
        }
    }


    public void useChest(Entity en){
        if(en instanceof ArmorStand){
            if(en.getName().equals(ChatColor.GOLD + "[?]")){
                chests.replace((ArmorStand) en,false);
                ((ArmorStand) en).setHealth(0);
                en.remove();
            }
        }
    }*/
}
