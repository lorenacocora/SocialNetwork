package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendshipRequestDTO {

    private String from;
    private String to;
    private LocalDateTime date;


    public FriendshipRequestDTO(String from, String to, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipRequestDTO that = (FriendshipRequestDTO) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, date);
    }
}
