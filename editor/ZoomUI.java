package editor;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;

/**
 * This represents a component wrapper for providing a way to zoom in on a component and its children while 
 * keeping the mouse events accurate. It is associated with a JLayer object.
 * @author Andrew Chu
 * @version June 2023
 */
public class ZoomUI extends LayerUI<JComponent> {
    private Component lastEnteredRealComponent;
    private Component lastPressedTargetComponent;
    private double zoomLevel;
    private JLayer<JComponent> layer;
    private boolean isDispatchingEvent;

    /**
     * This constructs a new ZoomUI object.
     */
    public ZoomUI() {
        super();
        this.isDispatchingEvent = false;
        this.zoomLevel = 1.0;
    }

    /**
     * This configures the JLayer to be wrapped.
     * @param component The JLayer to wrap.
     */
    @Override
    public void installUI(JComponent component) {
        super.installUI(component);

        this.layer = (JLayer<JComponent>) component;
        this.layer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    /**
     * This resets the wrapped JLayer.
     * @param component The wrapped JLayer.
     */
    @Override
    public void uninstallUI(JComponent component) {
        this.layer.setLayerEventMask(0);
        super.uninstallUI(component);
    }

    /**
     * This processes mouse events and makes sure they are appropriate for the zoom level of nested components.
     * @param event The event dispatched to this JLayer.
     * @param layer The JLayer the event was dispatched to.
     */
    @Override
    public void eventDispatched(AWTEvent event, final JLayer<? extends JComponent> layer) {
        if (event instanceof MouseEvent && !this.isDispatchingEvent) {
            this.isDispatchingEvent = true;
            this.redispatchMouseEvent((MouseEvent) event);
            this.isDispatchingEvent = false;
        } else {
            super.eventDispatched(event, layer);
        }
        layer.repaint();
    }

    /**
     * This sets the zoom level for the nested components.
     * @param zoomLevel The zoom scale factor.
     */
    public void setZoom(double zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    /**
     * This paints the components nested in the JLayer at the appropriate scale level.
     */
    @Override
    public void paint(Graphics graphics, JComponent component) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.scale(zoomLevel, zoomLevel);
        this.layer.paint(g2);
        g2.dispose();
    }

    /**
     * This processes a mouse event to occur at the appropriate position for the zoom level.
     * @param originalEvent The mouse event to process.
     */
    private void redispatchMouseEvent(MouseEvent originalEvent) {
        if (this.layer.getView() != null) {
            if (originalEvent.getComponent() != this.layer.getGlassPane()) {
                originalEvent.consume();
            }
            
            // Find the real point and the component that should be pressed.
            Point realPoint = calculateRealPoint(originalEvent);
            Component realTargetComponent = getRealComponent(realPoint);
            
            if (realTargetComponent != null) {
                realTargetComponent = getListeningComponent(originalEvent, realTargetComponent);
            }
            
            // Create and redispatch the mouse event.
            MouseEvent newEvent = null;
            switch (originalEvent.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                newEvent = createNewMouseEvent(originalEvent, realTargetComponent, realPoint, originalEvent.getID());
                if (newEvent != null) {
                    lastPressedTargetComponent = newEvent.getComponent();
                }
                break;
            case MouseEvent.MOUSE_RELEASED:
                newEvent = createNewMouseEvent(originalEvent, lastPressedTargetComponent, realPoint, originalEvent.getID());
                lastPressedTargetComponent = null;
                break;
            case MouseEvent.MOUSE_CLICKED:
                newEvent = createNewMouseEvent(originalEvent, realTargetComponent, realPoint, originalEvent.getID());
                lastPressedTargetComponent = null;
                break;
            case MouseEvent.MOUSE_MOVED:
                newEvent = createNewMouseEvent(originalEvent, realTargetComponent, realPoint, originalEvent.getID());
            case MouseEvent.MOUSE_ENTERED:
            case MouseEvent.MOUSE_EXITED:
                generateEnterExitEvents(originalEvent, realTargetComponent, realPoint);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                newEvent = createNewMouseEvent(originalEvent, lastPressedTargetComponent, realPoint, originalEvent.getID());
                generateEnterExitEvents(originalEvent, realTargetComponent, realPoint);
                break;
            }
            dispatchMouseEvent(newEvent);
        }
    }

    /**
     * Gets the component at the point.
     * @param realPoint This gets the real component intended to be clicked. 
     * @return The real component.
     */
    private Component getRealComponent(Point realPoint) {
        Component view = this.layer.getView();
        if (view == null) {
            return null;
        }

        Point viewPoint = SwingUtilities.convertPoint(this.layer, realPoint, view);
        return SwingUtilities.getDeepestComponentAt(view, viewPoint.x, viewPoint.y);
    }

    /**
     * This determines the point intended to be clicked relative to the entire JLayer.
     * @param mouseEvent The mouse event that the point comes from.
     * @return The real point.
     */
    private Point calculateRealPoint(MouseEvent mouseEvent) {
        Point point = mouseEvent.getPoint();

        point = SwingUtilities.convertPoint(mouseEvent.getComponent(), point, this.layer);
        point.x /= zoomLevel;
        point.y /= zoomLevel;
        return point;
    }

    /**
     * This reconstructs a mouse event using the real component and real point.
     * @param mouseEvent The original mouse event.
     * @param realComponent The component intended to be clicked.
     * @param realPoint The point intended to be clicked relative to the component.
     * @param id The event ID.
     * @return The new real mouse event.
     */
    private MouseEvent createNewMouseEvent(MouseEvent mouseEvent, Component realComponent, Point realPoint, int id) {
        if (realComponent == null) {
            return null;
        }

        Point newPoint = SwingUtilities.convertPoint(this.layer, realPoint, realComponent);
        return new MouseEvent(realComponent,
                id,
                mouseEvent.getWhen(),
                mouseEvent.getModifiers(),
                newPoint.x,
                newPoint.y,
                mouseEvent.getClickCount(),
                mouseEvent.isPopupTrigger(),
                mouseEvent.getButton());
    }

    /**
     * This dispatches a mouse event onto a component.
     * @param mouseEvent The event to dispatch.
     */
    private void dispatchMouseEvent(MouseEvent mouseEvent) {
        if (mouseEvent != null) {
            Component target = mouseEvent.getComponent();
            target.dispatchEvent(mouseEvent);
        }
    }

    /**
     * This gets the first child component of a parent component that is listening for mouse events.
     * @param event The event to be sent, containing the event type/ID.
     * @param parentComponent The parent component to search in.
     * @return
     */
    private Component getListeningComponent(MouseEvent event, Component parentComponent) {
        switch (event.getID()) {
            case MouseEvent.MOUSE_CLICKED:
            case MouseEvent.MOUSE_ENTERED:
            case MouseEvent.MOUSE_EXITED:
            case MouseEvent.MOUSE_PRESSED:
            case MouseEvent.MOUSE_RELEASED:
                return getMouseListeningComponent(parentComponent);
            case (MouseEvent.MOUSE_DRAGGED):
            case (MouseEvent.MOUSE_MOVED):
                return getMouseMotionListeningComponent(parentComponent);
        }
        return null;
    }

    /**
     * This gets the first child component of a parent component that is listening for plain mouse events.
     * @param parentComponent The parent componet to search in.
     * @return The listening child component, null if none are found. 
     */
    private Component getMouseListeningComponent(Component parentComponent) {
        if (parentComponent.getMouseListeners().length > 0) {
            return parentComponent;
        } else {
            Container parent = parentComponent.getParent();
            if (parent != null) {
                return getMouseListeningComponent(parent);
            } else {
                return null;
            }
        }
    }

    /**
     * This gets the first child component of a parent component that is listening for mouse motion events.
     * @param parentComponent The parent component to search in.
     * @return The listening child component, null if none are found.
     */
    private Component getMouseMotionListeningComponent(Component parentComponent) {
        if (parentComponent.getMouseMotionListeners().length > 0 || parentComponent.getMouseListeners().length > 0) {
            return parentComponent;
        }

        Container parent = parentComponent.getParent();
        if (parent != null) {
            return getMouseMotionListeningComponent(parent);
        }

        return null;
    }

    /**
     * This generates the appropriate mouse enter and mouse exit events.
     * @param originalEvent The unprocessed mouse event.
     * @param newRealComponent The real component intended to be clicked.
     * @param realPoint The real point intended to be clicked.
     */
    private void generateEnterExitEvents( MouseEvent originalEvent, Component newRealComponent, Point realPoint) {
        if (lastEnteredRealComponent != newRealComponent) {
            dispatchMouseEvent(
                    createNewMouseEvent(originalEvent, lastEnteredRealComponent, realPoint, MouseEvent.MOUSE_EXITED));
            lastEnteredRealComponent = newRealComponent;
            dispatchMouseEvent(
                    createNewMouseEvent(originalEvent, lastEnteredRealComponent, realPoint, MouseEvent.MOUSE_ENTERED));
        }
    }
}