/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import packets.Packet;
import packets.Packet.SpawnEntity;

/**
 *
 * @author fredr
 */
public class Player extends MovingEntity {
    public static final float CYLINDER_HEIGHT = 5f;
    public static final float CYLINDER_RADIUS = 2f;
    public static final float MASS = 50f;
    public static final float SPEED = 8f;
    public static final float SNOWBALL_CD = 0.5f;
    
    static ArrayList<Player> players = new ArrayList();
    
    Vector3f playerLeft = new Vector3f();
    Vector3f playerForward = new Vector3f();
    
    private boolean canThrowSnowball = true;
    private float timeSinceLastThrow;
    
    
    String username;
    int level;
    int exp;
    int hp;
    int maxHp;
    int dmg;
    int ammo;
    
    HostedConnection connection;
    BetterCharacterControl controller;
    
        
    private Player(String username, HostedConnection connection, int level, int exp, int ammo, float startX,  float startY, float startZ) {
        
        this.type = Packet.PLAYER;
        this.username = username;
        this.level = level;
        this.exp = exp;
        this.maxHp = 20 + (level * 5);
        this.hp = this.maxHp;
        this.dmg = 10 + (level * 1);
        this.ammo = ammo;
        this.connection = connection;
        this.direction = new Vector3f();
        this.controller = new BetterCharacterControl(CYLINDER_RADIUS, CYLINDER_HEIGHT, MASS);
        this.setLocalTranslation(startX, startY, startZ);
        
        Main.bulletAppState.getPhysicsSpace().add(controller);
        Main.refRootNode.attachChild(this);
        this.addControl(controller);
        for(MovingEntity entity : Modeling.getEntities())
        {
            connection.send(new SpawnEntity(
                    entity.getLocalTranslation(),
                    entity.entityId,
                    entity.type)
                    );
        }
        Modeling.addEntity(this);
        //controller.warp(new Vector3f(startX, startY, startZ));
        
        
    }
    
    static void authenticate(String username, String password, HostedConnection connection) {
        List<String> account;
        try {
            if(Files.exists(Paths.get("C:/Accounts/" + username + ".txt"))) {
                account = Files.readAllLines(Paths.get("C:/Accounts/" + username + ".txt"));
                if(password.equals(account.get(0))){
                    Player player = new Player(username,    connection,
                                                            Integer.parseInt(account.get(1)), 
                                                            Integer.parseInt(account.get(2)), 
                                                            Integer.parseInt(account.get(3)),
                                                            Float.parseFloat(account.get(4)),
                                                            Float.parseFloat(account.get(5)),
                                                            Float.parseFloat(account.get(6))
                    );
                    Player.players.add(player);
                    connection.send(new Packet.AuthPlayer(player.level, player.exp, player.ammo, player.getLocalTranslation().x, player.getLocalTranslation().y, player.getLocalTranslation().z, player.getEntityId()));
                    Networking.server.broadcast(Filters.notEqualTo(connection), new SpawnEntity(player.getLocalTranslation(), player.entityId, Packet.PLAYER));
                } else {
                    System.out.println("ERROR PASSWORD MISSMATCH! #" + password + "#" + account.get(0) + "#");
                
                }
            } else {
                Player player = new Player(username, connection, 0, 0, 0, 40f, 10f, 0);
                connection.send(new Packet.AuthPlayer(0, 0, 0, 40f, 10f, 0, player.getEntityId()));
                Player.create(username, password);
                Player.players.add(player);
                Networking.server.broadcast(Filters.notEqualTo(connection), new SpawnEntity(player.getLocalTranslation(), player.entityId, Packet.PLAYER));
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    void save() throws IOException {
        FileWriter fw = new FileWriter("C:/Accounts/" + username + ".txt", false);
        BufferedWriter bw = new BufferedWriter(fw);
        BufferedReader br = new BufferedReader(new FileReader("C:/Accounts/" + username + ".txt"));
        
        String password = br.readLine();
                
        bw.write(password);
        bw.newLine();
        bw.write(this.level);
        bw.newLine();
        bw.write(this.exp);
        bw.newLine();
        bw.write(this.ammo);
        bw.newLine();
        bw.write("" + this.getLocalTranslation().x);
        bw.newLine();
        bw.write("" + this.getLocalTranslation().y);
        bw.newLine();
        bw.write("" + this.getLocalTranslation().z);
        
        bw.close();
    }
    
    static void create(String username, String password) throws IOException {
        FileWriter fw = new FileWriter("C:/Accounts/" + username + ".txt", false);
        BufferedWriter bw = new BufferedWriter(fw);
        
        bw.write(password);
        bw.newLine();
        bw.write("0");
        bw.newLine();
        bw.write("0");
        bw.newLine();
        bw.write("0");
        bw.newLine();
        bw.write("0");
        bw.newLine();
        bw.write("0");
        bw.newLine();
        bw.write("0");
        
        bw.close();
        
    }
    
    void addExp() {
        int exp = 50;
        if(this.level < 10) {
            if(this.exp + exp < 100 * this.level) {
                this.exp += exp;
            } else {
                this.exp = this.exp + exp - 100 * this.level;
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

    @Override
    public synchronized void update(float tpf) 
    {
        
        if(!canThrowSnowball && timeSinceLastThrow == SNOWBALL_CD)
        {
            canThrowSnowball = true;
            timeSinceLastThrow = 0f;
        }
        else if(timeSinceLastThrow != SNOWBALL_CD)
        {
            timeSinceLastThrow = timeSinceLastThrow+tpf >= SNOWBALL_CD ? SNOWBALL_CD : timeSinceLastThrow+tpf;
        }
        
    }
    
    public synchronized void setForwardAndLeft(Vector3f forward, Vector3f left)
    {
        playerLeft.set(left);
        playerForward.set(forward);
        controller.setViewDirection(forward);
    }
    
    public void throwSnowball(Vector3f direction)
    {
        if(ammo > 0 && canThrowSnowball)
        {
            Snowball snowball = new Snowball(this.getLocalTranslation(), direction);
            Networking.server.broadcast(new SpawnEntity(snowball.getLocalTranslation(), snowball.entityId, Packet.SNOWBALL));
            ammo--;
        }
    }

    @Override
    public void setViewDirection(Vector3f dir) {
        controller.setViewDirection(dir);
    }

    @Override
    public Vector3f getViewDirection() {
        return controller.getViewDirection();
    }
    
    public HostedConnection getConnection()
    {
        return connection;
    }
    
    public void setPosition(Vector3f position)
    {
        controller.warp(position);
    }
}
