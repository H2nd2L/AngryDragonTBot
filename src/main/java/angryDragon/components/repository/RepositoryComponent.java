package angryDragon.components.repository;

import angryDragon.repository.PetRepository;
import angryDragon.repository.UsersRepository;
import angryDragon.repository.AllExistingItemsRepository;
import angryDragon.repository.WalletsRepository;
import angryDragon.repository.impl.AllExistingItemsRepositoryImpl;
import angryDragon.repository.impl.PetRepositoryImpl;
import angryDragon.repository.impl.UsersRepositoryImpl;
import angryDragon.repository.impl.WalletsRepositoryImpl;


public class RepositoryComponent {
    private final UsersRepository usersRepository;
    private final AllExistingItemsRepository allExistingItemsRepository;
    private final WalletsRepository walletsRepository;
    private final PetRepository petRepository;

    public RepositoryComponent() {
        this.usersRepository = new UsersRepositoryImpl();
        this.allExistingItemsRepository = new AllExistingItemsRepositoryImpl();
        this.walletsRepository = new WalletsRepositoryImpl();
        this.petRepository = new PetRepositoryImpl();
    }

    public UsersRepository getUsersRepository(){
        return usersRepository;
    }

    public WalletsRepository getWalletsRepository(){
        return walletsRepository;
    }

    public AllExistingItemsRepository getAllExistingItemsRepository() {
        return allExistingItemsRepository;
    }

    public PetRepository getPetRepository() {
        return petRepository;
    }
}
