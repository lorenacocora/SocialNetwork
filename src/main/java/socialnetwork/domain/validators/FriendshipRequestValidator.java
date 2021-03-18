package socialnetwork.domain.validators;

import socialnetwork.domain.FriendshipRequest;

public class FriendshipRequestValidator implements Validator<FriendshipRequest> {

    @Override
    public void validate(FriendshipRequest entity) throws ValidationException {
       if(entity.getFrom()==entity.getTo())
           throw new ValidationException("You can not be your own friend! :(");
    }
}
