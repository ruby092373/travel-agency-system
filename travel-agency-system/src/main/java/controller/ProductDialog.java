package controller;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import model.Product;
import service.ProductService;
import service.impl.ProductServiceImpl;

public class ProductDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField nameField, priceField, regionField, daysField, descField;
    private Product currentProduct; // 用於區分是新增還是修改

    private ProductService productService = new ProductServiceImpl();

    /**
     * @param product 如果是修改，傳入產品物件；如果是新增，傳入 null
     */
    public ProductDialog(Product product) {
        this.currentProduct = product;
        
        setTitle(product == null ? "新增產品" : "修改產品");
        setModal(true); // 設為模態對話框，會阻擋父視窗
        setBounds(200, 200, 450, 400);
        getContentPane().setLayout(null);
        contentPanel.setBounds(0, 0, 434, 361);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel);
        contentPanel.setLayout(null);

        // ... UI 元件 ...
        JLabel lblName = new JLabel("行程名稱:");
        lblName.setBounds(30, 30, 80, 20);
        contentPanel.add(lblName);
        nameField = new JTextField();
        nameField.setBounds(120, 30, 280, 25);
        contentPanel.add(nameField);

        // ... 其他欄位 ...
        JLabel lblPrice = new JLabel("價格:");
        lblPrice.setBounds(30, 70, 80, 20);
        contentPanel.add(lblPrice);
        priceField = new JTextField();
        priceField.setBounds(120, 70, 150, 25);
        contentPanel.add(priceField);
        
        JLabel lblRegion = new JLabel("地區:");
        lblRegion.setBounds(30, 110, 80, 20);
        contentPanel.add(lblRegion);
        regionField = new JTextField();
        regionField.setBounds(120, 110, 150, 25);
        contentPanel.add(regionField);
        
        JLabel lblDays = new JLabel("天數:");
        lblDays.setBounds(30, 150, 80, 20);
        contentPanel.add(lblDays);
        daysField = new JTextField();
        daysField.setBounds(120, 150, 150, 25);
        contentPanel.add(daysField);
        
        JLabel lblDesc = new JLabel("描述:");
        lblDesc.setBounds(30, 190, 80, 20);
        contentPanel.add(lblDesc);
        descField = new JTextField();
        descField.setBounds(120, 190, 280, 60);
        contentPanel.add(descField);

        JButton btnSave = new JButton("儲存");
        btnSave.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        btnSave.setBounds(160, 280, 100, 35);
        contentPanel.add(btnSave);

        // 如果是修改模式，將現有資料填入欄位
        if (currentProduct != null) {
            nameField.setText(currentProduct.getName());
            priceField.setText(String.valueOf(currentProduct.getPrice()));
            regionField.setText(currentProduct.getRegion());
            daysField.setText(String.valueOf(currentProduct.getDurationDays()));
            descField.setText(currentProduct.getDescription());
        }

        btnSave.addActionListener(e -> {
            // 從欄位獲取資料
            Product p = (currentProduct == null) ? new Product() : currentProduct;
            p.setName(nameField.getText());
            p.setPrice(Integer.parseInt(priceField.getText()));
            p.setRegion(regionField.getText());
            p.setDurationDays(Integer.parseInt(daysField.getText()));
            p.setDescription(descField.getText());

            if (currentProduct == null) {
                productService.addProduct(p); // 新增
            } else {
                productService.updateProduct(p); // 修改
            }
            dispose(); // 關閉對話框
        });
    }
}
