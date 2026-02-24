package controller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import model.OrderItem;
import service.OrderService;
import service.impl.OrderServiceImpl;
import vo.ViewOrder;

public class ManageOrdersUI extends JDialog {

    private JTable orderTable;
    private JTextArea detailTextArea;
    
    private OrderService orderService = new OrderServiceImpl();

    public ManageOrdersUI() {
        setTitle("後台管理系統 - 訂單管理");
        setBounds(150, 150, 800, 550);
        setModal(true);
        getContentPane().setLayout(new BorderLayout(0, 0));

        // --- 按鈕區 ---
        JPanel topPanel = new JPanel();
        getContentPane().add(topPanel, BorderLayout.NORTH);
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JButton btnUpdateStatus = new JButton("更新狀態");
        btnUpdateStatus.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        topPanel.add(btnUpdateStatus);
        
        JButton btnDeleteOrder = new JButton("刪除訂單");
        btnDeleteOrder.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        topPanel.add(btnDeleteOrder);

        // --- 訂單列表與詳情 ---
        JPanel centerPanel = new JPanel();
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(new BorderLayout(0, 0));
        
        JScrollPane orderScrollPane = new JScrollPane();
        orderScrollPane.setBorder(new TitledBorder("所有訂單"));
        centerPanel.add(orderScrollPane, BorderLayout.WEST);
        
        orderTable = new JTable();
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderScrollPane.setViewportView(orderTable);

        JScrollPane detailScrollPane = new JScrollPane();
        detailScrollPane.setBorder(new TitledBorder("訂單詳情"));
        centerPanel.add(detailScrollPane, BorderLayout.CENTER);

        detailTextArea = new JTextArea();
        detailTextArea.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        detailTextArea.setEditable(false);
        detailScrollPane.setViewportView(detailTextArea);

        // --- 事件監聽 ---
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && orderTable.getSelectedRow() != -1) {
                ViewOrder selectedOrder = ((ViewOrderTableModel) orderTable.getModel()).getOrderByRow(orderTable.getSelectedRow());
                displayOrderDetails(selectedOrder);
            }
        });
        
        btnUpdateStatus.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "請先選擇要更新的訂單。");
                return;
            }
            ViewOrder selectedOrder = ((ViewOrderTableModel) orderTable.getModel()).getOrderByRow(selectedRow);
            
            String[] statuses = {"處理中", "已付款", "已完成", "已取消"};
            Object newStatus = JOptionPane.showInputDialog(this, "請選擇新的訂單狀態：",
                    "更新狀態", JOptionPane.QUESTION_MESSAGE, null, statuses, selectedOrder.getStatus());
            
            if (newStatus != null) {
                orderService.updateOrderStatus(selectedOrder.getOrderId(), (String) newStatus);
                loadAllOrders();
            }
        });

        btnDeleteOrder.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "請先選擇要刪除的訂單。");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "確定要永久刪除此訂單嗎？\n此操作無法復原！", "確認刪除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                ViewOrder selectedOrder = ((ViewOrderTableModel) orderTable.getModel()).getOrderByRow(selectedRow);
                orderService.deleteOrder(selectedOrder.getOrderId());
                loadAllOrders();
            }
        });

        // 啟動時載入所有訂單
        loadAllOrders();
    }
    
    /**
     * 【完整版】從 Service 獲取訂單資料並顯示在表格上
     */
    private void loadAllOrders() {
        new SwingWorker<List<ViewOrder>, Void>() {
            @Override
            protected List<ViewOrder> doInBackground() throws Exception {
                // 在背景執行緒呼叫 Service
                return orderService.getAllOrdersForView();
            }

            @Override
            protected void done() {
                try {
                    // 獲取背景執行緒的結果
                    List<ViewOrder> orders = get();
                    // 將結果設定到表格上
                    orderTable.setModel(new ViewOrderTableModel(orders));
                } catch (Exception e) {
                    // 如果發生任何錯誤，印出錯誤訊息並彈出提示框
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ManageOrdersUI.this, "載入訂單時發生錯誤，請檢查主控台(Console)訊息。", "錯誤", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    /**
     * 【完整版】在右側文字區顯示選中訂單的詳細資訊
     */
    private void displayOrderDetails(ViewOrder order) {
        if (order == null) {
            detailTextArea.setText("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("訂單編號: ").append(order.getOrderId()).append("\n");
        sb.append("顧客姓名: ").append(order.getCustomerName() == null ? "(顧客資料不存在)" : order.getCustomerName()).append("\n");
        sb.append("訂單總額: $").append(order.getTotalPrice()).append("\n");
        sb.append("------------------------------------------\n");
        
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            for (OrderItem item : order.getOrderItems()) {
                sb.append("產品: ").append(item.getProductName()).append("\n");
                sb.append("  單價: $").append(item.getUnitPrice()).append("\n");
                sb.append("  人數: ").append(item.getQuantity()).append("\n");
                sb.append("  小計: $").append(item.getUnitPrice() * item.getQuantity()).append("\n\n");
            }
        } else {
            sb.append("此訂單沒有詳細的產品項目。");
        }
        detailTextArea.setText(sb.toString());
    }
    
    /**
     * 【完整版】自訂 TableModel，增加對 null 值的健壯性處理
     */
    private class ViewOrderTableModel extends DefaultTableModel {
        private List<ViewOrder> orders;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        public ViewOrderTableModel(List<ViewOrder> orders) {
            super(new String[]{"訂單ID", "顧客姓名", "訂單日期", "狀態", "總金額"}, 0);
            this.orders = orders;
            for (ViewOrder vo : orders) {
                // 【關鍵】在格式化日期或獲取顧客姓名時，先檢查是否為 null
                Object dateString = (vo.getOrderDate() == null) ? "N/A" : dateFormat.format(vo.getOrderDate());
                Object customerName = (vo.getCustomerName() == null) ? "(顧客資料不存在)" : vo.getCustomerName();
                
                addRow(new Object[]{
                    vo.getOrderId(), 
                    customerName, 
                    dateString, 
                    vo.getStatus(), 
                    vo.getTotalPrice()
                });
            }
        }
        
        public ViewOrder getOrderByRow(int row) { 
            if (row >= 0 && row < orders.size()) {
                return orders.get(row);
            }
            return null;
        }
        
        @Override 
        public boolean isCellEditable(int row, int column) { 
            return false; 
        }
    }
}
