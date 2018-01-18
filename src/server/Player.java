/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.scene.Node;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import packets.Packet;
import packets.Packet.SpawnEntity;
import packets.Packet.Death;

/**
 *
 * @author fredr
 */
public class Player extends MovingEntity {
    public static final float CYLINDER_HEIGHT = 5f;
    public static final float CYLINDER_RADIUS = 2f;
    public static final float MASS = 150f;
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
    CharacterControl controller;
    Node shootNode;
    
        
    private Player(String username, HostedConnection connection, int level, int exp, int ammo, float startX,  float startY, float startZ) {
        this.name = "Player";
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
        this.controller = new CharacterControl(new CylinderCollisionShape(new Vector3f(CYLINDER_RADIUS, CYLINDER_HEIGHT/2f, 0f),1), 1f);
        
        shootNode = new Node();
        this.attachChild(shootNode);
        shootNode.setLocalTranslation(0, 5f, 5f);
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
                                                            Float.parseFloat(account.get(5)) + 5f,
                                                            Float.parseFloat(account.get(6))
                    );
                    Player.players.add(player);
                    connection.send(new Packet.AuthPlayer(player.level, player.exp, player.ammo, player.getLocalTranslation().x, player.getLocalTranslation().y, player.getLocalTranslation().z, player.getEntityId()));
                    Networking.server.broadcast(Filters.notEqualTo(connection), new SpawnEntity(player.getLocalTranslation(), player.entityId, Packet.PLAYER));
                } else {
                    System.out.println("ERROR PASSWORD MISSMATCH! #" + password + "#" + account.get(0) + "#");
                
                }
            } else {
                Player player = new Player(username, connection, 1, 0, 0, 100f, 10f, 0);
                connection.send(new Packet.AuthPlayer(0, 0, 0, 100f, 10f, 0, player.getEntityId()));
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
        bw.write("1");
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
                this.hp = maxHp;
                this.dmg = 10 + (level * 1);
            }
            this.connection.send(new Packet.UpdateGUI(this.hp, this.ammo, this.exp, this.level));
        }
    }
    
    public boolean takeDamage(int damage){
        hp = hp - damage < 0 ? 0 : hp - damage;
        
        this.connection.send(new Packet.UpdateGUI(this.hp, this.ammo, this.exp, this.level));
        
        
        if(hp < 1) {
            Random rand = new Random();
            
            float x = rand.nextFloat() * 120 - 60;
            float z = rand.nextFloat() * 120 - 60;
            float y = Main.terrain.getHeight(new Vector2f(x, z));
            this.hp = this.maxHp;
            this.connection.send(new Packet.UpdateGUI(this.hp, this.ammo, this.exp, this.level));
            Networking.server.broadcast(new Death(this.entityId, x, y, z));
            this.controller.warp(new Vector3f(x, y, z));
            return true;
        } else {
            return false;
        }
    }
    
    public void reload(){
        this.ammo++;
        this.connection.send(new Packet.UpdateGUI(this.hp, this.ammo, this.exp, this.level));
    }

    @Override
    public synchronized void update(float tpf) 
    {
        
        if(!canThrowSnowball && timeSinceLastThrow == SNOWBALL_CD)
        {
            canThrowSnowball = true;
            timeSinceLastThrow = 0f;
        }
        else if(!canThrowSnowball && timeSinceLastThrow != SNOWBALL_CD)
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
        System.out.println("AMMO: " + this.ammo + ", canThrowSnowball: " + canThrowSnowball);
        if(ammo > 0 && canThrowSnowball)
        {
            Snowball snowball = new Snowball(shootNode.getWorldTranslation(), direction, entityId);
            Networking.server.broadcast(new SpawnEntity(snowball.getLocalTranslation(), snowball.entityId, Packet.SNOWBALL));
            ammo--;
            canThrowSnowball=false;
            this.connection.send(new Packet.UpdateGUI(this.hp, this.ammo, this.exp, this.level));
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
    
    public void setDirection(Vector3f direction)
    {
        this.direction = direction;
    }

    @Override
    public void destroyEntity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
