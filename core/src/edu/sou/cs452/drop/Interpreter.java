package edu.sou.cs452.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import java.util.ArrayList;
import java.util.List;

import edu.sou.cs452.drop.Stmt.Rotation;
import edu.sou.cs452.drop.Stmt.Init;

public class Interpreter extends ApplicationAdapter implements InputProcessor, Stmt.Visitor<Void> {
    private Texture beaverImage;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    /**
     * This rectangle is populated by the Init statement and
     * represents the starting point for the Interpreter.
     */
    private List<Rectangle> beavers = new ArrayList<Rectangle>();

    static boolean hadError = false;

    private ShapeRenderer shapeRenderer;

    private IntervalInterpreter intervalInterpreter;
    private SignInterpreter signInterpreter;

    Interpreter() {
        intervalInterpreter = new IntervalInterpreter();
        signInterpreter = new SignInterpreter();
    }

    @Override
    public void create() {
        beaverImage = new Texture(Gdx.files.internal("beaver-tile-small.png"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1200);
        camera.update();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        Gdx.input.setInputProcessor(this);
        Gdx.gl.glLineWidth(3f);
    }

    @Override
    public void render() {
        // These colors are chosen to match the background of
        // the parachuting beavers.
        ScreenUtils.clear(0.66f, 0.82f, 0f, 0.5f);

        // Update the overApproximation rectangle
        Rectangle overApproximation = intervalInterpreter.getOverapproximationRectangle();
        Rectangle signOverApproximation = signInterpreter.getOverapproximationRectangle();

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Rectangle beaver : beavers) {
            batch.draw(beaverImage, beaver.x + (1920 / 2), beaver.y + (1200 / 2));
        }
        batch.end();

        drawAxes();

        // Draw the overApproximation rectangle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 0);
        shapeRenderer.setProjectionMatrix(camera.combined);

        float xStart = overApproximation.x + (1920 / 2) - 64;
        if (xStart < 1) {
            xStart = 1;
        }
        float yStart = overApproximation.y + (1200 / 2) - 64;
        if (yStart < 1) {
            yStart = 1;
        }

        float width = overApproximation.width + 128;
        if (xStart + width > 1920) {
            width = 1920 - xStart - 1;
        }
        float height = overApproximation.height + 128;
        if (yStart + height > 1200) {
            height = 1200 - yStart - 1;
        }

        shapeRenderer.rect(xStart, yStart, width, height);
        shapeRenderer.end();

        // Draw the overApproximation rectangle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 1, 0);
        shapeRenderer.setProjectionMatrix(camera.combined);

        float signXStart = signOverApproximation.x + (1920 / 2);
        if (signXStart < 1) {
            signXStart = 1;
        }
        float signYStart = signOverApproximation.y + (1200 / 2);
        if (signYStart < 1) {
            signYStart = 1;
        }

        float signWidth = signOverApproximation.width + 64;
        if (signWidth > 1920) {
            signWidth = 1920 - signXStart - 1;
        }
        float signHeight = signOverApproximation.height + 64;
        if (signHeight > 1200) {
            signHeight = 1200 - signYStart - 1;
        }

        shapeRenderer.rect(signXStart, signYStart, signWidth, signHeight);
        shapeRenderer.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE) {
            Gdx.app.exit();
        }
        return false;
    }

    private void drawAxes() {
        float centerX = 1920 / 2;
        float centerY = 1200 / 2;
        float axisLength = 2000;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);

        // Draw the horizontal axis (X)
        shapeRenderer.line(centerX - axisLength / 2, centerY, centerX + axisLength / 2, centerY);

        // Draw the vertical axis (Y)
        shapeRenderer.line(centerX, centerY - axisLength / 2, centerX, centerY + axisLength / 2);

        shapeRenderer.end();
    }

    // Implement other InputProcessor methods as required

    // === BEGIN: STUBS FOR UNUSED InputProcessor METHODS ===
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    // === BEGIN: INTERPRETER METHODS ===

    void interpret(List<Stmt> statements) {
        intervalInterpreter.interpret(statements);
        signInterpreter.interpret(statements);

        for (Stmt statement : statements) {
            execute(statement);
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    @Override
    public Void visitOrStmt(Stmt.Or stmt) {
        boolean executeLeftBlock = Math.random() < 0.5;

        if (executeLeftBlock) {
            for (Stmt s : stmt.left) {
                execute(s);
            }
        } else {
            for (Stmt s : stmt.right) {
                execute(s);
            }
        }

        return null;
    }

    @Override
    public Void visitIterStmt(Stmt.Iter stmt) {
        // Generate a random number of iterations between 5 and 10
        int iterations = 5 + (int) (Math.random() * 6);

        for (int i = 0; i < iterations; i++) {
            for (Stmt s : stmt.body) {
                execute(s);
            }
        }

        return null;
    }

    @Override
    public Void visitTranslationStmt(Stmt.Translation stmt) {
        if (beavers.isEmpty()) {
            throw new IllegalStateException("Cannot perform translation: Beavers list is empty.");
        }

        Rectangle lastBeaver = beavers.get(beavers.size() - 1);
        Rectangle newBeaver = new Rectangle();

        newBeaver.x = lastBeaver.x + stmt.u;
        newBeaver.y = lastBeaver.y + stmt.v;
        newBeaver.width = lastBeaver.width;
        newBeaver.height = lastBeaver.height;

        beavers.add(newBeaver);

        return null;
    }

    /**
     * Visits a Rotation statement and applies a clockwise rotation transformation
     * to the position of the last beaver in the list of beavers.
     * The rotation is performed around a specified point (u, v) by a given angle.
     *
     * @param stmt The Rotation statement containing the rotation parameters:
     *             the x-coordinate (u) and y-coordinate (v) of the point
     *             around which the rotation is performed, and the angle
     *             in degrees for the clockwise rotation.
     * @throws IllegalStateException if the beavers list is empty, since
     *                               there must be a beaver to apply the rotation
     *                               transformation.
     */
    @Override
    public Void visitRotationStmt(Rotation stmt) {
        if (beavers.isEmpty()) {
            throw new IllegalStateException("Cannot perform rotation: Beavers list is empty.");
        }

        Rectangle lastBeaver = beavers.get(beavers.size() - 1);
        float newX = (float) (stmt.u + (lastBeaver.x - stmt.u) * Math.cos(Math.toRadians(stmt.angle))
                - (lastBeaver.y - stmt.v) * Math.sin(Math.toRadians(stmt.angle)));
        float newY = (float) (stmt.v + (lastBeaver.x - stmt.u) * Math.sin(Math.toRadians(stmt.angle))
                + (lastBeaver.y - stmt.v) * Math.cos(Math.toRadians(stmt.angle)));

        Rectangle newBeaver = new Rectangle();
        newBeaver.x = newX;
        newBeaver.y = newY;
        newBeaver.width = lastBeaver.width;
        newBeaver.height = lastBeaver.height;

        beavers.add(newBeaver);

        return null;
    }

    /**
     * Visits an Init statement, creating a new beaver Rectangle and adding it to
     * the list of beavers.
     * The beaver is placed at a random location within the specified range of
     * coordinates.
     *
     * @param stmt The Init statement containing the range of coordinates for the
     *             new beaver.
     * @return null, as this method does not return any meaningful value.
     */
    @Override
    public Void visitInitStmt(Init stmt) {
        float x = (float) (Math.random() * (stmt.p2.x - stmt.p1.x) + stmt.p1.x);
        float y = (float) (Math.random() * (stmt.p2.y - stmt.p1.y) + stmt.p1.y);

        Rectangle beaver = new Rectangle();
        beaver.x = x;
        beaver.y = y;
        beaver.width = 64;
        beaver.height = 64;

        beavers.add(beaver);

        return null;
    }

}
