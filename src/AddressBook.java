// Fig. 8.38: AddressBook.java
// An address book database example that allows information to
// be inserted, updated and deleted. The example uses
// transactions to ensure that the operations complete
// successfully.

// Java core packages
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// Java extension packages
import javax.swing.*;
import javax.swing.event.*;
import com.alee.laf.WebLookAndFeel;

public class AddressBook extends JFrame {

    // reference for manipulating multiple document interface
    private JDesktopPane desktop;

    private int addAddressCount = 0;

    // reference to database access object
    private AddressBookDataAccess database;

    // references to Actions
    Action newAction, saveAction, deleteAction,
            searchAction, exitAction, addAddressAction,
            addEmailAction, addPhoneAction;

    // set up database connection and GUI
    public AddressBook()
    {
        super( "Address Book" );

        // create database connection
        try {
            database = new CloudscapeDataAccess();
        }

        // detect problems with database connection
        catch ( Exception exception ) {
            exception.printStackTrace();
            System.exit( 1 );
        }

        // database connection successful, create GUI
        JToolBar toolBar = new JToolBar();
        JMenu fileMenu = new JMenu( "File" );
        fileMenu.setMnemonic( 'F' );

        // Set up actions for common operations. Private inner
        // classes encapsulate the processing of each action.
        newAction = new NewAction();
        saveAction = new SaveAction();
        saveAction.setEnabled( false );    // disabled by default
        deleteAction = new DeleteAction();
        deleteAction.setEnabled( false );  // disabled by default
        searchAction = new SearchAction();
        exitAction = new ExitAction();
        addAddressAction = new addAddressAction();
        addEmailAction = new addEmailAction();
        addPhoneAction = new addPhoneAction();


        // add actions to tool bar
        toolBar.add( newAction );
        toolBar.add( saveAction );
        toolBar.add( deleteAction );
        toolBar.add( new JToolBar.Separator() );
        toolBar.add( searchAction );
        toolBar.add(addAddressAction );
        toolBar.add( addEmailAction );
        toolBar.add( addPhoneAction );

        // add actions to File menu
        fileMenu.add( newAction );
        fileMenu.add( saveAction );
        fileMenu.add( deleteAction );
        fileMenu.addSeparator();
        fileMenu.add( searchAction );
        fileMenu.add( addAddressAction );
        fileMenu.add( addEmailAction );
        fileMenu.add( addPhoneAction );
        fileMenu.addSeparator();
        fileMenu.add( exitAction );

        // set up menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add( fileMenu );
        setJMenuBar( menuBar );

        // set up desktop
        desktop = new JDesktopPane();

        // get the content pane to set up GUI
        Container c = getContentPane();
        c.add( toolBar, BorderLayout.NORTH );
        c.add( desktop, BorderLayout.CENTER );

        // register for windowClosing event in case user
        // does not select Exit from File menu to terminate
        // application
        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing( WindowEvent event )
                    {
                        shutDown();
                    }
                }
        );

        // set window size and display window
        Toolkit toolkit = getToolkit();
        Dimension dimension = toolkit.getScreenSize();

        // center window on screen
        setBounds( 100, 100, dimension.width - 200,
                dimension.height - 200 );

        setVisible( true );
    }  // end AddressBook constructor

    // close database connection and terminate program
    private void shutDown()
    {
        database.close();   // close database connection
        System.exit( 0 );   // terminate program
    }

    // create a new AddressBookEntryFrame and register listener
    private AddressBookEntryFrame createAddressBookEntryFrame()
    {
        AddressBookEntryFrame frame = new AddressBookEntryFrame();
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        frame.addInternalFrameListener(
                new InternalFrameAdapter() {

                    // internal frame becomes active frame on desktop
                    public void internalFrameActivated(
                            InternalFrameEvent event )
                    {
                        saveAction.setEnabled( true );
                        deleteAction.setEnabled( true );
                    }

                    // internal frame becomes inactive frame on desktop
                    public void internalFrameDeactivated(
                            InternalFrameEvent event )
                    {
                        addAddressCount = 0;
                        saveAction.setEnabled( false );
                        deleteAction.setEnabled( false );
                    }
                }  // end InternalFrameAdapter anonymous inner class
        ); // end call to addInternalFrameListener

        return frame;
    }  // end method createAddressBookEntryFrame

    // method to launch program execution
    public static void main( String args[] )
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Install WebLaF as application L&F
                    WebLookAndFeel.install ();
                    new AddressBook();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
    }

    // Private inner class defines action that enables
    // user to input new entry. User must "Save" entry
    // after inputting data.
    private class NewAction extends AbstractAction {

        // set up action's name, icon, descriptions and mnemonic
        public NewAction() {
            putValue(NAME, "New");
            putValue(SMALL_ICON, new ImageIcon(
                    getClass().getResource("images/New24.png")));
            putValue(SHORT_DESCRIPTION, "New");
            putValue(LONG_DESCRIPTION,
                    "Add a new address book entry");
            putValue(MNEMONIC_KEY, new Integer('N'));
        }

        // display window in which user can input entry
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // create new internal window
                    AddressBookEntryFrame entryFrame =
                            createAddressBookEntryFrame();

                    // set new AddressBookEntry in window
                    entryFrame.setAddressBookEntry(
                            new AddressBookEntry());

                    // display window
                    desktop.add(entryFrame);
                    entryFrame.setVisible(true);
                }
            });
        }  // end inner class NewAction
    }

    // inner class defines an action that can save new or
    // updated entry
    private class SaveAction extends AbstractAction {

        // set up action's name, icon, descriptions and mnemonic
        public SaveAction()
        {
            putValue( NAME, "Save" );
            putValue( SMALL_ICON, new ImageIcon(
                    getClass().getResource( "images/Save24.png" ) ) );
            putValue( SHORT_DESCRIPTION, "Save" );
            putValue( LONG_DESCRIPTION,
                    "Save an address book entry" );
            putValue( MNEMONIC_KEY, new Integer( 'S' ) );
        }

        // save new entry or update existing entry
        public void actionPerformed( ActionEvent e )
        {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // get currently active window
                    AddressBookEntryFrame currentFrame =
                            (AddressBookEntryFrame) desktop.getSelectedFrame();

                    // obtain AddressBookEntry from window
                    AddressBookEntry person =
                            currentFrame.getAddressBookEntry();

                    //Checking if EirCode is valid
                    boolean isEircodeValid = false;
                    if (addAddressCount == 0)
                        isEircodeValid = isValid(person.getEircode());

                    if (isEircodeValid) {

                        // insert person in address book
                        try {

                            // Get personID. If 0, this is a new entry;
                            // otherwise an update must be performed.
                            int personID = person.getPersonID();

                            // determine string for message dialogs
                            String operation =
                                    (personID == 0) ? "Insertion" : "Update";

                            // insert or update entry
                            if (personID == 0)
                                database.newPerson(person);
                            else
                                database.savePerson(person);

                            // display success message
                            JOptionPane.showMessageDialog(desktop,
                                    operation + " successful");
                        }  // end try

                        // detect database errors
                        catch (DataAccessException exception) {
                            JOptionPane.showMessageDialog(desktop, exception,
                                    "DataAccessException",
                                    JOptionPane.ERROR_MESSAGE);
                            exception.printStackTrace();
                        }

                        // close current window and dispose of resources
                        currentFrame.dispose();

                    } else {
                        JOptionPane.showMessageDialog(desktop, "Eircode Format Is Wrong\nShould contain 2 part code with 7 characters\nExample: A65 F4E2",
                                "Wrong format",
                                JOptionPane.ERROR_MESSAGE);
                    }

                }  // end method actionPerformed
            });
        }
    }  // end inner class SaveAction

    // inner class defines action that deletes entry
    private class DeleteAction extends AbstractAction {

        // set up action's name, icon, descriptions and mnemonic
        public DeleteAction()
        {
            putValue( NAME, "Delete" );
            putValue( SMALL_ICON, new ImageIcon(
                    getClass().getResource( "images/Delete24.png" ) ) );
            putValue( SHORT_DESCRIPTION, "Delete" );
            putValue( LONG_DESCRIPTION,
                    "Delete an address book entry" );
            putValue( MNEMONIC_KEY, new Integer( 'D' ) );
        }

        // delete entry
        public void actionPerformed( ActionEvent e ) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // get currently active window
                    AddressBookEntryFrame currentFrame =
                            (AddressBookEntryFrame) desktop.getSelectedFrame();

                    // get AddressBookEntry from window
                    AddressBookEntry person =
                            currentFrame.getAddressBookEntry();

                    // If personID is 0, this is new entry that has not
                    // been inserted. Therefore, delete is not necessary.
                    // Display message and return.
                    if (person.getPersonID() == 0) {
                        JOptionPane.showMessageDialog(desktop,
                                "New entries must be saved before they can be " +
                                        "deleted. \nTo cancel a new entry, simply " +
                                        "close the window containing the entry");
                        return;
                    }

                    // delete person
                    try {
                        database.deletePerson(person);

                        // display message indicating success
                        JOptionPane.showMessageDialog(desktop,
                                "Deletion successful");
                    }

                    // detect problems deleting person
                    catch (DataAccessException exception) {
                        JOptionPane.showMessageDialog(desktop, exception,
                                "Deletion failed", JOptionPane.ERROR_MESSAGE);
                        exception.printStackTrace();
                    }

                    // close current window and dispose of resources
                    currentFrame.dispose();

                }  // end method actionPerformed
            });
        }
    }  // end inner class DeleteAction

    // inner class defines action that locates entry
    private class SearchAction extends AbstractAction {

        // set up action's name, icon, descriptions and mnemonic
        public SearchAction() {
            putValue(NAME, "Search");
            putValue(SMALL_ICON, new ImageIcon(
                    getClass().getResource("images/Find24.png")));
            putValue(SHORT_DESCRIPTION, "Search");
            putValue(LONG_DESCRIPTION,
                    "Search for an address book entry");
            putValue(MNEMONIC_KEY, new Integer('r'));
        }

        // locate existing entry
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    String lastName =
                            JOptionPane.showInputDialog(desktop,
                                    "Enter last name");

                    // if last name was input, search for it; otherwise,
                    // do nothing

                    if (lastName != null) {

                        // Execute search. If found, AddressBookEntry
                        // is returned containing data.
                        ArrayList<AddressBookEntry> person = database.findPerson(lastName);

                        if (person != null) {

                            // create window to display AddressBookEntry

                            // set AddressBookEntry to display
                            for (int i = 0; i < person.size(); i++) {
                                AddressBookEntryFrame entryFrame =
                                        createAddressBookEntryFrame();
                                entryFrame.setAddressBookEntry(person.get(i));
                                desktop.add(entryFrame);
                                entryFrame.setVisible(true);
                            }
                        } else {
                            JOptionPane.showMessageDialog(desktop,
                                    "Entry with last name \"" + lastName +
                                            "\" not found in address book");

                        }  // end "if ( lastName == null )"

                    }  // end method actionPerformed

                }  // end inner class SearchAction
            });
        }
    }
    private class addAddressAction extends AbstractAction {
        public addAddressAction() {
            putValue(NAME, "Address");
            putValue(SMALL_ICON, new ImageIcon(
                    getClass().getResource("images/books_06.png")));
            putValue(SHORT_DESCRIPTION, "Add Address");
            putValue(LONG_DESCRIPTION,
                    "Add a new address book entry");
            putValue(MNEMONIC_KEY, new Integer('C'));
        }

        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JDialog addressDialog;
                    addressDialog = new JDialog();
                    addressDialog.setSize(new Dimension(400, 100));
                    addressDialog.setTitle("Address: Select Add, Update or Delete");
                    addressDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // prevent user from doing something else

                    Container pane = addressDialog.getContentPane();
                    pane.setLayout(null);
                    JButton button = new JButton("Add Address");
                    pane.add(button);
                    button.setBounds(10, 10, 100, 40);

                    Container pane1 = addressDialog.getContentPane();
                    pane.setLayout(null);
                    JButton button1 = new JButton("Update Address");
                    pane.add(button1);
                    button1.setBounds(140, 10, 100, 40);

                    Container pane2 = addressDialog.getContentPane();
                    pane.setLayout(null);
                    JButton button2 = new JButton("Delete Address");
                    pane.add(button2);
                    button2.setBounds(270, 10, 100, 40);
                    addressDialog.setVisible(true);
                }
            });
        }
    }

    private class addEmailAction extends AbstractAction {
        public addEmailAction() {
            putValue(NAME, "Email");
            putValue(SMALL_ICON, new ImageIcon(
                    getClass().getResource("images/email_envelope.png")));
            putValue(SHORT_DESCRIPTION, "Add Email");
            putValue(LONG_DESCRIPTION,
                    "Add a new address book entry");
            putValue(MNEMONIC_KEY, new Integer('F'));
        }

        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JDialog emailDialog;
                    emailDialog = new JDialog();
                    emailDialog.setSize(new Dimension(400,100));
                    emailDialog.setTitle("Email: Select Add, Update or Delete");
                    emailDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // prevent user from doing something else

                    Container pane = emailDialog.getContentPane();
                    pane.setLayout(null);
                    JButton button = new JButton("Add Email");
                    pane.add(button);
                    button.setBounds(10,10,100,40);

                    Container pane1 = emailDialog.getContentPane();
                    pane.setLayout(null);
                    JButton button1 = new JButton("Update Email");
                    pane.add(button1);
                    button1.setBounds(140,10,100,40);

                    Container pane2 = emailDialog.getContentPane();
                    pane.setLayout(null);
                    JButton button2 = new JButton("Delete Email");
                    pane.add(button2);
                    button2.setBounds(270,10,100,40);
                    emailDialog.setVisible(true);
                }

            });
        }
    }

    private class addPhoneAction extends AbstractAction {
        public addPhoneAction() {
            putValue(NAME, "Phone");
            putValue(SMALL_ICON, new ImageIcon(
                    getClass().getResource("images/phone_buttons_2.png")));
            putValue(SHORT_DESCRIPTION, "Add Phone Number");
            putValue(LONG_DESCRIPTION,
                    "Add a new address book entry");
            putValue(MNEMONIC_KEY, new Integer('P'));
        }

        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    JDialog phoneDialog;
                    phoneDialog = new JDialog();
                    phoneDialog.setSize(new Dimension(400,100));
                    phoneDialog.setTitle("Phone: Select Add, Update or Delete");
                    phoneDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // prevent user from doing something else

                    Container pane = phoneDialog.getContentPane();
                    pane.setLayout(null);
                    JButton button = new JButton("Add Phone");
                    pane.add(button);
                    button.setBounds(10,10,100,40);

                    Container pane1 = phoneDialog.getContentPane();
                    pane.setLayout(null);
                    JButton button1 = new JButton("Update Phone");
                    pane.add(button1);
                    button1.setBounds(140,10,100,40);

                    Container pane2 = phoneDialog.getContentPane();
                    pane.setLayout(null);
                    JButton button2 = new JButton("Delete Phone");
                    pane.add(button2);
                    button2.setBounds(270,10,100,40);
                    phoneDialog.setVisible(true);
                }
            });
        }
    }

    // utility method to check the valildity of the eircode
    private boolean isValid(String input) {
        if((input.length() == 0) || (input.length() == 8 &&
                input.charAt(0) != ' ' &&
                input.charAt(1) != ' ' &&
                input.charAt(2) != ' ' &&
                input.charAt(3) == ' ' &&
                input.charAt(4) != ' ' &&
                input.charAt(5) != ' ' &&
                input.charAt(6) != ' ' &&
                input.charAt(7) != ' '))
            return true;
        else
            return false;
    }

    // inner class defines action that closes connection to
    // database and terminates program
    private class ExitAction extends AbstractAction {

        // set up action's name, descriptions and mnemonic
        public ExitAction() {
            putValue(NAME, "Exit");
            putValue(SHORT_DESCRIPTION, "Exit");
            putValue(LONG_DESCRIPTION, "Terminate the program");
            putValue(MNEMONIC_KEY, new Integer('x'));
        }

        // terminate program
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    shutDown();  // close database connection and terminate
                }
            });

        }  // end inner class ExitAction
    }
}



/**************************************************************************
 * (C) Copyright 2001 by Deitel & Associates, Inc. and Prentice Hall.     *
 * All Rights Reserved.                                                   *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/
