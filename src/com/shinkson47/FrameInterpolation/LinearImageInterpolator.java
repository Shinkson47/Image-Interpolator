package com.shinkson47.FrameInterpolation;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

/**
 * A static utility class for interpolating images
 *
 * @apiNote The usage of this class REQUIRES the availability of JavaFX in the runtime.
            This class operates on JavaFX Images, but a wrapper for an awt BufferedImage is implemented.
            For converting a BufferedImage to a JFX Image, see SwingFXUtils#toFXImage()
 * @version 1
 * @author <a href="https://www.shinkson47.in">Jordan T. Gray</a>
 */
public final class LinearImageInterpolator {

    /**
     * Buffered Image Wrapper.
     *
     * Calculates an inter-image blended from an imagery spectrum between <i>0 and 1</i>, defined by two existing images,
     * where <i>pre</i> represents <i>interpol = 0</i>, and <i>post</i> represents <i>interpol = 1</i>
     *
     * @param pre awt BufferedImage picture that represents <i>interpol = 0</i>
     * @param post awt BufferedImage picture that represents <i>interpol = 1</i>
     * @param interpol Blend value between <i>0.0 and 1.0</i>
     * @return Blended image, as found as <i>interpol</i> percents between <i>pre</i> and <i>post</i>
     */
    public static BufferedImage InterpolateImage(BufferedImage pre, BufferedImage post, double interpol) {
        return InterpolateImage(SwingFXUtils.toFXImage(pre, null), SwingFXUtils.toFXImage(post,null), interpol);
    }

    /**
     * Calculates an inter-image blended from an imagery spectrum between <i>0 and 1</i>, defined by two existing images,
     * where <i>pre</i> represents <i>interpol = 0</i>, and <i>post</i> represents <i>interpol = 1</i>
     * @param pre awt BufferedImage picture that represents <i>interpol = 0</i>
     * @param post awt BufferedImage picture that represents <i>interpol = 1</i>
     * @param interpol Blend value between <i>0.0 and 1.0</i>
     * @return Blended image, as found as <i>interpol</i> percents between <i>pre</i> and <i>post</i>
     */
    public static BufferedImage InterpolateImage(Image pre, Image post, double interpol) {
        return InterpolateImage((int)pre.getWidth(), (int)pre.getHeight(), pre.getPixelReader(), post.getPixelReader(), interpol);
    }

    /**
     * Volatile wrapper.
     *
     * Keeps final instances of the images.
     *
     * @implNote This method will only work for one image set, and is intended for repeating interpols on the same image set,
     *           such as the demonstration program this was developed with.
     *
     * Calculates an inter-image blended from an imagery spectrum between <i>0 and 1</i>, defined by two existing images,
     * where <i>pre</i> represents <i>interpol = 0</i>, and <i>post</i> represents <i>interpol = 1</i>
     * @param pre awt BufferedImage picture that represents <i>interpol = 0</i>
     * @param post awt BufferedImage picture that represents <i>interpol = 1</i>
     * @param interpol Blend value between <i>0.0 and 1.0</i>
     * @return Blended image, as found as <i>interpol</i> percents between <i>pre</i> and <i>post</i>
     */
    public static BufferedImage VolatileInterpolateImage(final Image pre, final Image post, double interpol) {
        return InterpolateImage((int)pre.getWidth(), (int)pre.getHeight(), pre.getPixelReader(), post.getPixelReader(), interpol);
    }

    /**
     * Interpolates between two images at multiple points.
     *
     * Calculates an inter-image blended from an imagery spectrum between <i>0 and 1</i>, defined by two existing images,
     * where <i>pre</i> represents <i>interpol = 0</i>, and <i>post</i> represents <i>interpol = 1</i>
     *
     * @param pre awt BufferedImage picture that represents <i>interpol = 0</i>
     * @param post awt BufferedImage picture that represents <i>interpol = 1</i>
     * @param interpol Blend value between <i>0.0 and 1.0</i>
     * @return Array of blended images found at all provided <i>interpol</i>s percents between <i>pre</i> and <i>post</i>
     */
    public static BufferedImage[] InterpolateImage(Image pre, Image post, double[] interpol) {
        ArrayList<BufferedImage> OutputBuffer = new ArrayList<BufferedImage>();

        for (int i = 0; i <= interpol.length -1; i++)
            OutputBuffer.add(InterpolateImage(pre,post,interpol[i]));

        BufferedImage[] ReturnArray = new BufferedImage[interpol.length];
        OutputBuffer.toArray(ReturnArray);
        return ReturnArray;
    }

