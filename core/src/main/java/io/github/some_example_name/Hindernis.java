package io.github.some_example_name;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Hindernis {
    private static final float POSX = 50;
    private static final float POSY = 50;
    private Sprite worldSprite;
    private SpriteBatch spriteBatchRenderer;
    private Rectangle hitbox;

    public Hindernis(String spritePath, SpriteBatch spriteBatchRenderer, Rectangle hitbox) {
        this.worldSprite = new Sprite(new Texture(spritePath));
        this.spriteBatchRenderer = spriteBatchRenderer;
        this.hitbox = hitbox;
        hitbox.setX(POSX);
        hitbox.setY(POSY);
    }

    public void render(OrthographicCamera cam) {
        spriteBatchRenderer.setProjectionMatrix(cam.combined);
        spriteBatchRenderer.begin();
        worldSprite.draw(spriteBatchRenderer);
        spriteBatchRenderer.end();
    }


}
