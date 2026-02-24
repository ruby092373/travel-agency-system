// 位於: controller
package controller;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import model.Customer;
import model.Order;
import model.OrderItem;
import service.OrderService;
import service.impl.OrderServiceImpl;

public class OrderHistoryUI extends JDialog {

    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextArea detailTextArea;
    private Customer currentCustomer;

    private OrderService orderService = new OrderServiceImpl();

    public OrderHistoryUI(Customer customer) {
        this.currentCustomer = customer;
        setTitle("我的訂單紀錄");
        setBounds(150, 150, 750, 500);
        getContentPane().setLayout(new BorderLayout(0, 0));

        // --- 左側訂單列表 ---
        JScrollPane orderScrollPane = new JScrollPane();
        orderScrollPane.setBorder(new TitledBorder("訂單列表"));
        getContentPane().add(orderScrollPane, BorderLayout.WEST);
        
        orderTable = new JTable();
        tableModel = new DefaultTableModel(null, new String[]{"訂單ID", "訂單日期", "狀態", "總金額"}) { // 加入狀態
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        orderTable.setModel(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderScrollPane.setViewportView(orderTable);

        // --- 右側訂單詳情 ---
        JScrollPane detailScrollPane = new JScrollPane();
        detailScrollPane.setBorder(new TitledBorder("訂單詳情"));
        getContentPane().add(detailScrollPane, BorderLayout.CENTER);
        
        detailTextArea = new JTextArea();
        detailTextArea.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        detailTextArea.setEditable(false);
        detailScrollPane.setViewportView(detailTextArea);

        // --- 事件監聽 ---
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && orderTable.getSelectedRow() != -1) {
                int selectedRow = orderTable.getSelectedRow();
                Order selectedOrder = ((OrderTableModel) orderTable.getModel()).getOrderByRow(selectedRow);
                displayOrderDetails(selectedOrder);
            }
        });

        loadOrderHistory();
    }
    
    private void loadOrderHistory() {
        new SwingWorker<List<Order>, Void>() {
            @Override
            protected List<Order> doInBackground() throws Exception {
                return orderService.getOrderHistory(currentCustomer.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Order> orders = get();
                    orderTable.setModel(new OrderTableModel(orders));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    
    private void displayOrderDetails(Order order) {
        if (order == null) {
            detailTextArea.setText("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        sb.append("訂單編號: ").append(order.getId()).append("\n");
        sb.append("訂單狀態: ").append(order.getStatus()).append("\n");
        sb.append("訂單總額: $").append(order.getTotalPrice()).append("\n");
        sb.append("------------------------------------------\n");
        
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            for (OrderItem item : order.getOrderItems()) {
                sb.append("產品: ").append(item.getProductName()).append("\n");
                
                // 【處理出發日期】
                String dateStr = (item.getDepartureDate() == null) ? "N/A" : dateFormat.format(item.getDepartureDate());
                sb.append("  出發日期: ").append(dateStr).append("\n");
                
                sb.append("  單價: $").append(item.getUnitPrice()).append("\n");
                sb.append("  人數: ").append(item.getQuantity()).append("\n");
                sb.append("  小計: $").append(item.getUnitPrice() * item.getQuantity()).append("\n\n");
            }
        } else {
             sb.append("此訂單沒有詳細的產品項目。");
        }
        detailTextArea.setText(sb.toString());
    }
    
    // 自訂 TableModel
    private class OrderTableModel extends DefaultTableModel {
        private List<Order> orders;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        public OrderTableModel(List<Order> orders) {
            super(new String[]{"訂單ID", "訂單日期", "狀態", "總金額"}, 0);
            this.orders = orders;
            for (Order o : orders) {
                String dateStr = (o.getOrderDate() == null) ? "N/A" : dateFormat.format(o.getOrderDate());
                addRow(new Object[]{o.getId(), dateStr, o.getStatus(), o.getTotalPrice()});
            }
        }
        
        public Order getOrderByRow(int row) {
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
