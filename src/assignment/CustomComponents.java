package assignment;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author SOF/20/B1/03 - Sandun
 */
public class CustomComponents {

    // Customizing Toolbar Button
    public static JButton customToolbarButton(String iconPath, String tooltip, int iconWidth, int iconHeight) {
        ImageIcon originalIcon = new ImageIcon(CustomComponents.class.getResource(iconPath));
        Image scaledImage = originalIcon.getImage().getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JButton button = new JButton(scaledIcon);

        button.setToolTipText(tooltip);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(30, 30));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(true);
                button.setBackground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(false);
            }
        });

        return button;
    }

    public static ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        return scaledIcon;
    }

    // Customizing Separator
    public static JSeparator customSeparator(int orientation, int width, int height) {
        JSeparator separator = new JSeparator(orientation);
        separator.setPreferredSize(new Dimension(width, height));
        return separator;
    }

    public static class DatabaseListCellRenderer extends DefaultListCellRenderer {

        private final ImageIcon mysqlIcon;
        private final ImageIcon mssqlIcon;
        private final ImageIcon oracleIcon;

        public DatabaseListCellRenderer() {
            mysqlIcon = CustomComponents.scaleIcon(
                    new ImageIcon(getClass().getResource("/icons/mysql.png")), 20, 20);
            mssqlIcon = CustomComponents.scaleIcon(
                    new ImageIcon(getClass().getResource("/icons/mssql.png")), 20, 20);
            oracleIcon = CustomComponents.scaleIcon(
                    new ImageIcon(getClass().getResource("/icons/oracle.png")), 20, 20);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            String serverInfo = value.toString().toLowerCase();

            if (serverInfo.startsWith("mysql")) {
                label.setIcon(mysqlIcon);
            } else if (serverInfo.startsWith("mssql")) {
                label.setIcon(mssqlIcon);
            } else if (serverInfo.startsWith("oracledb")) {
                label.setIcon(oracleIcon);
            }

            label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

            return label;
        }
    }

}
