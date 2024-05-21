package com.deemaso.grotto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.deemaso.core.GameWorld;
import com.deemaso.grotto.systems.GrottoRenderSystem;

public class AndroidFastRenderView extends SurfaceView implements Runnable {
    private final Bitmap frameBuffer;
    private Thread renderThread = null;
    private final SurfaceHolder holder;
    private final GameWorld gameWorld;
    private volatile boolean running = false;
    
    public AndroidFastRenderView(Context context, GameWorld gw, int bufferWidth, int bufferHeight) {
        super(context);
        this.gameWorld = gw;
        //find RenderSystem in gameWorld
        this.frameBuffer = Bitmap.createBitmap(bufferWidth, bufferHeight, Bitmap.Config.ARGB_8888);
        this.holder = getHolder();
    }

    public Bitmap getFrameBuffer() {
        return frameBuffer;
    }

    /** Starts the game loop in a separate thread.
     */
    public void resume() {
        gameWorld.resume();
        running = true;
        renderThread = new Thread(this);
        renderThread.start();         
    }

    /** Stops the game loop and waits for it to finish
     */
    public void pause() {
        gameWorld.pause();
        running = false;
        while(true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                // just retry
            }
        }
    }

    public void run() {
        Rect dstRect = new Rect();
        long startTime = System.nanoTime(), fpsTime = startTime, frameCounter = 0;

        /*** The Game Main Loop ***/
        while (running) {
            if(!holder.getSurface().isValid()) {
                // too soon (busy waiting), this only happens on startup and resume
                continue;
            }

            long currentTime = System.nanoTime();
            // deltaTime is in seconds
            float deltaTime = (currentTime-startTime) / 1000000000f,
                  fpsDeltaTime = (currentTime-fpsTime) / 1000000000f;
            startTime = currentTime;

            gameWorld.update(deltaTime);
            //gameWorld.render(); -- Now done in RenderSystem

            // Draw framebuffer on screen
            Canvas canvas = holder.lockCanvas();
            canvas.getClipBounds(dstRect);
            // Scales to actual screen resolution
            canvas.drawBitmap(frameBuffer, null, dstRect, null);
            holder.unlockCanvasAndPost(canvas);

            // Measure FPS
            frameCounter++;
            if (fpsDeltaTime > 1) { // Print every second
                //Log.d("FastRenderView", "Current FPS = " + frameCounter);
                frameCounter = 0;
                fpsTime = currentTime;
            }
        }
    }
}