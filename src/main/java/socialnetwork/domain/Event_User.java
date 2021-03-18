package socialnetwork.domain;

import java.util.Objects;

public class Event_User extends Entity<Tuple<Long,Long>> {

    private Long id_e, id_u;
    private boolean notify;


    public Event_User(Long id_e, Long id_u, boolean notify) {
        this.id_e = id_e;
        this.id_u = id_u;
        this.notify = notify;
        setId(new Tuple<>(id_e,id_u));
    }


    public boolean getNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event_User that = (Event_User) o;
        return notify == that.notify &&
                Objects.equals(id_e, that.id_e) &&
                Objects.equals(id_u, that.id_u);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_e, id_u, notify);
    }

    @Override
    public String toString() {
        return "Event_User{" +
                "id_e=" + id_e +
                ", id_u=" + id_u +
                ", notify=" + notify +
                '}';
    }
}
