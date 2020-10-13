package com.shinkson47.FrameInterpolation.demo.frames;

public class PlaybackThread implements Runnable {

    private static int TIME_FRAME = 1;

    private long lastMillis;
    private FramesController controller;
    private int fps;

    private boolean run = true;

    public PlaybackThread(int _fps, FramesController _controller){
        fps = _fps;
        controller = _controller;
    }

    @Override
    public void run() {
        while(run){
            try {

                Thread.sleep(((TIME_FRAME * 1000) / fps) -
                        (((TIME_FRAME * 1000) / fps - lastMillis < 0) ? 0 : lastMillis));
                lastMillis = System.currentTimeMillis();
                controller.stepFrame();
                lastMillis = System.currentTimeMillis() - lastMillis;
            } catch (InterruptedException ignored){}
        }
    }


    public void stopPlayback(){
        run = false;
    }

    public synchronized void setFPS(int value) {
        fps = value;
    }

    public synchronized boolean isRunning() {
        return run;
    }
}
