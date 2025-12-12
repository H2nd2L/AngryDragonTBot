package angryDragon.components;

import angryDragon.components.repository.RepositoryComponent;
import angryDragon.components.service.ServiceComponent;
import angryDragon.domain.item.Item;
import angryDragon.domain.item.WhatItemRestore;
import angryDragon.domain.pet.Pet;
import angryDragon.domain.status.Status;
import angryDragon.domain.user.User;
import angryDragon.domain.wallet.Wallet;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


public class App {
    private final RepositoryComponent repositoryComponent;
    private final ServiceComponent serviceComponent;
    private final Scanner scanner = new Scanner(System.in);


    public App() {
        this.repositoryComponent = new RepositoryComponent();
        this.serviceComponent = new ServiceComponent(repositoryComponent);
    }


    public void start(){
        printWelcome();

        while (true){
            System.out.print("\nВведите команду: ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch(command){
                case "/games" -> games();
                case "/add_pet" -> addPet();
                case "/add_user" -> addUser();
                case "/pet_status" -> statusOfPet();
                case "/inventory" -> inventory();
                case "/wallet" -> wallet();
                case "/shop" -> shop();
                case "/show_all_items" -> allExistingItems();
                case "/add_item" -> addItem();
                case "/use_item" -> useItem();
                case "/help" -> printHelp();
                default -> System.out.println("Неизвестная команда. Введите /help");
            }
        }
    }


    private void useItem() {
        System.out.print("Введите ID питомца: ");
        String petId = scanner.nextLine().trim();

        Pet pet = repositoryComponent.getPetRepository().findByPetId(petId);

        if (!petId.startsWith("P") || pet == null) {
            System.out.println("Неверный или не существующий ID питомца!");
            return;
        }

        System.out.print("Введите ID предмета: ");
        String itemId = scanner.nextLine().trim();

        List<String> items = serviceComponent.getInventoryService().showItemsOfPet(petId);

        if (!items.contains(itemId)) {
            System.out.println("У питомца нет такого предмета.");
            return;
        }

        Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);

        if (item == null) {
            System.out.println("Такого предмета не существует.");
            return;
        }

