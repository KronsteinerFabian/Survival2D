package io.github.some_example_name;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.LinkedList;

public class World {
    private Sprite worldSprite;
    private SpriteBatch spriteBatchRenderer;
    private LinkedList<Hindernis> hinderisse;

    public World(String worldSpritePath, SpriteBatch spriteBatchRenderer) {
        worldSprite = new Sprite(new Texture(worldSpritePath));
        this.spriteBatchRenderer = spriteBatchRenderer;
        hinderisse = new LinkedList<>();

    }

    public void render(OrthographicCamera cam) {
        spriteBatchRenderer.setProjectionMatrix(cam.combined);
        worldSprite.draw(spriteBatchRenderer);
        spriteBatchRenderer.end();
    }

    public void dispose() {

    }
}
