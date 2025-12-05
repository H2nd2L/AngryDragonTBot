package angryDragon.components.repository;

import angryDragon.repository.PetRepository;
import angryDragon.repository.UsersRepository;
import angryDragon.repository.AllExistingItemsRepository;
import angryDragon.repository.WalletsRepository;
import angryDragon.repository.impl.PetRepositoryImpl;
import angryDragon.repository.impl.UsersRepositoryImpl;
import angryDragon.repository.impl.WalletsRepositoryImpl;
import angryDragon.repository.impl.AllExistingItemsRepositoryImpl;

public class RepositoryComponent {

    private final UsersRepository userRepository;
    private final AllExistingItemsRepository allExistingItemsRepository;
    private final WalletsRepository walletsRepository;
    private final PetRepository petRepository;

    public RepositoryComponent(UsersRepository userRepository, AllExistingItemsRepository allExistingItemsRepository, WalletsRepository walletsRepository, PetRepository petRepository) {
        this.userRepository = userRepository;
        this.allExistingItemsRepository = allExistingItemsRepository;
        this.walletsRepository = walletsRepository;
        this.petRepository = petRepository;
    }

//    public enum RepositoryMode{
//        IN_MEMORY,
//        DATABASE
//    }
//
//    public RepositoryComponent(){
//        this(RepositoryMode.IN_MEMORY);
//    }
//
//    public RepositoryComponent(RepositoryMode mode){
//        switch(mode){
//            case DATABASE:
//                this.userRepository = new UsersRepositoryImpl();
//                this.walletsRepository = new WalletsRepositoryImpl();
//                this.allExistingItemsRepository = new AllExistingItemsRepositoryImpl();
//                this.petRepository = new PetRepositoryImpl();
//                break;
//            case IN_MEMORY:
//            default:
//                this.userRepository = new UsersRepositoryImpl();
//                this.walletsRepository = new WalletsRepositoryImpl();
//                this.allExistingItemsRepository = new AllExistingItemsRepositoryImpl();
//                this.petRepository = new PetRepositoryImpl();
//                break;
//        }
//    }

    public UsersRepository getUserRepository(){
        return userRepository;
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
