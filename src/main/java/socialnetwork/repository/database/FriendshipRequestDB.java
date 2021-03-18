package socialnetwork.repository.database;


import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class FriendshipRequestDB implements Repository<Long, FriendshipRequest> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<FriendshipRequest> validator;
    private Set<FriendshipRequest> requests;

    public FriendshipRequestDB(String url, String username, String password, Validator<FriendshipRequest> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.requests = new HashSet<>();
        findAll();
    }


    @Override
    public FriendshipRequest findOne(Long aLong) {
        if(aLong==null)
            throw new IllegalArgumentException("id must not be null!");
        for(FriendshipRequest fr : findAll()){
            if(fr.getId().equals(aLong))
                return fr;
        }
        return null;
    }


    @Override
    public Iterable<FriendshipRequest> findAll() {
        requests.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM requests");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long from = resultSet.getLong("from_r");
                Long to = resultSet.getLong("to_r");
                String status = resultSet.getString("status");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_r"));

                FriendshipRequest fr=new FriendshipRequest(from,to,status,date);
                fr.setId(id);
                requests.add(fr);
            }
            return requests;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public Iterable<FriendshipRequest> getPage(Long userId, int indexPage) {
        requests.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM requests WHERE status LIKE 'pending' AND (from_r="+userId+" OR to_r="+userId+") LIMIT 10 OFFSET "+Integer.toString(indexPage));
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long from = resultSet.getLong("from_r");
                Long to = resultSet.getLong("to_r");
                String status = resultSet.getString("status");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_r"));

                FriendshipRequest fr=new FriendshipRequest(from,to,status,date);
                fr.setId(id);
                requests.add(fr);
            }
            return requests;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public int nextPage(Long userId, int indexPage) throws Exception {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS NUMBER FROM requests WHERE (from_r="+userId+" OR to_r="+userId+") AND status LIKE 'pending'");
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            if (indexPage + 10 < resultSet.getInt("NUMBER"))
            {
                indexPage+=10;
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
            indexPage-=10;
            return indexPage;
        }
    }

    @Override
    public FriendshipRequest save(FriendshipRequest fr) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO requests (from_r,to_r,status,date_r) VALUES(?,?,?,?)"))
        {
            if(fr==null)
                throw new IllegalArgumentException("entity must not be null");
            validator.validate(fr);
            statement.setLong(1,fr.getFrom());
            statement.setLong(2,fr.getTo());
            statement.setString(3,fr.getStatus());
            statement.setString(4,fr.getDate().toString());
            statement.executeUpdate();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public FriendshipRequest delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM requests WHERE id = " + aLong + ";"))
        {
            if(aLong==null)
                throw new IllegalArgumentException("id must not be null!");
            FriendshipRequest toReturn=findOne(aLong);
            statement.executeUpdate();
            return toReturn;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public FriendshipRequest update(FriendshipRequest entity) {
        validator.validate(entity);
        delete(entity.getId());
        return save(entity);
    }

    @Override
    public int size() {
        return requests.size();
    }
}