    /**
     * Calculates an inter-image blended from an imagery spectrum between <i>0 and 1</i>, defined by two existing images,
     * where <i>pre</i> represents <i>interpol = 0</i>, and <i>post</i> represents <i>interpol = 1</i>
     *
     * @implSpec width & height MUST NOT exceed the dimensions of the smallest image.
     * @implNote For best results, interpolate between two images of the same resolution.
     *
     * @param width width of the image to extract
     * @param height height of the image to extract
     * @param preReader Pixel reader of the first image
     * @param postReader Pixel reader of the second image
     * @param interpol Point of image interpolation, between <i>0.0 and 1.0<i/>.
     * @throws IndexOutOfBoundsException if width or height are larger than the images found in either of the pixel readers.
     * @return Blended image, as found as <i>interpol</i> percents between <i>pre</i> and <i>post</i>
     */
    public static BufferedImage InterpolateImage(int width, int height, PixelReader preReader, PixelReader postReader, double interpol) {
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);                               // Image buffer to write calculation results to.
        WritableRaster outRaster = out.getRaster();                                                                     // Create a better reference to the raster used in writing pixels.

        for(int pixelX = 0; pixelX <= out.getWidth()-1; pixelX++)                                                       // For every row,
            for(int pixelY = 0; pixelY <= out.getHeight()-1; pixelY++){                                                 // and every column,

                Color preCurrentPixel = preReader.getColor(pixelX, pixelY);                                             // Get the color of the current pixel on both images.
                Color postCurrentPixel = postReader.getColor(pixelX, pixelY);

                outRaster.setPixel(pixelX,pixelY, new int[]{                                                            // Write to out;
                        lerpPixel255(preCurrentPixel.getRed(), postCurrentPixel.getRed(), interpol),                    // lerped value between pre and post R, G, and B, according to interpol.
                        lerpPixel255(preCurrentPixel.getGreen(), postCurrentPixel.getGreen(), interpol),
                        lerpPixel255(preCurrentPixel.getBlue(), postCurrentPixel.getBlue(), interpol),
                });
            }

        return out;
    }


    /**
     * Lerps an RGB luminosity provided from <i>0.0 to 1.0</i> based on
     * <i>inter</i>, then uses the value as an interpol to lerp between <i>0 and 255</i>,
     * thus converting the double 0-1 to and rgb 0-255.
     *
     * For example,
     * pre 0, post 1, inter 0.5:
     *
     * lerp(pre, post) = 0.5
     * lerp(0, 255, 0.5) = 128
     *
     * @param origin value that represents <i>interpol = 0</i>
     * @param target value that represents <i>interpol = 1</i>
     * @param interpol Interpolation point between the two values.
     * @return The single R, G, or B value from <i>0 to 255</i> portrayed at the point of interpol.
     */
    public static int lerpPixel255(double origin, double target, double interpol){
        return (int) lerp(0, 255, lerp(origin, target, interpol));
    }

    /**
     * Positive linear interpolation between two double values.
     *
     * Locates a value at a specific point on a numeric spectrum defined by <i>origin and target</i>.
     *
     * @param origin value that represents <i>interpol = 0</i>
     * @param target value that represents <i>interpol = 1</i>
     * @param interpol Interpolation point between the two values.
     * @return The value from <i>origin to target</i> portrayed at the point of interpol.
     */
    public static double lerp(double origin, double target, double interpol) {
        interpol = 1-interpol;
        return (origin * interpol) + (target * (1-interpol));
    }

    /**
     * Positive linear interpolation between two integer values.
     *
     * Locates a value at a specific point on a numeric spectrum defined by <i>origin and target</i>.
     *
     * @param origin value that represents <i>interpol = 0</i>
     * @param target value that represents <i>interpol = 1</i>
     * @param interpol Interpolation point between the two values.
     * @return The value from <i>origin to target</i> portrayed at the point of interpol.
     */
    public static double lerp(int origin, int target, int interpol) {
        interpol = 1-interpol;
        return (origin * interpol) + (target * (1-interpol));
    }

    public static BufferedImage[] forAll(FrameBuffer frameBuffer, int exponent) {
        BufferedImage[] InputBuffer = new BufferedImage[frameBuffer.getBufferLength()];
        frameBuffer.getFrameBuffer().toArray(InputBuffer);
        BufferedImage[] OutputBuffer = new BufferedImage[(frameBuffer.getBufferLength() * (exponent + 1)) + exponent + 1];

        for(int currentFrame = 1; currentFrame <= InputBuffer.length - 1; currentFrame++){
            for (int currentFrameInter = 0; currentFrameInter <= exponent; currentFrameInter++)
                OutputBuffer[(currentFrame * (exponent + 1)) + currentFrameInter] = InterpolateImage(InputBuffer[currentFrame-1], InputBuffer[currentFrame], (double) currentFrameInter / (double) exponent);
        }

        return OutputBuffer;
    }
}
