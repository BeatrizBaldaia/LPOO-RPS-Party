package com.rpsparty.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.rpsparty.game.RPSParty;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.rpsparty.game.controller.RockGameController;
import com.rpsparty.game.controller.ScissorGameController;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Vector;


public class ScissorsGameScreen extends ScreenAdapter{
    /**
     * How much meters does a pixel represent.
     */
    public final static float PIXEL_TO_METER = 0.04f;

    private final RPSParty game;
    /**
     * The camera used to show the viewport.
     */
    private final OrthographicCamera camera;
    /**
     * The width of the viewport in meters. The height is
     * automatically calculated using the screen ratio.
     */
    private static final float VIEWPORT_WIDTH = 20;
    private Texture semiCircle;
    private ShapeRenderer quad = new ShapeRenderer();

    private Map<Integer,Vector<Integer>> positions =new HashMap<Integer,Vector<Integer>>();
    private LinkedHashSet<Integer> hashSet = new LinkedHashSet<Integer>();
    private int circleSize;
    private float timeToPlay = 5;//3 segundos para o mini-jogo

    public ScissorsGameScreen(RPSParty game) {
        this.game = game;
        loadAssets();
        camera = createCamera();
        circleSize = ScissorGameController.getInstance().getCircleSize();
        setSemiCircleTexture();
    }
    /**
     * Loads the assets needed by this screen.
     */
    private void loadAssets() {
        this.game.getAssetManager().load( "bigSemiCirc.png" , Texture.class);
        this.game.getAssetManager().load( "mediumSemiCirc.png" , Texture.class);
        this.game.getAssetManager().load( "smallSemiCirc.png" , Texture.class);
        this.game.getAssetManager().load( "extrasmallSemiCirc.png" , Texture.class);
        this.game.getAssetManager().finishLoading();
    }
    /**
     * Creates the camera used to show the viewport.
     *
     * @return the camera
     */
    private OrthographicCamera createCamera() {
        float ratio = ((float) Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth());
        OrthographicCamera camera = new OrthographicCamera(VIEWPORT_WIDTH / PIXEL_TO_METER, VIEWPORT_WIDTH / PIXEL_TO_METER * ratio);

        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
        return camera;
    }

    /**
     * Renders this screen.
     *
     * @param delta time since last renders in seconds.
     */
    @Override
    public void render(float delta) {
        game.backpressed = false;
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();
        saveFingerPosition();
        game.getBatch().begin();
        game.getBatch().draw(semiCircle, Gdx.graphics.getWidth()/2 - semiCircle.getWidth(), Gdx.graphics.getHeight()/2-semiCircle.getHeight()/2);
        game.getBatch().end();
        drawLine();
        if (Gdx.input.isKeyPressed(Keys.BACK)) {
            if (!game.backpressed) {
                game.backpressed = true;
            } else if (game.backpressed) {
                game.backpressed = false;
                Gdx.app.exit();
            }
        }

        timeToPlay -= delta;
        if(timeToPlay <= 0) {
            //fim do mini-jogo
            RockGameController.getInstance().finalResult();
            game.setScreen(new GameScreen(game));
        }

    }


    public void saveFingerPosition() {
        if(Gdx.input.getX() < Gdx.graphics.getWidth()/2)//so desenhar de metade do ecra para a direita
            return;
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        Integer x = Math.round(touchPos.x);
        Integer y = Math.round(touchPos.y);

        if (hashSet.add(y)) {//primeira vez que o dedo passa no ecra na altura y
            if(positions.get(x) != null) {//x ja existe no map
                positions.get(x).add(y);
            } else {
                Vector<Integer> v =new Vector<Integer>();
                v.add(y);
                positions.put(x, v);
            }
            ScissorGameController.getInstance().setMyPoints(Gdx.input.getX(), Gdx.input.getY());
        }

    }

    public void drawLine() {
        camera.update();
        quad.setProjectionMatrix(camera.combined);
        quad.begin(ShapeType.Filled);
        quad.setColor(0, 0, 0, 1);

        for(Map.Entry<Integer, Vector<Integer>> entry : positions.entrySet()) {
            for(Integer y : entry.getValue()) {
                quad.rect(entry.getKey(), y, 3, 3);
            }
        }
        quad.end();
    }

    public void setSemiCircleTexture() {
        switch (circleSize) {
            case 0:
                semiCircle = game.getAssetManager().get("bigSemiCirc.png");
                ScissorGameController.getInstance().setRadius(semiCircle.getWidth());
                break;
            case 1:
                semiCircle = game.getAssetManager().get("mediumSemiCirc.png");
                ScissorGameController.getInstance().setRadius(semiCircle.getWidth());
                break;
            case 2:
                semiCircle = game.getAssetManager().get("smallSemiCirc.png");
                ScissorGameController.getInstance().setRadius(semiCircle.getWidth());
                break;
            case 3:
                semiCircle = game.getAssetManager().get("extrasmallSemiCirc.png");
                ScissorGameController.getInstance().setRadius(semiCircle.getWidth());
                break;
            default:
                break;
        }
    }


}