/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.util.ArrayList;

/**
 *
 * @author Anton
 */
public class Modeling
{
    private static ArrayList<MovingEntity> movingEntities = new ArrayList<>();
    private float currentTpf = 0f;
    private float nextTpf = 0f;
    private boolean running = true;
    
    public void update(float tpf) 
    {            
        for(MovingEntity entity : movingEntities)
        {
            entity.update(tpf);
        }
    }
    
    public static void addEntity(MovingEntity entity)
    {
        movingEntities.add(entity);
    }
    
    public synchronized void updateTpf(float tpf)
    {
        this.nextTpf += tpf;
    }
    
    private synchronized void setCurrentTpf()
    {
        currentTpf = nextTpf;
        nextTpf = 0f;
    }
    
    private synchronized float getNextTpf()
    {
        return nextTpf;
    }
}