        ItemEffects(pet, item);
        serviceComponent.getInventoryService().removeItemById(petId, itemId);
        System.out.println("Предмет успешно использован!");
    }


    private void ItemEffects(Pet pet, Item item){
        Status status = pet.getStatus();
        int newStatusValue;
        String message;

        switch (item.getItemType()) {
            case ENERGY -> {
                newStatusValue = status.getEnergy() + item.getItemRegenerationAmount();
                status.setEnergy(newStatusValue);
                message = "Энергия теперь: " + newStatusValue;
            }
            case HUNGER -> {
                newStatusValue = status.getHunger() + item.getItemRegenerationAmount();
                status.setHunger(newStatusValue);
                message = "Голод теперь: " + newStatusValue;
            }
            case JOY -> {
                newStatusValue = status.getJoy() + item.getItemRegenerationAmount();
                status.setJoy(newStatusValue);
                message = "Радость теперь: " + newStatusValue;
            }
            default -> message = "Предмет не имеет эффекта.";
        }
        System.out.println("Предмет использован! " + message);
    }


    private void addItem() {

        System.out.print("Введите ID предмета: ");
        String itemId = scanner.nextLine().trim();

        if (!itemId.startsWith("I") || repositoryComponent.getAllExistingItemsRepository().getItemById(itemId) != null) {
            System.out.println("Неверный или уже существующий ID предмета!");
            return;
        }

        System.out.print("Введите название предмета: ");
        String name = scanner.nextLine().trim();

        System.out.print("Введите цену предмета: ");
        int price;
        try {
            price = Integer.parseInt(scanner.nextLine().trim());
            if (price <= 0 || price > 500) {
                System.out.println("Цена должна быть больше 0 и меньше 500!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректная цена!");
            return;
        }

        System.out.print("Введите тип статуса (ENERGY, HUNGER, JOY): ");
        WhatItemRestore itemType;
        try {
            itemType = WhatItemRestore.valueOf(scanner.nextLine().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный тип статуса!");
            return;
        }

        System.out.print("Введите сколько предмет восстанавливает статуса: ");
        int regeneration;
        try {
            regeneration = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Некорректное число восстановления!");
            return;
        }

        Item item = new Item(itemId, name, regeneration, itemType, price);
        repositoryComponent.getAllExistingItemsRepository().addItem(item);
        System.out.println("Предмет успешно добавлен!");
    }


    private void allExistingItems() {
        Set<Item> items = repositoryComponent.getAllExistingItemsRepository().getAllExistingItems();

        if(items.isEmpty()){
            System.out.println("Список предметов пуст.");
            return;
        }

        System.out.println("Все существующие предметы:");

        int counter = 1;
        for (Item item : items) {
            System.out.println(counter + ")  " + getItemInfo(item));
            counter += 1;
        }
    }


    private String getItemInfo(Item item){
        return  "ID: " + item.getItemId() +
                "  Название: " + item.getItemName() +
                "  Цена: " + item.getItemPrice() +
                "  Тип: " + item.getItemType() +
                "  Восстанавливает: " + item.getItemRegenerationAmount();
    }


    private void games() {
        System.out.println("""
                Список игр:
                 1) Wordle
                 2) Виселица
                 3) Однорукий бандит
                 4) Крестики-нолики""");
    }


    private void addPet(){
        System.out.print("Введите ID пользователя: ");
        String userId = scanner.nextLine().trim();

        if (!userId.startsWith("U") || repositoryComponent.getUsersRepository().findById(userId) == null){
            System.out.println("Неверный или не существующий ID пользователя!");
            return;
        }

        if (repositoryComponent.getPetRepository().findByUserId(userId) != null){
            System.out.println("У пользователя уже есть питомец");
            return;
        }

        System.out.print("Введите ID питомца: ");
        String petId = scanner.nextLine().trim();

        if (!petId.startsWith("P") || repositoryComponent.getPetRepository().findByPetId(petId) != null) {
            System.out.println("Неверный или уже существующий ID питомца!");
            return;
        }

        System.out.print("Введите имя питомца: ");
        String petName = scanner.nextLine().trim();
        Status petStatus = new Status();
        LocalDate dateOfCreation = LocalDate.now();
        Pet pet = new Pet(userId, dateOfCreation, petName, petId, petStatus);
        repositoryComponent.getPetRepository().addPet(pet);
        System.out.println("Питомец успешно создан!");
    }


    private void addUser(){
        System.out.print("Введите ID пользователя: ");
        String userId = scanner.nextLine().trim();

        if (!userId.startsWith("U") || repositoryComponent.getUsersRepository().findById(userId) != null){
            System.out.println("Неверный или уже существующий ID пользователя!");
            return;
        }

        System.out.print("Введите имя пользователя: ");
        String userName = scanner.nextLine().trim();
        User user = new User(userId, userName);
        repositoryComponent.getUsersRepository().addUser(user);
        Wallet wallet = new Wallet(userId);
        repositoryComponent.getWalletsRepository().addWallet(wallet);
        System.out.println("Пользователь успешно создан!");
    }


    private void statusOfPet(){
        System.out.print("Введите ID питомца: ");
        String petId = scanner.nextLine().trim();

        Pet pet = repositoryComponent.getPetRepository().findByPetId(petId);

        if (!petId.startsWith("P") || pet == null) {
            System.out.println("Неверный или не существующий ID питомца!");
            return;
        }

        Status petStatus = pet.getStatus();
        System.out.println("Энергия: " + petStatus.getEnergy() +
                "\nРадость: " + petStatus.getJoy() +
                "\nГолод: " + petStatus.getHunger()
        );
    }


    private void inventory(){
        System.out.print("Введите ID питомца: ");
        String petId = scanner.nextLine().trim();

        Pet pet = repositoryComponent.getPetRepository().findByPetId(petId);

        if (!petId.startsWith("P") || pet == null) {
            System.out.println("Неверный или не существующий ID питомца!");
            return;
        }

        List<String> itemIds = serviceComponent.getInventoryService().showItemsOfPet(petId);

        if(itemIds.isEmpty()){
            System.out.println("Инвентарь пуст");
            return;
        }

        System.out.println("Инвентарь питомца:");
        int counter = 1;
        for (String itemId : itemIds) {
            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);
            System.out.println(counter + ")  " + getItemInfo(item));
            counter += 1;
        }
    }


    private void wallet(){
        System.out.print("Введите ID пользователя: ");
        String userId = scanner.nextLine().trim();

        User user = repositoryComponent.getUsersRepository().findById(userId);

        if (!userId.startsWith("U") || user == null){
            System.out.println("Неверный или не существующий ID пользователя!");
            return;
        }

        System.out.println("Баланс пользователя: " + repositoryComponent.getWalletsRepository().getUserCashValue(userId) + " монет");
    }


    private void shop(){
        System.out.print("  Магазин:" + "\n1 - Купить предмет" +
                "\n2 - Продать предмет" + "\n3 - Показать каталог магазина" +
                "\n4 - Добавить предмет в магазин" + "\nВведи свой выбор (цифру): ");

        String choice = scanner.nextLine().trim();

        String userId = "";
        int userCashValue = -1;
        if (!choice.equals("3") && !choice.equals("4")){
            System.out.print("Введите ID пользователя: ");
            userId = scanner.nextLine().trim();

            if (!userId.startsWith("U") || repositoryComponent.getUsersRepository().findById(userId) == null){
                System.out.println("Неверный или не существующий ID пользователя!");
                return;
            }

            userCashValue = repositoryComponent.getWalletsRepository().getUserCashValue(userId);
        }

        String itemId = "";
        int itemPrice = 1;
        if(!choice.equals("3")){
            System.out.print("Введите ID предмета: ");
            itemId = scanner.nextLine().trim();
            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);

            if (!itemId.startsWith("I") || item == null) {
                System.out.println("Неверный или не существующий ID предмета!");
                return;
            }

            itemPrice = item.getItemPrice();
        }

        List<String> catalog = serviceComponent.getShopService().getCurrentShopCatalog();

        switch (choice){
            case "1" -> {
                if(!catalog.contains(itemId)){
                    System.out.println("Такого предмета нет в магазине!");
                    return;
                }

                if(userCashValue - itemPrice < 0){
                    System.out.println("У пользователя недостаточно средств!");
                    return;
                }

                Pet pet = repositoryComponent.getPetRepository().findByUserId(userId);
                String petId = pet.getPetId();
                Status status = pet.getStatus();
                status.setEnergy(status.getEnergy() - 2);
                status.setJoy(status.getJoy() - 2);

                serviceComponent.getShopService().buyItem(userId, itemPrice, userCashValue);
                serviceComponent.getInventoryService().addItemToPet(petId, itemId);
                System.out.println("Предмет успешно приобретён!");
            }

            case "2" -> {
                Pet pet = repositoryComponent.getPetRepository().findByUserId(userId);
                String petId = pet.getPetId();
                Status status = pet.getStatus();
                status.setEnergy(status.getEnergy() - 2);

                serviceComponent.getShopService().sellItem(userId, itemPrice, userCashValue);
                serviceComponent.getInventoryService().removeItemById(petId, itemId);
                System.out.println("Предмет успешно продан!");
            }

            case "3" -> {
                System.out.println("  Каталог магазина:");
                int counter = 1;
                for(String itemId_from_catalog : catalog){
                    Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId_from_catalog);
                    System.out.println(counter + ")  " + getItemInfo(item));
                }
            }

            case "4" -> {
                if(catalog.contains(itemId)){
                    System.out.println("Такой предмет уже есть в магазине!");
                    return;
                }

                serviceComponent.getShopService().addItemIdToCatalog(itemId);
                System.out.println("Предмет успешно добавлен в магазин!");
            }
            default -> System.out.println("Неверный выбор!");
        }
    }


    private void printWelcome(){
        String availableCommandsDescription = """
        
        >>> Бот запущен <<<
        
        /add_pet - Добавить питомца
        /add_user - Добавить пользователя
        /pet_status - Просмотр энергии питомца
        /inventory - Инвентарь питомца
        /wallet - Кошелек
        /shop - Магазин
        /show_all_items - Показать все предметы
        /add_item - Добавить предмет
        /use_item - Использовать предмет
        /games - Показать список доступных игр
        /help - Справка""";
        System.out.println(availableCommandsDescription);
    }


    private void printHelp(){
        System.out.println("""
                /add_pet - Добавить питомца
                /add_user - Добавить пользователя
                /pet_status - Просмотр энергии питомца
                /inventory - Инвентарь питомца
                /wallet - Кошелек
                /shop - Магазин
                /show_all_items - Показать все предметы
                /add_item - Добавить предмет
                /use_item - Использовать предмет
                /games - Показать список доступных игр
                /help - Справка
                
                ID пользователя должен начинаться с U (Пример: U12352)
                ID питомца должен начинаться с P (Пример: P32111)
                ID предмета должен начинаться с I (Пример: I25132)""");
    }

}

