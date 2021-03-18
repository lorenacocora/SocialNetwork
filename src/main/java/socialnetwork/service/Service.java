package socialnetwork.service;
import jdk.vm.ci.meta.Local;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.database.*;
import socialnetwork.utils.MyObservable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class Service extends MyObservable {
    private final UserDB repoUsers;
    private final FriendshipDB repoFriendships;
    private final MessagesDB repoMessages;
    private final FriendshipRequestDB repoFriendshipRequest;
    private final EventsDB repoEvents;
    private final EventsUsersDB repoEventsUsers;

    public Service(UserDB repoUsers, FriendshipDB repoFriendships, MessagesDB repoMessages, FriendshipRequestDB repoFriendshipRequest, EventsDB repoEvents, EventsUsersDB repoEventsUsers) {
        this.repoUsers = repoUsers;
        this.repoFriendships=repoFriendships;
        this.repoMessages=repoMessages;
        this.repoFriendshipRequest=repoFriendshipRequest;
        this.repoEvents = repoEvents;
        this.repoEventsUsers = repoEventsUsers;
    }


    /**
     * @param id should not be null
     * @return the user with the given id, null if the users does not exist
     */
    public User getUser(Long id){
        return repoUsers.findOne(id);
    }


    /**
     * @return an arraylist with all the users
     */
    public ArrayList<User> getAllUsers(){
        ArrayList<User> users = new ArrayList<>();
        repoUsers.findAll().forEach(users::add);

        return users;
    }


    /**
     * @return an arraylist with only a page of users
     */
    public ArrayList<User> getPageWithUsers(int indexPage){
        ArrayList<User> users = new ArrayList<>();
        repoUsers.getPage(indexPage).forEach(users::add);

        return users;
    }


    /**
     * @return an arraylist with the next page of users
     * @throws Exception if there is no next page to be fetched
     */
    public ArrayList<User> getNextPageWithUsers(int indexPage) throws Exception {
        indexPage = repoUsers.nextPage(indexPage);
        return getPageWithUsers(indexPage);
    }


    /**
     * @return an arraylist with the previous page of users
     * @throws Exception if there is no previous page to be fetched
     */
    public ArrayList<User> getPrevPageWithUsers(int indexPage) throws Exception {
        indexPage = repoUsers.prevPage(indexPage);
        return getPageWithUsers(indexPage);
    }


    /**
     * adds an user
     * @param user should not be null
     * @return null if the given user is saved, the user otherwise
     * @throws IllegalArgumentException if the given user is null
     * @throws ValidationException if the given user is not valid
     */
    public User addUser(User user) {
        return repoUsers.save(user);
    }


    /**
     * removes an user, his friendships, the messages sent by the user and all the friendship requests from or to him
     * @param id must not be null
     * @return null if the given entity cannot be deleted, the deleted entity otherwise
     * @throws IllegalArgumentException if the given id is null
     */
    public User removeUser(Long id){
        if(id==null)
            throw  new NullPointerException("Id must not be null!");

        reloadFriends();
        User userToDelete= repoUsers.findOne(id);
        User toReturn=repoUsers.delete(id);
        if(userToDelete!=null){
            //deleting the user from the list of friends of his friends
            for( User friend: userToDelete.getFriends()){
                friend.removeFriend(userToDelete);
                Long id1=userToDelete.getId();
                Long id2=friend.getId();
                //deleting the friendships containing the user
                if(repoFriendships.delete(new Tuple<>(id1, id2))==null)
                    repoFriendships.delete(new Tuple<>(id2, id1));
            }

            //deleting the messages that the user sent and deleting the user from the list of receivers where he appears
            List<Message> messages= new ArrayList<>();
            repoMessages.findAll().forEach(messages::add);
            for(Message m:messages){
                if(m.getTo().contains(id)&&m.getTo().size()>1) {
                    m.getTo().remove(id);
                    repoMessages.update(m);
                }
                else if(m.getFrom().equals(id)||(m.getTo().contains(id)&&m.getTo().size()==1))
                    repoMessages.delete(m.getId());

                if(m.getReply().equals(id)) {
                    m.setReply(-1L);
                    repoMessages.update(m);
                }
            }

            //deleting the friendship requests containing the user
            List<FriendshipRequest> requests= new ArrayList<>();
            repoFriendshipRequest.findAll().forEach(requests::add);

            for(FriendshipRequest fr:requests){
                if(fr.getTo().equals(id)||fr.getFrom().equals(id))
                    repoFriendshipRequest.delete(fr.getId());
            }
        }
        return toReturn;
    }


    /**
     * @param firstname should not be null
     * @param lastname should not be null
     * @return null if no user has the given attributes, the user otherwise
     */
    public User getUserByName(String firstname, String lastname){
        for(User x:repoUsers.findAll()){
            if((x.getFirstname().equals(firstname))&&(x.getLastname().equals(lastname)))
                return x;
        }
        return null;
    }


    /**
     * @param username should not be null
     * @return null if no user has the given username, the user otherwise
     */
    public User getUserByUsername(String username){
        for(User x: repoUsers.findAll()){
            if(x.getUsername().equals(username))
                return x;
        }
        return null;
    }


    /**
     * @param u1 should not be null
     * @param u2 should not be null
     * @return null if the friendship request does not exist, the friendship request otherwise
     */
    public FriendshipRequest getRequestByUsers(User u1, User u2){
        for(FriendshipRequest fr: repoFriendshipRequest.findAll()){
            if(fr.getFrom().equals(u1.getId())&&fr.getTo().equals(u2.getId()))
                return fr;
            else if(fr.getFrom().equals(u2.getId())&&fr.getTo().equals(u1.getId()))
                return fr;
        }
        return null;
    }


    /**
     * creates a friendship
     * @param u1 must not be null
     * @param u2 must not be null
     * @return the friendship if it already exists, null otherwise
     * @throws ValidationException if the friendship is not valid
     * @throws IllegalArgumentException if the friendship is null
     */

    public Friendship befriend(User u1,User u2){
        if(u1==null||u2==null)
            throw new NullPointerException("User must not be null!");
        Friendship friendship=new Friendship(u1.getId(),u2.getId(), LocalDateTime.now());
        friendship.setId(new Tuple(u1.getId(),u2.getId()));
        for(Friendship x:repoFriendships.findAll())
        {
            if((x.getId().getLeft().equals(u1.getId())&&x.getId().getRight().equals(u2.getId()))||(x.getId().getLeft().equals(u2.getId())&&x.getId().getRight().equals(u1.getId())))
                return friendship;
        }
        Friendship result=repoFriendships.save(friendship);
        if (result == null) {
            u1.setFriend(u2);
            u2.setFriend(u1);
        }
        return result;
    }



    /**
     * deletes a friendship
     * @param u1 should not be null
     * @param u2 should not be null
     * @return null if the friendship could not be deleted, the friendship otherwise
     * @throws IllegalArgumentException if the id is null
     */
    public Friendship unfriend(User u1, User u2){
        if(u1==null||u2==null)
            throw new NullPointerException("User must not be null!");
        Friendship result= repoFriendships.delete(new Tuple(u1.getId(),u2.getId()));
        if(result==null)
            result=repoFriendships.delete(new Tuple(u2.getId(),u1.getId()));
        if(result!=null){
            u1.removeFriend(u2);
            u2.removeFriend(u1);
            repoFriendshipRequest.delete(getRequestByUsers(u1,u2).getId());
        }
        return result;
    }



    /**
     * creates the bond between friendships and users
     * @throws IllegalArgumentException if the id of the users is null
     */
    public void reloadFriends(){
        for(User user: repoUsers.findAll())
            user.clearFriends();
        for(Friendship friendship: repoFriendships.findAll()){
            Long id1=friendship.getId().getLeft();
            Long id2=friendship.getId().getRight();
            User user1=repoUsers.findOne(id1);
            User user2=repoUsers.findOne(id2);
            user1.setFriend(user2);
            user2.setFriend(user1);
        }
    }


    /**
     * creates a graph and counts the number of isolated groups of users
     * @return the number of isolated groups in the graph
     */
    public int numberOfCommunities(){
        ArrayList<Integer> list=new ArrayList<>();
        Graph graph = getGraph(list);
        return graph.countGroups();
    }

    /**
     * creates a graph and computes the path
     * @return the longest path in the graph
     */
    public ArrayList<Long> getLongestPath() {
        ArrayList<Integer> list=new ArrayList<>();
        Graph graph=getGraph(list);
        ArrayList<Integer> maxPath = graph.getLongestPath();
        ArrayList<Long> pathToReturn=new ArrayList<>();
        maxPath.forEach(vertex->pathToReturn.add(list.get(vertex).longValue()));
        return pathToReturn;
    }


    /**
     * @param list which memorizes users' ids and their correspondent vertex id
     * @return a graph where the users are vertexes and friendships are edges
     */
    private Graph getGraph(ArrayList<Integer> list) {
        int index=0;
        int i1,i2;
        Graph graph= new Graph(repoUsers.size());
        for(Friendship friendship: repoFriendships.findAll()){
            int u1=friendship.getId().getLeft().intValue();
            int u2=friendship.getId().getRight().intValue();

            if(!list.contains(u1)){
                i1=index;
                index++;
                list.add(u1);
            }
            else{
                i1=list.lastIndexOf(u1);
            }
            if(!list.contains(u2)){
                i2=index;
                index++;
                list.add(u2);
            }
            else{
                i2= list.lastIndexOf(u2);
            }
            graph.addRelation(i1,i2);
        }
        return graph;
    }


    /**
     * creates a list containing the friends of the given user, using stream
     * @param user should not be null
     * @return a list of DTO Friendships
     */
    public ArrayList<FriendDataDTO> getFriendshipsByUser(User user){
        Long idUser= getUserByName(user.getFirstname(),user.getLastname()).getId();
        ArrayList<Friendship> friendships=new ArrayList<>();
        repoFriendships.findAll().forEach(friendships::add);

        ArrayList<FriendDataDTO> friendDataDTOList = (ArrayList<FriendDataDTO>) friendships.stream()
                .filter(f->f.getId().getLeft().equals(idUser))
                .map(f->new FriendDataDTO(repoUsers.findOne(f.getId().getRight()),f.getDate()))
                .collect(Collectors.toList());

        friendDataDTOList.addAll(friendships.stream()
                .filter(f->f.getId().getRight().equals(idUser))
                .map(f->new FriendDataDTO(repoUsers.findOne(f.getId().getLeft()),f.getDate()))
                .collect(Collectors.toList()));

        return friendDataDTOList;
    }

    public ArrayList<FriendDataDTO> getPageWithFriendshipsByUser(User user, int indexPage){
        Long idUser=getUserByName(user.getFirstname(),user.getLastname()).getId();
        ArrayList<Friendship> friendships=new ArrayList<>();
        repoFriendships.getPage(idUser,indexPage).forEach(friendships::add);

        ArrayList<FriendDataDTO> friendDataDTOList = (ArrayList<FriendDataDTO>) friendships.stream()
                .filter(f->f.getId().getLeft().equals(idUser))
                .map(f->new FriendDataDTO(repoUsers.findOne(f.getId().getRight()),f.getDate()))
                .collect(Collectors.toList());

        friendDataDTOList.addAll(friendships.stream()
                .filter(f->f.getId().getRight().equals(idUser))
                .map(f->new FriendDataDTO(repoUsers.findOne(f.getId().getLeft()),f.getDate()))
                .collect(Collectors.toList()));

        return friendDataDTOList;
    }

    public ArrayList<FriendDataDTO> getNextPageWithFriendshipsByUser(User user, int indexPage) throws Exception {
        Long idUser=getUserByName(user.getFirstname(),user.getLastname()).getId();
        indexPage = repoFriendships.nextPage(idUser, indexPage);
        return getPageWithFriendshipsByUser(user, indexPage);
    }

    public ArrayList<FriendDataDTO> getPrevPageWithFriendshipsByUser(User user, int indexPage) throws Exception {
        indexPage = repoFriendships.prevPage(indexPage);
        return getPageWithFriendshipsByUser(user,indexPage);
    }

    /**
     * creates a list containing the friends of the given user that became friends on a given month, using stream()
     * @param start should not be null
     * @param end should not be null
     * @param user should not be null
     * @return an array of all the friendships created in the given interval of time by the given user
     */
    public ArrayList<FriendDataDTO> getFriendshipsByPeriod(LocalDate start, LocalDate end, User user){

        return (ArrayList<FriendDataDTO>) getFriendshipsByUser(user).stream()
                .filter(fr->fr.getDate().toLocalDate().isAfter(start) && fr.getDate().toLocalDate().isBefore(end))
                .collect(Collectors.toList());
    }

    /**
     * @param start should not be null
     * @param end should not be null
     * @param user should not be null
     * @return an array of all the messages sent and received in the given interval of time by the given user
     */
    public ArrayList<Message> getMessagesByPeriod(LocalDate start, LocalDate end, User user){

        return (ArrayList<Message>) getMessagesByUser(user).stream()
                .filter(m->!m.getFrom().equals(user.getId()))
                .filter(m->m.getDate().toLocalDate().isAfter(start) && m.getDate().toLocalDate().isBefore(end))
                .collect(Collectors.toList());
    }

    /**
     * @param start should not be null
     * @param end should not be null
     * @param user should not be null
     * @param sender should not be null
     * @return an array of all the messages sent by the sender to the user in the given interval of time
     */
    public ArrayList<Message> getMessagesByPeriodAndSender(LocalDate start, LocalDate end, User user, User sender){

        return (ArrayList<Message>) getMessagesByUser(user).stream()
                .filter(m->m.getFrom().equals(sender.getId())&&m.getDate().toLocalDate().isAfter(start)&&m.getDate().toLocalDate().isBefore(end))
                .collect(Collectors.toList());
    }

    /**
     * @param user should not be null
     * @return a list containing the messages of the given user (sent and received)
     */
    public ArrayList<Message> getMessagesByUser(User user){
        Long idUser=getUserByName(user.getFirstname(), user.getLastname()).getId();
        ArrayList<Message> messages=new ArrayList<>();
        repoMessages.findAll().forEach(messages::add);

        return (ArrayList<Message>) messages.stream()
                .filter(m->m.getFrom().equals(idUser)||m.getTo().contains(idUser))
                .collect(Collectors.toList());
    }


    /**
     * sends a message
     * @param user should not be null
     * @param friends should not be null
     * @param msg should not be null
     * @param date should not be null
     * @return null if the message was sent, the message otherwise
     */
    public Message sendMessage(User user, ArrayList<User> friends, String msg, LocalDateTime date)  {
        if(user==null)
            throw new NullPointerException("User must not be null!");

        ArrayList<Long> friendsId=new ArrayList<>();
        for(User f : friends)
        {
            if(getUserByName(f.getFirstname(),f.getLastname())!=null)
                friendsId.add(getUserByName(f.getFirstname(), f.getLastname()).getId());
        }
        Message msgToSend= new Message(getUserByName(user.getFirstname(),user.getLastname()).getId(),friendsId,msg,date);
        Message result = repoMessages.save(msgToSend);
        notifyObservers();
        return result;
    }


    /**
     * creates a list of the users that have sent messages to the given user
     * @param user should not be null
     * @return a list of users
     */
    private ArrayList<User> getFriendsToReplyTo(User user){
        Long idUser = getUserByName(user.getFirstname(),user.getLastname()).getId();
        ArrayList<Message> messages=new ArrayList<>();
        repoMessages.findAll().forEach(messages::add);

        return (ArrayList<User>) messages.stream().filter(m->m.getTo().contains(idUser))
                .map(m->repoUsers.findOne(m.getFrom()))
                .collect(Collectors.toList());
    }

    /**
     * sends the message to every user that has sent a message to the given user
     * @param user should not be null
     * @param msg should not be null
     * @param date should not be null
     * @return null if the message was sent, the message otherwise
     */
    public Message sendMessageToAll(User user, String msg, LocalDateTime date){
        if(user==null)
            throw new NullPointerException("User must not be null!");

        return sendMessage(user,getFriendsToReplyTo(user),msg,date);
    }


    /**
     * replies to a message if the given message id exists
     * @param messageid should not be null
     * @param user should not be null
     * @param message should not be null
     * @param date should not be null
     * @return null if the was sent, the message otherwise
     * @throws NullPointerException
     */
    public Message replyMessage(Long messageid, User user, String message, LocalDateTime date) throws NullPointerException{
        Message toReplyTo=repoMessages.findOne(messageid);
        if(toReplyTo==null)
            throw new NullPointerException("You can't reply to a message that doesnt exits!");

        if(!toReplyTo.getFrom().equals(getUserByName(user.getFirstname(), user.getLastname()).getId())||!toReplyTo.getTo().contains(getUserByName(user.getFirstname(), user.getLastname())))
            return toReplyTo;

        Message msgToSend= new Message(getUserByName(user.getFirstname(), user.getLastname()).getId(), Collections.singletonList(toReplyTo.getFrom()),message,date,toReplyTo.getId());
        return repoMessages.save(msgToSend);
    }


    /**
     * creates a sorted list with the conversation between 2 given users
     * @param u1 should not be null
     * @param u2 should not be null
     * @return a list of messages
     */
    public ArrayList<Message> getConversation(User u1, User u2){
        Long id1=getUserByName(u1.getFirstname(),u1.getLastname()).getId();
        Long id2=getUserByName(u2.getFirstname(), u2.getLastname()).getId();

        ArrayList<Message> messages=new ArrayList<>();
        repoMessages.findAll().forEach(messages::add);

        return (ArrayList<Message>) messages.stream()
                .filter(m->m.getFrom().equals(id1)||m.getFrom().equals(id2))
                .filter(m->m.getTo().contains(id1)||m.getTo().contains(id2))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());
    }


    /**
     * creates a list of the friendship requests of the given user that are pending
     * @param user should not be null
     * @return a list of friendship requests DTO
     */
    public ArrayList<FriendshipRequestDTO> getRequestsByUser(User user){
        Long idUser=getUserByName(user.getFirstname(),user.getLastname()).getId();
        ArrayList<FriendshipRequest> requests=new ArrayList<>();
        repoFriendshipRequest.findAll().forEach(requests::add);
        ArrayList<FriendshipRequestDTO> requestsDTO=new ArrayList<>();

        requests.stream()
                .filter(r->r.getFrom().equals(idUser)||r.getTo().equals(idUser))
                .filter(r->r.getStatus().equals("pending"))
                .forEach(r->requestsDTO.add(new FriendshipRequestDTO(repoUsers.findOne(r.getFrom()).getFullname(),repoUsers.findOne(r.getTo()).getFullname(),r.getDate())));

        return requestsDTO;
    }


    /**
     * @param user should not be null
     * @return an arraylist of only one page of the friendship requests of the given user
     */
    public ArrayList<FriendshipRequestDTO> getPageWithRequestsByUsers(User user, int indexPage){
        Long idUser=getUserByName(user.getFirstname(),user.getLastname()).getId();
        ArrayList<FriendshipRequestDTO> requestsDTO=new ArrayList<>();
        repoFriendshipRequest.getPage(idUser,indexPage)
                .forEach(r->requestsDTO.add(new FriendshipRequestDTO(repoUsers.findOne(r.getFrom()).getFullname(),repoUsers.findOne(r.getTo()).getFullname(),r.getDate())));

        return requestsDTO;
    }


    /**
     * @param user should not be null
     * @return an arraylist of the next page of the friendship requests of the given user
     */
    public ArrayList<FriendshipRequestDTO> getNextPageWithRequestsByUsers(User user, int indexPage) throws Exception {
        Long idUser=getUserByName(user.getFirstname(),user.getLastname()).getId();
        indexPage = repoFriendshipRequest.nextPage(idUser, indexPage);
        return getPageWithRequestsByUsers(user, indexPage);
    }


    /**
     * @param user should not be null
     * @return an arraylist of the next page of the friendship requests of the given user
     */
    public ArrayList<FriendshipRequestDTO> getPrevPageWithRequestsByUsers(User user, int indexPage) throws Exception {
        indexPage = repoFriendshipRequest.prevPage(indexPage);
        return getPageWithRequestsByUsers(user, indexPage);
    }


    /**
     * sends a friendship request with the status pending
     * @param u1 should not be null
     * @param u2 should not be null
     * @return the friendship request if it already exists, null if the friendship request is sent
     */
    public FriendshipRequest sendFriendshipRequest(User u1, User u2){
        Long from=getUserByName(u1.getFirstname(),u1.getLastname()).getId();
        Long to=getUserByName(u2.getFirstname(),u2.getLastname()).getId();

        if(getRequestByUsers(u1,u2)!=null)
            return getRequestByUsers(u1,u2);

        FriendshipRequest fr=new FriendshipRequest(from,to,"pending",LocalDateTime.now());
        repoFriendshipRequest.save(fr);
        return null;
    }


    /**
     * updates the friendship request according to the response, befriends the user with the sender if the response is yes
     * @param idRequest should not be null
     * @param user should not be null
     * @param response should not be null
     * @return the friendship request if it succeeds in being updated to approved or rejected, null if it doesn't exist or if it doesn't succeed
     * @throws Exception if the request is not for the user or if the status is not pending
     */
    public FriendshipRequest respondRequest(Long idRequest, User user, String response) throws Exception {
        Long userId = getUserByName(user.getFirstname(), user.getLastname()).getId();
        FriendshipRequest toRespondTo = repoFriendshipRequest.findOne(idRequest);

        if (toRespondTo == null)
            return null;
        else if (!toRespondTo.getTo().equals(userId))
            throw new Exception("You cannot respond to a friendship request sent by you!");

        User friend = repoUsers.findOne(repoFriendshipRequest.findOne(idRequest).getFrom());
        if (response.equals("Yes")) {
            Friendship friendship = befriend(friend, user);
            if (friendship == null) {
                toRespondTo.setStatus("approved");
                repoFriendshipRequest.update(toRespondTo);
                notifyObservers();
                return toRespondTo;
            }
            else return null;
        }
        else{
            toRespondTo.setStatus("rejected");
            repoFriendshipRequest.update(toRespondTo);
            return toRespondTo;
        }
    }


    /**
     * deletes a friendship request
     * @param idRequest should not be null
     * @param user should not be null
     * @return null if the friendship request could not be deleted, the request otherwise
     */
    public FriendshipRequest cancelRequest(Long idRequest, User user) throws Exception{

        Long userId = getUserByName(user.getFirstname(), user.getLastname()).getId();
        FriendshipRequest toCancel = repoFriendshipRequest.findOne(idRequest);

        if(toCancel==null)
            return null;
        else if (!toCancel.getFrom().equals(userId))
            throw new Exception("You cannot cancel a friendship request that was not sent by you!");

        return repoFriendshipRequest.delete(idRequest);
    }


    /**
     * @param start should not be null
     * @param end should not be null
     * @param user should not be null
     * @return a map with the people and the number of messages they have sent to the given user
     */
    public Map<String, Integer> getRaportNumberOfMessagesBySender(LocalDate start, LocalDate end, User user){

        Map<String, Integer> mapMessages = new HashMap<>();
        ArrayList<Message> messages = getMessagesByPeriod(start, end, user);

        for(Message m : messages){
            mapMessages.putIfAbsent(getUser(m.getFrom()).getFullname(),0);
            mapMessages.put(getUser(m.getFrom()).getFullname(),mapMessages.get(getUser(m.getFrom()).getFullname())+1);
        }

        return mapMessages;

    }

    /**
     * @param start should not be null
     * @param end should not be null
     * @param user should not be null
     * @param sender should not be null
     * @return a map with the dates and the number of messages that have been sent in that day to the given user by the given sender
     */
    public Map<LocalDate, Integer> getRaportNumberOfMessagesByDate(LocalDate start, LocalDate end, User user, User sender){

        Map<LocalDate, Integer> mapMessages = new HashMap<>();
        ArrayList<Message> messages = getMessagesByPeriodAndSender(start, end, user,sender);

        for(Message m : messages){
            mapMessages.putIfAbsent(m.getDate().toLocalDate(),0);
            mapMessages.put(m.getDate().toLocalDate(),mapMessages.get(m.getDate().toLocalDate())+1);
        }

        return mapMessages;

    }


    /**
     * @return an arraylist containing al the events
     */
    public ArrayList<Event> getAllEvents(){
        ArrayList<Event> events = new ArrayList<>();
        repoEvents.findAll().forEach(events::add);

        return events;
    }


    /**
     * @return an arraylist with only a page of events
     */
    public ArrayList<Event> getPageWithEvents(int indexPage){
        ArrayList<Event> events = new ArrayList<>();
        repoEvents.getPage(indexPage).forEach(events::add);

        return events;
    }


    /**
     * @return an arraylist with the next page of events
     * @throws Exception if there is no next page to be fetched
     */
    public ArrayList<Event> getNextPageWithEvents(int indexPage) throws Exception {
        indexPage = repoEvents.nextPage(indexPage);
        return getPageWithEvents(indexPage);
    }


    /**
     * @return an arraylist with the previous page of events
     * @throws Exception if there is no previous page to be fetched
     */
    public ArrayList<Event> getPrevPageWithEvents(int indexPage) throws Exception {
        indexPage = repoEvents.prevPage(indexPage);
        return getPageWithEvents(indexPage);
    }


    /**
     * @param name should not be null
     * @param location should not be null
     * @param date_e should not be null
     * @return the event if it already exists, null otherwise
     */
    public Event addEvent(String name, String location, LocalDate date_e){
        Event event= new Event(name,location,date_e);
        return repoEvents.save(event);
    }


    /**
     * @param name should not be null
     * @param location should not be null
     * @param date_e should not be null
     * @return the event with the given name, location and date is it exists, null otherwise
     */
    public Event getEventByNameLocationDate(String name, String location, LocalDate date_e){
        for(Event event : repoEvents.findAll()){
            if(event.getName().equals(name)&&event.getLocation().equals(location)&&event.getDate().equals(date_e))
                return event;
        }
        return null;
    }


    /**
     * @param event should not be null
     * @param user should not be null
     * @return the event_user if it already exists, null otherwise
     */
    public Event_User goingToEvent(Event event, User user){
        Event_User event_user= new Event_User(event.getId(),user.getId(),true);
        return repoEventsUsers.save(event_user);
    }


    /**
     * @param event should not be null
     * @param user should not be null
     * @return the event_user if it is deleted, null otherwise
     */
    public Event_User notGoingToEvent(Event event, User user){
        return repoEventsUsers.delete(new Tuple<>(event.getId(), user.getId()));
    }

    /**
     * @param event should not be null
     * @param user should not be null
     * @return the event_user if it is updated, null otherwise
     */
    public Event_User muteEvent(Event event, User user){
        Event_User event_user = new Event_User(event.getId(), user.getId(), false);
        return repoEventsUsers.update(event_user);
    }

    /**
     * @param event should not be null
     * @param user should not be null
     * @return the event_user if it is updated, null otherwise
     */
    public Event_User unmuteEvent(Event event, User user){
        Event_User event_user = new Event_User(event.getId(), user.getId(), true);
        return repoEventsUsers.update(event_user);
    }


    /**
     * @param user should be null
     * @return an arraylist of the events that the given user is going to and that are notifiable
     */
    public ArrayList<Event> getEventsToNotifyByUser(User user){

        ArrayList<Event_User> events_users = new ArrayList<>();
        repoEventsUsers.findAll().forEach(events_users::add);

        ArrayList<Event> events = new ArrayList<>();

        events_users.stream()
                .filter(eu->eu.getNotify()==true)
                .filter(eu->eu.getId().getRight().equals(user.getId()))
                .forEach(eu->events.add(repoEvents.findOne(eu.getId().getLeft())));


        ArrayList<Event> toReturn = (ArrayList<Event>) events.stream()
                .filter(e->e.getDate().isBefore(LocalDate.now().plusDays(7))&&e.getDate().isAfter(LocalDate.now().minusDays(1)))
                .collect(Collectors.toList());

        return toReturn;

    }


    /**
     * @param user should be null
     * @return an arraylist of the events that the given user is going to
     */
    public ArrayList<Event> getEventsByUser(User user){

        ArrayList<Event_User> events_users = new ArrayList<>();
        repoEventsUsers.findAll().forEach(events_users::add);

        ArrayList<Event> events = new ArrayList<>();

        events_users.stream()
                .filter(eu->eu.getId().getRight().equals(user.getId()))
                .forEach(eu->events.add(repoEvents.findOne(eu.getId().getLeft())));

        return events;

    }


    /**
     * @param event should not be null
     * @param user shouold not be null
     * @return true if the event is notifiable, false otherwise
     */
    public Boolean isEventNotifiable(Event event, User user){
        return repoEventsUsers.findOne(new Tuple<>(event.getId(), user.getId())).getNotify();
    }

    /**
     * @param newUser should not be null
     * @return null if the user is updated, the user otherwise
     */
    public User updateUser(User newUser){
        return repoUsers.update(newUser);
    }

}
