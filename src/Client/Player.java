/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;

/**
 *
 * @author Anton
 */
public class Player extends MovingEntity{
    public static final float CYLINDER_HEIGHT = 5f;
    public static final float CYLINDER_RADIUS = 2f;
    public static final float MASS = 1f;
    
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
    
    BetterCharacterControl controller;
    Geometry model;
    
    public Player (int level, 
            int nSnowballs, 
            int maxHealth, 
            int dmg, 
            Vector3f startPos, 
            Spatial model)
    //Load existing character       
    // Kanske finns ett bättre sätt att få in datan till objektet?
    {
        this.level = level;
        this.nSnowballs = nSnowballs;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.dmg = dmg;
        
        this.setLocalTranslation(startPos);
        
        controller = new BetterCharacterControl(CYLINDER_RADIUS, CYLINDER_HEIGHT, MASS);
        this.addControl(controller);
        this.attachChild(model);    
    }
    
    public Player (Vector3f startPos, Spatial model) // New character
    {
        this(START_LEVEL, 
                START_SNOWBALLS, 
                START_HEALTH, 
                START_DMG, 
                startPos,
                model
        );
    }
    
    public void levelUp()
    {
        //TODO
    }
    
    public void takeDamage(int damage)
    {
        currentHealth = currentHealth - damage < 0 ? 0 : currentHealth - damage;
    }
    
    public void reload(int nSnowballs)
    {
        this.nSnowballs += nSnowballs;
    }
    
    public void awardExperince(int experience)
    {
        this.experience = experience;
    }
    
    @Override
    public void correctPosition(float tpf) 
    {
        if(truePositionReached)
        {
            controller.setWalkDirection(localDirection.mult(speed));
        }
        
        else
        {
            float distance = truePosition.subtract(this.getLocalTranslation()).length();
            
            if(distance < tpf*speed)
            {
                controller.warp(truePosition);
                localDirection = trueDirection;
                truePositionReached = true;
            }
            else
            {
                correctDirection(tpf);
                controller.warp(localDirection.mult(speed));
            }
        }
    }   
}
