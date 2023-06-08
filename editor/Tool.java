package editor;

public class Tool {
    private String toolType;
    
    public Tool(String toolType) {
        this.toolType = toolType;
    }

    public String getType() {
        return this.toolType;
    }

    public void setType(String toolType) {
        this.toolType = toolType;
    }
}
