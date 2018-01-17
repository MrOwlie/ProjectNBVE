/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author mrowlie
 */
public class Packet {
    public static final int PLAYER = 1;
    public static final int SNOWBALL = 2;
    
    abstract public static class MyAbstractMessage extends AbstractMessage {
        
        public MyAbstractMessage() {
        }
    }
  
    @Serializable
    public static class TimeSync extends MyAbstractMessage {
        protected float time;
        
        public TimeSync() {
            
        }
        
        public TimeSync(float time) {
            this.time = time;
        }
        
        public float getTime(){
            return this.time;
        }
        
    }
    
    @Serializable
    public static class TimeDiff extends MyAbstractMessage {
        protected float diff;
        
        public TimeDiff() {
            
        }
        
        public TimeDiff(float diff) {
            this.diff = diff;
        }
        
        public float getDiff() {
            return this.diff;
        }
        
    }
    
    @Serializable
    public static class KeyPressed extends MyAbstractMessage {
        protected int entityId;
        protected String key;
        protected boolean state;
        
        public KeyPressed() {
            
        }
        
        public KeyPressed(String key, boolean state, int entityId) {
            this.key = key;
            this.state = state;
            this.entityId = entityId;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public boolean getState() {
            return this.state;
        }
        
        public int getEntityId()
        {
            return entityId;
        }
        
    }
    
    
    
   /* @Serializable
    public static class KeyReleased extends MyAbstractMessage {
        protected byte key;
        
        public KeyReleased() {
            
        }
        
        public KeyReleased(byte key) {
            this.key = key;
        }
        
        public byte getKey() {
            return this.key;
        }
        
    }
   */
    @Serializable
    public static class PlayerOrientation extends MyAbstractMessage
    {
        protected Vector3f forward;
        protected Vector3f left;
        protected Vector3f position;
        
        
        int entityId;
        
        public PlayerOrientation(){
        
        }
        
        public PlayerOrientation(Vector3f forward, Vector3f left, Vector3f position, int entityId){
            this.forward = forward;
            this.left = left;
            this.position = position;
            this.entityId = entityId;
        }
        
        public Vector3f getLeft()
        {
            return left;
        }
        
        public Vector3f getForward()
        {
            return forward;
        }
        
        public Vector3f getPosition()
        {
            return position;
        }
        
        public int getEntityId()
        {
            return entityId;
        }
        
    }
    
    @Serializable
    public static class Authenticate extends MyAbstractMessage {
        protected String username;
        protected String password;
        
        public Authenticate() {
            
        }
        
        public Authenticate(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        public String getUsername() {
            return this.username;
        }
        
        public String getPassword() {
            return this.password;
        }
        
    }
    
    @Serializable
    public static class AuthPlayer extends MyAbstractMessage {
        protected int level;
        protected int exp;
        protected int ammo;
        protected float x;
        protected float y;
        protected float z;
        protected int entityId;
        
        public AuthPlayer() {
            
        }
        
        public AuthPlayer(int level, int exp, int ammo, float x, float y, float z, int entityId) {
            this.level = level;
            this.exp = exp;
            this.ammo = ammo;
            this.x = x;
            this.y = y;
            this.z = z;
            this.entityId = entityId;
        }
        
        public int getLevel() {
            return this.level;
        }
        
        public int getExp() {
            return this.exp;
        }
        
        public int getAmmo() {
            return this.ammo;
        }
        
        public float getX() {
            return this.x;
        }
        
        public float getY() {
            return this.y;
        }
        
        public float getZ() {
            return this.z;
        }
        
        public int getEntityId()
        {
            return entityId;
        }
        
    }
    
    
    @Serializable
    public static class Error extends MyAbstractMessage {
        protected String error;
        
        public Error() {
            
        }
        
        public Error(String error) {
            this.error = error;
        }
        
        public String getError() {
            return this.error;
        }
    }
    
    @Serializable
    public static class UpdateEntity extends MyAbstractMessage {
        int entityId;
        
        Vector3f truePosition;
        Vector3f trueDirection;
        Vector3f viewDirection;
        
        public UpdateEntity(){
            
        }
        
        public UpdateEntity(Vector3f truePosition, Vector3f trueDirection, Vector3f viewDirection, int entityId){
            this.entityId = entityId;
            this.truePosition = truePosition;
            this.viewDirection = viewDirection;
            this.trueDirection = trueDirection;
        }
        
        public int getEntityId()
        {
            return entityId;
        }
        public Vector3f getTruePos()
        {
            return truePosition;
        }
        public Vector3f getTrueDir()
        {
            return trueDirection;
        }
        public Vector3f getViewDirection()
        {
            return viewDirection;
        }
        
    }
    
    @Serializable
    public static class SpawnEntity extends MyAbstractMessage {
        protected Vector3f position;
        protected int entityId;
        protected int entityType;
        
        public SpawnEntity(){
            
        }
        
        public SpawnEntity(Vector3f position, int entityId, int entityType){
            this.entityId = entityId;
            this.position = position;
            this.entityType = entityType;
        }
        
        public Vector3f getPos()
        {
            return position;
        }
        
        public int getEntityId()
        {
            return entityId;
        }
        
        public int getEntityType()
        {
            return entityType;
        }
    }
    
    @Serializable
    public static class SpawnSnowpile extends MyAbstractMessage {
        int id;
        float x;
        float z;
        
        public SpawnSnowpile() {
            
        }
        
        public SpawnSnowpile(int id, float x, float z) {
            this.id = id;
            this.x = x;
            this.z = z;
            
        }
        
        public int getId() {
            return this.id;
        }
        
        public float getX() {
            return this.x;
        }
        
        public float getZ() {
            return this.z;
        }
        
    }
    
    @Serializable
    public static class DespawnSnowpile extends MyAbstractMessage {
        int id;
        
        public DespawnSnowpile() {
            
        }
        
        public DespawnSnowpile(int id) {
            
        }
        
        public int getId() {
            return this.id;
        }
    }
    
    @Serializable
    public static class ThrowSnowball extends MyAbstractMessage{
        protected Vector3f direction;
        protected int entityId;
        public ThrowSnowball(){
            
        }
        
        public ThrowSnowball(Vector3f direction, int entityId)
        {
            this.direction = direction;
            this.entityId = entityId;
        }
        
        public Vector3f getDirection()
        {
            return direction;
        }
        
        public int getEntityId()
        {
            return entityId;
        }
    }
    
}
