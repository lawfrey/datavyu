package org.openshapa.controllers.component;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.SimpleTimeZone;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import org.openshapa.event.component.NeedleEvent;
import org.openshapa.event.component.NeedleEventListener;

import org.openshapa.models.component.MixerView;
import org.openshapa.models.component.NeedleModel;
import org.openshapa.models.component.Viewport;

import org.openshapa.views.component.NeedlePainter;
import org.openshapa.views.component.NeedlePositionPainter;


/**
 * NeedleController is responsible for managing a NeedlePainter
 */
public final class NeedleController implements PropertyChangeListener {

    /** View */
    private final NeedlePainter view;
    private final NeedlePositionPainter positionView;

    /** Models */
    private final NeedleModel needleModel;
    private final MixerView mixer;

    /** Listeners interested in needle painter events */
    private final EventListenerList listenerList;

    public NeedleController(final MixerView mixer) {
        view = new NeedlePainter();
        positionView = new NeedlePositionPainter();

        needleModel = new NeedleModel();

        this.mixer = mixer;

        view.setMixerView(mixer);
        view.setNeedleModel(needleModel);

        positionView.setMixerView(mixer);
        positionView.setNeedleModel(needleModel);
        
        mixer.addPropertyChangeListener(this);

        final NeedleListener needleListener = new NeedleListener();
        view.addMouseListener(needleListener);
        view.addMouseMotionListener(needleListener);

        listenerList = new EventListenerList();
    }

    /**
     * Register the listener to be notified of needle events
     *
     * @param listener
     */
    public void addNeedleEventListener(final NeedleEventListener listener) {

        synchronized (this) {
            listenerList.add(NeedleEventListener.class, listener);
        }
    }

    /**
     * Removed the listener from being notified of needle events
     *
     * @param listener
     */
    public void removeNeedleEventListener(final NeedleEventListener listener) {

        synchronized (this) {
            listenerList.remove(NeedleEventListener.class, listener);
        }
    }

    /**
     * Used to fire a new event informing listeners about the new needle time.
     *
     * @param newTime
     */
    private void fireNeedleEvent(final long newTime) {

        synchronized (this) {
            NeedleEvent e = new NeedleEvent(this, newTime);
            Object[] listeners = listenerList.getListenerList();

            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {

                if (listeners[i] == NeedleEventListener.class) {
                    ((NeedleEventListener) listeners[i + 1]).needleMoved(e);
                }
            }
        }
    }

    /**
     * Set the current time to be represented by the needle.
     *
     * @param currentTime
     */
    public void setCurrentTime(final long currentTime) {

        /** Format for representing time. */
        DateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");
        df.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));
        needleModel.setCurrentTime(currentTime);
        view.setToolTipText(df.format(new Date(currentTime)));
        view.setNeedleModel(needleModel);
        positionView.setNeedleModel(needleModel);
    }

    /**
     * @return Current time represented by the needle
     */
    public long getCurrentTime() {
        return needleModel.getCurrentTime();
    }

    /**
     * @return a copy of the needle model
     */
    public NeedleModel getNeedleModel() {

        // return a copy to avoid model tainting
        return needleModel.copy();
    }

    @Override public void propertyChange(final PropertyChangeEvent evt) {

        if (Viewport.NAME.equals(evt.getPropertyName())) {
            view.repaint();
            positionView.repaint();
        }
    }

    /**
     * @return View used by the controller
     */
    public JComponent getView() {
        return view;
    }
    
    public JComponent getPositionView() {
    	return positionView;
    }

    /**
     * Inner class used to handle intercepted events.
     */
    private final class NeedleListener extends MouseInputAdapter {
        private final Cursor eastResizeCursor = Cursor.getPredefinedCursor(
                Cursor.E_RESIZE_CURSOR);
        private final Cursor defaultCursor = Cursor.getDefaultCursor();

        @Override public void mouseEntered(final MouseEvent e) {
            JComponent source = (JComponent) e.getSource();
            source.setCursor(eastResizeCursor);
        }

        @Override public void mouseExited(final MouseEvent e) {
            JComponent source = (JComponent) e.getSource();
            source.setCursor(defaultCursor);
        }

        @Override public void mouseMoved(final MouseEvent e) {
            mouseEntered(e);
        }

        @Override public void mouseDragged(final MouseEvent e) {
            int x = e.getX();

            // Bound the x values
            if (x < 0) {
                x = 0;
            }

            if (x > view.getSize().width) {
                x = view.getSize().width;
            }

            Viewport viewport = mixer.getViewport();

            // Calculate the time represented by the new location
            long newTime = viewport.computeTimeFromXOffset(x)
                + viewport.getViewStart();

            if (newTime < 0) {
                newTime = 0;
            }

            if (newTime > viewport.getViewEnd()) {
                newTime = viewport.getViewEnd();
            }

            fireNeedleEvent(newTime);
        }
    }


}
