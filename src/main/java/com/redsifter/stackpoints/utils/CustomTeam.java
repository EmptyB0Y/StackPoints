package com.redsifter.stackpoints.utils;

import com.redsifter.stackpoints.StackPoints;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;


public class CustomTeam {
    public ArrayList<Player> players = new ArrayList<Player>();
    private int playernb = 0;
    private int nb;
    public Channel ch;
    public String name;
    public boolean full = false;

    public CustomTeam(int number, String nm){
        nb = number;
        name = nm;
        ChatColor color = ChatColor.GOLD;
        if(name.equals("HIDERS")){
            color = ChatColor.DARK_GREEN;
        }
        else if(name.equals("SEEKERS")){
            color = ChatColor.RED;
        }
        ch = new Channel(nm,color);
    }

    public void setPlayers(ArrayList<Player> lst){
        int iterator = 0;
        if(lst.size() < StackPoints.MAXPLAYERS){
            iterator = lst.size();
        }
        else{
            iterator = StackPoints.MAXPLAYERS;
        }
        for (int i = 0;i < iterator;i++){
            players.add(lst.get(i));
            playernb++;
            ch.addPlayer(lst.get(i));
        }
    }

    public boolean addPlayer(Player pl){
        if(!full) {
            players.add(pl);
            playernb++;
            ch.addPlayer(pl);
            if (playernb == StackPoints.MAXPLAYERS) {
                full = true;
            }
            return true;
        }
        return false;
    }

    public void remPlayer(String pl){
        players.remove(Bukkit.getPlayerExact(pl));
        playernb--;
        ch.remPlayer(Bukkit.getPlayerExact(pl));
    }

    public void flush(){
        players.clear();
        playernb = 0;
        ch.flush();
    }

    public void chat(String name,String msg){
        ch.broadcast(name+":"+msg);
    }
}