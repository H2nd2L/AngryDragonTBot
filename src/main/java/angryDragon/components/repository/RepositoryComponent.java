package angryDragon.components.repository;

import angryDragon.business.repository.PetRepository;
import angryDragon.business.repository.UsersRepository;
import angryDragon.business.repository.AllExistingItemsRepository;
import angryDragon.business.repository.WalletsRepository;
import angryDragon.business.repository.impl.AllExistingItemsRepositoryImpl;
import angryDragon.business.repository.impl.PetRepositoryImpl;
import angryDragon.business.repository.impl.UsersRepositoryImpl;
import angryDragon.business.repository.impl.WalletsRepositoryImpl;


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
