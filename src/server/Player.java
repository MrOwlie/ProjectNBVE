/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fredr
 */
public class Player {
    
    static ArrayList<Player> players;
    
    String username;
    int level;
    int exp;
    int hp;
    int maxHp;
    int dmg;
    
    @SuppressWarnings("LeakingThisInConstructor")
    private Player(String username, int level, int exp) {
        this.username = username;
        this.level = level;
        this.exp = exp;
        this.maxHp = 20 + (level * 5);
        this.hp = this.maxHp;
        this.dmg = 10 + (level * 1);
    }
    
    static Player authenticate(String username, String password) {
        List<String> account;
        try {
            account = Files.readAllLines(Paths.get("./Accounts/" + username));
            if(password.equals(account.get(1))){
                Player player = new Player(account.get(0), Integer.parseInt(account.get(2)), Integer.parseInt(account.get(3)));
                Player.players.add(player);
                return player;

            } else {
                return null;
            }
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    void addExp(int exp) {
        if(this.level < 10) {
            if(this.exp + exp < 100) {
                this.exp += exp;
            } else {
                this.exp = this.exp + exp - 100;
                this.level++;
                this.maxHp = 20 + (level * 5);
                this.dmg = 10 + (level * 1);
            }
        }
    }
    
    
}
