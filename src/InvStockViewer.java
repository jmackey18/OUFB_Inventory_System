import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class InvStockViewer extends JFrame {

    private JLabel inventoryTableLabel;
    private JLabel foodBankLogo;
    private JTable inventoryTable;

    private ArrayList<Integer> inventoryStockList, inventoryTargetList, productSKUList;
    private ArrayList<String> productNameList, stockStatusList;

    private final Object lock = new Object();
    private boolean frameClosed = false;

    public InvStockViewer() {
        //Grabbing all data
        InventoryManager invAccess = new InventoryManager();
        productSKUList = invAccess.getProductSKUs();
        inventoryStockList = invAccess.getInventoryNumbers();
        inventoryTargetList = invAccess.getInvTargetNumbers();
        productNameList = invAccess.getProductNames();
        stockStatusList = invAccess.getStockStatusList();

        //Double-array to store inventoryTable's values
        Object[][] inventoryTableVals = new Object[productNameList.size()][5];
        //inventoryTable's column headings
        String[] inventoryColumnHeadings = {"Product SKU", 
                                            "Product Name",
                                            "Current Product Stock",
                                            "Target (minimum) Inventory",
                                            "Product Inventory Status"};
        //Label creation section
        inventoryTableLabel = new JLabel("Current Product Inventory");
        foodBankLogo = new JLabel();
        foodBankLogo.setIcon(new ImageIcon("/Users/jmackey/Downloads/output-onlinepngtools-2.png"));
        //Initializing layout constraint
        GridBagConstraints layoutConst = null;

        //Creating JFrame & initializing inventoryTable
        setTitle("OU Food Bank Inventory Stock");
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(255, 237, 199));
        
        inventoryTable = new JTable(inventoryTableVals, inventoryColumnHeadings) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component comp = super.prepareRenderer(renderer, row, column);
            Color alternateColor = new Color(253, 249, 216);
            Color regColor = new Color(222, 97, 97);
            if(!comp.getBackground().equals(getSelectionBackground())) {
               Color c = (row % 2 == 0 ? alternateColor : regColor);
               comp.setBackground(c);
               c = null;
            }
            return comp;
         }
        };
        
    
        updateTable();
        inventoryTable.setRowHeight(20);
        inventoryTable.getColumn("Product Name").setPreferredWidth(150);
        inventoryTable.getColumn("Current Product Stock").setPreferredWidth(125);
        inventoryTable.getColumn("Target (minimum) Inventory").setPreferredWidth(160);
        inventoryTable.getColumn("Product Inventory Status").setPreferredWidth(150);
        
        inventoryTable.setEnabled(false);
        inventoryTable.getTableHeader().setReorderingAllowed(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                notifyFrameClosed();
            }
        });
        
        //Adding components via GridBagLayout
        setLayout(new GridBagLayout());

        //Food Bank Logo
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(0, 10, 1, 5);
        layoutConst.anchor = GridBagConstraints.EAST;
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 0;
        layoutConst.gridy = 0;
        add(foodBankLogo, layoutConst);

        //inventoryTableLabel
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(10, 10, 5, 0);
        layoutConst.fill = GridBagConstraints.CENTER;
        layoutConst.gridx = 0;
        layoutConst.gridy = 1;
        add(inventoryTableLabel, layoutConst);

        //inventoryTable's column headings
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(1, 0, 0, 0);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 0;
        layoutConst.gridy = 2;
        add(inventoryTable.getTableHeader(), layoutConst);

        //inventoryTable
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(0, 0, 0, 0);
        layoutConst.fill = GridBagConstraints.CENTER;
        layoutConst.gridx = 0;
        layoutConst.gridy = 3;
        add(inventoryTable, layoutConst);

    }

    public void updateTable() {
        final int productSKUCol = 0,
                    productNameCol = 1, 
                    productStockCol = 2, 
                    targetCol = 3,
                    stockStatusCol = 4;

        for(int i = 0; i < productNameList.size(); i++) {
            inventoryTable.setValueAt(productSKUList.get(i), i, productSKUCol);
            inventoryTable.setValueAt(productNameList.get(i), i, productNameCol);
            inventoryTable.setValueAt(inventoryStockList.get(i), i, productStockCol);
            inventoryTable.setValueAt(inventoryTargetList.get(i), i, targetCol);
            inventoryTable.setValueAt(stockStatusList.get(i), i, stockStatusCol);
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