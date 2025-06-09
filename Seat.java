public class Seat {
    private final String number;
    private final String flightCode;
    private boolean reserved;

    public Seat(String number, String flightCode, boolean reserved) {
        this.flightCode = flightCode;
        this.number = number;
        this.reserved = reserved;
    }

    public String getNumber() {
        return number;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public boolean isReserved() {
        return reserved;
    }

}
