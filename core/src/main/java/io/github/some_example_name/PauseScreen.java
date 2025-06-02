package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseScreen implements Screen {
    private final Main main;
    private final Screen previousScreen;
    private Stage stage;
    private Skin skin;
    private Music music;

    public PauseScreen(Main main, Screen previousScreen) {
        this.main = main;
        this.previousScreen = previousScreen;
        music = Gdx.audio.newMusic(Gdx.files.internal("music/pauseMenuMusic.mp3"));
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        System.out.println("laden");
        skin = new Skin(Gdx.files.internal("uiskin/uiskin.json")); // UI-Skin
        System.out.println("geladen");

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("Pause Menu", skin);
        TextButton resume = new TextButton("Weiter", skin);
        TextButton quit = new TextButton("Beenden", skin);

        resume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                music.stop();
                previousScreen.resume();
                main.setScreen(previousScreen); // zurück ins Spiel
            }
        });

        quit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // oder MainMenu
            }
        });

        table.add(title).padBottom(20).row();
        table.add(resume).pad(10).row();
        table.add(quit).pad(10).row();

        music.play();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
