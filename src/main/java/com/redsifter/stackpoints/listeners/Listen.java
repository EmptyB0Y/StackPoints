package com.redsifter.stackpoints.listeners;

import com.redsifter.stackpoints.StackPoints;
import org.bukkit.event.Listener;

public class Listen implements Listener {

    private StackPoints main;
    public Listen(StackPoints sp){
        this.main = sp;
    }
}
