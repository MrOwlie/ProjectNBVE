/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.ArrayList;

/**
 *
 * @author Anton
 */
public class Modeling 
{
    public final float timeSinceUpdate 
    private ArrayList<MovingEntity> entities = new ArrayList<>();
    private float timeSinceLastUpdate;
    
    public void update (float tpf)
    {
        for(MovingEntity entity : entities)
        {
            entity.update(tpf);
        }
    }
    
    public void addEntity(MovingEntity entity)
    {
        entities.add(entity);
    }
}
