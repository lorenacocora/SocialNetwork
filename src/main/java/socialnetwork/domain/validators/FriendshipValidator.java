package socialnetwork.domain.validators;
import socialnetwork.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getId().getLeft()==entity.getId().getRight())
            throw new ValidationException("You cannot be your own friend :(\n");
    }
}
