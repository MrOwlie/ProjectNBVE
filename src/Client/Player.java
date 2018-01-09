/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author Anton
 */
public class Player extends MovingEntity{
    public static final float CYLINDER_HEIGHT = 5f;
    public static final float CYLINDER_RADIUS = 2f;
    public static final float MASS = 5f;
    
    public static final float SPEED = 8f;
    
    public static final int START_LEVEL = 1;
    public static final int START_HEALTH = 50;
    public static final int START_DMG = 10;
    public static final int START_SNOWBALLS = 3;
    
    private int level;
    private int experience;
    private int nSnowballs;
    private int currentHealth;
    private int maxHealth;
    private int dmg;
    
    private BetterCharacterControl controller;
    private Camera playerCam;
    
    private boolean input[] = new boolean[4];
    
    public Player (int level, 
            int nSnowballs, 
            int maxHealth, 
            int dmg, 
            Vector3f startPos,
            Camera playerCam)
    //Load existing character       
    // Kanske finns ett bättre sätt att få in datan till objektet?
    {
        this.level = level;
        this.nSnowballs = nSnowballs;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.dmg = dmg;
        this.playerCam = playerCam;
        this.setLocalTranslation(startPos);
        
        controller = new BetterCharacterControl(CYLINDER_RADIUS, CYLINDER_HEIGHT, MASS);
        //controller.warp(startPos);
        controller.setGravity(new Vector3f(0f,1f,0f));
        Main.bulletAppState.getPhysicsSpace().add(controller);
        Main.refRootNode.attachChild(this);
        this.addControl(controller);
    }
    
    public Player (Vector3f startPos, Camera playerCam) // New character
    {
        this(START_LEVEL, 
                START_SNOWBALLS, 
                START_HEALTH, 
                START_DMG, 
                startPos,
                playerCam
        );
    }
    
    @Override
    public void update(float tpf)
    {
        Vector3f forward = playerCam.getDirection().clone().normalize();
        Vector3f left = playerCam.getLeft().clone().normalize();
        forward.y = 0;
        left.y = 0;
        
        trueDirection.set(Vector3f.ZERO);
        
        if(input[0])trueDirection.addLocal(forward);
        if(input[1])trueDirection.addLocal(left);
        if(input[2])trueDirection.addLocal(forward.negate());
        if(input[3])trueDirection.addLocal(left.negate());
        
        trueDirection.normalizeLocal();
        
        correctDirection(tpf);
        correctPosition(tpf);
    }
    
    @Override
    protected void correctPosition(float tpf) 
    {
        if(truePositionReached)
        {
            controller.setWalkDirection(trueDirection.mult(SPEED));
        }
        
        else
        {
            float distance = truePosition.subtract(this.getLocalTranslation()).length();
            
            if(distance < tpf*CORRECTION_SPEED)
            {
                controller.warp(truePosition);
                localDirection = trueDirection;
                truePositionReached = true;
            }
            else
            {
                controller.warp(localDirection.mult(CORRECTION_SPEED));
            }
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
        }
    }
}
