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

public class ZoomUI extends LayerUI<JComponent> {
    private Component lastEnteredTargetComponent;
    private Component lastPressedTargetComponent;
    private double zoomLevel;
    private JLayer<JComponent> layer;
    private boolean isDispatchingEvent;

    public ZoomUI() {
        super();
        this.isDispatchingEvent = false;
        this.zoomLevel = 1.0;
    }

    @Override
    public void installUI(JComponent component) {
        super.installUI(component);

        this.layer = (JLayer<JComponent>) component;
        this.layer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void uninstallUI(JComponent component) {
        this.layer.setLayerEventMask(0);
        super.uninstallUI(component);
    }

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

    public void setZoom(double zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    @Override
    public void paint(Graphics graphics, JComponent component) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.scale(zoomLevel, zoomLevel);
        this.layer.paint(g2);
        g2.dispose();
    }

    private void redispatchMouseEvent(MouseEvent originalEvent) {
        if (this.layer.getView() != null) {
            if (originalEvent.getComponent() != this.layer.getGlassPane()) {
                originalEvent.consume();
            }
            MouseEvent newEvent = null;

            Point realPoint = calculateTargetPoint(originalEvent);
            Component realTargetComponent = getTarget(realPoint);

            if (realTargetComponent != null) {
                realTargetComponent = getListeningComponent(originalEvent, realTargetComponent);
            }

            switch (originalEvent.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                newEvent = transformMouseEvent(originalEvent, realTargetComponent, realPoint, originalEvent.getID());
                if (newEvent != null) {
                    lastPressedTargetComponent = newEvent.getComponent();
                }
                break;
            case MouseEvent.MOUSE_RELEASED:
                newEvent = transformMouseEvent(originalEvent, lastPressedTargetComponent, realPoint, originalEvent.getID());
                lastPressedTargetComponent = null;
                break;
            case MouseEvent.MOUSE_CLICKED:
                newEvent = transformMouseEvent(originalEvent, realTargetComponent, realPoint, originalEvent.getID());
                lastPressedTargetComponent = null;
                break;
            case MouseEvent.MOUSE_MOVED:
                newEvent = transformMouseEvent(originalEvent, realTargetComponent, realPoint, originalEvent.getID());
            case MouseEvent.MOUSE_ENTERED:
            case MouseEvent.MOUSE_EXITED:
                generateEnterExitEvents(originalEvent, realTargetComponent, realPoint);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                newEvent = transformMouseEvent(originalEvent, lastPressedTargetComponent, realPoint, originalEvent.getID());
                generateEnterExitEvents(originalEvent, realTargetComponent, realPoint);
                break;
            }
            dispatchMouseEvent(newEvent);
        }
    }

    private Component getTarget(Point targetPoint) {
        Component view = this.layer.getView();
        if (view == null) {
            return null;
        }

        Point viewPoint = SwingUtilities.convertPoint(this.layer, targetPoint, view);
        return SwingUtilities.getDeepestComponentAt(view, viewPoint.x, viewPoint.y);
    }

    private Point calculateTargetPoint(MouseEvent mouseEvent) {
        Point point = mouseEvent.getPoint();

        point = SwingUtilities.convertPoint(mouseEvent.getComponent(), point, this.layer);
        point.x /= zoomLevel;
        point.y /= zoomLevel;
        return point;
    }

    private MouseEvent transformMouseEvent(MouseEvent mouseEvent, Component targetComponent, Point targetPoint, int id) {
        if (targetComponent == null) {
            return null;
        }

        Point newPoint = SwingUtilities.convertPoint(this.layer, targetPoint, targetComponent);
        return new MouseEvent(targetComponent,
                id,
                mouseEvent.getWhen(),
                mouseEvent.getModifiers(),
                newPoint.x,
                newPoint.y,
                mouseEvent.getClickCount(),
                mouseEvent.isPopupTrigger(),
                mouseEvent.getButton());
    }

    private void dispatchMouseEvent(MouseEvent mouseEvent) {
        if (mouseEvent != null) {
            Component target = mouseEvent.getComponent();
            target.dispatchEvent(mouseEvent);
        }
    }

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

    private Component getMouseMotionListeningComponent(Component component) {
        if (component.getMouseMotionListeners().length > 0 || component.getMouseListeners().length > 0) {
            return component;
        }

        Container parent = component.getParent();
        if (parent != null) {
            return getMouseMotionListeningComponent(parent);
        }

        return null;
    }

    private void generateEnterExitEvents( MouseEvent originalEvent, Component newTargetComponent, Point realPoint) {
        if (lastEnteredTargetComponent != newTargetComponent) {
            dispatchMouseEvent(
                    transformMouseEvent(originalEvent, lastEnteredTargetComponent, realPoint, MouseEvent.MOUSE_EXITED));
            lastEnteredTargetComponent = newTargetComponent;
            dispatchMouseEvent(
                    transformMouseEvent(originalEvent, lastEnteredTargetComponent, realPoint, MouseEvent.MOUSE_ENTERED));
        }
    }
}