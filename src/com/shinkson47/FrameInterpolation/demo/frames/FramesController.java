package com.shinkson47.FrameInterpolation.demo.frames;

import com.shinkson47.FrameInterpolation.FrameBuffer;
import com.shinkson47.FrameInterpolation.LinearImageInterpolator;
import com.shinkson47.opex.backend.runtime.errormanagement.EMSHelper;
import com.shinkson47.opex.frontend.fxml.FXMLController;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * GUI controller for the Frame generation Prototype
 * @version 1
 * @author <a href="https://www.shinkson47.in">Jordan T. Gray</a>
 */
public class FramesController extends FXMLController {

    //#region FXML
    /**
     * Relative location of FXML document containing the definition for the POS window UI.
     */
    public static final String SLIDER_FXML = "/com/shinkson47/FrameInterpolation/demo/frames/FrameDemo.fxml";
    public ImageView img_pre;
    public ImageView img_post;
    public Slider sld_pos;
    public Button btn_prc;
    public ImageView img_inter;
    public Label lblTime;
    public Label lblAverage;
    public Label lblEstimated;
    public ImageView img_inter1;
    public ImageView img_inter2;
    public Label lblRT;
    public Label lblBufferSize;
    public Label lblEstTotal;
    public Slider sld_fps;
    public Slider sld_seek;
    public CheckBox chkPlayback;
    public Slider sldPlaybackFPS;
    public Label lblPlaybackFPS;
    //#endregion FXML

    private Image pre;
    private Image post;
    private FrameBuffer frameBuffer;
    private PlaybackThread playbackController;
    private Thread playbackThread;

    public FramesController() {
        super(SLIDER_FXML);
    }

    public void setFrameBuffer(com.shinkson47.FrameInterpolation.FrameBuffer buffer){
        frameBuffer = buffer;
        sld_seek.setMax(frameBuffer.getBufferLength());
        lblBufferSize.setText(String.valueOf(frameBuffer.getBufferLength() - 1));

        updatePrePost();
        autoTriggerInter();
    }

    @FXML
    private void updatePrePost() {
        setPreImage(frameBuffer.getFrame((int) sld_seek.getValue()));

        try {
            setPostImage(frameBuffer.getFrame(
                    (int) ((playbackController != null && playbackController.isRunning()) ? sld_seek.getValue() * (sld_fps.getValue() + 1) : sld_seek.getValue() + 1)));
        } catch (IndexOutOfBoundsException ignored) {}
    }

    /**
     * Sets the first (Left) image in the demo.
     * @param _pre Buffered image to set.
     */
    public void setPreImage(BufferedImage _pre){
        pre = SwingFXUtils.toFXImage(_pre, null);
        img_pre.setImage(pre);
    }

    /**
     * Sets the second (Right) image in the demo.
     * @param _post Buffered image to set.
     */
    public void setPostImage(BufferedImage _post){
        post = SwingFXUtils.toFXImage(_post, null);
        img_post.setImage(post);
    }

    /**
     * Demonstration view.
     * Calculates and displays an inter-frame, then updates the time ui.
     */
    public void calculateInter() {
        long preInterTime = System.nanoTime();                                                                          // Nano time before the image generation call.
        long totalTime = 0;                                                                                             // Store for time taken.

        BufferedImage[] images = LinearImageInterpolator.InterpolateImage(pre, post, new double[]{
                sld_pos.getValue() * 0.25, sld_pos.getValue() * 0.5, sld_pos.getValue() * 0.75
        });

        img_inter1.setImage(SwingFXUtils.toFXImage(images[0], null));
        img_inter.setImage(SwingFXUtils.toFXImage(images[1], null));
        img_inter2.setImage(SwingFXUtils.toFXImage(images[2], null));

        totalTime = System.nanoTime() - preInterTime;
        writeTimeUI(totalTime);                                                                                         // Render time data
    }

