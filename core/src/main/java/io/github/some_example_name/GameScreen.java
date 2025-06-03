package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.enemy.Enemy;
import io.github.some_example_name.enemy.EnemyAnimator;
import io.github.some_example_name.enemy.EnemySpawner;


import java.util.Comparator;

import java.util.TreeSet;


/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class GameScreen implements Screen {
    private final Main main;

    private boolean gameOver=true;
    private SpriteBatch batch;
    private Player player;
    private OrthographicCamera cam;
    private World world;
    private Hindernis hindernis;
    private PlayerAnimator playerAnimator;
    private Audio audio;
    private Texture mapTexture;
    private ScreenViewport screenViewport;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tileMapRenderer;
    private TiledMapTileLayer collisionLayer;
    private Enemy enemy;
    private EnemyAnimator enemyAnimator;
    private TreeSet<Entity> entities = new TreeSet<>(new Comparator<Entity>() {
        @Override
        public int compare(Entity o1, Entity o2) {
            if (o1.y>o2.y)
                return -1;
            else if (o1.y<o2.y)
                return 1;
            else
                return 0;
        }
    });

    private OrthographicCamera uiCamera;
    private Matrix4 uiMatrix;
    private BitmapFont font;
    private EnemySpawner enemySpawner;
    private boolean justTouched=false;
    private int kills=0;
    private Music mainMusic;


    public GameScreen(Main main){
        this.main=main;
        batch = new SpriteBatch();

        player = new Player();

        cam = new OrthographicCamera();
        screenViewport = new ScreenViewport(cam);


        playerAnimator = new PlayerAnimator();
        playerAnimator.create();
        player.setPlayerAnimator(playerAnimator);


        FileHandleResolver resolver = new InternalFileHandleResolver();
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        TmxMapLoader loader = new TmxMapLoader(resolver);
        tiledMap = loader.load("tileMap2/FabianStuff.tmx", params);


        tileMapRenderer = new OrthogonalTiledMapRenderer(tiledMap,5f);
        collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Ground");

        player.setTiledMapTileLayer(collisionLayer);

        enemy = new Enemy();
        enemyAnimator = new EnemyAnimator();
        enemyAnimator.create();
        enemy.setEnemyAnimator(enemyAnimator);

        entities.add(player);
        entities.add(enemy);

        audio = Gdx.audio;
        mainMusic = audio.newMusic(Gdx.files.internal("music/battleMusic.mp3"));
        mainMusic.play();


        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Bildschirmgröße
        uiMatrix = uiCamera.combined;


        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter paramsFont = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramsFont.size = 46;
        font = generator.generateFont(paramsFont);
        generator.dispose();

        enemySpawner = new EnemySpawner(collisionLayer.getWidth()*collisionLayer.getTileWidth(),collisionLayer.getHeight()*collisionLayer.getTileHeight());

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //world.step(1 / 60f, 6, 2);



        update();



        cam.position.set(player.x, player.y, 0);
        cam.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);


        tileMapRenderer.setView(cam);
        tileMapRenderer.render();

        batchDepthRender();

        batch.setProjectionMatrix(uiMatrix);
        batch.begin();
        font.draw(batch, "Leben: "+player.getHealth()+" Punkte: "+kills , 20, font.getXHeight()+20);
//        if (paused)
//            font.draw(batch,"Paused",Gdx.graphics.getWidth()/2-150/2,Gdx.graphics.getHeight()/2+font.getXHeight()/2);

        batch.end();

        //Shaperenders
        player.render(cam);
        player.renderHitBox(uiMatrix);
//        enemy.render(cam);
    }

    @Override
    public void resize(int width, int height) {
        screenViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        mainMusic.play();
    }

    @Override
    public void hide() {

    }


    public void batchDepthRender(){
        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        entities.removeAll(entities);

        for (Enemy e : enemySpawner.getEnemies()){
            entities.add(e);
        }

        entities.add(player);
//        entities.add(enemy);
        //System.out.println("\t"+entities.size());
        for (Entity e : entities){
            e.renderAnimation(batch);
        }

        //playerAnimator.render(batch);
        //player.renderAnimation(batch);
        //enemy.renderAnimation(batch);
        //batch.setProjectionMatrix(cam.invProjectionView);

        batch.end();
    }

    public void update() {
        handleInput();
//        if (!player.hitbox.overlaps(enemy.hitbox)) {
//            enemy.moveToPlayer(player.x, player.y);
//        }


        enemySpawner.updatePositions(player.x, player.y);
        enemySpawner.updateSpawning(player.x, player.y);
        enemySpawner.checkAttackBoxes(player);

        if(player.getHealth()<=0) {
            gameOver = true;
            mainMusic.stop();
            main.setScreen(new MainMenuScreen(main));
        }


        //System.out.println(player.hitbox.overlaps(enemy.hitbox));
    }

    private void handleInput() {
        int x = 0;
        int y = 0;

        if (Gdx.input.justTouched()){
            player.hit(Gdx.input.getX(),Gdx.input.getY());

//            if (player.getHitRectangle().overlaps(enemy.hitbox))
//                System.out.println("hit");

            kills+=enemySpawner.checkHits(player.getHitRectangle());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            cam.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            cam.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            //cam.translate(-3, 0, 0);
            //player.move(-VELOCITY,0);
            x = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            //cam.translate(3, 0, 0);
            //player.move(VELOCITY,0);
            x = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            //cam.translate(0, -3, 0);
            //player.move(0,-VELOCITY);
            y = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            //cam.translate(0, 3, 0);
            //player.move(0,VELOCITY);
            y = 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            mainMusic.pause();
            main.setScreen(new PauseScreen(main, this));
        }


        player.moveDirection(x, y);


        cam.zoom = MathUtils.clamp(cam.zoom, 0.3f, 1);

        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;
        //cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f);
        //cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
