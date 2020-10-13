package com.shinkson47.FrameInterpolation.demo.interpolation;

import com.shinkson47.FrameInterpolation.LinearImageInterpolator;
import com.shinkson47.opex.frontend.fxml.FXMLController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

/**
 * GUI controller for the Interpolator Demo.
 * @version 1
 * @author <a href="https://www.shinkson47.in">Jordan T. Gray</a>
 */
public class InterpolationController extends FXMLController {

    //#region FXML
    /**
     * Relative location of FXML document containing the definition for the POS window UI.
     */
    public static final String SLIDER_FXML = "/com/shinkson47/FrameInterpolation/demo/interpolation/FrameSlider.fxml";
    public ImageView img_pre;
    public ImageView img_post;
    public Slider sld_pos;
    public Button btn_prc;
    public ImageView img_inter;
    public Label lblTime;
    public Label lblAverage;
    public Label lblEstimated;
    public CheckBox chkTri;
    public ImageView img_inter1;
    public ImageView img_inter2;
    public Label lblRT;
    //#endregion FXML

    private Image pre;
    private Image post;

    public InterpolationController() {
        super(SLIDER_FXML);
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
     * Calculates and displays an inter-frame, then updates the time ui.
     */
    public void calculateInter() {
        long preInterTime = System.nanoTime();                                                                          // Nano time before the image generation call.
        long totalTime = 0;                                                                                             // Store for time taken.
        if(chkTri.isSelected()){
            BufferedImage[] images = LinearImageInterpolator.InterpolateImage(pre, post, new double[]{
                    sld_pos.getValue() * 0.25, sld_pos.getValue() * 0.5, sld_pos.getValue() * 0.75
            });

            totalTime = System.nanoTime() - preInterTime;

            img_inter1.setImage(SwingFXUtils.toFXImage(images[0], null));
            img_inter.setImage(SwingFXUtils.toFXImage(images[1], null));
            img_inter2.setImage(SwingFXUtils.toFXImage(images[2], null));

        } else {
            BufferedImage result = LinearImageInterpolator.VolatileInterpolateImage(                                    // Generate an interframe.
                    pre,
                    post, sld_pos.getValue());

            totalTime = System.nanoTime() - preInterTime;

            img_inter.setImage(SwingFXUtils.toFXImage(result, null));                                             // Render resulting image.
        }

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
    }



    /**
     * Calculate button press event
     */
    public void btn_press() {
        autoTriggerInter();
    }

    /**
     * Triggers the calculation and display of an interframe.
     */
    public void autoTriggerInter() {
        calculateInter();
    }


}
