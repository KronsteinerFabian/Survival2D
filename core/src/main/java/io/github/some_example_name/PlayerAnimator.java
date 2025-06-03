package io.github.some_example_name;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.tiled.TiledMap;
import org.w3c.dom.Text;

import java.text.BreakIterator;

public class PlayerAnimator implements ApplicationListener{
    private Animation<TextureRegion> standingAnimation;
    private TextureRegion[] standingFrames;

    private Animation<TextureRegion> walkingLeftAnimation;
    private TextureRegion[] walkingLeftFrames;

    private TextureRegion[] walkingRightFrames;
    private Animation<TextureRegion> walkingRightAnimation;

    private TextureRegion[] walkingDownFrames;
    private Animation<TextureRegion> walkingDownAnimation;

    private TextureRegion[] walkingUpFrames;
    private Animation<TextureRegion> walkingUpAnimation;

    private TextureRegion[] attackFrontFrames;
    private Animation<TextureRegion> attackFrontAnimation;

    private TextureRegion[] attackBackFrames;
    private Animation<TextureRegion> attackBackAnimation;

    private TextureRegion[] attackRightFrames;
    private Animation<TextureRegion> attackRightAnimation;

    private TextureRegion[] attackLeftFrames;
    private Animation<TextureRegion> attackLeftAnimation;

    private Facing facing = Facing.STANDING;

    private float xPos;
    private float yPos;

    private float frame_HEIGHT;
    private float frame_WIDTH;

    private static float SIZEING=5f;
    private AttackType attackType = AttackType.NONE;




    private static final int FRAME_COLS = 6, FRAME_ROWS = 10;
    private TextureRegion currFrame = new TextureRegion();
    Animation<TextureRegion> wholeAnimation; // Must declare frame type (TextureRegion)
    Texture walkSheet;
    float stateTime;

    @Override
    public void create() {
        walkSheet = new Texture(Gdx.files.internal("player.png"));

        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
            walkSheet.getWidth() / FRAME_COLS,
            walkSheet.getHeight() / FRAME_ROWS);

        standingFrames = new TextureRegion[FRAME_COLS];
        walkingRightFrames = new TextureRegion[FRAME_COLS];
        walkingLeftFrames = new TextureRegion[FRAME_COLS];
        walkingDownFrames = new TextureRegion[FRAME_COLS];
        walkingUpFrames = new TextureRegion[FRAME_COLS];

        attackFrontFrames = new TextureRegion[4];
        attackBackFrames = new TextureRegion[4];
        attackLeftFrames= new TextureRegion[4];
        attackRightFrames = new TextureRegion[4];

        TextureRegion[] wholeFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                switch (i){
                    case 0:
                        standingFrames[j]=tmp[i][j];
                        break;
                    case 4:
                        walkingRightFrames[j]=tmp[i][j];
                        walkingLeftFrames[j]= new TextureRegion(tmp[i][j]);
                        walkingLeftFrames[j].flip(true,false);
                        break;
                    case 3:
                        walkingDownFrames[j]=tmp[i][j];
                        break;
                    case 5:
                        walkingUpFrames[j]=tmp[i][j];
                        break;
                    case 6:
                        if (j<4)
                            attackFrontFrames[j]=tmp[i][j];
                        break;
                    case 7:
                        if (j<4) {
                            attackRightFrames[j] = tmp[i][j];
                            attackLeftFrames[j] = new TextureRegion(tmp[i][j]);
                            attackLeftFrames[j].flip(true,false);
                        }
                        break;
                    case 8:
                        if (j<4)
                            attackBackFrames[j]=tmp[i][j];
                        break;
                }
                wholeFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        wholeAnimation = new Animation<TextureRegion>(0.7f, wholeFrames);
        standingAnimation = new Animation<>(0.2f,standingFrames);
        walkingRightAnimation = new Animation<>(0.2f,walkingRightFrames);
        walkingLeftAnimation = new Animation<>(0.2f,walkingLeftFrames);
        walkingDownAnimation = new Animation<>(0.2f,walkingDownFrames);
        walkingUpAnimation = new Animation<>(0.2f,walkingUpFrames);

