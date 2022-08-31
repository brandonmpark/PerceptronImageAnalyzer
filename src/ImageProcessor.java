import java.io.*;

/**
 * Reads in images from a directory and processes them for use in the network.
 *
 * @author Eric Nelson, adapted by Brandon Park
 * @version 8/31/22
 */
public class ImageProcessor {
    private static int swapInt(int v) {
        return ((v >>> 24) | (v << 24) | ((v << 8) & 0x00FF0000) | ((v >> 8) & 0x0000FF00));
    }

    private static int swapShort(int v) {
        return (((v << 8) & 0xFF00) | ((v >> 8) & 0x00FF));
    }

    /**
     * Reads in the image at a given file path.
     *
     * @param filePath the file path of the image file.
     * @return returns the image formatted as a 2-dimensional array of doubles corresponding to each pixel in black
     * and white.
     * @throws IOException if an exception occurs during file read.
     */
    private static int[][] readImage(String filePath) throws IOException {
        FileInputStream fstream = null;
        DataInputStream in = null;

        boolean topDownDIB = false;

        try {
            fstream = new FileInputStream(filePath);
            in = new DataInputStream(fstream);
        } catch (FileNotFoundException e) {
            System.out.println("Image file not found at " + filePath + ". Aborting process.");
            System.exit(1);
        }

        int bfType = in.readUnsignedShort();
        int bfSize = swapInt(in.readInt());
        int bfReserved1 = swapShort(in.readUnsignedShort());
        int bfReserved2 = swapShort(in.readUnsignedShort());
        int bfOffBits = swapInt(in.readInt());
        int biSize = swapInt(in.readInt());
        int biWidth = swapInt(in.readInt());
        int biHeight = swapInt(in.readInt());
        int biPlanes = swapShort(in.readUnsignedShort());
        int biBitCount = swapShort(in.readUnsignedShort());
        int biCompression = swapInt(in.readInt());
        int biSizeImage = swapInt(in.readInt());
        int biXPelsPerMeter = swapInt(in.readInt());
        int biYPelsPerMeter = swapInt(in.readInt());
        int biClrUsed = swapInt(in.readInt());
        int biClrImportant = swapInt(in.readInt());

        if (biHeight < 0) {
            topDownDIB = true;
            biHeight = -biHeight;
        }

        if (biSize != 40) {
            for (int ii = 0; ii < biSize - 40; ++ii)
                in.readByte();

            biSize = 40;
        }

        int[][] imageArray = new int[biHeight][biWidth];
        int i, j;
        int rgbB, rgbG, rgbR, rgbReserved;
        int pel;

        for (int row = 0; row < biHeight; row++) {
            if (topDownDIB)
                i = row;
            else
                i = biHeight - row - 1;

            for (int col = 0; col < biWidth; col++) {
                j = col;

                rgbB = in.readUnsignedByte();
                rgbG = in.readUnsignedByte();
                rgbR = in.readUnsignedByte();
                rgbReserved = in.readUnsignedByte();

                pel = (rgbReserved << 24) | (rgbR << 16) | (rgbG << 8) | rgbB;
                imageArray[i][j] = pel & 0xFF;
            }
        }

        fstream.close();
        in.close();

        return imageArray;
    }

    /**
     * Gets and processes the image at a given file path for use in the network.
     *
     * @param filePath the file path of the image file.
     * @return returns the image formatted as an array of doubles corresponding to each pixel in black and white.
     */
    public static double[] getImage(String filePath) {
        int[][] imageArray = null;

        try {
            imageArray = readImage(filePath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Exception occurred during image processing. Aborting process.");
            System.exit(1);
        }

        double[] flattenedImage = new double[imageArray.length * imageArray[0].length];

        for (int row = 0; row < imageArray.length; row++) {
            for (int col = 0; col < imageArray[0].length; col++) {
                if (imageArray[row][col] < 75)
                    imageArray[row][col] = 0;
                flattenedImage[row * imageArray[0].length + col] = imageArray[row][col] / 255.0;
            }
        }

        return flattenedImage;
    }
}
