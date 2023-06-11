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

public class ZoomUI extends LayerUI<JComponent> {
    private Component lastEnteredTarget, lastPressedTarget;
    private AffineTransform transform;
    private JLayer<JComponent> installedLayer;
    private boolean isDispatchingEvent;

    public ZoomUI() {
        super();
        this.transform = AffineTransform.getScaleInstance(1, 1);
        this.isDispatchingEvent = false;
    }

    @Override
    public void installUI(JComponent component) {
        super.installUI(component);

        installedLayer = (JLayer<JComponent>) component;
        installedLayer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void uninstallUI(JComponent component) {
        JLayer<JComponent> layer = (JLayer<JComponent>) component;
        layer.setLayerEventMask(0);
        super.uninstallUI(component);
    }



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

    @Override
    public void paint(Graphics graphics, JComponent component) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        JLayer<? extends JComponent> layer = (JLayer<? extends JComponent>) component;
        g2.transform(transform);

        JComponent view = layer.getView();
        if (view != null) {
            if (view.getX() < 0 || view.getY() < 0) {
                g2.translate(view.getX(), view.getY());
                view.paint(g2);
            } else {
                layer.paint(g2);
            }
        }

        g2.dispose();
    }

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
                newEvent = transformMouseEvent(layer, originalEvent, realTarget, realPoint, originalEvent.getID());
                if (newEvent != null) {
                    lastPressedTarget = newEvent.getComponent();
                }
                break;
            case MouseEvent.MOUSE_RELEASED:
                newEvent = transformMouseEvent(layer, originalEvent, lastPressedTarget, realPoint, originalEvent.getID());
                lastPressedTarget = null;
                break;
            case MouseEvent.MOUSE_CLICKED:
                newEvent = transformMouseEvent(layer, originalEvent, realTarget, realPoint, originalEvent.getID());
                lastPressedTarget = null;
                break;
            case MouseEvent.MOUSE_MOVED:
                newEvent = transformMouseEvent(layer, originalEvent, realTarget, realPoint, originalEvent.getID());
            case MouseEvent.MOUSE_ENTERED:
            case MouseEvent.MOUSE_EXITED:
                generateEnterExitEvents(layer, originalEvent, realTarget, realPoint);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                newEvent = transformMouseEvent(layer, originalEvent, lastPressedTarget, realPoint, originalEvent.getID());
                generateEnterExitEvents(layer, originalEvent, realTarget, realPoint);
                break;
            }
            dispatchMouseEvent(newEvent);
        }
    }

    private Point transformPoint(JLayer<? extends JComponent> layer, Point point) {
        try {
            transform.inverseTransform(point, point);
        } catch (NoninvertibleTransformException event) {
            event.printStackTrace();
        }
        return point;
    }

    private Component getTarget(JLayer<? extends JComponent> layer, Point targetPoint) {
        Component view = layer.getView();
        if (view == null) {
            return null;
        }

        Point viewPoint = SwingUtilities.convertPoint(layer, targetPoint, view);
        return SwingUtilities.getDeepestComponentAt(view, viewPoint.x, viewPoint.y);
    }

    private Point calculateTargetPoint(JLayer<? extends JComponent> layer,
            MouseEvent mouseEvent) {
        Point point = mouseEvent.getPoint();

        point = SwingUtilities.convertPoint(mouseEvent.getComponent(), point, layer);
        return transformPoint(layer, point);
    }

    private MouseEvent transformMouseEvent(JLayer<? extends JComponent> layer, MouseEvent mouseEvent, Component target, Point targetPoint, int id) {
        if (target == null) {
            return null;
        }

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