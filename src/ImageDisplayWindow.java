import java.awt.FlowLayout;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

/**
 * This class puts together the main display window. It allows a user to
 * choose a picture from their files and see how quickly the SVD approximates
 * it using a slidebar.
 */
public class ImageDisplayWindow extends JFrame {

    private DefaultListModel listModel;
    
    private ImageDisplayListener listener;
    
    private JButton browse;
    private JButton compare;
    private JButton quit;
    private JButton save;
        
    private JLabel countLabel;
    private JLabel titleLabel;
    
    private JList list;
    
    private JPanel app;
    private JPanel buttons;
    private JPanel pictureWindow;
    private JPanel slidebar;
    private JPanel title;
    
    private JScrollPane scroller;

    private JSlider slider;
    
    /**
     * The constructor for the window. Uses various methods to initialize
     * the components and set them in place.
     */
    public ImageDisplayWindow() {
        
        setPanels();
        setList();
        setLayouts();
        setButtons();
        setListener();
        setLabels();
        buildContent();
        
        
        setContentPane( app );
        pack();
        setVisible( true );
          
    }
    
    /**
     * Replace the current image being displayed with a new image from the
     * SVD list.
     * 
     * @param img the image to display
     */
    public void changeDisplayImage( BufferedImage img ) {
        
        ImageIcon icon = new ImageIcon( img );
        
        listModel.removeAllElements();
        
        listModel.addElement( icon );
        
    }
    
    /**
     * Alter the text in the label to show what number the slidebar
     * is on.
     * 
     * @param text the text to display
     */
    public void changeLabelText( String text ) {
        
        countLabel.setText( text );
        
    }
    
    public int getLabelTextAsInt() {
        
        int number = -1;
        
        try {
            number = Integer.parseInt( countLabel.getText() );
        } catch ( NumberFormatException e ) {
            
        }
        
        return number;
        
    }
    
    /**
     * Set the slider and listener for the slider after the image has
     * been uploaded.
     * 
     * @param min first value on the slider
     * @param max last value on the slider
     */
    public void setSlider( int min, int max, int value ) {
                
        if ( slider != null ) {
            
            slider.setMinimum( min );
            slider.setMaximum( max );
            slider.setValue( value );
            
        } else {
            
            slider = new JSlider( min, max, value );
            
            slider.addChangeListener( listener );
            
            slidebar.add( slider );
            
            slidebar.add( countLabel ); 
            
        }
 
    }
    
    /**
     * Set save to enabled or disabled.
     * 
     * @param value enabled or disabled
     */
    public void toggleSaveAndCompare( boolean value ) {
        
        save.setEnabled( value );
        compare.setEnabled( value );
        
    }
    
    
    /***************************** private methods ***************************/
    
    
    /**
     * Add content to the panels.
     */
    private void buildContent() {
        
        buttons.add( browse );
        buttons.add( compare );
        buttons.add( save );
        buttons.add( quit );
        
        pictureWindow.add( scroller );
        
        title.add( titleLabel );
        
        app.add( title );
        app.add( pictureWindow );
        app.add( slidebar );
        app.add( buttons );
        
    }
    
    /**
     * Initialize the JButtons.
     */
    private void setButtons() {
        
        browse = new JButton( "Browse Files" );
        compare = new JButton( "Compare to Original" );
        quit = new JButton( "Quit" );
        save = new JButton( "Save" );
        compare.setEnabled( false );
        save.setEnabled( false );
        
    }
    
    /**
     * Add content to the JLabels.
     */
    private void setLabels() {
        
        titleLabel = new JLabel( "Math 448 SVD Analysis" );
        countLabel = new JLabel( "1" );
        
    }
    
    /**
     * Give the JPanels layouts.
     */
    private void setLayouts() {
        
        app.setLayout( new BoxLayout( app, BoxLayout.Y_AXIS ) );
        buttons.setLayout( new FlowLayout() );
        pictureWindow.setLayout( new FlowLayout() );
        slidebar.setLayout( new FlowLayout() );
        title.setLayout( new FlowLayout() );

    }
    
    
    /**
     * Set the JList of images to be displayed.
     */
    private void setList() {
        
        listModel = new DefaultListModel();
        
        list = new JList( listModel );
        
        scroller = new JScrollPane( list );
                
    }
    
    /**
     * Initialize the listener and add it to the buttons.
     */
    private void setListener() {
        
        listener = new ImageDisplayListener(this);
        
        browse.addActionListener( listener );
        compare.addActionListener( listener );
        quit.addActionListener( listener );
        save.addActionListener( listener );
        
    }
    
    /**
     * Initialize the JPanels.
     */
    private void setPanels() {
        
        app = new JPanel();
        buttons = new JPanel();
        pictureWindow = new JPanel();
        slidebar = new JPanel();
        title = new JPanel();
        
    }
    
}

