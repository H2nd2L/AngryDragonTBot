package angryDragon;
import angryDragon.repository.UsersRepository;
import angryDragon.repository.AllExistingItemsRepository;
import angryDragon.repository.WalletsRepository;
import angryDragon.service.InventoryService;
import angryDragon.service.ShopService;

import java.util.List;
import java.util.Scanner;

public class App {
    private final UsersRepository usersRepository;
    private final AllExistingItemsRepository allExistingItemsRepository;
    private final WalletsRepository walletsRepository;
    private final InventoryService inventoryService;
    private final ShopService shopService;
    private final Scanner scanner = new Scanner(System.in);

    public App(
            UsersRepository usersRepository,
            AllExistingItemsRepository allExistingItemsRepository,
            WalletsRepository walletsRepository,
            InventoryService inventoryService,
            ShopService shopService
    ){
        this.allExistingItemsRepository = allExistingItemsRepository;
        this.inventoryService = inventoryService;
        this.walletsRepository = walletsRepository;
        this.shopService = shopService;
        this.usersRepository = usersRepository;
    }

    public void start(){
        printWelcome();

        while (true){
            System.out.print("\nВведите команду : ");
            String command = scanner.nextLine().trim().toLowerCase();

                switch(command){
                    // case "/games" -> games();
                    case "/add_pet" -> addPet();
                    case "/add_user" -> addUser();
                    //case "/my_pet" -> myPet(); хз надо или нет
                    case "/status_of_pet" -> statusOfPet();
                    case "/inventory" -> inventory();
                    case "/wallet" -> wallet();
                    //case "/items_of_my_pet" -> itemsOfPet(); тоже хз
                    case "/shop" -> shop();
                    case "/help" -> printHelp();
                    default -> System.out.println("Неизвестная команда. Введите /help");
                }
        }
    }


    private void addPet(){

    }


    private void addUser(){

    }

    private void statusOfPet(){

    }


    private void inventory(){

    }

    private void wallet(){

    }

    private void shop(){

    }




    private void printWelcome(){
        System.out.println("    !!!!!!ПРИВЕТИКИ!!!!!");
        printHelp();
    }


    private void printHelp(){
        System.out.println("\nДоступные команды:");
        System.out.println("  /add_pet           - Добавить питомца");
        System.out.println("  /add_user          - Добавить пользователя");
        System.out.println("  /status_of_pet     - Просмотр энергии питомца");
        System.out.println("  /inventory         - Инвентарь питомца");
        System.out.println("  /wallet            - Кошелек");
        System.out.println("  /shop              - Магазин");
        System.out.println("  /help              - Эта справка");
    }

}

