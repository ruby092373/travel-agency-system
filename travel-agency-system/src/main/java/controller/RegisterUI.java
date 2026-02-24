package controller;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import model.Customer;
import service.LoginService;
import service.impl.LoginServiceImpl;

public class RegisterUI extends JDialog { // 使用 JDialog，彈出式視窗

    private final JPanel contentPanel = new JPanel();
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private LoginService loginService = new LoginServiceImpl();

    /**
     * 建立 Dialog。
     */
    public RegisterUI() {
        setTitle("顧客註冊");
        setBounds(150, 150, 400, 350);
        getContentPane().setLayout(null);
        contentPanel.setBounds(0, 0, 384, 311);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel);
        contentPanel.setLayout(null);

        JLabel lblTitle = new JLabel("建立新帳號");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("微軟正黑體", Font.BOLD, 22));
        lblTitle.setBounds(120, 20, 150, 30);
        contentPanel.add(lblTitle);

        JLabel lblName = new JLabel("姓名：");
        lblName.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        lblName.setBounds(50, 80, 80, 20);
        contentPanel.add(lblName);

        nameField = new JTextField();
        nameField.setBounds(160, 80, 160, 25);
        contentPanel.add(nameField);
        nameField.setColumns(10);

        JLabel lblUsername = new JLabel("帳號：");
        lblUsername.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        lblUsername.setBounds(50, 120, 80, 20);
        contentPanel.add(lblUsername);

        usernameField = new JTextField();
        usernameField.setBounds(160, 120, 160, 25);
        contentPanel.add(usernameField);

        JLabel lblPassword = new JLabel("密碼：");
        lblPassword.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        lblPassword.setBounds(50, 160, 80, 20);
        contentPanel.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(160, 160, 160, 25);
        contentPanel.add(passwordField);

        JLabel lblConfirmPassword = new JLabel("確認密碼：");
        lblConfirmPassword.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        lblConfirmPassword.setBounds(50, 200, 80, 20);
        contentPanel.add(lblConfirmPassword);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(160, 200, 160, 25);
        contentPanel.add(confirmPasswordField);

        JButton btnSubmit = new JButton("確認送出");
        btnSubmit.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        btnSubmit.setBounds(130, 250, 120, 35);
        contentPanel.add(btnSubmit);

        // --- 事件監聽 ---
        
        btnSubmit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String name = nameField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "姓名、帳號、密碼皆不可為空！");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "兩次輸入的密碼不一致！");
                    return;
                }
                
                Customer c = new Customer();
                c.setName(name);
                c.setUsername(username);
                c.setPassword(password); // 傳入原始密碼，Service 層會負責加密
                
                // 呼叫 Service 進行註冊
                Customer registeredCustomer = loginService.registerCustomer(c);
                
                if (registeredCustomer != null) {
                    JOptionPane.showMessageDialog(null, "註冊成功！請使用新帳號登入。");
                    dispose(); // 關閉註冊視窗
                } else {
                    JOptionPane.showMessageDialog(null, "註冊失敗，此帳號已被使用！", "錯誤", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
