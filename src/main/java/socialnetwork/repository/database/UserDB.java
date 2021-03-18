package socialnetwork.repository.database;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserDB implements Repository<Long, User> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;
    private Set<User> users;

    public UserDB(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.users = new HashSet<>();
        findAll();
    }


    @Override
    public User findOne(Long aLong) {
        if (aLong == null)
            throw new IllegalArgumentException("id must not be null!");
        for (User user : users) {
            if (user.getId().equals(aLong))
                return user;
        }
        return null;
    }


    @Override
    public Iterable<User> findAll() {
        users.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                String username = resultSet.getString("username");
                String saltHashedPassword = resultSet.getString("password_h");
                String photoPath = resultSet.getString("photo_path");

                User user = new User(firstname, lastname, username, saltHashedPassword,photoPath);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public Iterable<User> getPage(int indexPage) {
        users.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users LIMIT 10 OFFSET " + Integer.toString(indexPage));
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                String username = resultSet.getString("username");
                String saltHashedPassword = resultSet.getString("password_h");
                String photoPath = resultSet.getString("photo_path");

                User user = new User(firstname, lastname, username, saltHashedPassword,photoPath);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public int nextPage(int indexPage) throws Exception{

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS NUMBER FROM users");
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            if (indexPage + 10 < resultSet.getInt("NUMBER")) {
                indexPage += 10;
                return indexPage;
            }
            else throw new Exception("There is no next other page!");

        } catch (SQLException e) {
            e.printStackTrace();
            return indexPage;
        }
    }

    public int prevPage(int indexPage) throws Exception{
        if (indexPage == 0)
            throw new Exception("There is no previous page!");
        else {
            indexPage-=10;
            return indexPage;
        }
    }

    @Override
    public User save(User user) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (firstname,lastname,username,password_h,photo_path) VALUES(?,?,?,?,?)")) {
            if (user == null)
                throw new IllegalArgumentException("entity must not be null");
            validator.validate(user);
            for (User u : findAll()) {
                if (u.getUsername().equals(user.getUsername()))
                    return u;
            }
            statement.setString(1, user.getFirstname());
            statement.setString(2, user.getLastname());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getSaltHashedPassword());
            statement.setString(5,user.getPhotoPath());
            statement.executeUpdate();
            findAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = " + aLong + ";")) {
            if (aLong == null)
                throw new IllegalArgumentException("id must not be null!");
            User toReturn = findOne(aLong);
            statement.executeUpdate();
            findAll();
            return toReturn;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User update(User entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET photo_path=? WHERE id=?")) {
            if (entity == null)
                throw new IllegalArgumentException("entity must not be null");
            validator.validate(entity);
            statement.setString(1, entity.getPhotoPath());
            statement.setLong(2, entity.getId());
            statement.executeUpdate();
            findAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int size() {
        return users.size();
    }
}
