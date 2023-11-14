import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class OrderViewer extends JFrame {
    private JLabel foodBankLogo;
    
    private JLabel outgoingOrdersLabel, incomingOrdersLabel, expiredOrdersLabel;
    private JTable outgoingOrdersTable, incomingOrdersTable, expiredOrdersTable;

    private LinkedList<OnlineOrder> outgoingOrderQueue, incomingOrderQueue, expiredOrderQueue;

    private final Object lock = new Object();
    private boolean frameClosed = false;

    public OrderViewer() {
        //Grabbing all online order data
        OrderManager orderAccess = new OrderManager();
        outgoingOrderQueue = orderAccess.getOutgoingOrderQueue();
        incomingOrderQueue = orderAccess.getIncomingOrderQueue();
        expiredOrderQueue = orderAccess.getExpiredOrderQueue();

        //Table column headings
        String[] onlineOrdersTableHeadings = {"Pickup Time",
                                            "First Name",
                                            "Last Name",
                                            "Item, Quantity"};
        
        //Label creation section
        foodBankLogo = new JLabel();
        foodBankLogo.setIcon(new ImageIcon("/Users/jmackey/Downloads/output-onlinepngtools-2.png"));

        outgoingOrdersLabel = new JLabel("Outgoing Orders");
        incomingOrdersLabel = new JLabel("Incoming Orders");
        expiredOrdersLabel = new JLabel("Expired Orders");

        //Initializing layout constraint
        GridBagConstraints layoutConst = null;

        setTitle("OU Food Bank Online Orders");
        setLocationRelativeTo(null);
        setPreferredSize(new Dimension(700, 500));
        getContentPane().setBackground(new Color(132, 22, 23));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        //Outgoing orders table creation
        outgoingOrdersTable = createTable(outgoingOrderQueue.size(), onlineOrdersTableHeadings);
        outgoingOrdersTable.setEnabled(false);
        outgoingOrdersTable.getTableHeader().setReorderingAllowed(false);
        
        //Incoming orders table creation
        incomingOrdersTable = createTable(incomingOrderQueue.size(), onlineOrdersTableHeadings);
        incomingOrdersTable.setEnabled(false);
        incomingOrdersTable.getTableHeader().setReorderingAllowed(false);

        //Expired orders table creation
        expiredOrdersTable = createTable(expiredOrderQueue.size(), onlineOrdersTableHeadings);
        expiredOrdersTable.setEnabled(false);
        expiredOrdersTable.getTableHeader().setReorderingAllowed(false);

        //Inserting all online orders to tables
        updateTables();
        
        outgoingOrdersTable.getColumn("Item, Quantity").setCellRenderer(new MultiLineTableCellRenderer());
        incomingOrdersTable.getColumn("Item, Quantity").setCellRenderer(new MultiLineTableCellRenderer());
        expiredOrdersTable.getColumn("Item, Quantity").setCellRenderer(new MultiLineTableCellRenderer());

        outgoingOrdersTable.getColumn("Item, Quantity").setPreferredWidth(225);
        incomingOrdersTable.getColumn("Item, Quantity").setPreferredWidth(225);
        expiredOrdersTable.getColumn("Item, Quantity").setPreferredWidth(225);

        outgoingOrdersTable.getColumn("Pickup Time").setPreferredWidth(150);
        incomingOrdersTable.getColumn("Pickup Time").setPreferredWidth(150);
        expiredOrdersTable.getColumn("Pickup Time").setPreferredWidth(150);
        
        //Adding components via GridBagLayout
        setLayout(new GridBagLayout());

        // Wrap the entire content in a JScrollPane
        JPanel contentPanel = createContentPanel(layoutConst);
        contentPanel.setBackground(new Color(255, 237, 199)); // Set your desired background color
        JScrollPane scrollPane = new JScrollPane(contentPanel);

        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(0, 0, 0, 0);
        layoutConst.fill = GridBagConstraints.CENTER;
        layoutConst.gridx = 0;
        layoutConst.gridy = 0;

        add(scrollPane, layoutConst);

        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                notifyFrameClosed();
            }
        });

        pack();
        setVisible(true);
        System.out.println("Showing online orders...");

    }

    private JPanel createContentPanel(GridBagConstraints layoutConst) {
        JPanel contentPanel = new JPanel(new GridBagLayout());
    
        //Food Bank Logo
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(0, 10, 1, 5);
        layoutConst.fill = GridBagConstraints.CENTER;
        layoutConst.gridx = 0;
        layoutConst.gridy = 0;
        contentPanel.add(foodBankLogo, layoutConst);
    
        //------------------------------------------------------------------------------------------------------------------------------------------------------
    
        //outgoingOrders Label
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(15, 0, 5, 0);
        layoutConst.fill = GridBagConstraints.CENTER;
        layoutConst.gridx = 0;
        layoutConst.gridy = 1;
        contentPanel.add(outgoingOrdersLabel, layoutConst);
    
        //outgoingOrdersTable's column headings
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(1, 0, 0, 0);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 0;
        layoutConst.gridy = 2;
        contentPanel.add(outgoingOrdersTable.getTableHeader(), layoutConst);
    
        //outgoingOrdersTable
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(0, 0, 0, 0);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 0;
        layoutConst.gridy = 3;
        contentPanel.add(outgoingOrdersTable, layoutConst);
    
        //------------------------------------------------------------------------------------------------------------------------------------------------------
    
        //incomingOrders Label
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(20, 0, 5, 0);
        layoutConst.fill = GridBagConstraints.CENTER;
        layoutConst.gridx = 0;
        layoutConst.gridy = 5;
        contentPanel.add(incomingOrdersLabel, layoutConst);
    
        //incomingOrdersTable's column headings
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(1, 0, 0, 0);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 0;
        layoutConst.gridy = 6;
        contentPanel.add(incomingOrdersTable.getTableHeader(), layoutConst);
    
        //incomingOrdersTable
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(0, 0, 0, 0);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 0;
        layoutConst.gridy = 7;
        contentPanel.add(incomingOrdersTable, layoutConst);
    
        //------------------------------------------------------------------------------------------------------------------------------------------------------
    
        //expiredOrders Label
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(20, 0, 5, 0);
        layoutConst.fill = GridBagConstraints.CENTER;
        layoutConst.gridx = 0;
        layoutConst.gridy = 9;
        contentPanel.add(expiredOrdersLabel, layoutConst);
    
        //expiredOrdersTable's column headings
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(1, 0, 0, 0);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 0;
        layoutConst.gridy = 10;
        contentPanel.add(expiredOrdersTable.getTableHeader(), layoutConst);
    
        //expiredOrdersTable
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(0, 0, 0, 0);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 0;
        layoutConst.gridy = 11;
        contentPanel.add(expiredOrdersTable, layoutConst);
    
        return contentPanel;
    }
    
    private JTable createTable(int rowCount, String[] columnNames) {
        JTable table = new JTable(new Object[rowCount][columnNames.length], columnNames) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                Color alternateColor = new Color(253, 249, 216);
                Color regColor = new Color(222, 97, 97);
                if (!comp.getBackground().equals(getSelectionBackground())) {
                    Color c = (row % 2 == 0 ? alternateColor : regColor);
                    comp.setBackground(c);
                    c = null;
                }
                return comp;
            }
        };
    
        return table;
    }

    private void updateTables() {
        final int timeCol = 0,
                    firstNameCol = 1,
                    lastNameCol = 2,
                    orderCol = 3;
        
        for(int i = 0; i < outgoingOrderQueue.size(); i++) {
            outgoingOrdersTable.setValueAt(outgoingOrderQueue.get(i).getFormattedTime(), i, timeCol);
            outgoingOrdersTable.setValueAt(outgoingOrderQueue.get(i).getFirstName(), i, firstNameCol);
            outgoingOrdersTable.setValueAt(outgoingOrderQueue.get(i).getLastName(), i, lastNameCol);
            outgoingOrdersTable.setValueAt(outgoingOrderQueue.get(i).getOrderList().viewerToString(), i, orderCol);
        }

        for(int i = 0; i < incomingOrderQueue.size(); i++) {
            incomingOrdersTable.setValueAt(incomingOrderQueue.get(i).getFormattedTime(), i, timeCol);
            incomingOrdersTable.setValueAt(incomingOrderQueue.get(i).getFirstName(), i, firstNameCol);
            incomingOrdersTable.setValueAt(incomingOrderQueue.get(i).getLastName(), i, lastNameCol);
            incomingOrdersTable.setValueAt(incomingOrderQueue.get(i).getOrderList().viewerToString(), i, orderCol);
        }

        for(int i = 0; i < expiredOrderQueue.size(); i++) {
            expiredOrdersTable.setValueAt(expiredOrderQueue.get(i).getFormattedTime(), i, timeCol);
            expiredOrdersTable.setValueAt(expiredOrderQueue.get(i).getFirstName(), i, firstNameCol);
            expiredOrdersTable.setValueAt(expiredOrderQueue.get(i).getLastName(), i, lastNameCol);
            expiredOrdersTable.setValueAt(expiredOrderQueue.get(i).getOrderList().viewerToString(), i, orderCol);
        }

    }

    public void waitForFrameClose() {
        synchronized (lock) {
            while (!frameClosed) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void notifyFrameClosed() {
        synchronized (lock) {
            frameClosed = true;
            lock.notifyAll();
        }
    }
    
}
