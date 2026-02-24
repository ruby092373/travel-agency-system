// 位於: controller
package controller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import model.Customer;
import model.Product;
import service.ProductService;
import service.ShoppingCartService;
import service.impl.ProductServiceImpl;
import service.impl.ShoppingCartServiceImpl;
import util.DateLabelFormatter; // 必須要有我們自訂的 Formatter

public class CustomerMainUI extends JFrame {

    private JPanel contentPane;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private Customer currentCustomer;
    private JComboBox<Integer> daysComboBox;

    private ProductService productService = new ProductServiceImpl();
    private ShoppingCartService cartService = new ShoppingCartServiceImpl();

    public CustomerMainUI(Customer customer) {
        this.currentCustomer = customer;
        setTitle("旅行社訂購系統 - 歡迎, " + currentCustomer.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // --- 上方控制區 ---
        JPanel topPanel = new JPanel();
        contentPane.add(topPanel, BorderLayout.NORTH);
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JLabel lblRegion = new JLabel("地區：");
        lblRegion.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        topPanel.add(lblRegion);

        String[] regions = {"全部地區", "北部", "中部", "南部", "東部", "離島"};
        JComboBox<String> regionComboBox = new JComboBox<>(regions);
        topPanel.add(regionComboBox);

        JLabel lblDays = new JLabel("天數：");
        lblDays.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        topPanel.add(lblDays);

        daysComboBox = new JComboBox<>();
        topPanel.add(daysComboBox);

        JButton btnSearch = new JButton("查詢");
        btnSearch.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        topPanel.add(btnSearch);

        JButton btnLogout = new JButton("登出");
        btnLogout.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        topPanel.add(btnLogout);

        // --- 中間產品列表 ---
        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        productTable = new JTable();
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("行程名稱");
        columnNames.add("價格");
        columnNames.add("地區");
        columnNames.add("天數");
        columnNames.add("描述");
        
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable.setModel(tableModel);
        scrollPane.setViewportView(productTable);

        // --- 下方按鈕區 ---
        JPanel bottomPanel = new JPanel();
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        
        JButton btnAddToCart = new JButton("加入購物車");
        btnAddToCart.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        bottomPanel.add(btnAddToCart);
        
        JButton btnViewCart = new JButton("查看購物車");
        btnViewCart.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        bottomPanel.add(btnViewCart);
        
        JButton btnMyOrders = new JButton("我的訂單");
        btnMyOrders.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        bottomPanel.add(btnMyOrders);

        // --- 事件監聽 ---
        btnSearch.addActionListener(e -> loadProducts((String) regionComboBox.getSelectedItem(), (Integer) daysComboBox.getSelectedItem()));
        
        btnAddToCart.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "請先在表格中選擇一個行程！");
                return;
            }
            int productId = (int) tableModel.getValueAt(selectedRow, 0);

            // 建立日曆元件，並套用自訂的 Formatter 解決點擊無效的問題
            UtilDateModel model = new UtilDateModel();
            Properties p = new Properties();
            p.put("text.today", "Today");
            p.put("text.month", "Month");
            p.put("text.year", "Year");
            JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
            JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter()); // 【關鍵】
            
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

            final JComponent[] inputs = new JComponent[] {
                    new JLabel("出發日期:"),
                    datePicker,
                    new JLabel("旅遊人數:"),
                    quantitySpinner
            };
            
            int result = JOptionPane.showConfirmDialog(this, inputs, "選擇出發日期與人數", JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
                int quantity = (Integer) quantitySpinner.getValue();

                // 日期驗證邏輯
                if (selectedDate == null) {
                    JOptionPane.showMessageDialog(this, "請選擇出發日期！", "錯誤", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
                
                Calendar maxDate = Calendar.getInstance();
                maxDate.add(Calendar.YEAR, 1);

                if (selectedDate.before(today.getTime())) {
                    JOptionPane.showMessageDialog(this, "出發日期不能早於今天！", "錯誤", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (selectedDate.after(maxDate.getTime())) {
                    JOptionPane.showMessageDialog(this, "出發日期不能超過一年！", "錯誤", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                cartService.addToCart(currentCustomer.getId(), productId, quantity, selectedDate);
                JOptionPane.showMessageDialog(this, "已成功加入購物車！");
            }
        });

        btnViewCart.addActionListener(e -> {
            ShoppingCartUI cartUI = new ShoppingCartUI(currentCustomer);
            cartUI.setModal(true);
            cartUI.setVisible(true);
        });

        btnMyOrders.addActionListener(e -> {
            OrderHistoryUI historyUI = new OrderHistoryUI(currentCustomer);
            historyUI.setModal(true);
            historyUI.setVisible(true);
        });
        
        btnLogout.addActionListener(e -> {
            new LoginUI().setVisible(true);
            this.dispose();
        });

        // 初始載入
        loadDynamicOptions();
        loadProducts(null, 0);
    }
    
    private void loadDynamicOptions() {
        new SwingWorker<List<Integer>, Void>() {
            @Override
            protected List<Integer> doInBackground() throws Exception {
                return productService.getDistinctDurationDays();
            }
            
            @Override
            protected void done() {
                try {
                    List<Integer> days = get();
                    daysComboBox.removeAllItems();
                    daysComboBox.addItem(0); // 0 代表不限
                    for (Integer day : days) {
                        daysComboBox.addItem(day);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CustomerMainUI.this, "載入天數選項時發生錯誤。");
                }
            }
        }.execute();
    }
    
    private void loadProducts(String region, Integer durationDays) {
        new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return productService.searchProducts(region, durationDays);
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
                        rowData.add(p.getDescription());
                        tableModel.addRow(rowData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
