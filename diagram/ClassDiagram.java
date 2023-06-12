package diagram;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;

/**
 * This represents a diagram component that stores a class.
 * @author Andrew Chu
 * @version June 2023
 */
public class ClassDiagram extends InterfaceDiagram {
    /**
     * This constructs a new Class Diagram component.
     * @param titleText The title of this diagram.
     * @param pos The position of this diagram.
     */
    public ClassDiagram(String titleText, Point pos) {
        super(titleText, pos);
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(2, 2, 2, 2);

        this.add(new DiagramBody(), constraints);
        this.resizeDiagramToFit();
    }

    /**
     * This sets the properties text of this diagram.
     * @param propertiesText The new properties text.
     */
    public void setPropertiesText(String propertiesText) {
        this.getPropertiesBody().setText(propertiesText);
    }

    /**
     * This gets the properties text of this diagram.
     * @return The properties text.
     */
    public String getPropertiesText() {
        return this.getPropertiesBody().getText();
    }

    /**
     * This gets the last properties text set.
     * @return The last properties text.
     */
    public String getLastPropertiesText() {
        return this.getPropertiesBody().getLastText();
    }

    /**
     * This gets the DiagramBody component for the properties.
     * @return The properties DiagramBody component.
     */
    public DiagramBody getPropertiesBody() {
        return this.getBody(0);
    }

    @Override
    public DiagramBody getMethodBody() {
        return this.getBody(1);
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        this.getPropertiesBody().setEnabled(isEnabled);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() || this.getPropertiesBody().isEnabled();
    }
    
    @Override
    public void addFocusListener(FocusListener focusListener) {
        super.addFocusListener(focusListener);
        this.getPropertiesBody().addFocusListener(focusListener);
    }

    @Override
    public void addMouseListener(MouseListener mouseListener) {
        super.addMouseListener(mouseListener);
        this.getPropertiesBody().addMouseListener(mouseListener);
    }

    @Override
    public boolean isFocusOwner() {
        return super.isFocusOwner() || this.getPropertiesBody().isFocusOwner();
    }

    @Override
    public boolean textChanged() {
        return super.textChanged() || !this.getPropertiesText().equals(this.getLastPropertiesText());
    }

    @Override
    public void resizeDiagramToFit() {
        if (this.getNumBodies() != 2) {
            return;
        }

        DiagramTitle diagramTitle = this.getDiagramTitle();

        int newWidth = Math.max(diagramTitle.getPreferredWidth(), MIN_WIDTH);
        newWidth = Math.max(newWidth, this.getPropertiesBody().getPreferredWidth());
        newWidth = Math.max(newWidth, this.getMethodBody().getPreferredWidth());

        int newHeight = diagramTitle.getPreferredHeight() + 
                this.getPropertiesBody().getPreferredHeight() + 
                this.getMethodBody().getPreferredHeight();
        newHeight = Math.max(newHeight, MIN_HEIGHT);

        this.setSize(newWidth + 2, newHeight + 4);
        this.revalidate();
    }
}
