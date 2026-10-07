package za.co.wethinkcode.scootshare.service;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.scootshare.model.CargoScooter;
import za.co.wethinkcode.scootshare.model.ElectricScooter;
import za.co.wethinkcode.scootshare.model.StandardScooter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FleetReportTest {

    private final FleetReport report = new FleetReport();

    @Test
    void anEmptyFleetReportsZeroes() {
        assertThat(report.summary(List.of())).isEqualTo("low=0, ok=0, full=0");
    }

    @Test
    void countsEachBand() {
        List<ElectricScooter> fleet = List.of(
                new StandardScooter("S1", 100),
                new StandardScooter("S2", 15),
                new StandardScooter("S3", 50),
                new StandardScooter("S4", 70));

        assertThat(report.summary(fleet)).isEqualTo("low=1, ok=2, full=1");
    }

    @Test
    void bandBoundariesAreInclusiveOfTwentyAndExclusiveOfOneHundred() {
        List<ElectricScooter> fleet = List.of(
                new StandardScooter("S1", 19),
                new StandardScooter("S2", 20),
                new StandardScooter("S3", 99),
                new StandardScooter("S4", 100));

        assertThat(report.summary(fleet)).isEqualTo("low=1, ok=2, full=1");
    }

    @Test
    void emptyScootersCountAsLow() {
        assertThat(report.summary(List.of(new CargoScooter("C1", 0)))).isEqualTo("low=1, ok=0, full=0");
    }
}
