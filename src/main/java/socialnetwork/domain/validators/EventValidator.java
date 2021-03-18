package socialnetwork.domain.validators;
import socialnetwork.domain.Event;

public class EventValidator implements Validator<Event>{

    @Override
    public void validate(Event entity) throws ValidationException {

        String errors=new String();
        if(entity.getName().length()<3||entity.getName().length()>30)
            errors+="Invalid name! The name should have between 3 and 30 characters!\n";
        if(entity.getLocation().length()<3||entity.getLocation().length()>30)
            errors+="Invalid location! The location should have between 3 and 30 characters!\n";

        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
