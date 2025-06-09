import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlightFrame extends JFrame implements ItemListener {
    private final Connection connection;
    private final JComboBox<String> flightDropdown;
    private final Map<String, String> flightLabelToCode;
    private AirplanePanel airplanePanel;

    public FlightFrame(Connection connection) throws SQLException {
        super("Airline Reservation System");
        this.connection = connection;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setResizable(false);



        ArrayList<Flight> flights = loadFlights();


        flightLabelToCode = new HashMap<>();
        flightDropdown = new JComboBox<>();

        for (Flight flight : flights) {
            String label = flight.getFlightCode()+" "+flight.getOrigin()+" to "+flight.getDestination()+" ";
            flightLabelToCode.put(label, flight.getFlightCode());
            flightDropdown.addItem(label);
        }
        flightDropdown.addItemListener(this);
        add(flightDropdown, BorderLayout.NORTH);


        if (!flights.isEmpty()) {
            String firstFlightCode = flights.get(0).getFlightCode();
            loadSeatsForFlight(firstFlightCode);
        }
    }

    private ArrayList<Flight> loadFlights() throws SQLException {
        ArrayList<Flight> flights = new ArrayList<>();
        String query = "SELECT f_origin, f_destination, f_code FROM flight";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                flights.add(new Flight(
                        rs.getString("f_origin"),
                        rs.getString("f_destination"),
                        rs.getString("f_code")
                ));
            }
        }
        return flights;
    }

    private ArrayList<Seat> loadSeats(String flightCode) throws SQLException {
        ArrayList<Seat> seats = new ArrayList<>();
        String query = "SELECT s_number, f_code, s_reserved FROM seat WHERE f_code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, flightCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    seats.add(new Seat(
                            rs.getString("s_number"),
                            rs.getString("f_code"),
                            "1".equals(rs.getString("s_reserved"))
                    ));
                }
            }
        }
        return seats;
    }

    private void loadSeatsForFlight(String flightCode) throws SQLException {
        ArrayList<Seat> seats = loadSeats(flightCode);

        if (airplanePanel != null) {
            remove(airplanePanel);
        }

        airplanePanel = new AirplanePanel(seats, connection);
        add(airplanePanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String selectedLabel = (String) flightDropdown.getSelectedItem();
            String flightCode = flightLabelToCode.get(selectedLabel);

            if (flightCode != null) {
                try {
                    loadSeatsForFlight(flightCode);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Error loading seats for flight.",
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
