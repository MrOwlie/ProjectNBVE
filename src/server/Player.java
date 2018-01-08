/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.network.HostedConnection;
import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
    int ammo;
    
    HostedConnection connection;
    
    private Player(String username, HostedConnection connection, int level, int exp, int ammo) {

        this.username = username;
        this.level = level;
        this.exp = exp;
        this.maxHp = 20 + (level * 5);
        this.hp = this.maxHp;
        this.dmg = 10 + (level * 1);
        this.ammo = ammo;
        this.connection = connection;
    }
    
    static void authenticate(String username, String password, HostedConnection connection) {
        List<String> account;
        try {
            account = Files.readAllLines(Paths.get("./Accounts/" + username));
            if(password.equals(account.get(0))){
                Player player = new Player(username,    connection,
                                                        Integer.parseInt(account.get(1)), 
                                                        Integer.parseInt(account.get(2)), 
                                                        Integer.parseInt(account.get(3)));
                Player.players.add(player);
            } else {
                System.out.println("ERROR PASSWORD MISSMATCH! #" + password + "#" + account.get(0) + "#");
            }
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void save() throws IOException {
        FileWriter fw = new FileWriter("./Accounts/" + this.username, false);
        BufferedWriter bw = new BufferedWriter(fw);
        BufferedReader br = new BufferedReader(new FileReader("./Accounts/" + this.username));
        
        String password = br.readLine();
                
        bw.write(password);
        bw.newLine();
        bw.write(this.level);
        bw.newLine();
        bw.write(this.exp);
        bw.newLine();
        bw.write(this.ammo);
        
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
    
    public void takeDamage(int damage){
        hp = hp - damage < 0 ? 0 : hp - damage;
    }
    
    public void reload(){
        this.ammo++;
    }
    
}
