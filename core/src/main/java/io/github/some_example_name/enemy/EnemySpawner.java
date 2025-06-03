package io.github.some_example_name.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.Player;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.badlogic.gdx.math.MathUtils.random;

public class EnemySpawner {
    private List<Enemy> enemies = new LinkedList<>();

    private int max_map_width;
    private int max_map_height;
    private float secs_between_spawn=5;
    private float lastSpawn=5;
    private static final float SPAWN_RADIUS_X = 500f;
    private static final float SPAWN_RADIUS_Y = 200f;
    private static final int SPAWN_OFFSET_Y = 600;
    private static final int MAX_ENEMIES = 20;  // Beispiel, falls du Limit möchtest


    public EnemySpawner(int max_map_width, int max_map_height){
        this.max_map_height=max_map_height;
        this.max_map_width=max_map_width;
    }

//    public void updateSpawning(float playerX,float playerY){
//        lastSpawn+=Gdx.graphics.getDeltaTime();
//        if (lastSpawn>=secs_between_spawn){
//            System.out.println("spawn");
//            EnemyAnimator enemyAnimator = new EnemyAnimator();
//            enemyAnimator.create();
//            Enemy enemy = new Enemy(playerX+ 150,playerY+150);
//            enemy.setEnemyAnimator(enemyAnimator);
//            enemies.add(enemy);
//
//            lastSpawn=0;
//        }
//    }



    public void updateSpawning(float playerX, float playerY) {
        lastSpawn += Gdx.graphics.getDeltaTime();

        if (lastSpawn >= secs_between_spawn) {
            // Optional: spawn nur wenn nicht zu viele Gegner existieren
            if (enemies.size() < MAX_ENEMIES) {
                System.out.println("spawn");

                EnemyAnimator enemyAnimator = new EnemyAnimator();
                enemyAnimator.create();

                Enemy enemy = new Enemy();
                enemy.setEnemyAnimator(enemyAnimator);

                // Zufällige Spawnposition um Spieler herum
                float spawnX, spawnY;
                if (random.nextBoolean()) {
                    spawnX = playerX + random.nextInt((int) SPAWN_RADIUS_X);
                    spawnY = playerY + SPAWN_OFFSET_Y + random.nextInt((int) SPAWN_RADIUS_Y);
                } else {
                    spawnX = playerX - random.nextInt((int) SPAWN_RADIUS_X);
                    spawnY = playerY - SPAWN_OFFSET_Y - random.nextInt((int) SPAWN_RADIUS_Y);
                }

                enemy.x = spawnX;
                enemy.y = spawnY;

                enemies.add(enemy);
            }

            lastSpawn = 0;
        }
    }


    public int checkHits(Rectangle hitRectangle){
        int kills=0;
        Iterator<Enemy> enemyIterator = enemies.iterator();
        Enemy e = null;
        while (enemyIterator.hasNext()){
            e = enemyIterator.next();
            if (e.hitbox.overlaps(hitRectangle)) {
                System.out.println("took damage!!!");
                if (e.takeDamage()) {
                    kills++;
                    enemyIterator.remove();
                }
            }
        }
        return kills;
    }

    public void updatePositions(float playerX,float playerY){
        for (Enemy e: enemies){
            e.moveToPlayer(playerX,playerY);
        }
    }

    public Collection<Enemy> getEnemies(){
        return enemies;
    }

    public void checkAttackBoxes(Player player){
        for (Enemy e: enemies){
            if(player.hitbox.overlaps(e.attackBox)){
                if (e.attack())
                    player.takeDamage();
            }
        }
    }
}
