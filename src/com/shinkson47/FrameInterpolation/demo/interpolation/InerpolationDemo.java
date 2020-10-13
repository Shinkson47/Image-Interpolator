package com.shinkson47.FrameInterpolation.demo.interpolation;

import com.shinkson47.opex.backend.runtime.environment.OPEX;
import com.shinkson47.opex.backend.runtime.errormanagement.EMSHelper;
import com.shinkson47.opex.frontend.fxml.FXMLMain;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.*;

/**
 * Entry point to a shitty demo UI for prototypes.FrameSlider.LinearImageInterpolator
 *
 * Uses tools from the Open Phoenix Engine to simplify creation and usage of FXML.
 * @version 1
 * @author <a href="https://www.shinkson47.in">Jordan T. Gray</a>
 */
public class InerpolationDemo extends FXMLMain<InterpolationController> {

    final static FileChooser chooser = new FileChooser();

    //#region constructors
    public InerpolationDemo(){
        super(new InterpolationController());
    }

    public InerpolationDemo(InterpolationController _dummyController) {
        super(_dummyController);
    }
    //#endregion

    //#region OPEX#FXMLMain invokations
    @Override
    protected void preLoad() {

    }

    /**
     * Initalises the newly created window with user chosen image files.
     *
     * Does not throw any exceptions, but will automatically terminate the demo via OPEX without warning
     * if chosen files are not valid images, or no file is chosen.
     */
    @Override
    protected void postLoad() {
        try {
            controller.setPreImage(ImageIO.read(new File(chooser.showOpenDialog(null).getAbsolutePath())));
            controller.setPostImage(ImageIO.read(new File(chooser.showOpenDialog(null).getAbsolutePath())));
        } catch (IOException | NullPointerException e) {
            EMSHelper.handleException(e,true);
            OPEX.Stop();
        }
    }
    //#endregion

    /**
     * JRE Entry point. Starts demo ui.
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}
