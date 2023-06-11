package editor;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;

/**
 * 
 * This UI apply an {@link AffineTransform} to all the visible objects, and apply the inversion
 *  of the same transform to all the mouse event's coordinates
 * 
 * @author Andrea.Maracci based on the pbjar JXLayer extension
 *
 */
public class ZoomUI extends LayerUI<JComponent> {
    private Component lastEnteredTarget, lastPressedTarget;
    private AffineTransform transform = AffineTransform.getScaleInstance(1, 1);
    private JLayer<JComponent> installedLayer;
    private boolean isDispatchingEvent = false;

    /**
     * Process the mouse events and map the mouse coordinates inverting the internal affine transformation.
     * 
     * @param
     * event the event to be dispatched
     * layer the layer this LayerUI is set to
     * 
     */
    @Override
    public void eventDispatched(AWTEvent event, final JLayer<? extends JComponent> layer) {
        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;

            if (!isDispatchingEvent) {
                isDispatchingEvent = true;
                redispatchMouseEvent(mouseEvent, layer);
                isDispatchingEvent = false;
            }
        } else {
            super.eventDispatched(event, layer);
        }
        layer.repaint();
    }

    public void setZoom(double zoomLevel) {
        this.transform.setToScale(zoomLevel, zoomLevel);
    }

    /**
     * Paint the specified component {@code c} applying the transformation on it's graphic
     * 
     * @param
     * graphics - the Graphics context in which to paint
     * c - the component being painted
     */
    @Override
    public void paint(Graphics graphics, JComponent component) {
        if (graphics instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            JLayer<? extends JComponent> layer = (JLayer<? extends JComponent>) component;
            g2.transform(transform);
            JComponent view = layer.getView();
            if (view != null) {
                if (view.getX() < 0 || view.getY() < 0) {
                    g2.translate(view.getX(), view.getY());
                    view.paint(g2);
                    return;
                }
            }
            g2.dispose();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uninstallUI(JComponent component) {
        JLayer<JComponent> layer = (JLayer<JComponent>) component;
        layer.setLayerEventMask(0);
        super.uninstallUI(component);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void installUI(JComponent component) {
        super.installUI(component);

        installedLayer = (JLayer<JComponent>) component;
        installedLayer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
                | AWTEvent.MOUSE_WHEEL_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
    }

    /**
     * Process the mouse events and map the mouse coordinates inverting the internal affine transformation.
     * It consume the original event, calculates the mapped mouse coordinates and find the real target of the mouse event.
     * It than create a new event with the correct informations in it and redispatch it to the target event
     * 
     * @param originalEvent the event to be dispatched
     * @param layer the layer this LayerUI is set to
     */
    private void redispatchMouseEvent(MouseEvent originalEvent, JLayer<? extends JComponent> layer) {
        if (layer.getView() != null) {
            if (originalEvent.getComponent() != layer.getGlassPane()) {
                originalEvent.consume();
            }
            MouseEvent newEvent = null;

            Point realPoint = calculateTargetPoint(layer, originalEvent);
            Component realTarget = getTarget(layer, realPoint);

            if (realTarget != null) {
                realTarget = getListeningComponent(originalEvent, realTarget);
            }

            switch (originalEvent.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                newEvent = transformMouseEvent(layer, originalEvent, realTarget, realPoint);
                if (newEvent != null) {
                    lastPressedTarget = newEvent.getComponent();
                }
                break;
            case MouseEvent.MOUSE_RELEASED:
                newEvent = transformMouseEvent(layer, originalEvent, lastPressedTarget, realPoint);
                lastPressedTarget = null;
                break;
            case MouseEvent.MOUSE_CLICKED:
                newEvent = transformMouseEvent(layer, originalEvent, realTarget, realPoint);
                lastPressedTarget = null;
                break;
            case MouseEvent.MOUSE_MOVED:
                newEvent = transformMouseEvent(layer, originalEvent, realTarget, realPoint);
                generateEnterExitEvents(layer, originalEvent, realTarget, realPoint);
                break;
            case MouseEvent.MOUSE_ENTERED:
                generateEnterExitEvents(layer, originalEvent, realTarget, realPoint);
                break;
            case MouseEvent.MOUSE_EXITED:
                generateEnterExitEvents(layer, originalEvent, realTarget, realPoint);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                newEvent = transformMouseEvent(layer, originalEvent, lastPressedTarget, realPoint);
                generateEnterExitEvents(layer, originalEvent, realTarget, realPoint);
                break;
            }
            dispatchMouseEvent(newEvent);
        }
    }

    /**
     * Apply the inverse transformation to {@code point}
     * 
     * @param layer the layer this LayerUI is set to
     * @param point the starting point
     * @return the transformed point
     */
    private Point transformPoint(JLayer<? extends JComponent> layer, Point point) {
        if (transform != null) {
            try {
                transform.inverseTransform(point, point);
            } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
            }
        }
        return point;
    }

    /**
     * Find the deepest component in the AWT hierarchy
     * 
     * @param layer  the layer to which this UI is installed
     * @param targetPoint the point in layer's coordinates
     * @return the component in the specified point
     */
    private Component getTarget(JLayer<? extends JComponent> layer, Point targetPoint) {
        Component view = layer.getView();
        if (view == null) {
            return null;
        } else {
            Point viewPoint = SwingUtilities.convertPoint(layer, targetPoint, view);
            return SwingUtilities.getDeepestComponentAt(view, viewPoint.x, viewPoint.y);
        }
    }

    /**
     * Convert the {@code mouseEvent}'s coordinates to the {@code layer}'s space
     * @param layer the layer this LayerUI is set to
     * @param mouseEvent the original mouse event
     * @return the {@code mouseEvent}'s point transformed to the {@code layer}'s coordinate space
     */
    private Point calculateTargetPoint(JLayer<? extends JComponent> layer,
            MouseEvent mouseEvent) {
        Point point = mouseEvent.getPoint();

        point = SwingUtilities.convertPoint(mouseEvent.getComponent(), point, layer);
        return transformPoint(layer, point);

        }

    private MouseEvent transformMouseEvent(JLayer<? extends JComponent> layer, MouseEvent mouseEvent, Component target, Point realPoint) {
        return transformMouseEvent( layer, mouseEvent, target, realPoint, mouseEvent.getID());
    }

    /**
     * Create the new event to being dispatched
     */
    private MouseEvent transformMouseEvent(JLayer<? extends JComponent> layer, MouseEvent mouseEvent, Component target, Point targetPoint, int id) {
        if (target == null) {
            return null;
        } else {
            Point newPoint = SwingUtilities.convertPoint(layer, targetPoint, target);
            return new MouseEvent(target,
                    id,
                    mouseEvent.getWhen(),
                    mouseEvent.getModifiers(),
                    newPoint.x,
                    newPoint.y,
                    mouseEvent.getClickCount(),
                    mouseEvent.isPopupTrigger(),
                    mouseEvent.getButton());
        }
    }

    /**
     * dispatch the {@code mouseEvent}
     * @param mouseEvent the event to be dispatched
     */
    private void dispatchMouseEvent(MouseEvent mouseEvent) {
        if (mouseEvent != null) {
            Component target = mouseEvent.getComponent();
            target.dispatchEvent(mouseEvent);
        }
    }

    /**
     * Get the listening component associated to the {@code component}'s {@code event}
     */
    private Component getListeningComponent(MouseEvent event, Component component) {
        switch (event.getID()) {
            case MouseEvent.MOUSE_CLICKED:
            case MouseEvent.MOUSE_ENTERED:
            case MouseEvent.MOUSE_EXITED:
            case MouseEvent.MOUSE_PRESSED:
            case MouseEvent.MOUSE_RELEASED:
                return getMouseListeningComponent(component);
            case (MouseEvent.MOUSE_DRAGGED):
            case (MouseEvent.MOUSE_MOVED):
                return getMouseMotionListeningComponent(component);
        }
        return null;
    }

    /**
     * Cycles through the {@code component}'s parents to find the {@link Component} with associated {@link MouseListener}
     */
    private Component getMouseListeningComponent(Component component) {
        if (component.getMouseListeners().length > 0) {
            return component;
        } else {
            Container parent = component.getParent();
            if (parent != null) {
                return getMouseListeningComponent(parent);
            } else {
                return null;
            }
        }
    }

    /**
     * Cycles through the {@code component}'s parents to find the {@link Component} with associated {@link MouseMotionListener}
     */
    private Component getMouseMotionListeningComponent(Component component) {
        /*
         * Mouse motion events may result in MOUSE_ENTERED and MOUSE_EXITED.
         * 
         * Therefore, components with MouseListeners registered should be
         * returned as well.
         */
        if (component.getMouseMotionListeners().length > 0 || component.getMouseListeners().length > 0) {
            return component;
        } else {
            Container parent = component.getParent();
            if (parent != null) {
                return getMouseMotionListeningComponent(parent);
            } else {
                return null;
            }
        }
    }

    /**
     * Generate a {@code MOUSE_ENTERED} and {@code MOUSE_EXITED} event when the target component is changed
     */
    private void generateEnterExitEvents( JLayer<? extends JComponent> layer,MouseEvent originalEvent, Component newTarget, Point realPoint) {
        if (lastEnteredTarget != newTarget) {
            dispatchMouseEvent(
                    transformMouseEvent(layer, originalEvent, lastEnteredTarget, realPoint, MouseEvent.MOUSE_EXITED));
            lastEnteredTarget = newTarget;
            dispatchMouseEvent(
                    transformMouseEvent(layer, originalEvent, lastEnteredTarget, realPoint, MouseEvent.MOUSE_ENTERED));
        }
    }
}