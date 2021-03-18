package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {

    private Long id;
    private Long from;
    private List<Long> to;
    private String message;
    private LocalDateTime date;
    private Long reply;

    public Message(Long from, List<Long> to, String message, LocalDateTime date, Long reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply=reply;
    }

    public Message(Long from, List<Long> to, String message, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply=-1L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public List<Long> getTo() {
        return to;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", date=" + date.format(Constants.DATE_TIME_FORMATTER) +
                ", reply=" + reply +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(id, message1.id) &&
                Objects.equals(from, message1.from) &&
                Objects.equals(to, message1.to) &&
                Objects.equals(message, message1.message) &&
                Objects.equals(date, message1.date) &&
                Objects.equals(reply, message1.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFrom(), getTo(), getMessage(), getDate(), getReply());
    }
}

