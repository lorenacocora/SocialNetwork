package socialnetwork.domain.validators;

import socialnetwork.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String errors=new String();
        if(!entity.getFirstname().matches("[A-Z]*[a-z]*"))
            errors+="Invalid first name! Only english letters please :)!\n";
        if(!entity.getLastname().matches("[A-Z]*[a-z]*"))
            errors+="Invalid last name! Only english letters please :)!\n";
        if(!entity.getUsername().matches("[A-Z]*[a-z]*"))
            errors+="Invalid username! Only english letters please :)!\n";

        if(entity.getFirstname().length()<3||entity.getFirstname().length()>15)
            errors+="Invalid first name! The name should have between 3 and 15 characters!\n";
        if(entity.getLastname().length()<3||entity.getLastname().length()>15)
            errors+="Invalid last name! The name should have between 3 and 15 characters!\n";
        if(entity.getUsername().length()<3||entity.getUsername().length()>15)
            errors+="Invalid username! The username should have between 3 and 15 characters!\n";

        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
