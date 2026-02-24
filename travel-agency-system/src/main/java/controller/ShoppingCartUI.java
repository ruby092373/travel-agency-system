// 位於: controller
package controller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import model.Customer;
import model.ShoppingCartItem;
import service.OrderService;
import service.ShoppingCartService;
import service.impl.OrderServiceImpl;
import service.impl.ShoppingCartServiceImpl;

public class ShoppingCartUI extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotalPrice;
    private Customer currentCustomer;

    private ShoppingCartService cartService = new ShoppingCartServiceImpl();
    private OrderService orderService = new OrderServiceImpl();

    public ShoppingCartUI(Customer customer) {
        this.currentCustomer = customer;
        setTitle("我的購物車");
        setBounds(150, 150, 750, 400); // 視窗加寬
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPane = new JScrollPane();
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        cartTable = new JTable();
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("行程名稱");
        columnNames.add("單價");
        columnNames.add("人數");
        columnNames.add("出發日期"); // 【新增欄位】
        columnNames.add("小計");
        
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable.setModel(tableModel);
        scrollPane.setViewportView(cartTable);

        JPanel bottomPanel = new JPanel();
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setLayout(new BorderLayout(0, 0));

        JPanel buttonPanel = new JPanel();
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JButton btnRemoveItem = new JButton("移除商品");
        buttonPanel.add(btnRemoveItem);
        
        JButton btnCheckout = new JButton("確認結帳");
        btnCheckout.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        buttonPanel.add(btnCheckout);
        
        JPanel totalPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) totalPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        bottomPanel.add(totalPanel, BorderLayout.CENTER);
        
        lblTotalPrice = new JLabel("總金額：$0");
        lblTotalPrice.setFont(new Font("微軟正黑體", Font.BOLD, 18));
        totalPanel.add(lblTotalPrice);

        // --- 事件監聽 ---
        btnRemoveItem.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "請先選擇要移除的商品。");
                return;
            }
            int itemId = (int) tableModel.getValueAt(selectedRow, 0);
            cartService.removeItem(itemId);
            loadCartItems();
        });

        btnCheckout.addActionListener(e -> {
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "您的購物車是空的！");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "確定要結帳嗎？", "確認", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                orderService.createOrderFromCart(currentCustomer.getId());
                JOptionPane.showMessageDialog(this, "結帳成功！感謝您的訂購。");
                dispose();
            }
        });

        loadCartItems();
    }

    private void loadCartItems() {
        new SwingWorker<List<ShoppingCartItem>, Void>() {
            @Override
            protected List<ShoppingCartItem> doInBackground() throws Exception {
                return cartService.getCartItems(currentCustomer.getId());
            }

            @Override
            protected void done() {
                try {
                    List<ShoppingCartItem> items = get();
                    tableModel.setRowCount(0);
                    int totalPrice = 0;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    for (ShoppingCartItem item : items) {
                        Vector<Object> rowData = new Vector<>();
                        rowData.add(item.getId());
                        rowData.add(item.getProduct().getName());
                        rowData.add(item.getProduct().getPrice());
                        rowData.add(item.getQuantity());
                        
                        // 【處理出發日期】防呆，避免 null 導致錯誤
                        String dateStr = (item.getDepartureDate() == null) ? "N/A" : dateFormat.format(item.getDepartureDate());
                        rowData.add(dateStr);
                        
                        int subtotal = item.getProduct().getPrice() * item.getQuantity();
                        rowData.add(subtotal);
                        
                        tableModel.addRow(rowData);
                        totalPrice += subtotal;
                    }
                    lblTotalPrice.setText("總金額：$" + totalPrice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
