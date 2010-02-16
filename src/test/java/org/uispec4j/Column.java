package org.uispec4j;

import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.SpreadsheetCell;
import java.awt.Component;
import java.util.Iterator;
import java.util.Vector;
import junit.framework.Assert;



/**
 *
 */
public class Column extends AbstractUIComponent {
    /**
     * UISpec4J convention to declare type.
     */
    public static final String TYPE_NAME = "SpreadsheetColumn";
    /**
     * UISpec4J convention to declare associated class.
     */
    public static final Class[] SWING_CLASSES = {SpreadsheetColumn.class};

    /**
     * Since this is an Adaptor class, this is the class being adapted.
     */
    private SpreadsheetColumn ssColumn;

    /**
     * Column constructor.
     * @param spreadsheetColumn actual spreadsheetColumn class being adapted
     */
    public Column(final SpreadsheetColumn spreadsheetColumn) {
        Assert.assertNotNull(spreadsheetColumn);
        this.ssColumn = spreadsheetColumn;
    }

    public Component getAwtComponent() {
        return ssColumn;
    }

    public String getDescriptionTypeName() {
        return TYPE_NAME;
    }

    /**
     * @return String name of column
     */
    public final String getHeaderName() {
        String headerText = ssColumn.getText();
        String headerName = headerText.substring(0,
                headerText.lastIndexOf("  ("));
        return headerName;
    }

    /**
     * @return String type of column
     */
    public final String getHeaderType() {
        String headerText = ssColumn.getText();
        String headerType = headerText.substring(
                headerText.lastIndexOf("(") + 1, headerText.length() - 1);
        return headerType;
    }

    /**
     * Returns cells in the column.
     * @return Vector<Cell> cells in column
     */
    public final Vector<Cell> getCells() {
        Vector<SpreadsheetCell> originalCells = ssColumn.getCells();
        Vector<Cell> returnCells = new Vector();

        // Iterate through cells, create new Cells and add to new vector
        Iterator itr = originalCells.iterator();
        while (itr.hasNext()) {
            returnCells.add(new Cell((SpreadsheetCell) itr.next()));
        }

        return returnCells;
    }



    /**
     * Requests focus for this column, for example to create new cells.
     */
    public final void requestFocus() {
        ssColumn.setSelected(true);
        //ssColumn.requestFocus();
    }

    /**
     * Deselect column.
     */
    public final void deselect() {
        ssColumn.setSelected(false);
    }

    /**
     * Select column.
     */
    public final void select() {
        ssColumn.setSelected(true);
    }

    /**
     * Returns true if column is selected.
     */
    public boolean isSelected() {
        return ssColumn.isSelected();
    }
}
