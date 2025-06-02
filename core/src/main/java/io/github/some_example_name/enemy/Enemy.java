package io.github.some_example_name.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.AttackType;
import io.github.some_example_name.Entity;
import io.github.some_example_name.Facing;

public class Enemy extends Entity {
    //public float x = 2500;
    //public float y = 2500;
    private EnemyAnimator enemyAnimator;
    private ShapeRenderer shapeRenderer;
    private static final float MAX_VELOCITY = 10 * 6f / 60f;
    private static final float DRAG = 1 * 6f / 60f;
    private float current_velocityX = 0;
    private float current_velocityY = 0;
    private TiledMapTileLayer tiledMapTileLayer;
    private int tilexCenter = 0;
    private int tilexRight = 0;
    private int tileyCenter = 0;
    private int hitboxWidth = 80;
    private int attackBoxWidth = 100;
    public Rectangle hitbox = new Rectangle(x - hitboxWidth / 2, y - hitboxWidth / 2, hitboxWidth, hitboxWidth);
    public Rectangle attackBox = new Rectangle(x - attackBoxWidth / 2, y - attackBoxWidth / 2, attackBoxWidth, attackBoxWidth);
    private double health = 4;
    private float secondsSinceLastAttack = 0;
    private double secondsBetweenAttacks = 4;


    public Enemy() {
        x = 2500;
        y = 2500;
        shapeRenderer = new ShapeRenderer();
    }


    public Enemy(float x, float y) {
        this.x = x;
        this.y = y;
        shapeRenderer = new ShapeRenderer();
    }

    public void setEnemyAnimator(EnemyAnimator enemyAnimator) {
        this.enemyAnimator = enemyAnimator;
    }

    public void setTiledMapTileLayer(TiledMapTileLayer tiledMapTileLayer) {
        this.tiledMapTileLayer = tiledMapTileLayer;
    }

    public boolean attack() {
        boolean firstAttack;
        if (secondsSinceLastAttack == 0f) {
            enemyAnimator.updateAttackTpye(AttackType.ATTACKING);
            firstAttack = true;
        } else {
            firstAttack = false;
        }
        secondsSinceLastAttack += Gdx.graphics.getDeltaTime();

        if (secondsSinceLastAttack >= secondsBetweenAttacks) {
            secondsSinceLastAttack = 0f;
            enemyAnimator.updateAttackTpye(AttackType.ATTACKING);
            return true;
        }


        return firstAttack;
    }


    public void render(OrthographicCamera cam) {
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(x, y, 10, 20);
        //shapeRenderer.circle(x + enemyAnimator.getFrame_WIDTH() * (1 / EnemyAnimator.getSIZEING()) / 2, y, 5, 20);
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        shapeRenderer.rect(attackBox.x, attackBox.y, attackBox.width, attackBox.height);
        shapeRenderer.end();


        // System.out.println(x+" "+y);
    }

    @Override
    public void renderAnimation(SpriteBatch batch) {
        enemyAnimator.render(batch);
    }

    public void move(float plusX, float plusY) {
        x += plusX;
        y += plusY;
    }

    public boolean takeDamage() { //true if enemy is dead
        health -= 1.5;

        if (health <= 0) {
            return true;
        }

        return false;
    }

    public void moveToPlayer(float playerX, float playerY) {
        //double degree = Math.atan2(y-playerY,x-playerX);
        //System.out.println(degree);

        Vector2 gegnerPos = new Vector2(x, y);
        Vector2 spielerPos = new Vector2(playerX, playerY);

        Vector2 richtung = new Vector2(spielerPos).sub(gegnerPos).nor();

        gegnerPos.add(richtung.scl(MAX_VELOCITY));

        x = gegnerPos.x;
        y = gegnerPos.y;
        current_velocityX = richtung.x * MAX_VELOCITY;
        current_velocityY = richtung.y * MAX_VELOCITY;

        updateAnimation();
        hitbox.setPosition(x - hitboxWidth / 2, y - hitboxWidth / 2);
        attackBox.setPosition(x - attackBoxWidth / 2, y - attackBoxWidth / 2);

    }

    private boolean checkHorizontalTileCollision(float x, float y) {
        //Pixel / float koordinate an den Grenzen der player Textur
        float rightxPos = x + enemyAnimator.getFrame_WIDTH() * (1 / EnemyAnimator.getSIZEING()) / 2;
        float leftxPos = x - enemyAnimator.getFrame_WIDTH() * (1 / EnemyAnimator.getSIZEING()) / 2;
        //mapping auf tiles
        int rightTile = (int) (rightxPos / 16 / 5);
        int leftTile = (int) (leftxPos / 16 / 5);

        tilexRight = rightTile;
        tileyCenter = (int) (y / 16 / EnemyAnimator.getSIZEING());


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
        tileyCenter = (int) (y / 16 / EnemyAnimator.getSIZEING());
        //float upyPos = y + playerAnimator.getFrame_HEIGHT() * (1 / PlayerAnimator.getSIZEING()) / 2;
        float upyPos = y + enemyAnimator.getFrame_HEIGHT() * (1 / EnemyAnimator.getSIZEING() / 3);
        //float downyPos = y - playerAnimator.getFrame_HEIGHT() * (1 / PlayerAnimator.getSIZEING()) / 2;
        float downyPos = y - enemyAnimator.getFrame_HEIGHT() * (1 / EnemyAnimator.getSIZEING());

        int tilexCenter = (int) (x / 16 / EnemyAnimator.getSIZEING());


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
        if (current_velocityX == 0 && current_velocityY == 0) {
            // System.out.println("Standing");
            enemyAnimator.updateFacing(Facing.STANDING);
        } else if (current_velocityX > 0) {
            // System.out.println("Right");
            enemyAnimator.updateFacing(Facing.RIGHT);
        } else if (current_velocityX < 0) {
            //  System.out.println("Left");
            enemyAnimator.updateFacing(Facing.LEFT);
        } else if (current_velocityY > 0) {
            // System.out.println("Up");
            enemyAnimator.updateFacing(Facing.UP);
        } else if (current_velocityY < 0) {
            //  System.out.println("Down");
            enemyAnimator.updateFacing(Facing.DOWN);
        }

        enemyAnimator.updatePosition(x, y);
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


}
