package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public class Player extends Entity {
    //public float x = 2000;
    //public float y = 2000;
    private PlayerAnimator playerAnimator;
    private ShapeRenderer shapeRenderer;
    private static final float MAX_VELOCITY = 20 * 6f / 60f;
    private static final float DRAG = 1 * 6f / 60f;
    private float current_velocityX = 0;
    private float current_velocityY = 0;
    private TiledMapTileLayer tiledMapTileLayer;
    private int tilexCenter = 0;
    private int tilexRight = 0;
    private int tileyCenter = 0;
    public Rectangle hitbox = new Rectangle(x-40,y-40,80,80);
    private float hitX=-50;
    private float hitY=-50;
    private int hitWidth=100;
    private Rectangle hitRectangle;
    private double health = 6;
    private Music swordSound;


    public Player() {
        shapeRenderer = new ShapeRenderer();
        x=2000;
        y=2000;
        swordSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/sword-clash.mp3"));
        //playerAnimator = new PlayerAnimator();
    }

    public void setPlayerAnimator(PlayerAnimator playerAnimator) {
        this.playerAnimator = playerAnimator;
    }

    public void setTiledMapTileLayer(TiledMapTileLayer tiledMapTileLayer) {
        this.tiledMapTileLayer = tiledMapTileLayer;
    }

    public void render(OrthographicCamera cam) {
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(x, y, 2, 20);
        shapeRenderer.circle(x + playerAnimator.getFrame_WIDTH() * (1 / PlayerAnimator.getSIZEING()) / 2, y, 5, 20);
        if (hitRectangle!=null)
            shapeRenderer.rect(hitRectangle.x,hitRectangle.y,hitWidth,hitWidth);
        //shapeRenderer.rect(x-40,y-40,80,80);
        shapeRenderer.end();


        // System.out.println(x+" "+y);
    }

    public boolean takeDamage(){//true if dead
        health-=0.5;
        if(health<=0)
            return true;

        return false;
    }

    public void renderHitBox(Matrix4 matrix4){
        if (hitX>=0 && hitY>=0){
            shapeRenderer.setProjectionMatrix(matrix4);
            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.begin();
            shapeRenderer.rect(hitX-hitWidth/2,hitY-hitWidth/2,hitWidth,hitWidth);
            shapeRenderer.end();
            hitY=-50;
            hitX=-50;
        }
    }


    @Override
    public void renderAnimation(SpriteBatch batch) {
        playerAnimator.render(batch);
    }

    public void move(float plusX, float plusY) {
        x += plusX;
        y += plusY;
    }

    public void hit(int hitX, int hitY){ //hitY 0 ist ganz oben, nicht wie bei zb. font rendern
        swordSound.stop();
        swordSound.play();
        System.out.println(hitX+" "+hitY);




        this.hitX=hitX;
        this.hitY=hitY;

        // Position des Treffers relativ zur Spielfigur berechnen
        float worldHitX = hitX - Gdx.graphics.getWidth() / 2 + x; // angenommen Kamera ist zentriert auf Spieler
        float worldHitY = Gdx.graphics.getHeight() / 2 - hitY + y;

        hitRectangle = new Rectangle(worldHitX - hitWidth / 2, worldHitY - hitWidth / 2, hitWidth, hitWidth);

        // Abstand berechnen (einfache euklidische Entfernung)
        float dx = (hitRectangle.x + hitRectangle.width / 2) - x;
        float dy = (hitRectangle.y + hitRectangle.height / 2) - y;

        float distance = (float)Math.sqrt(dx * dx + dy * dy);

        // Wenn zu weit entfernt, skaliere den Vektor auf 300 Pixel
        if (distance > 100) {
            System.out.println("weit");
            float scale = 100f / distance;
            dx *= scale;
            dy *= scale;
        }

        // Endgültige Position mit korrektem Abstand
        float finalX = x + dx;
        float finalY = y + dy;

        // Rechteck zentriert auf finalX/finalY
        hitRectangle = new Rectangle(
            finalX - hitWidth / 2f,
            finalY - hitWidth / 2f,
            hitWidth,
            hitWidth
        );

        //System.out.println("Hit pos: " + hitRectangle.x + ", " + hitRectangle.y);

    }

    public void moveDirection(int x, int y) {
        //System.out.println("x: "+x+" y: "+y);


        if (x == 0) current_velocityX = applyDrag(current_velocityX);
        if (y == 0) current_velocityY = applyDrag(current_velocityY);


        if (x == 1) {
            current_velocityX = MAX_VELOCITY;
        } else if (x == -1) {
            current_velocityX = -MAX_VELOCITY;
        }

        if (y == 1) {
            current_velocityY = MAX_VELOCITY;
        } else if (y == -1) {
            current_velocityY = -MAX_VELOCITY;
        }

        if (x != 0 && y != 0) {
//            System.out.println("hahahahahahahH");
            if (x == y) {
                current_velocityY = (float) (current_velocityY * 0.7);
                current_velocityX = (float) (current_velocityX * 0.7);
            } else if (x == (-1 * y)) {
                current_velocityY = (float) (current_velocityY * 0.7);
                current_velocityX = (float) (current_velocityX * 0.7);
            }
        }

//        if (x!=0 && x!=0){
//            x+=Math.sqrt((current_velocityX*current_velocityX)/2);
//        }

        if (checkHorizontalTileCollision((this.x+current_velocityX),this.y))
            this.x += current_velocityX;
        else
            current_velocityX=0;

        if(checkVerticalTileCollision(this.x,this.y+current_velocityY))
            this.y += current_velocityY;
        else
            current_velocityY=0;




        updateAnimation();
        hitbox.setPosition(this.x-40,this.y-40);
    }

    private boolean checkHorizontalTileCollision(float x,float y) {
        //Pixel / float koordinate an den Grenzen der player Textur
        float rightxPos = x + playerAnimator.getFrame_WIDTH() * (1 / PlayerAnimator.getSIZEING()) / 2;
        float leftxPos = x - playerAnimator.getFrame_WIDTH() * (1 / PlayerAnimator.getSIZEING()) / 2;
        //mapping auf tiles
        int rightTile = (int) (rightxPos / 16 / 5);
        int leftTile = (int) (leftxPos / 16 / 5);

        tilexRight = rightTile;
        tileyCenter = (int) (y / 16 / PlayerAnimator.getSIZEING());


       // System.out.println(" " + tilexRight);
//        System.out.println("    " + playerAnimator.getFrame_WIDTH() / 2 / PlayerAnimator.getSIZEING());
        if (tiledMapTileLayer.getCell(leftTile, tileyCenter) == null || tiledMapTileLayer.getCell(rightTile, tileyCenter) == null) {
            //System.out.println("Horizontally out of Bounds");
            return false;
        }
        return true;
    }

    private boolean checkVerticalTileCollision(float x, float y) {


        //Pixel / float koordinate an den Grenzen der player Textur
        tileyCenter = (int) (y / 16 / PlayerAnimator.getSIZEING());
        //float upyPos = y + playerAnimator.getFrame_HEIGHT() * (1 / PlayerAnimator.getSIZEING()) / 2;
        float upyPos = y + playerAnimator.getFrame_HEIGHT() * (1 / PlayerAnimator.getSIZEING() /3);
        //float downyPos = y - playerAnimator.getFrame_HEIGHT() * (1 / PlayerAnimator.getSIZEING()) / 2;
        float downyPos = y - playerAnimator.getFrame_HEIGHT() * (1/PlayerAnimator.getSIZEING());

        int tilexCenter = (int) (x / 16 / PlayerAnimator.getSIZEING());


        //mapping auf tiles
        int upTile = (int) (upyPos / 16 / 5);
        int downTile = (int) (downyPos / 16 / 5);

        tilexRight = upTile;


        if (tiledMapTileLayer.getCell(tilexCenter, (int) upTile) == null || tiledMapTileLayer.getCell(tilexCenter, downTile) == null) {
            //System.out.println("Vertically out of Bounds");
            return false;
        }
        return true;
    }

    private void updateAnimation() {
        if (hitX>=0 && hitY>=0){
            AttackType attackType=AttackType.ATTACKING;
            if (hitRectangle.x+hitRectangle.width/2>x)
                attackType=AttackType.RIGHT;
            else if (hitRectangle.x+hitRectangle.width/2<x)
                attackType=AttackType.LEFT;
            else if(hitRectangle.y+hitRectangle.width/2>y)
                attackType=AttackType.UP;
            else if(hitRectangle.y+hitRectangle.width/2<y)
                attackType=AttackType.DOWN;

            playerAnimator.updateAttack(attackType);
        }else {

            if (current_velocityX == 0 && current_velocityY == 0) {
                // System.out.println("Standing");
                playerAnimator.updateFacing(Facing.STANDING);
            } else if (current_velocityX > 0) {
                // System.out.println("Right");
                playerAnimator.updateFacing(Facing.RIGHT);
            } else if (current_velocityX < 0) {
                //  System.out.println("Left");
                playerAnimator.updateFacing(Facing.LEFT);
            } else if (current_velocityY > 0) {
                // System.out.println("Up");
                playerAnimator.updateFacing(Facing.UP);
            } else if (current_velocityY < 0) {
                //  System.out.println("Down");
                playerAnimator.updateFacing(Facing.DOWN);
            }
        }

        playerAnimator.updatePosition(x, y);
    }

    private float applyDrag(float velocity) {
        if (velocity > 0) {
            velocity -= DRAG;
            if (velocity < 0) velocity = 0;
        } else if (velocity < 0) {
            velocity += DRAG;
            if (velocity > 0) velocity = 0;
        }
        return velocity;
    }

    public Rectangle getHitRectangle() {
        return hitRectangle;
    }

    public void resetHitRectangle(){
        hitRectangle=null;
    }

    public double getHealth(){
        return health;
    }
}
