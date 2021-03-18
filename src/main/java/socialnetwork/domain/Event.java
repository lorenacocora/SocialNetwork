package socialnetwork.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Event extends Entity<Long>{

    private String name;
    private String location;
    private LocalDate date;

    public Event(String name, String location, LocalDate date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getDate() {
        return date;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(name, event.name) &&
                Objects.equals(location, event.location) &&
                Objects.equals(date, event.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location, date);
    }

    @Override
    public String toString() {
        return  name + " | Location: " + location + " | Date: " + date;
    }
}
