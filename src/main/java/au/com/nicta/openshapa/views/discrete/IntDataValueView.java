package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.IntDataValue;
import java.awt.event.KeyEvent;

/**
 *
 * @author cfreeman
 */
public final class IntDataValueView extends DataValueView {

    /**
     *
     * @param intValue
     * @param editable
     */
    IntDataValueView(final IntDataValue intValue,
                           final boolean editable) {
        super(intValue, editable);
    }

    public void keyPressed(KeyEvent e) {
        //e.consume();
    }

    public void keyReleased(KeyEvent e) {
        //e.consume();
    }

    public void keyTyped(KeyEvent e) {
        //e.consume();
    }
}
