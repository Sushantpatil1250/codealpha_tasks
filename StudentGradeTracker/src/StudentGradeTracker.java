import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StudentGradeTracker extends JFrame {

    private JTextField nameField;
    private JTextField marksField;

    private JTable table;
    private DefaultTableModel tableModel;

    private JLabel avgLabel;
    private JLabel highestLabel;
    private JLabel lowestLabel;
    private JLabel topperLabel;
    private JLabel totalLabel;

    private ArrayList<Student> students;

    public StudentGradeTracker() {

        students = new ArrayList<>();

        setTitle("Student Grade Tracker");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // ================= HEADER =================

        JLabel heading = new JLabel(
                "STUDENT GRADE TRACKER",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setOpaque(true);
        heading.setBackground(new Color(41, 128, 185));
        heading.setForeground(Color.WHITE);

        add(heading, BorderLayout.NORTH);

        // ================= INPUT PANEL =================

        JPanel inputPanel = new JPanel();

        inputPanel.add(new JLabel("Student Name:"));

        nameField = new JTextField(15);
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Marks:"));

        marksField = new JTextField(10);
        inputPanel.add(marksField);

        JButton addButton = new JButton("Add Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton clearButton = new JButton("Clear All");
        JButton reportButton = new JButton("Generate Report");
        JButton saveButton = new JButton("Save Report");

        inputPanel.add(addButton);
        inputPanel.add(deleteButton);
        inputPanel.add(clearButton);
        inputPanel.add(reportButton);
        inputPanel.add(saveButton);

        add(inputPanel, BorderLayout.SOUTH);

        // ================= TABLE =================

        String[] columns = {
                "Name",
                "Marks",
                "Grade"
        };

        tableModel = new DefaultTableModel(columns, 0);

        table = new JTable(tableModel);

        table.setRowHeight(30);

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        table.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        JScrollPane scrollPane =
                new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        // ================= REPORT PANEL =================

        JPanel reportPanel = new JPanel();

        reportPanel.setLayout(
                new GridLayout(5, 1, 10, 10)
        );

        reportPanel.setBorder(
                BorderFactory.createTitledBorder("Summary")
        );

        avgLabel = new JLabel("Average Marks : ");
        highestLabel = new JLabel("Highest Marks : ");
        lowestLabel = new JLabel("Lowest Marks : ");
        topperLabel = new JLabel("Top Performer : ");
        totalLabel = new JLabel("Total Students : ");

        avgLabel.setFont(new Font("Arial", Font.BOLD, 14));
        highestLabel.setFont(new Font("Arial", Font.BOLD, 14));
        lowestLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topperLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));

        reportPanel.add(avgLabel);
        reportPanel.add(highestLabel);
        reportPanel.add(lowestLabel);
        reportPanel.add(topperLabel);
        reportPanel.add(totalLabel);

        add(reportPanel, BorderLayout.EAST);

        // ================= BUTTON ACTIONS =================

        addButton.addActionListener(e -> addStudent());

        deleteButton.addActionListener(e -> deleteStudent());

        clearButton.addActionListener(e -> clearAll());

        reportButton.addActionListener(e -> generateReport());

        saveButton.addActionListener(e -> saveReport());

        setVisible(true);
    }

    // ================= ADD STUDENT =================

    private void addStudent() {

        String name = nameField.getText().trim();

        if (name.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter student name"
            );

            return;
        }

        try {

            int marks =
                    Integer.parseInt(
                            marksField.getText()
                    );

            if (marks < 0 || marks > 100) {

                JOptionPane.showMessageDialog(
                        this,
                        "Marks must be between 0 and 100"
                );

                return;
            }

            Student student =
                    new Student(name, marks);

            students.add(student);

            tableModel.addRow(
                    new Object[]{
                            name,
                            marks,
                            getGrade(marks)
                    }
            );

            nameField.setText("");
            marksField.setText("");

            JOptionPane.showMessageDialog(
                    this,
                    "Student Added Successfully!"
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid marks"
            );
        }
    }

    // ================= DELETE STUDENT =================

    private void deleteStudent() {

        int row = table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student"
            );

            return;
        }

        students.remove(row);
        tableModel.removeRow(row);

        generateReport();
    }

    // ================= CLEAR ALL =================

    private void clearAll() {

        students.clear();

        tableModel.setRowCount(0);

        avgLabel.setText("Average Marks : ");
        highestLabel.setText("Highest Marks : ");
        lowestLabel.setText("Lowest Marks : ");
        topperLabel.setText("Top Performer : ");
        totalLabel.setText("Total Students : ");
    }

    // ================= REPORT =================

    private void generateReport() {

        if (students.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No student data available"
            );

            return;
        }

        double total = 0;

        int highest = Integer.MIN_VALUE;
        int lowest = Integer.MAX_VALUE;

        String topper = "";

        for (Student student : students) {

            int marks = student.getMarks();

            total += marks;

            if (marks > highest) {

                highest = marks;
                topper = student.getName();
            }

            if (marks < lowest) {

                lowest = marks;
            }
        }

        double average =
                total / students.size();

        avgLabel.setText(
                "Average Marks : "
                        + String.format("%.2f", average)
        );

        highestLabel.setText(
                "Highest Marks : " + highest
        );

        lowestLabel.setText(
                "Lowest Marks : " + lowest
        );

        topperLabel.setText(
                "Top Performer : " + topper
        );

        totalLabel.setText(
                "Total Students : "
                        + students.size()
        );
    }

    // ================= SAVE REPORT =================

    private void saveReport() {

        try {

            FileWriter writer =
                    new FileWriter("student_report.txt");

            writer.write(
                    avgLabel.getText() + "\n"
            );

            writer.write(
                    highestLabel.getText() + "\n"
            );

            writer.write(
                    lowestLabel.getText() + "\n"
            );

            writer.write(
                    topperLabel.getText() + "\n"
            );

            writer.write(
                    totalLabel.getText() + "\n"
            );

            writer.close();

            JOptionPane.showMessageDialog(
                    this,
                    "Report Saved Successfully!"
            );

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error Saving File!"
            );
        }
    }

    // ================= GRADE =================

    private String getGrade(int marks) {

        if (marks >= 90)
            return "A+";
        else if (marks >= 80)
            return "A";
        else if (marks >= 70)
            return "B+";
        else if (marks >= 60)
            return "B";
        else if (marks >= 50)
            return "C";
        else
            return "Fail";
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                StudentGradeTracker::new
        );
    }
}