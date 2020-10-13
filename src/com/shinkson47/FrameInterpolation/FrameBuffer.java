package com.shinkson47.FrameInterpolation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * TODO CHECK RESOLUTIONS
        EXTEND ARRAYLIST
 */
public class FrameBuffer {

    protected ArrayList<BufferedImage> frameBuffer = new ArrayList<BufferedImage>();

    public static final byte MIN_VALID_IMAGES = 2;

    public FrameBuffer(Path directory) throws IllegalStateException {
        this(new File(directory.toString()));
    }

    /**
     * Creates a BufferedVideoStream, with the frame buffer populated from a video file.
     * @param file
     */
    public FrameBuffer(File directory) throws IllegalStateException {
        if (!validateImport(directory)) invalidState();
    }

    public FrameBuffer(BufferedImage[] buffer) {
        if(buffer.length < 2) invalidState();

        for (BufferedImage image : buffer){
            add(image);
        }
    }

    private void invalidState() {
        throw new IllegalStateException("Invalid folder structure passed. Must only contain images of the same resolution.");
    }

    /**
     * Imports a folder containing video frames of the same resolution.
     *
     * @implNote SIDE EFFECT: Clears frame buffer before importing.
     * @param directory Folder containing images to import.
     * @return <i>false</i> if directory is contains less than two children,
                is not a directory, contains files that are not images, or contains less than two valid images.
                Otherwise <i>true</i>
     */
    public boolean validateImport(File directory) {
        frameBuffer.clear();
        if (!directory.isDirectory() ||
            directory.listFiles().length < MIN_VALID_IMAGES) return false;                                              // Directory passed is not a directory, or does not have enough files.

        for (File file: directory.listFiles())
            if (file.isDirectory())
                return false;                                                                                           // Folder contains a sub-folder.
            else
                try {
                  add(ImageIO.read(file));                                                                              // Read next images, and append to buffer
                } catch (IOException e){
                  return false;                                                                                         // Folder contains items that're not valid images OR failed to read images.
                }

        if (frameBuffer.size() < MIN_VALID_IMAGES) return false;                                                        // Not Enough images for comparable.
        return true;
    }

    private void add(BufferedImage toAdd) {
        if(toAdd == null) return;
        frameBuffer.add(toAdd);
    }


    public BufferedImage getFrame(int i){
        return frameBuffer.get(i);
    }

    public int getBufferLength() {
        return frameBuffer.size();
    }

    public ArrayList<BufferedImage> getFrameBuffer(){
        ArrayList<BufferedImage> i = new ArrayList<BufferedImage>();
        i.addAll(frameBuffer);
        return i;
    }






    /**
     * TODO
     * @param bi
     * @return
     * @throws
     *
     */
    public BufferedImage ImageFromBuffer(ByteBuffer bi) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(bi.array()));
    }
}
