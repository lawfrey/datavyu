package org.openshapa.uitests;

import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import java.util.Vector;
import org.uispec4j.Cell;
import org.uispec4j.Clipboard;
import org.uispec4j.Key;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.TextBox;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;

/**
 * Bug 496:
 * Pasting "2398392310820831" into a new cell, shows 23983 as highlighted.
 * Expected: Nothing highlighted or entire value highlighted.
 */
public final class UIBug496Test extends UISpecTestCase {

    /**
     * Initialiser called before each unit test.
     *
     * @throws java.lang.Exception When unable to initialise test
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setAdapter(new MainClassAdapter(OpenSHAPA.class, new String[0]));
    }

     /**
     * Called after each test.
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        OpenSHAPA.getApplication().cleanUpForTests();
        super.tearDown();
    }

    static {
      UISpec4J.init();
    }

    /**
     * Bug 496 test.
     *
     * @throws java.lang.Exception on any error
     */
    public void testBug496() throws Exception {
        String varName = "intVar";
        String varType = "INTEGER";
        String varRadio = "integer";

        String testInput = "2398392310820831";

         // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        //1. Create new INTEGER variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varType, varRadio);
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        //3. Create new cell, check that they have been created
        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();

        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
        //5. Check copy pasting
        Clipboard.putText(testInput);
        // Delete existing cell contents.
        Cell c = cells.elementAt(0);
        c.selectAllAndTypeKey(Cell.VALUE, Key.DELETE);
        // Paste new contents.
        TextBox t = c.getValue();
        t.pasteFromClipboard();
        assertTrue(t.getText().equalsIgnoreCase("2398392310820831"));
        assertTrue(t.getSelectedText() == null);
    }

    /**
     * Create a new variable.
     *
     * @param varName String for the name of the variable
     * @param varType String for the variable type
     * @param varRadio String for the corresponding radio button to click
     *
     * @throws java.lang.Exception on any error
     */
    private void createNewVariable(final String varName,
            final String varType,
            final String varRadio) throws Exception {
        // 1. Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        // 2a. Create new variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("New Variable").triggerClick());
        newVarWindow.getTextBox("nameField").insertText(varName, 0);
        newVarWindow.getRadioButton(varRadio).click();
        newVarWindow.getButton("Ok").click();
    }

}

