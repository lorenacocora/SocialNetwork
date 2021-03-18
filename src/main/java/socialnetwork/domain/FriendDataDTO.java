package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;

public class FriendDataDTO {

    private User user;
    private LocalDateTime date;

    public FriendDataDTO(User user, LocalDateTime date) {
        this.user = user;
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public String getFirstname() {return user.getFirstname();}
    public String getLastname() {return user.getLastname();}

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return user.getFirstname() + " | " + user.getLastname() + " | " +date.format(Constants.DATE_TIME_FORMATTER);
    }
}
