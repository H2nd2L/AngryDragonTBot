package angryDragon.business.repository;

import angryDragon.business.domain.user.User;

public interface UsersRepository {
    void addUser(User user);

    User findById(String userId);
}