    /**
     * Updates Time lables according to the total time provided.
     * @param totalTime nano time taken to calculate an inter-frame.
     */
    private void writeTimeUI(long totalTime) {
        lblTime.setText(String.valueOf(totalTime));
        lblAverage.setText(totalTime / (pre.getWidth() * pre.getHeight()) + "");
        double perMin = ((totalTime * (30 * 60))/1e+9) / 60;
        lblEstimated.setText(perMin + " mins per min");
        lblRT.setText((perMin < 1) ? "True!" : "Nah, slower m9");

        double estTime = ((totalTime*sld_fps.getValue())/1e+9/60 * frameBuffer.getBufferLength() * sld_fps.getValue());
        lblEstTotal.setText(
                (estTime < 0.01) ? " < 1s" :
                        estTime + " mins"

        );
    }



    /**
     * Calculate button press event
     */
    public void btn_press() {
        autoTriggerInter();
        new Alert(Alert.AlertType.INFORMATION, "Will begin processing, estimated completion time is " + lblEstTotal.getText()+ ". Be patient.").showAndWait();
        long time = System.currentTimeMillis();
        setFrameBuffer(new FrameBuffer(LinearImageInterpolator.forAll(frameBuffer, (int) sld_fps.getValue())));
        time = (System.currentTimeMillis() - time) / 1000;
        new Alert(Alert.AlertType.INFORMATION, "Task completed in " + time + " seconds. Result is now being shown in preview.").showAndWait();
    }

    /**
     * Triggers the calculation and display of an interframe.
     */
    public void autoTriggerInter() {
        calculateInter();
    }

    public void exportAll(ActionEvent actionEvent) {
        BufferedImage[] images = new BufferedImage[frameBuffer.getBufferLength()];
        frameBuffer.getFrameBuffer().toArray(images);
        try {
            writeOutAll(images);
        } catch (IOException e) {
            EMSHelper.handleException(e);
            new Alert(null, "Could not export! " + e.getMessage()).showAndWait();
        }
    }

    public static void writeOutAll(BufferedImage[] images) throws IOException {
        for (int i = 0; i <= images.length - 1; i++)
            writeOut(images[i], String.valueOf(i));
    }

    public static void writeOut(BufferedImage image, String name) throws IOException {
        assertExportLocation();
        ImageIO.write(image, "png", new File(exportFolder.getAbsolutePath() + "/" + exportFolder.getName() + "_" + name + ".png"));
    }

    private static File exportFolder;
    public static boolean assertExportLocation(){
        if (exportFolder != null) return false;
        new Alert(Alert.AlertType.INFORMATION, "Select an empty to export to.").showAndWait();
        exportFolder = new File(FramesDemo.chooser.showDialog(null).getAbsolutePath());
        if(!exportFolder.exists())
            exportFolder.mkdirs();

        if (!exportFolder.isDirectory() |
                exportFolder.listFiles().length != 0){
            exportFolder = null;
            return false;
        }

        return true;
    }



    public synchronized void stepFrame() {
        if(sld_seek.getValue() >= frameBuffer.getBufferLength() - 2) {
            stopPlayback();
            return;
        }
        sld_seek.setValue(sld_seek.getValue() + 1);
        updatePrePost();
    }

    public void updatePlaybackFPS() {
        assertPlaybackController();
        playbackController.setFPS((int) sldPlaybackFPS.getValue());
        lblPlaybackFPS.setText(String.valueOf(sldPlaybackFPS.getValue()));
    }


    public void updatePlaybackCheck() {
        assertPlaybackController();
        if(chkPlayback.isSelected())
            startPlayback();
        else
            stopPlayback();
    }

    private void startPlayback() {
        playbackThread = new Thread(playbackController);
        playbackThread.start();
    }

    private void stopPlayback() {
        chkPlayback.setSelected(false);
        playbackController.stopPlayback();
        playbackController = null;

    }

    private void assertPlaybackController() {
        if (playbackController == null)
            playbackController = new PlaybackThread((int) sldPlaybackFPS.getValue(), this);
    }
}
