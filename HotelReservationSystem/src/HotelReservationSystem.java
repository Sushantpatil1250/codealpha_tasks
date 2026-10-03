import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class HotelReservationSystem extends JFrame {

    private JTextField nameField;
    private JTextField roomField;

    private JComboBox<String> roomTypeBox;

    private JTable table;
    private DefaultTableModel tableModel;

    private JLabel totalRoomsLabel;
    private JLabel bookedRoomsLabel;
    private JLabel availableRoomsLabel;
    private JLabel occupancyLabel;

    private ArrayList<Room> reservations;

    private final int TOTAL_ROOMS = 20;

    public HotelReservationSystem() {

        reservations = new ArrayList<>();

        setTitle("Hotel Reservation System");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // HEADER

        JLabel heading = new JLabel(
                "HOTEL RESERVATION SYSTEM",
                SwingConstants.CENTER
        );

        heading.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        32
                )
        );

        heading.setForeground(Color.WHITE);
        heading.setOpaque(true);
        heading.setBackground(
                new Color(41,128,185)
        );

        add(heading, BorderLayout.NORTH);

        // INPUT PANEL

        JPanel inputPanel = new JPanel();

        inputPanel.add(
                new JLabel("Customer Name")
        );

        nameField = new JTextField(15);
        inputPanel.add(nameField);

        inputPanel.add(
                new JLabel("Room Number")
        );

        roomField = new JTextField(10);
        inputPanel.add(roomField);

        inputPanel.add(
                new JLabel("Room Type")
        );

        roomTypeBox =
                new JComboBox<>(
                        new String[]{
                                "Standard",
                                "Deluxe",
                                "Suite"
                        }
                );

        inputPanel.add(roomTypeBox);

        JButton bookButton =
                new JButton("Book Room");

        JButton checkoutButton =
                new JButton("Check Out");

        JButton deleteButton =
                new JButton("Delete");

        JButton clearButton =
                new JButton("Clear All");

        inputPanel.add(bookButton);
        inputPanel.add(checkoutButton);
        inputPanel.add(deleteButton);
        inputPanel.add(clearButton);

        add(inputPanel, BorderLayout.SOUTH);

        // TABLE

        String[] columns = {
                "Customer",
                "Room No",
                "Room Type",
                "Price",
                "Status"
        };

        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                );

        table = new JTable(tableModel);

        table.setRowHeight(35);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        table.setSelectionBackground(
                new Color(52,152,219)
        );

        JScrollPane scrollPane =
                new JScrollPane(table);

        add(scrollPane,
                BorderLayout.CENTER);

        // SUMMARY PANEL

        JPanel statsPanel =
                new JPanel();

        statsPanel.setLayout(
                new GridLayout(
                        4,
                        1,
                        10,
                        10
                )
        );

        statsPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Hotel Summary"
                )
        );

        totalRoomsLabel =
                new JLabel(
                        "Total Rooms : 20"
                );

        bookedRoomsLabel =
                new JLabel(
                        "Booked Rooms : 0"
                );

        availableRoomsLabel =
                new JLabel(
                        "Available Rooms : 20"
                );

        occupancyLabel =
                new JLabel(
                        "Occupancy Rate : 0%"
                );

        statsPanel.add(totalRoomsLabel);
        statsPanel.add(bookedRoomsLabel);
        statsPanel.add(availableRoomsLabel);
        statsPanel.add(occupancyLabel);

        add(statsPanel,
                BorderLayout.EAST);

        // BUTTON EVENTS

        bookButton.addActionListener(
                e -> bookRoom()
        );

        checkoutButton.addActionListener(
                e -> checkOut()
        );

        deleteButton.addActionListener(
                e -> deleteReservation()
        );

        clearButton.addActionListener(
                e -> clearAll()
        );

        setVisible(true);
    }

    // PRICE

    private int getPrice(String type){

        switch(type){

            case "Standard":
                return 1500;

            case "Deluxe":
                return 3000;

            case "Suite":
                return 5000;

            default:
                return 0;
        }
    }

    // BOOK ROOM

    private void bookRoom() {

        String customer =
                nameField.getText().trim();

        if(customer.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter Customer Name"
            );

            return;
        }

        try {

            int roomNo =
                    Integer.parseInt(
                            roomField.getText()
                    );

            if(roomNo < 1 ||
                    roomNo > TOTAL_ROOMS){

                JOptionPane.showMessageDialog(
                        this,
                        "Room Number must be between 1 and 20"
                );

                return;
            }

            for(Room room :
                    reservations){

                if(room.getRoomNumber()
                        == roomNo){

                    JOptionPane.showMessageDialog(
                            this,
                            "Room " + roomNo +
                                    " is already booked"
                    );

                    return;
                }
            }

            String type =
                    roomTypeBox
                            .getSelectedItem()
                            .toString();

            int price =
                    getPrice(type);

            Room room =
                    new Room(
                            roomNo,
                            type,
                            "Booked",
                            customer
                    );

            reservations.add(room);

            tableModel.addRow(
                    new Object[]{
                            customer,
                            roomNo,
                            type,
                            "₹" + price,
                            "Booked"
                    }
            );

            updateStats();

            nameField.setText("");
            roomField.setText("");

            JOptionPane.showMessageDialog(
                    this,
                    "Room Booked Successfully"
            );

        }
        catch(Exception ex){

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid Room Number"
            );
        }
    }

    // CHECK OUT

    private void checkOut() {

        int row = table.getSelectedRow();

        if(row == -1){

            JOptionPane.showMessageDialog(
                    this,
                    "Select a reservation first"
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Check Out this customer ?",
                "Confirm Check Out",
                JOptionPane.YES_NO_OPTION
        );

        if(choice == JOptionPane.YES_OPTION){

            reservations.remove(row);

            tableModel.removeRow(row);

            updateStats();

            JOptionPane.showMessageDialog(
                    this,
                    "Check Out Successful"
            );
        }
    }

    // DELETE

    private void deleteReservation() {

        int row =
                table.getSelectedRow();

        if(row == -1){

            JOptionPane.showMessageDialog(
                    this,
                    "Select Reservation"
            );

            return;
        }

        reservations.remove(row);

        tableModel.removeRow(row);

        updateStats();
    }

    // CLEAR ALL

    private void clearAll() {

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete All Reservations ?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION
                );

        if(choice ==
                JOptionPane.YES_OPTION){

            reservations.clear();

            tableModel.setRowCount(0);

            updateStats();
        }
    }

    // UPDATE STATS

    private void updateStats() {

        int booked = 0;

        for(Room room :
                reservations){

            if(room.getStatus()
                    .equals("Booked")){

                booked++;
            }
        }

        int available =
                TOTAL_ROOMS - booked;

        double occupancy =
                (booked * 100.0)
                        / TOTAL_ROOMS;

        bookedRoomsLabel.setText(
                "Booked Rooms : "
                        + booked
        );

        availableRoomsLabel.setText(
                "Available Rooms : "
                        + available
        );

        occupancyLabel.setText(
                String.format(
                        "Occupancy Rate : %.2f%%",
                        occupancy
                )
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                HotelReservationSystem::new
        );
    }
}