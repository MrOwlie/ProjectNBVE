/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import packets.Packet.PlayerOrientation;
import packets.Packet.ThrowSnowball;
import packets.Packet.Reload;

/**
 *
 * @author Anton
 */
public class Player extends Node{
    
    public static final float CYLINDER_HEIGHT = 5f;
    public static final float CYLINDER_RADIUS = 2f;
    public static final float MASS = 150f;
    
    public static final float SPEED = 8f;
    public static final float UPDATE_FREQUENCY = 0.05f;
    
    public static final int START_LEVEL = 1;
    public static final int START_HEALTH = 50;
    public static final int START_DMG = 10;
    public static final int START_SNOWBALLS = 3;
    
    private int level;
    private int experience;
    private int nSnowballs;
    private int dmg;
    private int entityId;
    
    BitmapFont uiFont;
    BitmapText uiHp;
    BitmapText uiAmmo;
    BitmapText uiExp;
    BitmapText uiLevel;
    
    private float timeSinceUpdate = 0f;
            
    private BetterCharacterControl controller;
    private Camera playerCam;
    private Vector3f direction = new Vector3f();
    private boolean input[] = new boolean[5];
    
    private AudioNode stepAudio; 
    
    public Player (
            int level,
            int experience,
            int nSnowballs,  
            Vector3f startPos,
            Camera playerCam,
            int entityId
    )
    {
        
        this.level = level;
        this.experience = experience;
        this.nSnowballs = nSnowballs;
        this.playerCam = playerCam;
        this.setLocalTranslation(startPos);
        this.entityId = entityId;
        
        controller = new BetterCharacterControl(CYLINDER_RADIUS, CYLINDER_HEIGHT, MASS);
        controller.setGravity(new Vector3f(0f,1f,0f));
        Main.bulletAppState.getPhysicsSpace().add(controller);
        Main.refRootNode.attachChild(this);
        this.addControl(controller);
        
        stepAudio = new AudioNode(Main.refAssetManager, "Sounds/snow_footsteps.wav", DataType.Stream);
        stepAudio.setLooping(true);  
        stepAudio.setVolume(0.5f);
        this.attachChild(stepAudio);
        
        
        Main.refFlyCam.setEnabled(true);
        Node camNode = new Node();
        this.attachChild(camNode);
        camNode.setLocalTranslation(new Vector3f(0f,5f,0f));
        ChaseCamera chaseCam = new ChaseCamera(playerCam, camNode, Main.refInputManager);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setMaxDistance(1f);
        chaseCam.setMinDistance(1f);
        chaseCam.setDragToRotate(false);
        
        //UI
        uiFont = Main.refAssetManager.loadFont("Interface/Fonts/Console.fnt");
        
        uiExp = new BitmapText(uiFont, false);
        uiExp.setSize(26);
        uiExp.setColor(ColorRGBA.Magenta);
        uiExp.setText(this.experience + " / " + 100 * this.level);
        uiExp.setLocalTranslation(1, 50, 0);
        Main.refGuiNode.attachChild(uiExp);
        
        uiLevel = new BitmapText(uiFont, false);
        uiLevel.setSize(26);
        uiLevel.setColor(ColorRGBA.Pink);
        uiLevel.setText("" + 1);
        uiLevel.setLocalTranslation(1, 100, 0);
        Main.refGuiNode.attachChild(uiLevel);
        
        uiAmmo = new BitmapText(uiFont, false);
        uiAmmo.setSize(26);
        uiAmmo.setColor(ColorRGBA.Blue);
        uiAmmo.setText("" + 0);
        uiAmmo.setLocalTranslation(1, 150, 0);
        Main.refGuiNode.attachChild(uiAmmo);
        
        uiHp = new BitmapText(uiFont, false);
        uiHp.setSize(26);
        uiHp.setColor(ColorRGBA.Red);
        uiHp.setText(((this.level * 5) + 20) + " / " + ((this.level * 5) + 20));
        uiHp.setLocalTranslation(1, 200, 0);
        Main.refGuiNode.attachChild(uiHp);
        
        
        
        
        
        
    }
    
    public void updateUI(int hp, int ammo, int level, int exp) {
        this.nSnowballs = ammo;
        this.level = level;
        this.experience = exp;
        Main.refGuiNode.detachAllChildren();
        uiExp.setText("Exp: " + exp + " / " + 100 * level);
        uiHp.setText("HP: " + hp + " / " + ((this.level * 5) + 20));
        uiAmmo.setText("Ammo: " + ammo);
        uiLevel.setText("Level: " + level);
        Main.refGuiNode.attachChild(uiExp);
        Main.refGuiNode.attachChild(uiHp);
        Main.refGuiNode.attachChild(uiAmmo);
        Main.refGuiNode.attachChild(uiLevel);
        
    }
    

    public void update(float tpf)
    {   
        Vector3f forward = playerCam.getDirection().clone().normalize();
        Vector3f left = playerCam.getLeft().clone().normalize();
        forward.y = 0;
        left.y = 0;
            
        if(controller.isOnGround())
        {
            direction.set(Vector3f.ZERO);

            if(input[0])direction.addLocal(forward);
            if(input[1])direction.addLocal(left);
            if(input[2])direction.addLocal(forward.negate());
            if(input[3])direction.addLocal(left.negate());
            if(input[4])controller.jump();
            direction.normalizeLocal();
            controller.setWalkDirection(direction.mult(SPEED));
            
            if(direction.equals(Vector3f.ZERO)) stepAudio.stop();
            else stepAudio.play();
        }
        else stepAudio.stop();
            
        timeSinceUpdate += tpf;
        if(timeSinceUpdate >= UPDATE_FREQUENCY)
        {
            timeSinceUpdate = 0f;
            NetWrite.addMessage(new PlayerOrientation
            (
            forward,
            left,
            this.getLocalTranslation(),
            direction,
            this.entityId
            ));
        }
    }
    
    public void input(String name, boolean state)
    {
        switch(name)
        {
            case "W":
                input[0] = state;
                break;
            case "A":
                input[1] = state;
                break;
            case "S":
                input[2] = state;
                break;
            case "D":
                input[3] = state;
                break;
            case "Jump":
                input[4] = state;
                break;
            case "Throw":
                if(state)
                {
                    NetWrite.addMessage(new ThrowSnowball(playerCam.getDirection(),entityId));
                }
                break;
                
            case "Reload":
                if(state) {
                    System.out.println("Writing reload message");
                    NetWrite.addMessage(new Reload());
                }
        }
    }
}
