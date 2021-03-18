package socialnetwork.repository.database;


import socialnetwork.domain.Event_User;
import socialnetwork.domain.Tuple;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class EventsUsersDB implements Repository<Tuple<Long,Long>, Event_User> {

    private final String url;
    private final String username;
    private final String password;
    private Set<Event_User> events_users;
    private int indexFrom = 0;

    public EventsUsersDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.events_users = new HashSet<>();
        findAll();
    }

    @Override
    public Event_User findOne(Tuple<Long,Long> aLong) {
        if (aLong == null)
            throw new IllegalArgumentException("id must not be null!");
        for (Event_User event_user : events_users) {
            if (event_user.getId().getLeft().equals(aLong.getLeft())&&event_user.getId().getRight().equals(aLong.getRight()))
                return event_user;
        }
        return null;
    }


    @Override
    public Iterable<Event_User> findAll() {
        events_users.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM events_users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id_e = resultSet.getLong("id_e");
                Long id_u = resultSet.getLong("id_u");
                Boolean notify = resultSet.getBoolean("notify");

                Event_User event_user = new Event_User(id_e,id_u,notify);
                event_user.setId(new Tuple<>(id_e,id_u));
                events_users.add(event_user);
            }
            return events_users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events_users;
    }

//    public Iterable<Event_User> getPage() {
//        events_users.clear();
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement("SELECT * FROM events_users LIMIT 10 OFFSET " + Integer.toString(indexFrom));
//             ResultSet resultSet = statement.executeQuery()) {
//
//            while (resultSet.next()) {
//                Long id_e = resultSet.getLong("id_e");
//                Long id_u = resultSet.getLong("id_u");
//                Boolean notify = resultSet.getBoolean("notify");
//
//                Event_User event_user = new Event_User(id_e, id_u, notify);
//                event_user.setId(new Tuple<>(id_e, id_u));
//                events_users.add(event_user);
//            }
//            return events_users;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return events_users;
//    }
//
//    public void nextPage() throws Exception {
//
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS NUMBER FROM events_users");
//             ResultSet resultSet = statement.executeQuery()) {
//
//            resultSet.next();
//            if (indexFrom + 10 < resultSet.getInt("NUMBER"))
//                indexFrom += 10;
//            else throw new Exception("There is no next other page!");
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void prevPage() throws Exception {
//        if (indexFrom == 0)
//            throw new Exception("There is no previous page!");
//        else indexFrom -= 10;
//    }

    @Override
    public Event_User save(Event_User event_user) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO events_users (id_e,id_u,notify) VALUES(?,?,?)")) {
            if (event_user == null)
                throw new IllegalArgumentException("entity must not be null");
            for (Event_User eu : findAll()) {
                if (eu.getId().getLeft().equals(event_user.getId().getLeft()) && eu.getId().getRight().equals(event_user.getId().getRight()))
                    return eu;
            }
            statement.setLong(1, event_user.getId().getLeft());
            statement.setLong(2, event_user.getId().getRight());
            statement.setBoolean(3, event_user.getNotify());
            statement.executeUpdate();
            findAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Event_User delete(Tuple<Long,Long> aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM events_users WHERE id_e = " + aLong.getLeft() + " AND id_u = " + aLong.getRight())) {
            if (aLong == null)
                throw new IllegalArgumentException("id must not be null!");
            Event_User toReturn = findOne(aLong);
            statement.executeUpdate();
            findAll();
            return toReturn;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Event_User update(Event_User event_user) {
        Event_User toReturn = delete(event_user.getId());
        if(toReturn != null)
            save(event_user);
        findAll();
        return toReturn;
    }

    @Override
    public int size() {
        return events_users.size();
    }
}
