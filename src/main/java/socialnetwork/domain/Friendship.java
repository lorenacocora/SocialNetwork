package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;


public class Friendship extends Entity<Tuple<Long,Long>> {

    private LocalDateTime date;
    private Long id1,id2;

    public Friendship(Long id1,Long id2,LocalDateTime date) {
        this.id1=id1;
        this.id2=id2;
        this.date=date;
        setId(new Tuple(id1,id2));
    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Friendship{" + this.id1 + " "+ this.id2+" "+
                "date=" + this.date.format(Constants.DATE_TIME_FORMATTER) +
                '}';
    }
}
