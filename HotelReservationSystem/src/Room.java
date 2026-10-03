public class Room {

    private int roomNumber;
    private String roomType;
    private String status;
    private String customerName;

    public Room(int roomNumber,
                String roomType,
                String status,
                String customerName) {

        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = status;
        this.customerName = customerName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}