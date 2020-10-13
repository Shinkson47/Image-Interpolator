package com.shinkson47.FrameInterpolation.demo.frames;

import com.shinkson47.FrameInterpolation.FrameBuffer;
import com.shinkson47.opex.frontend.fxml.FXMLMain;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;

/**
 *
 *
 * Uses tools from the Open Phoenix Engine to simplify creation and usage of FXML.
 * @version 1
 * @author <a href="https://www.shinkson47.in">Jordan T. Gray</a>
 */
public class FramesDemo extends FXMLMain<FramesController> {

    final static DirectoryChooser chooser = new DirectoryChooser();

    //#region constructors
    public FramesDemo(){
        super(new FramesController());
    }

    public FramesDemo(FramesController _dummyController) {
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
        new Alert(Alert.AlertType.WARNING, "Loading directories of images can take quite some time, depending on resolution and quantity. Once you've selected your directory, be patient.").showAndWait();
        controller.setFrameBuffer(
                new FrameBuffer(
                        chooser.showDialog(null).getAbsoluteFile()
                )
        );
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
