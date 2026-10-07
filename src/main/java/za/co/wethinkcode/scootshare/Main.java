package za.co.wethinkcode.scootshare;

import za.co.wethinkcode.scootshare.db.Database;
import za.co.wethinkcode.scootshare.model.CargoScooter;
import za.co.wethinkcode.scootshare.model.ElectricScooter;
import za.co.wethinkcode.scootshare.model.StandardScooter;
import za.co.wethinkcode.scootshare.service.BatteryService;
import za.co.wethinkcode.scootshare.service.FleetReport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException {
        List<ElectricScooter> fleet = List.of(
                new StandardScooter("S1", 100),
                new StandardScooter("S2", 15),
                new CargoScooter("C1", 60));

        BatteryService battery = new BatteryService();
        for (ElectricScooter scooter : fleet) {
            System.out.printf("%s -> %s%n", scooter.describe(),
                    battery.statusOf(scooter.getBatteryPercent(), false, false));
        }
        System.out.println("Fleet: " + new FleetReport().summary(fleet));

        try (Connection connection = Database.connect(":memory:");
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT sqlite_version()")) {
            rs.next();
            System.out.println("Database ready (SQLite " + rs.getString(1) + ")");
        }
    }
}
