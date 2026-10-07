package za.co.wethinkcode.scootshare.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RiderTest {

    private static Field field(String name) throws NoSuchFieldException {
        return Rider.class.getDeclaredField(name);
    }

    @Test
    void allFieldsArePrivate() throws NoSuchFieldException {
        assertThat(Modifier.isPrivate(field("name").getModifiers())).as("name").isTrue();
        assertThat(Modifier.isPrivate(field("email").getModifiers())).as("email").isTrue();
        assertThat(Modifier.isPrivate(field("tripIds").getModifiers())).as("tripIds").isTrue();
    }

    @Test
    void nameAndEmailCannotBeReassigned() throws NoSuchFieldException {
        assertThat(Modifier.isFinal(field("name").getModifiers())).as("name is final").isTrue();
        assertThat(Modifier.isFinal(field("email").getModifiers())).as("email is final").isTrue();
    }

    @Test
    void constructorRejectsMissingOrBlankName() {
        assertThatThrownBy(() -> new Rider(null, "a@b.co.za")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Rider("   ", "a@b.co.za")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructorRejectsMissingOrBlankEmail() {
        assertThatThrownBy(() -> new Rider("Ann", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Rider("Ann", "")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nameAndEmailAreTrimmedAndEmailIsLowerCased() {
        Rider rider = new Rider("  Ann Smith ", "  Ann@Mail.CO.za ");
        assertThat(rider.getName()).isEqualTo("Ann Smith");
        assertThat(rider.getEmail()).isEqualTo("ann@mail.co.za");
    }

    @Test
    void addTripRecordsTheTrip() {
        Rider rider = new Rider("Ann", "ann@mail.co.za");
        rider.addTrip("T1");
        rider.addTrip("T2");
        assertThat(rider.getTripIds()).containsExactly("T1", "T2");
    }

    @Test
    void addTripRejectsNullOrBlankIds() {
        Rider rider = new Rider("Ann", "ann@mail.co.za");
        assertThatThrownBy(() -> rider.addTrip(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> rider.addTrip(" ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void callersCannotModifyTheTripListThroughTheGetter() {
        Rider rider = new Rider("Ann", "ann@mail.co.za");
        rider.addTrip("T1");

        assertThatThrownBy(() -> rider.getTripIds().add("HACKED"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(rider.getTripIds()).containsExactly("T1");
    }

    @Test
    void getterReturnsASnapshotNotALiveView() {
        Rider rider = new Rider("Ann", "ann@mail.co.za");
        rider.addTrip("T1");
        List<String> before = rider.getTripIds();

        rider.addTrip("T2");

        assertThat(before).containsExactly("T1");
        assertThat(rider.getTripIds()).containsExactly("T1", "T2");
    }

    @Test
    void ridersWithTheSameEmailAreEqual() {
        Rider a = new Rider("Ann", "ann@mail.co.za");
        Rider b = new Rider("Anna S", " ANN@mail.co.za");

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
    }

    @Test
    void ridersWithDifferentEmailsAreNotEqual() {
        assertThat(new Rider("Ann", "ann@mail.co.za")).isNotEqualTo(new Rider("Ann", "other@mail.co.za"));
    }
}
