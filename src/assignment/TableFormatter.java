/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
/**
 *
 * @author Sandun
 */
public class TableFormatter {
    private static final Color HEADER_BACKGROUND = new Color(51, 51, 51);
    private static final Color HEADER_FOREGROUND = Color.WHITE;
    private static final Color ALTERNATE_ROW_COLOR = new Color(240, 240, 240);
    private static final Color SELECTED_ROW_COLOR = new Color(184, 207, 229);
    private static final Font HEADER_FONT = new Font("Noto Sans Sinhala", Font.BOLD, 12);
    private static final Font CELL_FONT = new Font("Noto Sans Sinhala", Font.PLAIN, 12);

    public static JTable formatTable(JTable table) {
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setRowHeight(25);
        table.setIntercellSpacing(new Dimension(10, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        table.setAutoCreateRowSorter(true);

        // Headers
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                label.setBackground(HEADER_BACKGROUND);
                label.setForeground(HEADER_FOREGROUND);
                label.setFont(HEADER_FONT);
                label.setBorder(BorderFactory.createCompoundBorder(
                    label.getBorder(),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                return label;
            }
        });
        
        header.setResizingAllowed(true);
        header.setReorderingAllowed(false);

        // Custom cell formatting
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                
                label.setFont(CELL_FONT);
                label.setBorder(BorderFactory.createCompoundBorder(
                    label.getBorder(),
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)
                ));

                if (isSelected) {
                    label.setBackground(SELECTED_ROW_COLOR);
                    label.setForeground(Color.BLACK);
                } else {
                    label.setBackground(row % 2 == 0 ? Color.WHITE : ALTERNATE_ROW_COLOR);
                    label.setForeground(Color.BLACK);
                }

                
                if (value != null) {
                    if (value instanceof Number) {
                        label.setHorizontalAlignment(SwingConstants.RIGHT);
                        // Format numbers if contains
                        if (value instanceof Double || value instanceof Float) {
                            label.setText(String.format("%.2f", value));
                        }
                    } else if (value instanceof Boolean) {
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                    } else {
                        label.setHorizontalAlignment(SwingConstants.LEFT);
                    }
                }

                return label;
            }
        };

        // Apply format to the table
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            
            TableColumn column = table.getColumnModel().getColumn(i);
            int preferredWidth = getPreferredColumnWidth(table, i);
            column.setPreferredWidth(preferredWidth);
            column.setMinWidth(50);
        }

        return table;
    }

    private static int getPreferredColumnWidth(JTable table, int columnIndex) {
        int headerWidth = getHeaderWidth(table, columnIndex);
        int cellWidth = getMaxCellWidth(table, columnIndex);
        
        return Math.max(headerWidth, cellWidth) + 20;
    }

    private static int getHeaderWidth(JTable table, int columnIndex) {
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
        Object headerValue = table.getColumnModel().getColumn(columnIndex).getHeaderValue();
        Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0, columnIndex);
        return headerComp.getPreferredSize().width;
    }

    private static int getMaxCellWidth(JTable table, int columnIndex) {
        int maxWidth = 0;
        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer cellRenderer = table.getCellRenderer(row, columnIndex);
            Object value = table.getValueAt(row, columnIndex);
            Component cellComp = cellRenderer.getTableCellRendererComponent(table, value, false, false, row, columnIndex);
            maxWidth = Math.max(maxWidth, cellComp.getPreferredSize().width);
        }
        return maxWidth;
    }
}