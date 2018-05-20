import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Jama.Matrix;

import Jama.SingularValueDecomposition;

/**
 * Creates SVD approximations of a given image.
 * 
 * @author Brendan Armani, Andrew Levy, Andrew Tomassone
 * @version 1.0
 */
public class SVDImage {

    private BufferedImage image;
    
    private Matrix imgPixels;    
    
    private Matrix[] pictures;
    
    private int imgWidth;
    private int imgHeight;
    
    /**
     * Constructor for an SVDImage. Creates a bufferedImage for the image the
     * user wants to approximate, then makes a matrix of the image's RGB values 
     * and an array of the SVD approximations of the image.
     * 
     * @param imgName the name of the image file
     * @throws IOException input/output exception
     */
    public SVDImage( File file ) throws IOException {
        
        Image preImage = ImageIO.read( file );
                
        image = new BufferedImage( preImage.getWidth(null), preImage.getHeight(null), BufferedImage.TYPE_INT_RGB );
        
        Graphics2D g = image.createGraphics();
        g.drawImage(preImage, 0, 0, null);
        g.dispose();       

        imgWidth = image.getWidth();
        imgHeight = image.getHeight();
        imgPixels = new Matrix( imgWidth, imgHeight );
        pictures = new Matrix[imgHeight];
        
        populateMatrixRGBWithGray();
        constructSVDMatrix();      
        
    }    
    
    /**
     * Compare a specific singular value picture with the 
     * original picture in a separate frame.
     * 
     * @param index the index of pictures the user wants to draw
     */
    public void compareApproximation( BufferedImage image2 ) {
        
        JLabel pic1 = new JLabel( new ImageIcon( image ) );
        JLabel pic2 = new JLabel( new ImageIcon( image2 ) ); 
        JLabel pic1Label = new JLabel( "Original" );
        JLabel pic2Label = new JLabel ( "Current Image" );
        
        JPanel appWindow = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        
        appWindow.setLayout( new BoxLayout( appWindow, BoxLayout.X_AXIS ) );
        panel1.setLayout( new BoxLayout( panel1, BoxLayout.Y_AXIS ) );
        panel2.setLayout( new BoxLayout( panel2, BoxLayout.Y_AXIS ) );
        
        panel1.add( pic1 );
        panel1.add( pic1Label );
        panel2.add( pic2 );
        panel2.add( pic2Label );
        appWindow.add( panel1 );
        appWindow.add( panel2 );
           
        JFrame frame = new JFrame();
        frame.setContentPane( appWindow );
        frame.pack();
        frame.setVisible( true );
            
    }
    
    /**
     * Return the array of stored images calculating by adding the next
     * singular value to the previous image.
     * 
     * @return the list of images
     */
    public ArrayList<BufferedImage> getImageList() {
        
        ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();

        for ( int i = 0; i < imgHeight; i++ ) {

            BufferedImage tempImg = new BufferedImage( imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB );
            
            // construct matrices into buffered images
            for ( int j = 0; j < imgWidth; j++ ) {
                for ( int k = 0; k < imgHeight; k++ ) {
                    
                    int pixel = (int) pictures[i].get( j, k );
                    if ( pixel < 0 ) {
                        pixel *= -1;
                    }
                    int newValue = ( pixel << 16 ) | ( pixel << 8 ) | pixel;
                    tempImg.setRGB( j, k, newValue );
                }
            }
            
            list.add( tempImg );
            
        }
        
        // set the last picture in the list to the new image
        setGrayscaleImage( list.get( list.size() - 1 ) );
        
        return list;

    }
    
    /**
     * Get the height of the image.
     * 
     * @return the height
     */
    public int getHeight() {
        
        return imgHeight;
        
    }
    
    /**
     * Get the width of the image.
     * 
     * @return the width
     */
    public int getWidth() {
        
        return imgWidth;
        
    }
    
    /*********************** private methods *********************/
    
    /**
     * Populate the pictures array with each approximation using 
     * the singular values.
     */
    private void constructSVDMatrix() {
        
        SingularValueDecomposition svd = new SingularValueDecomposition( imgPixels );
        Matrix u = svd.getU();
        Matrix s = svd.getS();
        Matrix v = svd.getV();
        
        Matrix newU;
        double newS;
        Matrix newV;
        
        for ( int i = 0; i < imgPixels.getColumnDimension(); i++ ) {
            newU = u.getMatrix( 0, imgPixels.getRowDimension() - 1, i, i);
            newV = v.getMatrix( 0, imgPixels.getColumnDimension() - 1, i, i);
            newS = s.get( i, i );
            Matrix matrixToAdd = newU.times(newV.transpose() );
            matrixToAdd = matrixToAdd.times( newS );
            
            // construct next image from last
            if ( i == 0 ) {
                pictures[i] = matrixToAdd;
            } else {
                pictures[i] = pictures[i - 1].plus( matrixToAdd );
            }
              
        }
        
    }
    
    /**
     * Fill the imgPixels matrix with RGB values explicitly
     * converted to grayscale.
     */
    private void populateMatrixRGBWithGray() {
          
        for (int i = 0; i < imgWidth; i++) {
            for (int j = 0; j < imgHeight; j++) {

                int pixel = image.getRGB( i, j );

                double gray = getGrayscaleValues( pixel );
                
                imgPixels.set(  i, j, gray );

            }
            
        }
        
    }
    
    /**
     * Convert a pixel into a grayscale value.
     * 
     * @param pixel the pixel to convert
     * @return the grayscale value
     */
    private double getGrayscaleValues( int pixel ) {
        
        int red = (pixel >> 16) & 0x000000FF;
        int green = (pixel >> 8) & 0x000000FF;
        int blue = (pixel) & 0x000000FF;
        
        // matlab's method of conversion
        double gray = 0.2989 * red + 0.5870 * green + 0.1140 * blue;
        
        // avoid clusters of discolored pixels
        if ( gray >= 240 ) {
            gray -= 25;
        } else if ( gray <= 15 ) {
            gray += 25;
        }
        
        return gray;
        
    }
    
    /**
     * Set the currently colored image instance variable to its 
     * grayscale counterpart.
     * 
     * @param newImage grayscale image
     */
    private void setGrayscaleImage( BufferedImage newImage ) {
        
        image = newImage;
        
    }
    
}
