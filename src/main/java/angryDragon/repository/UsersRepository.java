package angryDragon.repository;

import angryDragon.domain.user.User;

public interface UsersRepository {
    void addUser(User user);

    User findById(String userId);
}
