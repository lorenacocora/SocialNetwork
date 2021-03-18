package socialnetwork.repository.database;

import socialnetwork.domain.Event;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class EventsDB implements Repository<Long, Event> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Event> validator;
    private Set<Event> events;

    public EventsDB(String url, String username, String password, Validator<Event> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.events = new HashSet<>();
        findAll();
    }


    @Override
    public Event findOne(Long aLong) {
        if (aLong == null)
            throw new IllegalArgumentException("id must not be null!");
        for (Event event : events) {
            if (event.getId().equals(aLong))
                return event;
        }
        return null;
    }


    @Override
    public Iterable<Event> findAll() {
        events.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM events");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String location = resultSet.getString("location");
                LocalDate date_e = LocalDate.parse(resultSet.getString("date_e"));

                Event event = new Event(name, location, date_e);
                event.setId(id);
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public Iterable<Event> getPage(int indexPage) {
        events.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM events LIMIT 10 OFFSET " + Integer.toString(indexPage));
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String location = resultSet.getString("location");
                LocalDate date_e = LocalDate.parse(resultSet.getString("date_e"));

                Event event = new Event(name, location, date_e);
                event.setId(id);
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public int nextPage(int indexPage) throws Exception {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS NUMBER FROM events");
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            if (indexPage + 10 < resultSet.getInt("NUMBER"))
            {
                indexPage += 10;
                return indexPage;
            }

            else throw new Exception("There is no next other page!");

        } catch (SQLException e) {
            e.printStackTrace();
            return indexPage;
        }
    }

    public int prevPage(int indexPage) throws Exception {
        if (indexPage == 0)
            throw new Exception("There is no previous page!");
        else {
            indexPage -= 10;
            return indexPage;
        }

    }

    @Override
    public Event save(Event event) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO events (name,location,date_e) VALUES(?,?,?)")) {
            if (event == null)
                throw new IllegalArgumentException("entity must not be null");
            validator.validate(event);
            for (Event e : findAll()) {
                if (e.getName().equals(event.getName()) && e.getLocation().equals(event.getLocation()) && e.getDate().equals(event.getDate()))
                    return e;
            }
            statement.setString(1, event.getName());
            statement.setString(2, event.getLocation());
            statement.setString(3, event.getDate().toString());
            statement.executeUpdate();
            findAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Event delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM events WHERE id = " + aLong + ";")) {
            if (aLong == null)
                throw new IllegalArgumentException("id must not be null!");
            Event toReturn = findOne(aLong);
            statement.executeUpdate();
            findAll();
            return toReturn;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Event update(Event entity) {
        validator.validate(entity);
        delete(entity.getId());
        Event toReturn = save(entity);
        findAll();
        return toReturn;
    }

    @Override
    public int size() {
        return events.size();
    }
}
