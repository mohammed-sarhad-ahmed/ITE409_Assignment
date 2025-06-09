public class Flight {
    private final String origin;
    private final String destination;
    private final String flightCode;

    public Flight(String origin, String destination, String flightCode) {
        this.origin = origin;
        this.destination = destination;
        this.flightCode = flightCode;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getFlightCode() {
        return flightCode;
    }
}
