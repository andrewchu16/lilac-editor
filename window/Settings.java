package window;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.io.PrintWriter;

public class Settings {
    String fileName;

    private boolean isMaximized;
    private Dimension dimension;
    private Point location;
    private ArrayList<String> openedFiles;

    public Settings(String fileName) {
        this.fileName = fileName;
        this.isMaximized = false;
        this.dimension = new Dimension();
        this.location = new Point();
        this.openedFiles = new ArrayList<String>();
    }

    public void setIsMaximized(boolean isMaximized) {
        this.isMaximized = isMaximized;
    }

    public boolean getIsMaximized() {
        return this.isMaximized;
    }

    public void setWindowDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public Dimension getWindowDimension() {
        return this.dimension;
    }

    public void setWindowLocation(Point location) {
        this.location = location;
    }

    public Point getWindowLocation() {
        return this.location;
    }

    public void setLastOpenedFiles(ArrayList<String> openedFiles) {
        this.openedFiles = openedFiles;
    }

    public ArrayList<String> getLastOpenedFiles() {
        return this.openedFiles;
    }

    public boolean load() {
        if (this.fileName == null) {
            return false;
        }

        // Open the file.
        File settingsFile = new File(this.fileName);
        Scanner input;
        
        try {
            input = new Scanner(settingsFile);
        } catch(FileNotFoundException ex) {
            System.err.println("Settings file cannot be loaded. [" + this.fileName + "]");
            return false;
        }

        // Try to read whether the window is maximized.
        String line;
        try {
            line = input.nextLine().strip();
        } catch (NoSuchElementException ex) {
            System.err.println("Settings file missing isMaximized. [" + this.fileName + "]");
            input.close();
            return false;
        }

        this.isMaximized = line.equals("isMaximized");

        // Try to read the last dimension of the window.
        try {
            line = input.nextLine().trim();
        } catch (NoSuchElementException ex) {
            System.err.println("Settings file missing dimension. [" + this.fileName + "]");
            input.close();
            return false;
        }

        String[] parts = line.split(" ");
        if (parts.length < 2) {
            System.err.println("Settings file improperly formatted dimension. ['" + line + "'']");
            input.close();
            return false;
        }
        
        this.dimension = new Dimension(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));

        // Try to read the last position of the window.
        try {
            line = input.nextLine().trim();
        } catch (NoSuchElementException ex) {
            System.err.println("Settings file missing position. [" + this.fileName + "]");
            input.close();
            return false;
        }

        parts = line.split(" ");
        if (parts.length < 2) {
            System.err.println("Settings file improperly formatted position. ['" + line + "'']");
            input.close();
            return false;
        }

        this.location = new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));

        // Try to read the last open files.
        try {
            line = input.nextLine().trim();
        } catch (NoSuchElementException ex) {
            System.err.println("Settings file missing last opened files. [" + this.fileName + "]");
            input.close();
            return false;
        }

        parts = line.split(" ");
        if (parts.length < 1) {
            System.err.println("Settings file improperly formatted last opened files. ['" + line + "'']");
            input.close();
            return false;
        }

        for (int i = 1; i < parts.length; i++) {
            this.openedFiles.add(parts[i]);
        }

        input.close();
        return true;
    }

    public boolean save() {
        if (this.fileName == null) {
            return false;
        }

        PrintWriter writer;
        try {
            writer = new PrintWriter(this.fileName);
        } catch (FileNotFoundException ex) {
            System.err.println("Cannot write settings file. [" + this.fileName + "]");
            return false;
        }

        // Write whether the window is maximized.
        if (this.isMaximized) {
            writer.println("isMaximized");
        } else {
            writer.println("not isMaximized");
        }

        // Write the dimension of the window.
        writer.println((int) this.dimension.getWidth() + " " + (int) this.dimension.getHeight());

        // Write the position of the window.
        writer.println((int) this.location.getX() + " " + (int) this.location.getY());

        // Write the last open files.
        writer.print(this.openedFiles.size());
        for (String openedFileName: this.openedFiles) {
            writer.print(" " + openedFileName);
        }

        writer.close();
        return true;
    }
}
