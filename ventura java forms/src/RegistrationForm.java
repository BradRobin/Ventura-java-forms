import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.Year;

public class RegistrationForm extends JFrame {
    private JTextField nameField, phoneField, addressField;
    private JComboBox<String> dayBox, monthBox, yearBox;
    private JRadioButton maleRadio, femaleRadio;
    private JCheckBox termsCheckBox;
    private JButton submitButton, resetButton, exitButton, registerButton;
    private JTable dataTable;
    private DefaultTableModel tableModel;

    private static final String URL = "jdbc:mysql://localhost:3306/registration_db";
    private static final String USER = "venturajavaform";
    private static final String PASSWORD = "your_password";

    public RegistrationForm() {
        setTitle("Registration Form");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(20);
        JLabel phoneLabel = new JLabel("Mobile:");
        phoneField = new JTextField(20);
        JLabel genderLabel = new JLabel("Gender:");
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        JLabel dobLabel = new JLabel("DOB:");
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.valueOf(i);
        }
        dayBox = new JComboBox<>(days);
        String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        monthBox = new JComboBox<>(months);
        int currentYear = Year.now().getValue();
        String[] years = new String[100];
        for (int i = 0; i < 100; i++) {
            years[i] = String.valueOf(currentYear - i);
        }
        yearBox = new JComboBox<>(years);
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField(20);
        termsCheckBox = new JCheckBox("Accept Terms And Conditions.");
        submitButton = new JButton("Submit");
        resetButton = new JButton("Reset");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(genderLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(maleRadio, gbc);
        gbc.gridx = 2;
        panel.add(femaleRadio, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(dobLabel, gbc);
        gbc.gridx = 1;
        panel.add(dayBox, gbc);
        gbc.gridx = 2;
        panel.add(monthBox, gbc);
        gbc.gridx = 3;
        panel.add(yearBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(addressLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panel.add(addressField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(termsCheckBox, gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        panel.add(submitButton, gbc);
        gbc.gridx = 2;
        panel.add(resetButton, gbc);

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

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Gender");
        tableModel.addColumn("Address");
        tableModel.addColumn("Contact");

        dataTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(dataTable);

        exitButton = new JButton("Exit");
        registerButton = new JButton("Register");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(exitButton);
        buttonPanel.add(registerButton);

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void handleSubmit() {
        if (!termsCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this, "You must accept the terms and conditions.");
            return;
        }

        String name = nameField.getText();
        String phone = phoneField.getText();
        String gender = maleRadio.isSelected() ? "Male" : "Female";
        String dob = yearBox.getSelectedItem() + "-" + (monthBox.getSelectedIndex() + 1) + "-"
                + dayBox.getSelectedItem();
        String address = addressField.getText();

        int uniqueId = storeInDatabase(name, phone, gender, dob, address);

        if (uniqueId != -1) {
            tableModel.addRow(new Object[] { uniqueId, name, gender, address, phone });
            JOptionPane.showMessageDialog(this, "Record added with ID: " + uniqueId);
        } else {
            JOptionPane.showMessageDialog(this, "Error adding record to database.");
        }
    }

    private void handleReset() {
        clearForm();
    }

    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        maleRadio.setSelected(true);
        dayBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
        addressField.setText("");
        termsCheckBox.setSelected(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegistrationForm();
            }
        });
    }

    private int storeInDatabase(String name, String phone, String gender, String dob, String address) {
        int uniqueId = -1;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to the database");

            String sql = "INSERT INTO users (name, phone, gender, dob, address) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setString(2, phone);
                pstmt.setString(3, gender);
                pstmt.setDate(4, Date.valueOf(dob));
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
