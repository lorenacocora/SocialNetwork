package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendshipRequest extends Entity<Long>  {

    private Long from;
    private Long to;
    private String status;
    private LocalDateTime date;


    public FriendshipRequest(Long from, Long to, String status, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.date=date;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDate() {return date;}

    @Override
    public String toString() {
        return "FriendshipRequest{" +
                "id=" + getId() +
                ", from=" + from +
                ", to=" + to +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipRequest that = (FriendshipRequest) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, status);
    }
}
