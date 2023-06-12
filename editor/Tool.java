package editor;

/**
 * This represents an object that contains information on what tool is currently selected.
 * To use, pass a reference to different classes that may want to read/write tool information (e.g. toolbar to canvas).
 * @author Andrew Chu
 * @version June 2023
 */
public class Tool {
    private String toolType;
    
    /**
     * This constructs a new Tool object.
     * @param toolType The initial tool type of this tool.
     */
    public Tool(String toolType) {
        this.toolType = toolType;
    }

    /**
     * This gets the type of tool currently selected.
     * @return
     */
    public String getType() {
        return this.toolType;
    }

    /**
     * This sets the type of tool currently selected.
     * @param toolType The tool to select.
     */
    public void setType(String toolType) {
        this.toolType = toolType;
    }

    /**
     * This checks if a tool is of a certain type.
     * @param Object The object to compare to this tool.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof String) {
            return this.toolType.equals((String) other);
        }
        return super.equals(other);
    }
}
