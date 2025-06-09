import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class App {
    public static void main(String[] args) {
        try {
            String url = "jdbc:mysql://localhost:3306/airlinereservationdb";
            String user = "root";
            String password = "newpassword";

            Connection con = DriverManager.getConnection(
                    url,
                    user,
                    password
            );
            FlightFrame window = new FlightFrame(con);
            window.setVisible(true);

            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        con.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
