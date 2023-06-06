package window;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class EditorMenuBar extends JMenuBar {
    public JMenuItem getMenuItem(String menuText, String menuItemText) {
        JMenu menu = this.getMenu(menuText);
        if (menu != null) {
            for (int j = 0; j < menu.getItemCount(); j++) {
                JMenuItem menuItem = menu.getItem(j);
                if (menuItem.getText().equals(menuItemText)) {
                    return menuItem;
                }
            }
        }

        return null;
    }

    public JMenuItem addMenuItem(String menuText, String menuItemText, String keyStroke, ActionListener actionListener) {
        JMenu menu = this.getMenu(menuText);

        if (menu == null) {
            menu = new JMenu(menuText);
            this.add(menu);
        }

        JMenuItem menuItem = new JMenuItem(menuItemText);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(keyStroke));
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);

        return menuItem;
    }

    public JMenuItem addMenuItem(String menuText, String menuItemText, ActionListener actionListener) {
        JMenu menu = this.getMenu(menuText);

        if (menu == null) {
            menu = new JMenu(menuText);
            this.add(menu);
        }

        JMenuItem menuItem = new JMenuItem(menuItemText);
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);

        return menuItem;
    }

    public JMenu getMenu(String menuText) {
        JMenu menu = null;
        for (int i = 0; i < this.getMenuCount() && menu == null; i++) {
            menu = this.getMenu(i);
            if (!menu.getText().equals(menuText)) {
                menu = null;
            }
        }

        return menu;
    }

    public void setMnemonic(String menuText, int mnemonic) {
        JMenu menu = this.getMenu(menuText);
        if (menu != null) {
            menu.setMnemonic(mnemonic);
        }
    }

    public void addSeparator(String menuText) {
        JMenu menu = this.getMenu(menuText);

        if (menu != null) {
            menu.addSeparator();
        }
    }
}
