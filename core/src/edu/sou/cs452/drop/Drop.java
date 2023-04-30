package edu.sou.cs452.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;


public class Drop extends ApplicationAdapter {
   private Texture dropImage;
   private Texture bucketImage;

   private OrthographicCamera camera;
   private SpriteBatch batch;

   private Rectangle bucket;




    @Override
    public void create() {
        // load the images for the droplet and the bucket, 64x64 pixels each
        //dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("beaver-tile-small.png"));      

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        
        batch = new SpriteBatch();

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 200;
        bucket.width = 64;
        bucket.height = 64;
        
    }

    @Override
    public void render() {
       ScreenUtils.clear(0.66f, 0.82f, 0, 1);

       camera.update();

       batch.setProjectionMatrix(camera.combined);
       batch.begin();
       batch.draw(bucketImage, bucket.x, bucket.y);
       batch.end();    
    }
 
}
