package socialnetwork.repository.database;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipDB implements Repository<Tuple<Long,Long>, Friendship> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;
    private  Set<Friendship> friendships;

    public FriendshipDB(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.friendships = new HashSet<>();
        findAll();
    }

    @Override
    public Friendship findOne(Tuple<Long,Long> aLong) {
        if(aLong==null)
            throw new IllegalArgumentException("id must not be null!");
        for(Friendship fr:friendships){
            if((fr.getId().getLeft().equals(aLong.getLeft())&&fr.getId().getRight().equals(aLong.getRight()))||(fr.getId().getLeft().equals(aLong.getRight())&&fr.getId().getRight().equals(aLong.getLeft())))
                    return fr;
        }
        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        friendships.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long u1 = resultSet.getLong("user1");
                Long u2 = resultSet.getLong("user2");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_f"));
                Friendship friendship = new Friendship(u1,u2,date);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    public Iterable<Friendship> getPage(Long userId,int indexPage) {
        friendships.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE user1="+userId+" OR user2="+userId+" LIMIT 10 OFFSET "+indexPage);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long u1 = resultSet.getLong("user1");
                Long u2 = resultSet.getLong("user2");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_f"));
                Friendship friendship = new Friendship(u1,u2,date);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    public int nextPage(Long userId, int indexPage) throws Exception {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS NUMBER FROM friendships WHERE user1="+userId+" OR user2="+userId);
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
        else{
            indexPage-=10;
            return indexPage;
        }
    }

    @Override
    public Friendship save(Friendship friendship) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships (user1,user2,date_f) VALUES(?,?,?)"))
        {
            if(friendship==null)
                throw new IllegalArgumentException("entity must not be null");
            validator.validate(friendship);
            statement.setLong(1,friendship.getId().getLeft());
            statement.setLong(2,friendship.getId().getRight());
            statement.setString(3,friendship.getDate().toString());
            statement.executeUpdate();
            findAll();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Friendship delete(Tuple<Long,Long> aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE user1 = " + aLong.getLeft()
                     + " AND user2 = " + aLong.getRight() + " OR user1 = " + aLong.getRight() + " AND user2 = " + aLong.getLeft() + ";"))
        {
            if(aLong==null)
                throw new IllegalArgumentException("id must not be null");
            Friendship toReturn=findOne(aLong);
            if(toReturn==null)
                return null;

            statement.executeUpdate();
            findAll();
            return toReturn;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Friendship update(Friendship entity) {
        validator.validate(entity);
        delete(entity.getId());
        Friendship toReturn = save(entity);
        findAll();
        return toReturn;
    }

    @Override
    public int size() {
        return friendships.size();
    }
}
