package com.driver;

import java.util.*;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String, User> userMap;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository() {
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMap = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public boolean isNewUser(String mobile) {
        if (userMap.containsKey(mobile)) return false;
        return true;
    }

    public void createUser(String name, String mobile) {
        userMap.put(mobile, new User(name, mobile));
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if (!groupUserMap.containsKey(group)) {
            throw new Exception("Group not exist");
        }
        if (!adminMap.get(group).equals(approver)) {
            throw new Exception("Approver don't have permissions");
        }
        if (!userExist(group, user)) {
            throw new Exception("User is not in group");
        }

        adminMap.put(group, user);
        return "SUCCESS";
    }

    public boolean userExist(Group group, User sender) {
        List<User> users = new ArrayList<>();
        for (User user : users) {
            if (user.equals(sender)) return true;
        }

        return false;
    }

    public Group createGroup(List<User> users) {
        if (users.size() == 2) return createPersonalChat(users);

        customGroupCount++;
        String grpName = "Group" + customGroupCount;
        Group group = new Group(grpName, users.size());
        groupUserMap.put(group, (List<User>) users.get(0));
        return group ;
    }

    public Group createPersonalChat (List<User>users){
        String grpName = users.get(1).getName() ;
        Group personalGroup = new Group(grpName,2) ;
        groupUserMap.put(personalGroup,users) ;
        return personalGroup ;
    }
    public int createMessage(String content){
        messageId++ ;
        Message message = new Message(messageId,content,new Date()) ;
        return messageId ;
    }

    public int SendMessage (Message message , User sender ,Group group) throws Exception{
       if(!groupUserMap.containsKey(group)) throw new Exception("Group deos not exist") ;
       if(userExist(group,sender)) throw new Exception("You are not allowed to send message") ;


       List<Message> messages = new ArrayList<>() ;
       if(groupMessageMap.containsKey(group)) messages = groupMessageMap.get(group) ;

       messages.add(message) ;
       groupMessageMap.put(group,messages) ;
       return messages.size() ;

    }
}
