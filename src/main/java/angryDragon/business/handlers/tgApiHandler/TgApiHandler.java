package angryDragon.business.handlers.tgApiHandler;

import angryDragon.business.domain.item.Item;
import angryDragon.business.domain.item.WhatItemRestore;
import angryDragon.business.domain.pet.Pet;
import angryDragon.business.domain.status.Status;
import angryDragon.business.domain.user.User;
import angryDragon.business.domain.wallet.Wallet;
import angryDragon.business.handlers.userSession.KeyPair;
import angryDragon.business.handlers.userSession.UserSession;
import angryDragon.components.repository.RepositoryComponent;
import angryDragon.components.service.ServiceComponent;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TgApiHandler {

    private final RepositoryComponent repositoryComponent;
    private final ServiceComponent serviceComponent;
    private final Map<Long, UserSession> userSessions;
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public TgApiHandler() {
        this.repositoryComponent = new RepositoryComponent();
        this.serviceComponent = new ServiceComponent(repositoryComponent);
        this.userSessions =new HashMap<>();
        statusChange();
    }

    /**
     * Изменение статуса всех питомцев в фоне
     */
    private void statusChange() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Pet pet : repositoryComponent.getPetRepository().returnPetRepository()) {
                Status status = pet.getStatus();
                status.setEnergy(status.getEnergy() + 2);
                status.setJoy(status.getJoy() - 2);
                status.setHunger(status.getHunger() - 3);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Обрабатывает входящие сообщения от пользователя
     * @param chatId ID чата пользователя
     * @param messageText полный текст сообщения
     * @return ответ для отправки пользователю
     */
    public String handleUpdateReceived(Long chatId, String messageText) {
        if (messageText == null || messageText.isBlank()) {
            return "Пустое сообщение";
        }

        UserSession session = userSessions.computeIfAbsent(chatId, k -> new UserSession());

        if (session.getState() != UserSession.SessionState.IDLE) {
            return handleSessionState(session, messageText, chatId);
        }

        String trimmedMessage = messageText.trim();

        if (trimmedMessage.equalsIgnoreCase("/cancel")) {
            session.reset();
            return "Текущий диалог отменён";
        }

        return switch (trimmedMessage) {
            case "/start" -> getWelcomeMessage();
            case "/help" -> getHelpMessage();
            case "/games" -> games();
            case "/shop" -> shop();
            case "/add_user" -> {
                session.setState(UserSession.SessionState.ADD_USER_WAITING_NAME);
                yield "Добавление пользователя\n\nВведите имя пользователя (Tim):";
            }
            case "/wallet" -> walletFromSession(chatId);
            case "/add_pet" -> {
                session.setState(UserSession.SessionState.ADD_PET_WAITING_PET_NAME);
                yield "Добавление питомца\n\nВведите имя питомца (Tigra):";
            }
            case "/pet_status" -> petStatusFromSession(chatId);
            case "/inventory" -> inventoryFromSession(chatId);
            case "/use_item" -> {
                session.setState(UserSession.SessionState.USE_ITEM_WAITING_ITEM_ID);
                yield "Использование предмета\n\nВведите ID предмета (Пример: I4789):";
            }
            case "/buy_item" -> {
                session.setState(UserSession.SessionState.BUY_ITEM_WAITING_ITEM_ID);
                yield "Покупка предмета\n\nВведите ID предмета (Пример: I4789):";
            }
            case "/sell_item" -> {
                session.setState(UserSession.SessionState.SELL_ITEM_WAITING_ITEM_ID);
                yield "Покупка предмета\n\nВведите ID предмета (Пример: I4789):";
            }

            // ADMIN
            case "/_all_items" -> _allItems();
            case "/_add_item" -> {
                session.setState(UserSession.SessionState.ADD_ITEM_WAITING_ID);
                yield "Добавление предмета\n\nШаг 1/5: Введите ID предмета (Пример: I4789):";
            }
            case "/_add_item_to_shop" -> {
                session.setState(UserSession.SessionState.ADD_ITEM_TO_SHOP_WAITING_ID);
                yield "Введите ID предмета (Пример: I4789):";
            }

            default -> "Неизвестная команда: " + trimmedMessage + "\nВведите /help для списка команд";
        };
    }

    /**
     * Обрабатывает состояние сессии в многошаговом диалоге
     */
    private String handleSessionState(UserSession session, String input, long chatId) {
        input = input.trim();

        if (input.equalsIgnoreCase("/cancel")) {
            session.reset();
            return "Диалог отменён.";
        }

        return switch (session.getState()) {
            // Добавление пользователя
            case ADD_USER_WAITING_NAME -> {
                if (input.isEmpty()) {
                    yield "Ошибка: имя пользователя не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }

                if (!input.matches("^[\\p{Lu}][\\p{L}]*$")) {
                    yield "Ошибка: имя пользователя должно состоять только из букв и начинаться с заглавной буквы. Попробуйте ещё раз или введите /cancel:";
                }

                String id = "U" + String.valueOf(chatId);
                String result = addUserFromSession(id, input);
                session.reset();
                yield result;
            }

            // Добавление питомца
            case ADD_PET_WAITING_PET_NAME -> {
                if (input.isEmpty()) {
                    yield "Ошибка: имя питомца не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }

                if (!input.matches("^[\\p{Lu}][\\p{L}]*$")) {
                    yield "Ошибка: имя питомца должно состоять только из букв и начинаться с заглавной буквы. Попробуйте ещё раз или введите /cancel:";
                }

                String userId = "U" + String.valueOf(chatId);
                String petId = "P" + String.valueOf(chatId);
                String result = addPetFromSession(userId, petId, input);
                session.reset();
                yield result;
            }

            // Использование предмета
            case USE_ITEM_WAITING_ITEM_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }

                if (!input.matches("^I[1-9][0-9]*$")){
                    yield "Ошибка: неверный ID предмета. ID должен начинаться с I и иметь цифры после. Попробуйте ещё раз или введите /cancel:";
                }

                String petId = "P" + String.valueOf(chatId);
                String result = useItemFromSession(petId, input);
                session.reset();
                yield result;
            }

            // Покупка предмета
            case BUY_ITEM_WAITING_ITEM_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }

                if (!input.matches("^I[1-9][0-9]*$")){
                    yield "Ошибка: неверный ID предмета. ID должен начинаться с I и иметь цифры после. Попробуйте ещё раз или введите /cancel:";
                }

                String userId = "U" + String.valueOf(chatId);
                String result = buyItemFromSession(userId, input);
                session.reset();
                yield result;
            }

            // Продажа предмета
            case SELL_ITEM_WAITING_ITEM_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }

                if (!input.matches("^I[1-9][0-9]*$")){
                    yield "Ошибка: неверный ID предмета. ID должен начинаться с I и иметь цифры после. Попробуйте ещё раз или введите /cancel:";
                }

                String userId = "U" + String.valueOf(chatId);
                String result = sellItemFromSession(userId, input);
                session.reset();
                yield result;
            }

            // Добавление предмета
            case ADD_ITEM_WAITING_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }

                if (!input.matches("^I[1-9][0-9]*$")){
                    yield "Ошибка: неверный ID предмета. ID должен начинаться с I и иметь цифры после. Попробуйте ещё раз или введите /cancel:";
                }

                session.putData(new KeyPair(chatId,"id"), input);
                session.setState(UserSession.SessionState.ADD_ITEM_WAITING_NAME);
                yield "Шаг 2/5: Введите название предмета:";
            }
            case ADD_ITEM_WAITING_NAME -> {
                if (input.isEmpty()) {
                    yield "Ошибка: название предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }

                session.putData(new KeyPair(chatId,"name"), input);
                session.setState(UserSession.SessionState.ADD_ITEM_WAITING_PRICE);
                yield "Шаг 3/5: Введите цену предмета (1-500):";
            }
            case ADD_ITEM_WAITING_PRICE -> {
                if (input.isEmpty()) {
                    yield "Ошибка: цена предмета не может быть пустой. Попробуйте ещё раз или введите /cancel:";
                }

                if (Integer.parseInt(input) < 1 || Integer.parseInt(input) > 500) {
                    yield "Ошибка: цена не может быть меньше 0 или больше 500. Попробуйте ещё раз или введите /cancel:";
                }

                session.putData(new KeyPair(chatId,"price"), input);
                session.setState(UserSession.SessionState.ADD_ITEM_WAITING_STATUS_TYPE);
                yield "Шаг 4/5: Введите тип статуса предмета (HUNGER | ENERGY | JOY):";
            }
            case ADD_ITEM_WAITING_STATUS_TYPE -> {
                if (input.isEmpty()) {
                    yield "Ошибка: статус предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }

                if (!(input.equals("HUNGER") || input.equals("JOY") || input.equals("ENERGY"))){
                    yield "Ошибка: выбран не существующий статус. Попробуйте ещё раз или введите /cancel:";
                }

                session.putData(new KeyPair(chatId,"status"), input);
                session.setState(UserSession.SessionState.ADD_ITEM_WAITING_STATUS_VALUE);
                yield "Шаг 5/5: Введите значение статуса (1-100):";
            }
            case ADD_ITEM_WAITING_STATUS_VALUE -> {
                if (input.isEmpty()) {
                    yield "Ошибка: значение статуса не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }

                if (Integer.parseInt(input) < 1 || Integer.parseInt(input) > 100){
                    yield "Ошибка: значение восстановления не может быть меньше 0 или больше 100. Попробуйте ещё раз или введите /cancel:";
                }

                String id = session.getData(new KeyPair(chatId,"id"));
                String name = session.getData(new KeyPair(chatId,"name"));
                int price = Integer.parseInt(session.getData(new KeyPair(chatId,"price")));
                WhatItemRestore status = WhatItemRestore.valueOf(session.getData(new KeyPair(chatId,"status")));
                int value = Integer.parseInt(input);
                String result = addItemFromSession(id, name, value, status, price);
                session.reset();
                yield result;
            }

            // Добавление предмета в магазин
            case ADD_ITEM_TO_SHOP_WAITING_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }

                if (!input.matches("^I[1-9][0-9]*$")){
                    yield "Ошибка: неверный ID предмета. ID должен начинаться с I и иметь цифры после. Попробуйте ещё раз или введите /cancel:";
                }

                String result = addItemToShopFromSession(input);
                session.reset();
                yield result;
            }

            default -> {
                session.reset();
                yield "Произошла ошибка. Попробуйте ещё раз";
            }
        };
    }

    /**
     * Добавление пользователя
     * @param id ID пользователя
     * @param name Имя пользователя
     */
    private String addUserFromSession(String id, String name) {
        try {
            if (repositoryComponent.getUsersRepository().findById(id) != null) {
                return "Вы уже создали свой профиль";
            }

            User user = new User(id, name);
            repositoryComponent.getUsersRepository().addUser(user);
            Wallet wallet = new Wallet(id);
            repositoryComponent.getWalletsRepository().addWallet(wallet);
            return "✓ Пользователь успешно добавлен:\n" +
                    "     ID: " + id + "\n" +
                    "     Имя: " + name + "\n" +
                    "     Баланс: " + wallet.getCashValue();
        } catch (Exception e) {
            return "✗ Ошибка при добавлении пользователя:\n" + e.getMessage();
        }
    }

    /**
     * Показ баланса пользователя
     * @param chatId ID пользователя
     * @return Баланс пользователя
     */
    private String walletFromSession(long chatId) {
        String userId = "U" + String.valueOf(chatId);
        User user = repositoryComponent.getUsersRepository().findById(userId);

        if (user == null) {
            return "Ошибка: не существует такого пользователя";
        }

        try {
            int cash = repositoryComponent.getWalletsRepository().getUserCashValue(userId);
            return "  Баланс пользователя: " + cash;
        } catch (Exception e) {
            return "✗ Ошибка при показе баланса:\n" + e.getMessage();
        }
    }

    /**
     * Добавление питомца
     * @param userId ID пользователя
     * @param petId ID питомца
     * @param name Имя питомца
     */
    private String addPetFromSession(String userId, String petId, String name) {
        try {
            if (repositoryComponent.getPetRepository().findByPetId(petId) != null) {
                return "У вас уже есть питомец";
            }

            Pet pet = new Pet(userId, LocalDate.now(), name, petId, new Status());
            repositoryComponent.getPetRepository().addPet(pet);
            return "✓ Питомец успешно добавлен:\n" +
                    "     ID питомца: " + petId + "\n" +
                    "     Имя: " + name + "\n" +
                    "     Дата создания: " + pet.getDateOfCreation();
        } catch (Exception e) {
            return "✗ Ошибка при добавлении питомца:\n" + e.getMessage();
        }
    }

    /**
     * Показ статуса питомца
     * @param chatId ID питомца
     * @return Статус питомца
     */
    private String petStatusFromSession(long chatId) {
        String petId = "P" + String.valueOf(chatId);
        Pet pet = repositoryComponent.getPetRepository().findByPetId(petId);

        if (pet == null) {
            return "Ошибка: вашего питомца ещё не существует";
        }

        try {
            Status petStatus = pet.getStatus();
            return  "Статус " + pet.getPetName() + "\n\n" +
                    " Энергия: " + petStatus.getEnergy() + "\n" +
                    " Радость: " + petStatus.getJoy() + "\n" +
                    " Голод: " + petStatus.getHunger();
        } catch (Exception e) {
            return "✗ Ошибка при показе статуса питомца:\n" + e.getMessage();
        }
    }

    /**
     * Показ инвентаря питомца
     * @param chatId ID питомца
     * @return Инвентарь питомца
     */
    private String inventoryFromSession(long chatId) {
        String petId = "P" + String.valueOf(chatId);
        Pet pet = repositoryComponent.getPetRepository().findByPetId(petId);

        if (pet == null) {
            return "Ошибка: вашего питомца ещё не существует";
        }

        try {
            List<String> itemIds = serviceComponent.getInventoryService().showItemsOfPet(petId);

            if (itemIds.isEmpty()) {
                return "Инвентарь питомца пуст";
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Инвентарь питомца: \n\n");
            int counter = 1;

            for (String itemId : itemIds) {
                Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);
                String itemInfo = counter + ") ITEM" + "\n" +
                        "  ID: " + item.getItemId() + "\n" +
                        "  Название: " + item.getItemName() + "\n" +
                        "  Цена: " + item.getItemPrice() + "\n" +
                        "  Тип: " + item.getItemType() + "\n" +
                        "  Восстанавливает: " + item.getItemRegenerationAmount() + "\n\n";
                stringBuilder.append(itemInfo);
                counter += 1;
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            return "✗ Ошибка при показе инвентаря питомца:\n" + e.getMessage();
        }
    }

    /**
     * Использование предмета
     * @param petId ID питомца
     * @param itemId ID предмета
     */
    private String useItemFromSession(String petId, String itemId) {
        try {
            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);
            if (item == null) {
                return "Такого предмета не существует";
            }

            List<String> items = serviceComponent.getInventoryService().showItemsOfPet(petId);
            if (!items.contains(itemId)) {
                return "У питомца нет такого предмета";
            }

            Pet pet = repositoryComponent.getPetRepository().findByPetId(petId);
            Status status = pet.getStatus();
            int newStatusValue;
            String message;

            switch (item.getItemType()) {
                case ENERGY -> {
                    newStatusValue = status.getEnergy() + item.getItemRegenerationAmount();
                    status.setEnergy(newStatusValue);
                    message = "Предмет использован! Энергия теперь: " + newStatusValue + " единиц";
                }
                case HUNGER -> {
                    newStatusValue = status.getHunger() + item.getItemRegenerationAmount();
                    status.setHunger(newStatusValue);
                    message = "Предмет использован! Сытость теперь: " + newStatusValue + " единиц";
                }
                case JOY -> {
                    newStatusValue = status.getJoy() + item.getItemRegenerationAmount();
                    status.setJoy(newStatusValue);
                    message = "Предмет использован! Радость теперь: " + newStatusValue + " единиц";
                }
                default -> message = "Предмет не имеет эффекта";
            }

            serviceComponent.getInventoryService().removeItemById(petId, itemId);
            return message;
        } catch (Exception e) {
            return "✗ Ошибка при использовании предмета:\n" + e.getMessage();
        }
    }

    /**
     * Покупка предмета
     * @param userId ID пользователя
     * @param itemId ID предмета
     */
    private String buyItemFromSession(String userId, String itemId) {
        try {
            List<String> catalog = serviceComponent.getShopService().getCurrentShopCatalog();
            if(!catalog.contains(itemId)){
                return"Такого предмета нет в магазине";
            }

            int userCashValue = repositoryComponent.getWalletsRepository().getUserCashValue(userId);
            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);
            int itemPrice = item.getItemPrice();

            if(userCashValue - itemPrice < 0){
                return "У пользователя недостаточно средств";
            }

            Pet pet = repositoryComponent.getPetRepository().findByUserId(userId);
            String petId = pet.getPetId();

            serviceComponent.getShopService().buyItem(userId, itemPrice, userCashValue);
            serviceComponent.getInventoryService().addItemToPet(petId, itemId);
            return "Предмет " + item.getItemName() + " успешно приобретён!";
        } catch (Exception e) {
            return "✗ Ошибка при покупке предмета:\n" + e.getMessage();
        }
    }

    /**
     * Продажа предмета
     * @param userId ID пользователя
     * @param itemId ID предмета
     */
    private String sellItemFromSession(String userId, String itemId) {
        try {
            int userCashValue = repositoryComponent.getWalletsRepository().getUserCashValue(userId);
            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);
            int itemPrice = item.getItemPrice();

            Pet pet = repositoryComponent.getPetRepository().findByUserId(userId);
            String petId = pet.getPetId();

            serviceComponent.getShopService().sellItem(userId, itemPrice, userCashValue);
            serviceComponent.getInventoryService().removeItemById(petId, itemId);
            return "Предмет " + item.getItemName() + " успешно продан!";
        } catch (Exception e) {
            return "✗ Ошибка при продаже предмета:\n" + e.getMessage();
        }
    }

    /**
     * Добавление предмета
     * @param id ID предмета
     * @param name Имя предмета
     * @param price Стоимость предмета
     * @param status Тип статуса
     * @param value Значение восстановления статуса
     */
    private String addItemFromSession(String id, String name, int value, WhatItemRestore status, int price) {
        try {
            Item item = new Item(id, name, value, status, price);
            repositoryComponent.getAllExistingItemsRepository().addItem(item);
            return "Предмет " + item.getItemName() +  " успешно добавлен!";
        } catch (Exception e) {
            return "✗ Ошибка при добавлении предмета:\n" + e.getMessage();
        }
    }

    /**
     * Добавление предмета в магазин
     * @param id ID предмета
     */
    private String addItemToShopFromSession(String id) {
        try {
            List<String> catalog = serviceComponent.getShopService().getCurrentShopCatalog();
            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(id);

            if(catalog.contains(id)){
                return "Такой предмет уже есть в магазине";
            }

            serviceComponent.getShopService().addItemIdToCatalog(id);
            return "Предмет "+ item.getItemName() + " успешно добавлен в магазин!";
        } catch (Exception e) {
            return "✗ Ошибка при добавлении предмета в магазин:\n" + e.getMessage();
        }
    }

    /**
     * @return Приветственное сообщение
     */
    private String getWelcomeMessage() {
        return "Добро пожаловать! " +
                "\nСоздайте профиль и питомца. Для этого введите /add_user и /add_pet";
    }

    /**
     * @return Справка по командам
     */
    private String getHelpMessage() {
        return """
                🐉 Доступные команды для управления драконом:
                
                /start - Приветствие
                /help - Справка
                /games - Показать список доступных игр
                /cancel - Отменить текущий диалог
                
                
                👤 Управление пользователем:
                /add_user - Создать свой профиль
                /wallet - Показать баланс

                
                🐹 Управление питомцем:
                /add_pet - Создать дракона
                /pet_status - Посмотреть статус дракона
                /inventory - Показать инвентарь дракона
                /use_item - Использовать предмет
                
                
                🛒 Магазин:
                /shop - Показать каталог магазина
                /buy_item - Купить предмет
                /sell_item - Продать предмет
                """;
    }

    /**
     * @return Список игр
     */
    private String games() {
        return """
                Список игр:
                  1) Wordle;
                  2) Виселица;
                  3) Однорукий бандит;
                  4) Крестики-нолики.
                """;
    }

    /**
     * @return Каталог магазина
     */
    private String shop() {
        List<String> catalog = serviceComponent.getShopService().getCurrentShopCatalog();

        if(catalog.isEmpty()){
            return "Каталог магазина пуст";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Каталог магазина: \n\n");
        int counter = 1;

        for (String itemId : catalog) {
            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);
            String itemInfo = counter + ") ITEM" + "\n" +
                    "   ID: " + item.getItemId() + "\n" +
                    "   Название: " + item.getItemName() + "\n" +
                    "   Цена: " + item.getItemPrice() + "\n" +
                    "   Тип: " + item.getItemType() + "\n" +
                    "   Восстанавливает: " + item.getItemRegenerationAmount() + "\n\n";
            stringBuilder.append(itemInfo);
            counter += 1;
        }

        return stringBuilder.toString();
    }

    /**
     * @return Все существующие предметы
     */
    private String _allItems() {
        Set<Item> items = repositoryComponent.getAllExistingItemsRepository().getAllExistingItems();

        if(items.isEmpty()){
            return "Список предметов пуст";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Все существующие предметы: \n\n");
        int counter = 1;

        for (Item item : items) {
            String itemInfo = counter + ") ITEM" + "\n" +
                    "   ID: " + item.getItemId() + "\n" +
                    "   Название: " + item.getItemName() + "\n" +
                    "   Цена: " + item.getItemPrice() + "\n" +
                    "   Тип: " + item.getItemType() + "\n" +
                    "   Восстанавливает: " + item.getItemRegenerationAmount() + "\n\n";
            stringBuilder.append(itemInfo);
            counter += 1;
        }

        return stringBuilder.toString();
    }

}
