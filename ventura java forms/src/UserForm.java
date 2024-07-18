import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserForm extends JFrame {
    private JTextField nameField;
    private JTextField phoneField;
    private JCheckBox maleCheckBox;
    private JCheckBox femaleCheckBox;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> yearComboBox;
    private JTextField addressField;
    private JCheckBox termsCheckBox;
    private JButton submitButton;
    private JButton resetButton;
    private JTable registeredTable;
    private DefaultTableModel tableModel;

    public UserForm() {
        setTitle("User Registration Form");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2));

        // Name
        formPanel.add(new JLabel("Name: "));
        nameField = new JTextField();
        formPanel.add(nameField);

        // Phone
        formPanel.add(new JLabel("Phone: "));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        // Gender
        formPanel.add(new JLabel("Gender: "));
        JPanel genderPanel = new JPanel();
        maleCheckBox = new JCheckBox("Male");
        femaleCheckBox = new JCheckBox("Female");
        genderPanel.add(maleCheckBox);
        genderPanel.add(femaleCheckBox);
        formPanel.add(genderPanel);

        // Date of Birth
        formPanel.add(new JLabel("Date of Birth: "));
        JPanel dobPanel = new JPanel();
        dayComboBox = new JComboBox<>(getDays());
        monthComboBox = new JComboBox<>(getMonths());
        yearComboBox = new JComboBox<>(getYears());
        dobPanel.add(dayComboBox);
        dobPanel.add(monthComboBox);
        dobPanel.add(yearComboBox);
        formPanel.add(dobPanel);

        // Address
        formPanel.add(new JLabel("Address: "));
        addressField = new JTextField();
        formPanel.add(addressField);

        // Terms and Conditions
        termsCheckBox = new JCheckBox("Agree to terms and conditions");
        formPanel.add(termsCheckBox);
        formPanel.add(new JLabel()); // Empty cell

        // Buttons
        submitButton = new JButton("Submit");
        resetButton = new JButton("Reset");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(resetButton);
        formPanel.add(buttonPanel);

        add(formPanel, BorderLayout.NORTH);

        // Table for registered persons
        String[] columnNames = { "ID", "Name", "Gender", "Phone", "Address" };
        tableModel = new DefaultTableModel(columnNames, 0);
        registeredTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(registeredTable);
        add(scrollPane, BorderLayout.CENTER);

        // Exit and Register buttons
        JPanel bottomPanel = new JPanel();
        JButton exitButton = new JButton("Exit");
        JButton registerButton = new JButton("Register");
        bottomPanel.add(exitButton);
        bottomPanel.add(registerButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleReset();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleReset();
            }
        });
    }

    private String[] getDays() {
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.valueOf(i);
        }
        return days;
    }

    private String[] getMonths() {
        String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December" };
        return months;
    }

    private String[] getYears() {
        String[] years = new String[100];
        for (int i = 0; i < 100; i++) {
            years[i] = String.valueOf(1924 + i);
        }
        return years;
    }

    private void handleReset() {
        nameField.setText("");
        phoneField.setText("");
        maleCheckBox.setSelected(false);
        femaleCheckBox.setSelected(false);
        dayComboBox.setSelectedIndex(0);
        monthComboBox.setSelectedIndex(0);
        yearComboBox.setSelectedIndex(0);
        addressField.setText("");
        termsCheckBox.setSelected(false);
    }

    private void handleSubmit() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String gender = maleCheckBox.isSelected() ? "Male" : femaleCheckBox.isSelected() ? "Female" : "";
        String day = (String) dayComboBox.getSelectedItem();
        String month = String.format("%02d", monthComboBox.getSelectedIndex() + 1);
        String year = (String) yearComboBox.getSelectedItem();
        String dob = year + "-" + month + "-" + day; // Format date as YYYY-MM-DD
        String address = addressField.getText();
        boolean agreedToTerms = termsCheckBox.isSelected();

        if (name.isEmpty() || phone.isEmpty() || gender.isEmpty() || address.isEmpty() || !agreedToTerms) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and agree to terms and conditions.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Store in database and get the unique ID
        int uniqueId = DatabaseHelper.storeInDatabase(name, phone, gender, dob, address);

        // Show success message with unique ID
        JOptionPane.showMessageDialog(this, "User registered with ID: " + uniqueId);

        // Update the table
        tableModel.addRow(new Object[] { uniqueId, name, gender, phone, address });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UserForm().setVisible(true);
            }
        });
    }
}

class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/venturajavaform";
    private static final String USER = "venturajavaform";
    private static final String PASSWORD = "root";

    public static int storeInDatabase(String name, String phone, String gender, String dob, String address) {
        int uniqueId = -1;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to the database");

            String sql = "INSERT INTO users (name, phone, gender, dob, address) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setString(2, phone);
                pstmt.setString(3, gender);
                pstmt.setString(4, dob);
                pstmt.setString(5, address);
                System.out.println("Executing insert with values: " + name + ", " + phone + ", " + gender + ", " + dob
                        + ", " + address);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Insert successful");
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            uniqueId = rs.getInt(1);
                            System.out.println("Generated ID: " + uniqueId);
                        }
                    }
                } else {
                    System.out.println("Insert failed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uniqueId;
    }
}
