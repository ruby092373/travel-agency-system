package controller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import model.Product;
import service.ProductService;
import service.impl.ProductServiceImpl;

public class EmployeeMainUI extends JFrame {

    private JPanel contentPane;
    private JTable productTable;
    private DefaultTableModel tableModel;

    private ProductService productService = new ProductServiceImpl();

    public EmployeeMainUI() {
        setTitle("後台管理系統"); // 標題簡化
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // --- 按鈕區 ---
        JPanel buttonPanel = new JPanel();
        contentPane.add(buttonPanel, BorderLayout.NORTH);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        // 【新】新增管理訂單按鈕
        JButton btnManageOrders = new JButton("管理訂單");
        btnManageOrders.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        buttonPanel.add(btnManageOrders);

        JButton btnAdd = new JButton("新增產品");
        btnAdd.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        buttonPanel.add(btnAdd);
        JButton btnUpdate = new JButton("修改產品");
        btnUpdate.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        buttonPanel.add(btnUpdate);
        JButton btnDelete = new JButton("刪除產品");
        btnDelete.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        buttonPanel.add(btnDelete);
        JButton btnRefresh = new JButton("重新整理產品");
        btnRefresh.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        buttonPanel.add(btnRefresh);
        
        JButton btnLogout = new JButton("登出");
        btnLogout.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        buttonPanel.add(btnLogout);

        // --- 產品列表 ---
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(new TitledBorder("產品列表")); // 加上標題
        contentPane.add(scrollPane, BorderLayout.CENTER);
        productTable = new JTable();
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("行程名稱");
        columnNames.add("價格");
        columnNames.add("地區");
        columnNames.add("天數");
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable.setModel(tableModel);
        scrollPane.setViewportView(productTable);
        
        // --- 事件監聽 ---
        
        // 【新】管理訂單按鈕事件
        btnManageOrders.addActionListener(e -> {
            ManageOrdersUI manageUI = new ManageOrdersUI();
            manageUI.setVisible(true);
        });
        
        btnRefresh.addActionListener(e -> loadAllProducts());
        btnAdd.addActionListener(e -> {
            ProductDialog dialog = new ProductDialog(null);
            dialog.setVisible(true);

            loadAllProducts();
        });
        btnUpdate.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "請先選擇要修改的產品。");
                return;
            }
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            Product p = productService.getProductById(productId); 
            ProductDialog dialog = new ProductDialog(p);
            dialog.setVisible(true);
            loadAllProducts();
        });
        btnDelete.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "請先選擇要刪除的產品。");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "確定要刪除此產品嗎？", "確認刪除", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int productId = (int) tableModel.getValueAt(selectedRow, 0);
                productService.deleteProduct(productId);
                loadAllProducts();
            }
        });
        
     // 【新增】登出按鈕事件監聽器
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "確定要登出系統嗎？", "確認登出", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // 打開登入介面
                new LoginUI().setVisible(true);
                // 關閉當前的後台介面
                this.dispose();
            }
        });
        
        // 啟動時載入產品
        loadAllProducts();
    }
    
    private void loadAllProducts() {
        new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return productService.getAllProducts();
            }
            @Override
            protected void done() {
                try {
                    List<Product> products = get();
                    tableModel.setRowCount(0);
                    for (Product p : products) {
                        Vector<Object> rowData = new Vector<>();
                        rowData.add(p.getId());
                        rowData.add(p.getName());
                        rowData.add(p.getPrice());
                        rowData.add(p.getRegion());
                        rowData.add(p.getDurationDays());
                        tableModel.addRow(rowData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
