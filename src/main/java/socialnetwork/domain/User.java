package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long>{


    private String firstname;
    private String lastname;
    private String username;
    private String saltHashedPassword;
    private String photoPath;
    private final List<User> friends;

    public User(String firstname, String lastname, String username, String saltHashedPassword, String photoPath) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username=username;
        this.saltHashedPassword=saltHashedPassword;
        this.photoPath=photoPath;
        this.friends=new ArrayList<User>();
    }


    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getUsername() {
        return username;
    }

    public String getSaltHashedPassword() {
        return saltHashedPassword;
    }

    public String getFullname(){return firstname +" "+ lastname;}

    public List<User> getFriends() {
        return friends;
    }

    public String getPhotoPath(){
        return photoPath;
    }

    public void setPhotoPath(String newPhotoPath){
        photoPath=newPhotoPath;
    }

    private String friendsToString(){
        String toReturn="";
        for(User x:friends){
            toReturn+=x.getFirstname();
            toReturn+=" ";
        }
        return toReturn;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstname().equals(that.getFirstname()) &&
                getLastname().equals(that.getLastname()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstname(), getLastname(), getFriends());
    }


    public void setFriend(User friend)
    {
        friends.add(friend);
    }

    public void removeFriend(User friend){
        friends.remove(friend);
    }

    @Override
    public String toString() {
        return "User{" +
                "Name= " + firstname + " " + lastname + ", friends=" + friendsToString() + '}';
    }

    public void clearFriends(){
        friends.clear();
    }

}