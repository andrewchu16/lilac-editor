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

    @Override
    public boolean equals(Object other) {
        if (other instanceof String) {
            return this.toolType.equals((String) other);
        }
        return super.equals(other);
    }
}
