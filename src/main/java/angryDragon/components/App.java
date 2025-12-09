package angryDragon.components;

import angryDragon.components.repository.RepositoryComponent;
import angryDragon.components.service.ServiceComponent;
import angryDragon.domain.item.Item;
import angryDragon.domain.item.WhatItemRestore;
import angryDragon.domain.pet.Pet;
import angryDragon.domain.status.Status;
import angryDragon.domain.user.User;
import angryDragon.repository.AllExistingItemsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class App {

    private final RepositoryComponent repositoryComponent;
    private final ServiceComponent serviceComponent;
    private final Scanner scanner = new Scanner(System.in);

    public App(RepositoryComponent repositoryComponent, ServiceComponent serviceComponent) {
        this.repositoryComponent = repositoryComponent;
        this.serviceComponent = serviceComponent;
    }

//    public App() {
//        this(RepositoryComponent.RepositoryMode.IN_MEMORY);
//    }
//
//    public App(RepositoryComponent.RepositoryMode mode){
//        this.repositoryComponent = new RepositoryComponent(mode);
//        this.serviceComponent = new ServiceComponent(repositoryComponent);
//    }

    public void start(){
        printWelcome();

        while (true){
            System.out.print("\nВведите команду : ");
            String command = scanner.nextLine().trim().toLowerCase();

                switch(command){
                    case "/games" -> games();
                    case "/add_pet" -> addPet();
                    case "/add_user" -> addUser();
                    case "/status_of_pet" -> statusOfPet();
                    case "/show_inventory" -> inventory();
                    case "/wallet" -> wallet();
                    //case "/items_of_my_pet" -> itemsOfPet(); тоже хз
                    case "/shop" -> shop(); //покупка и продажа сделать , добавление и убирание предмета на полку из allExisting items
                    case "/show_all_items" -> allExistingItems();
                    case "/add_item" -> addItem(); //добавление предмета в allExistingItems
                    case "/use_item" -> useItem(); // использование через инвентарь
                    case "/help" -> printHelp();
                    default -> System.out.println("Неизвестная команда. Введите /help");
                }
        }
    }

    private void useItem() {
        System.out.print("Введите ID питомца: ");
        String petId = scanner.nextLine().trim();

        Pet pet = repositoryComponent.getPetRepository().findByPetId(petId);

        if (pet == null) {
            System.out.println("Питомец не найден.");
            return;
        }

        System.out.print("Введите ID предмета: ");
        // проверка через while на то, что ID корректный (начинается на I)
        String itemId = scanner.nextLine().trim();

        Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);

        if (item == null) {
            System.out.println("Такого предмета не существует.");
            return;
        }

        List<String> items = serviceComponent.getInventoryService().showItemsOfPet(petId);

        if (!items.contains(itemId)) {
            System.out.println("У питомца нет такого предмета.");
            return;
        }

        Status status = pet.getStatus();
        int itemRegenAmount = item.getItemRegenerationAmount();
        WhatItemRestore itemType = item.getItemType();

        if (itemType == WhatItemRestore.ENERGY){
            int newStatus = status.getEnergy() + itemRegenAmount;
            status.setEnergy(newStatus);
            System.out.println("Предмет использован! Энергия теперь: " + newStatus);
        }
        else if(itemType == WhatItemRestore.HUNGER){
            int newStatus = status.getHunger() + itemRegenAmount;
            status.setHunger(newStatus);
            System.out.println("Предмет использован! Голод теперь: " + newStatus);
        }
        else{
            int newStatus = status.getJoy() + itemRegenAmount;
            status.setJoy(newStatus);
            System.out.println("Предмет использован! Радость теперь: " + newStatus);
        }

        serviceComponent.getInventoryService().removeItemsByIds(petId, List.of(itemId));
    }

    private void addItem() {

        System.out.print("\nВведите ID предмета (Пример: I21313): ");
        String itemId = scanner.nextLine().trim();

        System.out.print("\nВведите название предмета: ");
        String name = scanner.nextLine().trim();

        System.out.print("\nВведите цену предмета: ");
        int price = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("\nВведите тип статуса");
        WhatItemRestore itemType = WhatItemRestore.valueOf(scanner.nextLine().trim().toUpperCase());

        System.out.print("\nВведите сколько восстанавливает статуса: ");
        int regeneration = Integer.parseInt(scanner.nextLine().trim());

        Item item = new Item(itemId, name, regeneration, itemType, price);

        repositoryComponent.getAllExistingItemsRepository().addItem(item);

        System.out.println("\nПредмет успешно добавлен!");

    }

    private void allExistingItems() {
        Set<Item> items = repositoryComponent.getAllExistingItemsRepository().getAllExistingItems();

        if(items == null){
            System.out.println("Список предметов пуст.");
            return;
        }

        System.out.println("Все существующие предметы:");

        for (Item item : items) {
            System.out.print("\nID: " + item.getItemId() +
                    "\nНазвание: " + item.getItemName() +
                    "\nЦена: " + item.getItemPrice() +
                    "\nТип: " + item.getItemType() +
                    "\nВосстановление энергии: " + item.getItemRegenerationAmount() +
                    "\n");
        }
    }

    private void games() {
        System.out.println("    Список игр   ");
        // выводится список существующих игр
        System.out.print("\nВыбери игру: ");
        String game = scanner.nextLine().trim().toLowerCase();
        //запуск игры
    }

    private void addPet(){
        System.out.print("\nВведите ID пользователя: ");
        String userId = scanner.nextLine().trim();

        if (!userId.startsWith("U")){
            System.out.println("Неверный ID пользователя!"); // закольцевать
            return;
        }

        System.out.print("\nВведите ID питомца: ");
        String petId = scanner.nextLine().trim();

        if (!petId.startsWith("P")){ // закольцевать
            System.out.println("Неверный ID питомца!");
            return;
        }

        System.out.print("\nВведите имя питомца: ");
        String petName = scanner.nextLine().trim();
        Status petStatus = new Status();
        LocalDate dateOfCreation = LocalDate.now();
        Pet pet = new Pet(userId, dateOfCreation, petName, petId, petStatus);
        repositoryComponent.getPetRepository().addPet(pet);
    }

    private void addUser(){
        System.out.print("\nВведите ID пользователя: ");
        String userId = scanner.nextLine().trim();

        if (!userId.startsWith("U")){
            System.out.println("Неверный ID пользователя!");
            return;
        }

        System.out.print("\nВведите имя пользователя: ");
        String userName = scanner.nextLine().trim();
        User user = new User(userId, userName);
        repositoryComponent.getUserRepository().addUser(user);
    }

    private void statusOfPet(){
        System.out.print("\nВведите ID пользователя или питомца: ");

        while(true){
            String Id = scanner.nextLine().trim();
            if(Id.startsWith("P")){
                Pet pet = repositoryComponent.getPetRepository().findByPetId(Id);
                Status petStatus = pet.getStatus();
                System.out.println("Энергия: " + petStatus.getEnergy());
                System.out.println("Радость: " + petStatus.getJoy());
                System.out.println("Голод: " + petStatus.getHunger());
                break;
            }
            else if(Id.startsWith("U")){
                Pet pet = repositoryComponent.getPetRepository().findByUserId(Id);
                Status petStatus = pet.getStatus();
                System.out.println("Энергия: " + petStatus.getEnergy());
                System.out.println("Радость: " + petStatus.getJoy());
                System.out.println("Голод: " + petStatus.getHunger());
                break;
            }
            else{
                System.out.println("Неверный ID!");
                System.out.print("\nВведите ID ещё раз ");
            }
        }

    }

    private void inventory(){
        System.out.print("Введите ID питомца:");
        String petId = scanner.nextLine().trim();

        if (!petId.startsWith("P")){
            System.out.println("Неверный ID питомца!"); // закольцевать
            return;
        }

        Pet pet = repositoryComponent.getPetRepository().findByPetId(petId);

        if(pet == null){
            System.out.println("Питомец не найден");
            return;
        }

        List<String> itemIds = serviceComponent.getInventoryService().showItemsOfPet(petId);

        if(itemIds == null){
            System.out.println("Инвентарь пуст");
            return;
        }

        System.out.println("\nИнвентарь питомца:");
        for(String itemId : itemIds){
            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);

            if(item == null){
                System.out.println(itemId + " - <предмет не найден>");
                continue;
            }

            System.out.println(
                    "\n: " + item.getItemId() +
                    "\nНазвание: " + item.getItemName() +
                    "\nЦена: " + item.getItemPrice() +
                    "\nСколько восстанавливает энергии этот предмет: " + item.getItemRegenerationAmount() +
                    "\nТип: " + item.getItemType() +
                    "\n");
        }

    }

    private void wallet(){
        System.out.print("\nВведите ID пользователя: ");
        String userId = scanner.nextLine().trim();

        if (!userId.startsWith("U")){
            System.out.println("Неверный ID пользователя!");
            return;
        }

        User user = repositoryComponent.getUserRepository().findById(userId);

        if(user == null){
            System.out.println("Пользователь не найден.");
            return;
        }

        System.out.println("\nБаланс пользователя: " + repositoryComponent.getWalletsRepository().getUserCashValue(userId) + "монет");

    }

    private void shop(){
        System.out.println("""
                 Магазин:
               1 - Купить предмет
               2 - Продать предмет
               """);
        String choice = scanner.nextLine().trim();

        System.out.print("Введите ID пользователя: "); // проверка
        String userId = scanner.nextLine().trim();

        System.out.print("Введите ID предмета: "); // проверка
        String itemId = scanner.nextLine().trim();

        switch (choice){
            case "1" -> serviceComponent.getShopService().buyItem(itemId, userId);
            case "2" -> serviceComponent.getShopService().sellItem(itemId, userId);
            default -> System.out.println("Неверный выбор");
        }
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

