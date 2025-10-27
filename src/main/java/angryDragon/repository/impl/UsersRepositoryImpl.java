package angryDragon.repository.impl;

import angryDragon.domain.user.User;
import angryDragon.repository.UsersRepository;

import java.util.ArrayList;
import java.util.List;

public class UsersRepositoryImpl implements UsersRepository {
    private final List<User> users = new ArrayList<>();

    @Override
    public void addUser(User user){
        users.add(user);
    }

    @Override
    public User findById(String userId){
        for (User user : users){
            if (user.getUserId().equals(userId)){
                return user;
            }
        }
        return null;
    }
}
