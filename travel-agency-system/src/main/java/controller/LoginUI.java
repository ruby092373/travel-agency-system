package controller;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import model.Customer;
import model.Employee;
import service.LoginService;
import service.impl.LoginServiceImpl;

public class LoginUI extends JFrame {

    private JPanel contentPane;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final ButtonGroup buttonGroup = new ButtonGroup();

    // Controller 持有 Service 的實例
    private LoginService loginService = new LoginServiceImpl();

    /**
     * 啟動應用程式。
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginUI frame = new LoginUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 建立 Frame。
     */
    public LoginUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 350);
        setTitle("旅行社訂購系統 - 登入");
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitle = new JLabel("旅行社訂購系統");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("微軟正黑體", Font.BOLD, 24));
        lblTitle.setBounds(110, 25, 220, 35);
        contentPane.add(lblTitle);

        JLabel lblUsername = new JLabel("帳號：");
        lblUsername.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        lblUsername.setBounds(70, 90, 60, 20);
        contentPane.add(lblUsername);

        usernameField = new JTextField();
        usernameField.setBounds(140, 90, 180, 25);
        contentPane.add(usernameField);
        usernameField.setColumns(10);

        JLabel lblPassword = new JLabel("密碼：");
        lblPassword.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        lblPassword.setBounds(70, 130, 60, 20);
        contentPane.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(140, 130, 180, 25);
        contentPane.add(passwordField);

        JRadioButton radioCustomer = new JRadioButton("顧客登入");
        buttonGroup.add(radioCustomer);
        radioCustomer.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        radioCustomer.setSelected(true);
        radioCustomer.setBounds(100, 170, 100, 23);
        contentPane.add(radioCustomer);

        JRadioButton radioEmployee = new JRadioButton("員工登入");
        buttonGroup.add(radioEmployee);
        radioEmployee.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        radioEmployee.setBounds(230, 170, 100, 23);
        contentPane.add(radioEmployee);

        JButton btnLogin = new JButton("登入");
        btnLogin.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        btnLogin.setBounds(90, 210, 100, 35);
        contentPane.add(btnLogin);

        JButton btnRegister = new JButton("註冊");
        btnRegister.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        btnRegister.setBounds(240, 210, 100, 35);
        contentPane.add(btnRegister);
        
        // --- 事件監聽 ---

        btnRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 點擊註冊，打開註冊視窗
                RegisterUI registerUI = new RegisterUI();
                registerUI.setVisible(true);
            }
        });

        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "帳號和密碼不能為空！", "錯誤", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 禁用按鈕，防止重複點擊
                btnLogin.setEnabled(false);
                btnRegister.setEnabled(false);
                btnLogin.setText("登入中...");

                // 使用 SwingWorker 執行耗時的登入操作
                new SwingWorker<Object, Void>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        // 在背景執行緒中呼叫 Service
                        if (radioCustomer.isSelected()) {
                            return loginService.customerLogin(username, password);
                        } else {
                            return loginService.employeeLogin(username, password);
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            Object result = get(); // 取得背景任務的回傳值
                            if (result instanceof Customer) {
                                // 顧客登入成功
                                Customer customer = (Customer) result;
                                JOptionPane.showMessageDialog(null, "顧客 " + customer.getName() + "，歡迎光臨！");
                                // 打開顧客主介面，並將登入者資訊傳過去
                                new CustomerMainUI(customer).setVisible(true);
                                dispose(); // 關閉登入視窗
                            } else if (result instanceof Employee) {
                                // 員工登入成功
                                Employee employee = (Employee) result;
                                JOptionPane.showMessageDialog(null, "員工 " + employee.getName() + "，歡迎回來！");
                                // 打開員工後台管理介面
                                new EmployeeMainUI().setVisible(true);
                                dispose(); // 關閉登入視窗
                            } else {
                                // 登入失敗 (result is null)
                                JOptionPane.showMessageDialog(null, "帳號或密碼錯誤，請先註冊！", "登入失敗", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "登入時發生系統錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            // 無論成功或失敗，最後都恢復按鈕狀態
                            btnLogin.setEnabled(true);
                            btnRegister.setEnabled(true);
                            btnLogin.setText("登入");
                        }
                    }
                }.execute();
            }
        });
    }
}
