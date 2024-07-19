import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
    private JButton submitButton, resetButton;
    private JTable dataTable;
    private DefaultTableModel tableModel;

    private static final String URL = "jdbc:mysql://localhost:3306/registration_db";
    private static final String USER = "venturajavaform";
    private static final String PASSWORD = "your_password";

    public RegistrationForm() {

        // Set up the frame properties

        setTitle("Registration Form");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creates and adds the title label at the top of the frame

        JLabel titleLabel = new JLabel("Registration Form", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLUE);
        add(titleLabel, BorderLayout.NORTH);

        // Creates and adds the table panel to the east side of the frame

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        // Finalize frame setup

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
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Sets a custom font for labels and text fields

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font textFont = new Font("Arial", Font.PLAIN, 14);

        // Creates and configures form components

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        nameField = new JTextField(20);
        nameField.setFont(textFont);

        JLabel phoneLabel = new JLabel("Mobile:");
        phoneLabel.setFont(labelFont);
        phoneField = new JTextField(20);
        phoneField.setFont(textFont);

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(labelFont);
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        maleRadio.setFont(textFont);
        femaleRadio.setFont(textFont);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);

        JLabel dobLabel = new JLabel("DOB:");
        dobLabel.setFont(labelFont);

        // Create and populate date of birth dropdowns

        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.valueOf(i);
        }
        dayBox = new JComboBox<>(days);
        dayBox.setFont(textFont);

        String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        monthBox = new JComboBox<>(months);
        monthBox.setFont(textFont);

        int currentYear = Year.now().getValue();
        String[] years = new String[100];
        for (int i = 0; i < 100; i++) {
            years[i] = String.valueOf(currentYear - i);
        }
        yearBox = new JComboBox<>(years);
        yearBox.setFont(textFont);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(labelFont);
        addressField = new JTextField(20);
        addressField.setFont(textFont);

        // Checkbox for terms and conditions

        termsCheckBox = new JCheckBox("Accept Terms And Conditions.");
        termsCheckBox.setFont(textFont);

        // Create buttons and set their fonts

        submitButton = new JButton("Submit");
        resetButton = new JButton("Reset");

        submitButton.setFont(textFont);
        resetButton.setFont(textFont);

        // Add components to the panel using GridBagLayout

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

        // Adds action listeners for the buttons
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

        // Create and set up the table model

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Gender");
        tableModel.addColumn("Address");
        tableModel.addColumn("Contact");

        // Create and style the table

        dataTable = new JTable(tableModel);
        styleTable(dataTable);
        JScrollPane scrollPane = new JScrollPane(dataTable);

        // Add the table to the panel

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleTable(JTable table) {

        // Set table row height and font

        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setGridColor(Color.LIGHT_GRAY);

        // Style the table header

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(Color.LIGHT_GRAY);
        header.setForeground(Color.BLACK);

        // Centre-align table cell contents

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, cellRenderer);
    }

    private void handleSubmit() {

        // Ensures conditions and terms are accpeted

        if (!termsCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this, "You must accept the terms and conditions.");
            return;
        }

        // Gather form data

        String name = nameField.getText();
        String phone = phoneField.getText();
        String gender = maleRadio.isSelected() ? "Male" : "Female";
        String dob = yearBox.getSelectedItem() + "-" + (monthBox.getSelectedIndex() + 1) + "-"
                + dayBox.getSelectedItem();
        String address = addressField.getText();

        // Generate unique ID and show confirmation dialog

        int uniqueId = generateUniqueId();

        if (uniqueId != -1) {
            showConfirmationDialog(uniqueId, name, phone, gender, dob, address);
        } else {
            JOptionPane.showMessageDialog(this, "Error generating unique ID.");
        }
    }

    private int generateUniqueId() {
        int uniqueId = -1;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Query to get the next auto-increment ID

            String sql = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'registration_db' AND TABLE_NAME = 'users'";
            try (Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    uniqueId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uniqueId;
    }

    private void showConfirmationDialog(int id, String name, String phone, String gender, String dob, String address) {

        // Create and congifure the confirmtion dialog

        JDialog confirmationDialog = new JDialog(this, "Confirm Details", true);
        confirmationDialog.setLayout(new BorderLayout());

        // Set up table model for confirmation dialog

        DefaultTableModel confirmTableModel = new DefaultTableModel();
        confirmTableModel.addColumn("ID");
        confirmTableModel.addColumn("Name");
        confirmTableModel.addColumn("Gender");
        confirmTableModel.addColumn("DOB");
        confirmTableModel.addColumn("Address");
        confirmTableModel.addColumn("Contact");

        // Create and populate the confirmation table
        JTable confirmTable = new JTable(confirmTableModel);
        confirmTableModel.addRow(new Object[] { id, name, gender, dob, address, phone });
        styleTable(confirmTable);

        JScrollPane scrollPane = new JScrollPane(confirmTable);
        confirmationDialog.add(scrollPane, BorderLayout.CENTER);

        // Create and add buttons to the dialog
        JPanel buttonPanel = new JPanel();
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        confirmationDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Register button action listener
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int storedId = storeInDatabase(id, name, phone, gender, dob, address);
                if (storedId != -1) {
                    tableModel.addRow(new Object[] { storedId, name, gender, address, phone });
                    JOptionPane.showMessageDialog(confirmationDialog, "Record added with ID: " + storedId);
                } else {
                    JOptionPane.showMessageDialog(confirmationDialog, "Error adding record to database.");
                }
                confirmationDialog.dispose();
            }
        });

        // cancel button action listener
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmationDialog.dispose();
            }
        });

        // Set dialog size and location
        confirmationDialog.setSize(500, 300);
        confirmationDialog.setLocationRelativeTo(this);
        confirmationDialog.setVisible(true);
    }

    private int storeInDatabase(int id, String name, String phone, String gender, String dob, String address) {
        int storedId = -1;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO users (id, name, phone, gender, dob, address) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, phone);
                pstmt.setString(4, gender);
                pstmt.setString(5, dob);
                pstmt.setString(6, address);
                pstmt.executeUpdate();
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        storedId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return storedId;
    }

    private void handleReset() {
        // Reset form fields to thei default values
        nameField.setText("");
        phoneField.setText("");
        addressField.setText("");
        maleRadio.setSelected(true);
        dayBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
        termsCheckBox.setSelected(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationForm());
    }
}
