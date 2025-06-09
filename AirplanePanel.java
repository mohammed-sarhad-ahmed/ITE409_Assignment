import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class AirplanePanel extends JPanel {
    private  ArrayList<Seat> seats;
    private final Connection connection;
    private final ArrayList<JButton> seatButtons = new ArrayList<>();

    public AirplanePanel(ArrayList<Seat> seats, Connection connection) {
        this.seats = seats;
        seats.sort((s1, s2) -> {
            String num1 = s1.getNumber();
            String num2 = s2.getNumber();
            char letter1 = num1.charAt(num1.length() - 1);
            char letter2 = num2.charAt(num2.length() - 1);

            int number1 = Integer.parseInt(num1.substring(0, num1.length() - 1));
            int number2 = Integer.parseInt(num2.substring(0, num2.length() - 1));
            if (letter1 != letter2) {
                return Character.compare(letter2, letter1);
            }
            return Integer.compare(number1, number2);
        });
        this.connection = connection;
        setLayout(null);
        createSeatButtons();
    }

    private void createSeatButtons() {
        int columns = 5;
        int rows = 4;
        int buttonWidth = 60;
        int buttonHeight = 50;

        int fuselageStartX = 100;
        int fuselageEndX = 1000;
        int topY = 150;
        int bottomY = 400;

        int usableWidth = fuselageEndX - fuselageStartX;
        int usableHeight = bottomY - topY;

        int hGap = usableWidth / (columns + 1);
        int vGap = usableHeight / (rows + 1);


        for (int i = 0; i < seats.size(); i++) {
            int row = i / columns;
            int col = i % columns;

            int x = fuselageStartX + (col + 1) * hGap - buttonWidth / 2;
            int y = topY + (row + 1) * vGap + row * 10 - buttonHeight / 2;

            Seat seat = seats.get(i);
            JButton btn = new JButton(seat.getNumber());
            btn.setBounds(x, y, buttonWidth, buttonHeight);
            btn.setBackground(seat.isReserved() ? Color.RED : Color.GREEN);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setFocusable(false);
            btn.setFont(new Font("Arial", Font.PLAIN, 10));

            btn.addActionListener((ActionEvent e) -> {
                boolean newReservedStatus = !seat.isReserved();
                seat.setReserved(newReservedStatus);
                btn.setBackground(newReservedStatus ? Color.RED : Color.GREEN);
                updateSeatInDB(seat);
            });

            seatButtons.add(btn);
            add(btn);
        }
    }

    private void updateSeatInDB(Seat seat) {
        String sql = "UPDATE seat SET s_reserved = ? WHERE s_number = ? AND f_code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, seat.isReserved() ? "1" : "0");
            pstmt.setString(2, seat.getNumber());
            pstmt.setString(3, seat.getFlightCode());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to update seat reservation in database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.BLACK);

        int w = getWidth();
        int h = getHeight();

        if (w == 0 || h == 0) {
            g2d.dispose();
            return;
        }

        double fuselageTop = h * 0.2;
        double fuselageBottom = h * 0.8;
        double fuselageHeight = fuselageBottom - fuselageTop;

        double noseX = w * 0.02;

        int arcX = (int) noseX;
        int arcY = (int) fuselageTop;
        int arcWidth = (int) (fuselageHeight + 600);
        int arcHeight = (int) fuselageHeight;


        g2d.drawArc(arcX, arcY, arcWidth, arcHeight, 90, 180);

        int fuselageStartX = arcX + arcWidth / 2;
        int fuselageEndX = (int) (w * 0.95);
        int topLineY = arcY;
        int bottomLineY = arcY + arcHeight;


        g2d.drawLine(fuselageStartX, topLineY, fuselageEndX, topLineY);
        g2d.drawLine(fuselageStartX, bottomLineY, fuselageEndX, bottomLineY);


        int strutLength = (int) (fuselageHeight * 0.25);
        int midX = fuselageStartX + (fuselageEndX - fuselageStartX) / 2;


        g2d.drawLine(midX - 200, topLineY, midX + strutLength, topLineY - strutLength);
        g2d.drawLine(midX + 100, topLineY, midX + strutLength + 200, topLineY - strutLength);


        g2d.drawLine(midX - 200, bottomLineY, midX + strutLength, bottomLineY + strutLength);
        g2d.drawLine(midX + 100, bottomLineY, midX + strutLength + 200, bottomLineY + strutLength);

        g2d.dispose();
    }
}
