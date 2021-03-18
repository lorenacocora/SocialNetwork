package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MessagesDB implements Repository<Long, Message> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;
    private Set<Message> messages;

    public MessagesDB(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.messages = new HashSet<>();
        findAll();
    }

    @Override
    public Message findOne(Long aLong) {
        if (aLong == null)
            throw new IllegalArgumentException("id must not be null!");
        for (Message m : findAll()) {
            if (m.getId().equals(aLong))
                return m;
        }
        return null;
    }


    @Override
    public Iterable<Message> findAll() {
        messages.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long from = resultSet.getLong("from_m");

                List<Long> to = Arrays.asList(resultSet.getString("to_m").split(" ")).stream().map(fId -> Long.parseLong(fId)).collect(Collectors.toList());

                String message = resultSet.getString("message");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_m"));
                Long reply = resultSet.getLong("reply_m");

                Message msg = new Message(from, to, message, date, reply);
                msg.setId(id);
                messages.add(msg);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public Iterable<Message> getPage() {
        return null;
    }


    @Override
    public Message save(Message msg) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO messages (from_m,to_m,message,date_m,reply_m) VALUES(?,?,?,?,?)")) {
            if (msg == null)
                throw new IllegalArgumentException("entity must not be null");
            validator.validate(msg);

            statement.setLong(1, msg.getFrom());

            String toString = new String();
            for (Long friend : msg.getTo()) {
                toString += Long.toString(friend) + " ";
            }

            statement.setString(2, toString);
            statement.setString(3, msg.getMessage());
            statement.setString(4, msg.getDate().toString());
            statement.setLong(5, msg.getReply());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Message delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM messages WHERE id = " + aLong + ";")) {
            if (aLong == null)
                throw new IllegalArgumentException("id must not be null!");
            Message toReturn = findOne(aLong);
            statement.executeUpdate();
            return toReturn;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Message update(Message entity) {

        validator.validate(entity);
        delete(entity.getId());
        return save(entity);
    }

    @Override
    public int size() {
        return messages.size();
    }
}
