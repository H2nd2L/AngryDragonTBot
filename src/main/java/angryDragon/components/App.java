package angryDragon.components;

import angryDragon.components.repository.RepositoryComponent;
import angryDragon.components.service.ServiceComponent;
import angryDragon.domain.item.Item;
import angryDragon.domain.item.WhatItemRestore;
import angryDragon.domain.pet.Pet;
import angryDragon.domain.status.Status;
import angryDragon.domain.user.User;


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

        if (!petId.startsWith("P")){
            System.out.println("Неверный ID питомца!");
            return;
        }

        Pet pet = repositoryComponent.getPetRepository().findByPetId(petId);

        if (pet == null) {
            System.out.println("Питомец не найден.");
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
        serviceComponent.getInventoryService().removeItemsByIds(petId, List.of(itemId));
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

        System.out.print("\nВведите ID предмета");
        String itemId = scanner.nextLine().trim();

        if (repositoryComponent.getAllExistingItemsRepository().getItemById(itemId) != null) {
            System.out.println("Предмет с таким ID уже существует!");
            return;
        }

        System.out.print("\nВведите название предмета: ");
        String name = scanner.nextLine().trim();

        System.out.print("\nВведите цену предмета: ");
        int price;
        try {
            price = Integer.parseInt(scanner.nextLine().trim());
            if (price <= 0) {
                System.out.println("Цена должна быть больше 0!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректная цена!");
            return;
        }

        System.out.print("\nВведите тип статуса (ENERGY, HUNGER, JOY): ");
        WhatItemRestore itemType;
        try {
            itemType = WhatItemRestore.valueOf(scanner.nextLine().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный тип статуса!");
            return;
        }

        System.out.print("\nВведите сколько восстанавливает статуса: ");
        int regeneration;
        try {
            regeneration = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Некорректное число восстановления!");
            return;
        }

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
            System.out.println(getItemInfo(item));
        }
    }


    private String getItemInfo(Item item){
        return  "ID: " + item.getItemId() +
                "\nНазвание: " + item.getItemName() +
                "\nЦена: " + item.getItemPrice() +
                "\nТип: " + item.getItemType() +
                "\nВосстановление энергии: " + item.getItemRegenerationAmount() +
                "\n";
    }

    private void games() {
        System.out.println("    Список игр   ");
    }

    private void addPet(){
        System.out.print("\nВведите ID пользователя: ");
        String userId = scanner.nextLine().trim();

        if (!userId.startsWith("U")){
            System.out.println("Неверный ID пользователя!");
            return;
        }


        System.out.print("\nВведите ID питомца: ");
        String petId = scanner.nextLine().trim();

        if (!petId.startsWith("P") || repositoryComponent.getPetRepository().findByPetId(petId) != null) {
            System.out.println("Неверный или уже существующий ID питомца!");
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
        if (repositoryComponent.getUserRepository().findById(userId) != null) {
            System.out.println("Пользователь с таким ID уже существует!");
            return;
        }

        System.out.print("\nВведите имя пользователя: ");
        String userName = scanner.nextLine().trim();
        User user = new User(userId, userName);
        repositoryComponent.getUserRepository().addUser(user);
    }

    private void statusOfPet(){
        System.out.print("\nВведите ID питомца: ");
        String petId = scanner.nextLine().trim();

        if (!petId.startsWith("P")){
            System.out.println("Неверный ID питомца!"); // закольцевать
            return;
        }

        while(true){
            String Id = scanner.nextLine().trim();
            switch(petId.charAt(0)){
                case 'P' -> {
                    Pet pet = repositoryComponent.getPetRepository().findByPetId(Id);
                    Status petStatus = pet.getStatus();
                    System.out.println("Энергия: " + petStatus.getEnergy() + " Радость: " + petStatus.getJoy() + " Голод: " + petStatus.getHunger());
                    return;
                }
                case 'U' -> {
                    Pet pet = repositoryComponent.getPetRepository().findByUserId(Id);
                    Status petStatus = pet.getStatus();
                    System.out.println("Энергия: " + petStatus.getEnergy() + " Радость: " + petStatus.getJoy() + " Голод: " + petStatus.getHunger());
                    return;
                }
                default -> {
                    System.out.println("Неверный ID!");
                    System.out.print("\nВведите ID ещё раз ");
                }
            }
        }

    }

    private void inventory(){
        System.out.print("Введите ID питомца:");
        String petId = scanner.nextLine().trim();

        if (!petId.startsWith("P")){
            System.out.println("Неверный ID питомца!");
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
        for (String itemId : itemIds) {
            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);
            System.out.println(getItemInfo(item));
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

        if (!userId.startsWith("U")){
            System.out.println("Неверный ID пользователя!");
            return;
        }
        User user = repositoryComponent.getUserRepository().findById(userId);

        if(user == null){
            System.out.println("Пользователь не найден.");
            return;
        }

        System.out.print("Введите ID предмета: "); // проверка
        String itemId = scanner.nextLine().trim();

        switch (choice){
            case "1" -> serviceComponent.getShopService().buyItem(itemId, userId);
            case "2" -> serviceComponent.getShopService().sellItem(itemId, userId);
            default -> System.out.println("Неверный выбор");
        }
    }



    private void printWelcome(){
        String availableCommandsDescription = """
        !!!!!!ПРИВЕТИКИ!!!!!
        /add_pet - Добавить питомца
        /add_user - Добавить пользователя
        /status_of_pet - Просмотр энергии питомца
        /inventory - Инвентарь питомца
        /wallet - Кошелек
        /shop - Магазин
        /show_all_items - Показать все предметы
        /add_item - Добавить предмет
        /use_item - Использовать предмет
        /help - Справка
    """;
        System.out.println(availableCommandsDescription);
    }


    private void printHelp(){
        System.out.println("""
                /add_pet - Добавить питомца
                /add_user - Добавить пользователя
                /status_of_pet - Просмотр энергии питомца
                /inventory - Инвентарь питомца
                /wallet - Кошелек
                /shop - Магазин
                /show_all_items - Показать все предметы
                /add_item - Добавить предмет
                /use_item - Использовать предмет
                /help - Справка
                
                ID пользователя должен начинаться с U (Пример: U12352)
                ID питомца должен начинаться с P (Пример: P32111)
                ID предмета должен начинаться с I (Пример: I25132)
                """);
    }

}

