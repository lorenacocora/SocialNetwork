package socialnetwork.domain.validators;

import socialnetwork.domain.Message;

public class MessageValidator implements Validator<Message>{

    public void validate(Message entity) throws ValidationException{
        String errors=new String();
        if(entity.getTo().isEmpty())
            errors+="The users you want to send messages to do not exits!\n";
        if(entity.getMessage().isEmpty())
            errors+="The message cannot be empty!\n";
        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