        attackFrontAnimation = new Animation<>(0.2f,attackFrontFrames);
        attackBackAnimation = new Animation<>(0.2f,attackBackFrames);
        attackLeftAnimation = new Animation<>(0.2f,attackLeftFrames);
        attackRightAnimation = new Animation<>(0.2f,attackRightFrames);

        // Instantiate a SpriteBatch for drawing and reset the elapsed animation
        // time to 0
        stateTime = 0f;

        frame_HEIGHT = standingFrames[0].getRegionHeight()*SIZEING;
        frame_WIDTH = standingFrames[0].getRegionWidth()*SIZEING;

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    public float getFrame_WIDTH() {
        return frame_WIDTH;
    }

    public float getFrame_HEIGHT() {
        return frame_HEIGHT;
    }



    public void updatePosition(float x, float y){

        xPos=x-(frame_HEIGHT/2);
        yPos=y-(frame_WIDTH/3);
    }

    public void render(SpriteBatch spriteBatch) {
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
        TextureRegion currentFrame = new TextureRegion();

        // Get current frame of animation for the current stateTime
        //TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        //TextureRegion currentFrame = standingAnimation.getKeyFrame(stateTime, true);
        //TextureRegion currentFrame = walkingLeftAnimation.getKeyFrame(stateTime, true);
        //TextureRegion currentFrame = walkingRightAnimation.getKeyFrame(stateTime, true);
        //TextureRegion currentFrame = walkingDownAnimation.getKeyFrame(stateTime, true);
        //TextureRegion currentFrame = walkingUpAnimation.getKeyFrame(stateTime, true);

        if (attackType==AttackType.NONE) {

            switch (facing) {
                case STANDING:
                    currentFrame = standingAnimation.getKeyFrame(stateTime, true);
                    break;
                case LEFT:
                    currentFrame = walkingLeftAnimation.getKeyFrame(stateTime, true);
                    break;
                case RIGHT:
                    currentFrame = walkingRightAnimation.getKeyFrame(stateTime, true);
                    break;
                case DOWN:
                    currentFrame = walkingDownAnimation.getKeyFrame(stateTime, true);
                    break;
                case UP:
                    currentFrame = walkingUpAnimation.getKeyFrame(stateTime, true);
                    break;
            }

        }else {
            switch (attackType) {

                case LEFT:
                    currentFrame = attackLeftAnimation.getKeyFrame(stateTime,false);
                    if(attackLeftAnimation.isAnimationFinished(stateTime)) {
                        attackType = AttackType.NONE;
                        stateTime = 0f;
                    }

                    break;
                case RIGHT:
                    currentFrame = attackRightAnimation.getKeyFrame(stateTime,false);
                    if(attackRightAnimation.isAnimationFinished(stateTime)) {
                        attackType = AttackType.NONE;
                        stateTime = 0f;
                    }

                    break;
                case DOWN:
                    currentFrame = attackFrontAnimation.getKeyFrame(stateTime,false);
                    if(attackFrontAnimation.isAnimationFinished(stateTime)) {
                        attackType = AttackType.NONE;
                        stateTime = 0f;
                    }

                    break;
                case UP:
                    currentFrame = attackBackAnimation.getKeyFrame(stateTime,false);
                    if(attackBackAnimation.isAnimationFinished(stateTime)) {
                        attackType = AttackType.NONE;
                        stateTime = 0f;
                    }

                    break;

                default:
                    currentFrame = attackRightAnimation.getKeyFrame(stateTime,false);
                    if(attackRightAnimation.isAnimationFinished(stateTime)) {
                        attackType = AttackType.NONE;
                        stateTime = 0f;
                    }

                    break;
            }
        }

        spriteBatch.draw(currentFrame, xPos, yPos,frame_WIDTH,frame_HEIGHT); // Draw current frame at (50, 50)
    }

    public void updateFacing(Facing facing){
        this.facing=facing;
    }

    public void updateAttack(AttackType attackType){
        this.attackType=attackType;
    }

    public static float getSIZEING() {
        return SIZEING;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() { // SpriteBatches and Textures must always be disposed
        walkSheet.dispose();
    }
}

