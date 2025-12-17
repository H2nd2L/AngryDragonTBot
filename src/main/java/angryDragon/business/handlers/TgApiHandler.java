package angryDragon.business.handlers;

import angryDragon.business.domain.item.Item;
import angryDragon.business.domain.item.WhatItemRestore;
import angryDragon.business.domain.pet.Pet;
import angryDragon.business.domain.status.Status;
import angryDragon.business.domain.user.User;
import angryDragon.business.domain.wallet.Wallet;
import angryDragon.components.repository.RepositoryComponent;
import angryDragon.components.service.ServiceComponent;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TgApiHandler {

    private final RepositoryComponent repositoryComponent;
    private final ServiceComponent serviceComponent;
    private final Map<Long, UserSession> userSessions;

    public TgApiHandler() {
        this.repositoryComponent = new RepositoryComponent();
        this.serviceComponent = new ServiceComponent(repositoryComponent);
        this.userSessions =new HashMap<>();
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

        // Получаем или создаем сессию пользователя
        UserSession session = userSessions.computeIfAbsent(chatId, k -> new UserSession());

        // Если пользователь в активном диалоге, обрабатываем его состояние
        if (session.getState() != UserSession.SessionState.IDLE) {
            return handleSessionState(session, messageText);
        }

        // Обработка команд
        String trimmedMessage = messageText.trim();

        // Команды для отмены текущего диалога
        if (trimmedMessage.equalsIgnoreCase("/cancel")) {
            session.reset();
            return "Текущий диалог отменён.";
        }

        // Разбираем команду и параметры
        String[] parts = trimmedMessage.split("\\s+", 2);
        String command = parts[0];
        String params = parts.length > 1 ? parts[1] : "";

        return switch (command) {
            case "/start" -> getWelcomeMessage();
            case "/help" -> getHelpMessage();
            case "/games" -> games();
            case "/shop" -> shop();
            case "/all_items" -> allItems();
            case "/add_user" -> {
                if (params.isBlank()) {
                    // Запускаем пошаговый диалог
                    session.setState(UserSession.SessionState.ADD_USER_WAITING_ID);
                    yield "Добавление пользователя\n\nШаг 1/2: Введите ID пользователя (Пример: U7895):";
                } else {
                    yield addUser(params);
                }
            }
            case "/wallet" -> {
                if (params.isBlank()) {
                    session.setState(UserSession.SessionState.SHOW_WALLET_WAITING_ID);
                    yield "Введите ID пользователя (Пример: U7895): ";
                } else {
                    yield wallet(params);
                }
            }
            case "/add_pet" -> {
                if (params.isBlank()) {
                    session.setState(UserSession.SessionState.ADD_PET_WAITING_USER_ID);
                    yield "Добавление питомца\n\nШаг 1/3: Введите ID пользователя (Пример: U7895):";
                } else {
                    yield addPet(params);
                }
            }
            case "/pet_status" -> {
                if (params.isBlank()) {
                    session.setState(UserSession.SessionState.SHOW_PET_STATUS_WAITING_ID);
                    yield "Введите ID питомца (Пример: P4529): ";
                } else {
                    yield petStatus(params);
                }
            }
            case "/inventory" -> {
                if (params.isBlank()) {
                    session.setState(UserSession.SessionState.SHOW_INVENTORY_WAITING_ID);
                    yield "Введите ID питомца (Пример: P4529): ";
                } else {
                    yield inventory(params);
                }
            }
            case "/use_item" -> {
                if (params.isBlank()) {
                    session.setState(UserSession.SessionState.USE_ITEM_WAITING_PET_ID);
                    yield "Использование предмета\n\nШаг 1/2: Введите ID питомца (Пример: P4529):";
                } else {
                    yield useItem(params);
                }
            }
            case "/buy_item" -> {
                if (params.isBlank()) {
                    session.setState(UserSession.SessionState.BUY_ITEM_WAITING_USER_ID);
                    yield "Покупка предмета\n\nШаг 1/2: Введите ID пользователя (Пример: U7895):";
                } else {
                    yield buyItem(params);
                }
            }
            case "/sell_item" -> {
                if (params.isBlank()) {
                    session.setState(UserSession.SessionState.SELL_ITEM_WAITING_USER_ID);
                    yield "Продажа предмета\n\nШаг 1/2: Введите ID пользователя (Пример: U7895):";
                } else {
                    yield sellItem(params);
                }
            }
            case "/add_item" -> {
                if (params.isBlank()) {
                    session.setState(UserSession.SessionState.ADD_ITEM_WAITING_ID);
                    yield "Добавление предмета\n\nШаг 1/5: Введите ID предмета (Пример: I4789):";
                } else {
                    yield addItem(params);
                }
            }
            case "/add_shop" -> {
                if (params.isBlank()) {
                    session.setState(UserSession.SessionState.ADD_SHOP_WAITING_ID);
                    yield "Введите ID предмета (Пример: I4789):";
                } else {
                    yield addItemToShop(params);
                }
            }


            default -> "Неизвестная команда: " + command + "\nВведите /help для списка команд";
        };
    }

    /**
     * Обрабатывает состояние сессии в многошаговом диалоге
     */
    private String handleSessionState(UserSession session, String input) {
        input = input.trim();

        // Отмена текущего диалога
        if (input.equalsIgnoreCase("/cancel")) {
            session.reset();
            return "Диалог отменён.";
        }

        return switch (session.getState()) {
            // Добавление пользователя
            case ADD_USER_WAITING_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID пользователя не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                session.putData("id", input);
                session.setState(UserSession.SessionState.ADD_USER_WAITING_NAME);
                yield "Шаг 2/2: Введите имя пользователя:";
            }
            case ADD_USER_WAITING_NAME -> {
                if (input.isEmpty()) {
                    yield "Ошибка: имя пользователя не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                String id = session.getData("id");
                String result = addUserFromSession(id, input);
                session.reset();
                yield result;
            }

            // Показ баланса пользователя
            case SHOW_WALLET_WAITING_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID пользователя не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                String result = walletFromSession(input);
                session.reset();
                yield result;
            }

            // Добавление питомца
            case ADD_PET_WAITING_USER_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID пользователя не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                session.putData("userId", input);
                session.setState(UserSession.SessionState.ADD_PET_WAITING_PET_ID);
                yield "Шаг 2/3: Введите ID питомца (Пример: P4529):";
            }
            case ADD_PET_WAITING_PET_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID питомца не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                session.putData("petId", input);
                session.setState(UserSession.SessionState.ADD_PET_WAITING_PET_NAME);
                yield "Шаг 3/3: Введите имя питомца (Пример: Tigra):";
            }
            case ADD_PET_WAITING_PET_NAME -> {
                if (input.isEmpty()) {
                    yield "Ошибка: имя питомца не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                String userId = session.getData("userId");
                String petId = session.getData("petId");
                String result = addPetFromSession(userId, petId, input);
                session.reset();
                yield result;
            }

            // Показ статуса питомца
            case SHOW_PET_STATUS_WAITING_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID питомца не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                String result = petStatusFromSession(input);
                session.reset();
                yield result;
            }

            // Показ инвентаря питомца
            case SHOW_INVENTORY_WAITING_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID питомца не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                String result = inventoryFromSession(input);
                session.reset();
                yield result;
            }

            // Использование предмета
            case USE_ITEM_WAITING_PET_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID питомца не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                session.putData("petId", input);
                session.setState(UserSession.SessionState.USE_ITEM_WAITING_ITEM_ID);
                yield "Шаг 2/2: Введите ID предмета:";
            }
            case USE_ITEM_WAITING_ITEM_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                String petId = session.getData("petId");
                String result = useItemFromSession(petId, input);
                session.reset();
                yield result;
            }

            // Покупка предмета
            case BUY_ITEM_WAITING_USER_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID пользователя не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                session.putData("userId", input);
                session.setState(UserSession.SessionState.BUY_ITEM_WAITING_ITEM_ID);
                yield "Шаг 2/2: Введите ID предмета:";
            }
            case BUY_ITEM_WAITING_ITEM_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                String userId = session.getData("userId");
                String result = buyItemFromSession(userId, input);
                session.reset();
                yield result;
            }

            // Продажа предмета
            case SELL_ITEM_WAITING_USER_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID пользователя не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                session.putData("userId", input);
                session.setState(UserSession.SessionState.SELL_ITEM_WAITING_ITEM_ID);
                yield "Шаг 2/2: Введите ID предмета:";
            }
            case SELL_ITEM_WAITING_ITEM_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                String userId = session.getData("userId");
                String result = sellItemFromSession(userId, input);
                session.reset();
                yield result;
            }

            // Добавление предмета
            case ADD_ITEM_WAITING_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                session.putData("id", input);
                session.setState(UserSession.SessionState.ADD_ITEM_WAITING_NAME);
                yield "Шаг 2/5: Введите название предмета:";
            }
            case ADD_ITEM_WAITING_NAME -> {
                if (input.isEmpty()) {
                    yield "Ошибка: название предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                session.putData("name", input);
                session.setState(UserSession.SessionState.ADD_ITEM_WAITING_PRICE);
                yield "Шаг 3/5: Введите цену предмета (1-500):";
            }
            case ADD_ITEM_WAITING_PRICE -> {
                if (input.isEmpty()) {
                    yield "Ошибка: цена предмета не может быть пустой. Попробуйте ещё раз или введите /cancel:";
                }
                session.putData("price", input);
                session.setState(UserSession.SessionState.ADD_ITEM_WAITING_STATUS_TYPE);
                yield "Шаг 4/5: Введите тип статуса предмета (HUNGER | ENERGY | JOY):";
            }
            case ADD_ITEM_WAITING_STATUS_TYPE -> {
                if (input.isEmpty()) {
                    yield "Ошибка: статус предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                session.putData("status", input);
                session.setState(UserSession.SessionState.ADD_ITEM_WAITING_STATUS_VALUE);
                yield "Шаг 5/5: Введите значение статуса (1-100):";
            }
            case ADD_ITEM_WAITING_STATUS_VALUE -> {
                if (input.isEmpty()) {
                    yield "Ошибка: значение статуса не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                String id = session.getData("id");
                String name = session.getData("name");
                int price = Integer.parseInt(session.getData("price"));
                WhatItemRestore status = WhatItemRestore.valueOf(session.getData("status"));
                int value = Integer.parseInt(input);
                String result = addItemFromSession(id, name, value, status, price);
                session.reset();
                yield result;
            }

            // Добавление предмета в магазин
            case ADD_SHOP_WAITING_ID -> {
                if (input.isEmpty()) {
                    yield "Ошибка: ID предмета не может быть пустым. Попробуйте ещё раз или введите /cancel:";
                }
                String result = addItemToShopFromSession(input);
                session.reset();
                yield result;
            }

            default -> {
                session.reset();
                yield "Произошла ошибка. Попробуйте ещё раз.";
            }
        };
    }

    /**
     * Добавление пользователя пошагово
     * @param id ID пользователя
     * @param name Имя пользователя
     */
    private String addUserFromSession(String id, String name) {
        try {
            User user = new User(id, name);
            repositoryComponent.getUsersRepository().addUser(user);
            Wallet wallet = new Wallet(id);
            repositoryComponent.getWalletsRepository().addWallet(wallet);
            return "✓ Пользователь успешно добавлен:\n" +
                    "  ID: " + id + "\n" +
                    "  Имя: " + name + "\n" +
                    "  Баланс: " + wallet.getCashValue();
        } catch (Exception e) {
            return "✗ Ошибка при добавлении пользователя: " + e.getMessage();
        }
    }

    /**
     * Добавление пользователя
     * @param params { ID пользователя | Имя пользователя }
     * @implNote
     * <p> Формат: /add_user UserID|UserName
     * <p> Пример: /add_user U7895|Tim
     */
    private String addUser(String params) {
        if (params.isBlank()) {
            return "Использование: /add_user UserID|UserName\n" +
                    "Пример: /add_user U7895|Tim";
        }

        String[] parts = params.split("\\|", 2);
        if (parts.length < 2) {
            return "Ошибка: неверный формат. Используйте символ | для разделения\n" +
                    "Формат: /add_user UserID|UserName";
        }

        String id = parts[0].trim();
        String name = parts[1].trim();

        if (id.isEmpty() || name.isEmpty()) {
            return "Ошибка: ID и имя не могут быть пустыми";
        }

        try {
            User user = new User(id, name);
            repositoryComponent.getUsersRepository().addUser(user);
            Wallet wallet = new Wallet(id);
            repositoryComponent.getWalletsRepository().addWallet(wallet);
            return "✓ Пользователь успешно добавлен:\n" +
                    "  ID: " + id + "\n" +
                    "  Имя: " + name + "\n" +
                    "  Баланс: " + wallet.getCashValue();
        } catch (Exception e) {
            return "✗ Ошибка при добавлении пользователя: " + e.getMessage();
        }
    }

    /**
     * Показ баланса пользователя пошагово
     * @param id ID пользователя
     * @return Баланс пользователя
     */
    private String walletFromSession(String id) {
        try {
            int cash = repositoryComponent.getWalletsRepository().getUserCashValue(id);
            return "  Баланс пользователя: " + cash;
        } catch (Exception e) {
            return "✗ Ошибка при показе баланса" + e.getMessage();
        }
    }

    /**
     * Показ баланса пользователя
     * @param params ID пользователя
     * @implNote
     * <p> Формат: /wallet UserID
     * <p> Пример: /wallet U7895
     * @return Баланс пользователя
     */
    private String wallet(String params) {
        if (params.isBlank()) {
            return "Использование: /wallet UserID\n" +
                    "Пример: /wallet U7895";
        }

        String id = params.trim();

        if(id.isEmpty()) {
            return "Ошибка: ID не может быть пустым";
        }

        try {
            int cash = repositoryComponent.getWalletsRepository().getUserCashValue(id);
            return "  Баланс пользователя: " + cash;
        } catch (Exception e) {
            return "✗ Ошибка при показе баланса" + e.getMessage();
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
            Pet pet = new Pet(userId, LocalDate.now(), name, petId, new Status());
            repositoryComponent.getPetRepository().addPet(pet);
            return "✓ Питомец успешно добавлен:\n" +
                    "  ID питомца: " + petId + "\n" +
                    "  Имя: " + name + "\n" +
                    "  Дата создания: " + pet.getDateOfCreation();
        } catch (Exception e) {
            return "✗ Ошибка при добавлении питомца: " + e.getMessage();
        }
    }

    /**
     * Добавление питомца
     * @param params { ID пользователя | ID питомца | Имя питомца }
     * @implNote
     * <p> Формат: /add_pet UserID|PetID|PetName
     * <p> Пример: /add_pet U7895|P4529|Tigra
     */
    private String addPet(String params) {
        if (params.isBlank()) {
            return "Использование: /add_pet UserID|PetID|PetName\n" +
                    "Пример: /add_pet U7895|P4529|Tigra";
        }

        String[] parts = params.split("\\|", 3);
        if (parts.length < 3) {
            return "Ошибка: неверный формат. Используйте символ | для разделения\n" +
                    "Формат: /add_pet UserID|PetID|PetName";
        }

        String userId = parts[0].trim();
        String petId = parts[1].trim();
        String name = parts[2].trim();

        if (userId.isEmpty() || petId.isEmpty() || name.isEmpty()) {
            return "Ошибка: ID пользователя, ID питомца и имя не могут быть пустыми";
        }

        try {
            Pet pet = new Pet(userId, LocalDate.now(), name, petId, new Status());
            repositoryComponent.getPetRepository().addPet(pet);
            return "✓ Питомец успешно добавлен:\n" +
                    "  ID питомца: " + petId + "\n" +
                    "  Имя: " + name + "\n" +
                    "  Дата создания: " + pet.getDateOfCreation();
        } catch (Exception e) {
            return "✗ Ошибка при добавлении питомца: " + e.getMessage();
        }
    }

    /**
     * Показ статуса питомца пошагово
     * @param id ID питомца
     * @return Статус питомца
     */
    private String petStatusFromSession(String id) {
        try {
            Pet pet = repositoryComponent.getPetRepository().findByPetId(id);
            Status petStatus = pet.getStatus();
            return "  Энергия: " + petStatus.getEnergy() + "\n" +
                    "  Радость: " + petStatus.getJoy() + "\n" +
                    "  Голод: " + petStatus.getHunger();
        } catch (Exception e) {
            return "✗ Ошибка при показе статуса питомца: " + e.getMessage();
        }
    }

    /**
     * Показ статуса питомца
     * @param params ID пользователя
     * @implNote
     * <p> Формат: /pet_status PetID
     * <p> Пример: /pet_status P4529
     * @return Статус питомца
     */
    private String petStatus(String params) {
        if (params.isBlank()) {
            return "Использование: /pet_status PetID\n" +
                    "Пример: /pet_status P4529";
        }

        String id = params.trim();

        if(id.isEmpty()) {
            return "Ошибка: ID питомца не может быть пустым";
        }

        try {
            Pet pet = repositoryComponent.getPetRepository().findByPetId(id);
            Status petStatus = pet.getStatus();
            return "  Энергия: " + petStatus.getEnergy() + "\n" +
                    "  Радость: " + petStatus.getJoy() + "\n" +
                    "  Голод: " + petStatus.getHunger();
        } catch (Exception e) {
            return "✗ Ошибка при показе статуса питомца: " + e.getMessage();
        }
    }

    /**
     * Показ инвентаря питомца пошагово
     * @param id ID питомца
     * @return Инвентарь питомца
     */
    private String inventoryFromSession(String id) {
        try {
            List<String> itemIds = serviceComponent.getInventoryService().showItemsOfPet(id);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Инвентарь питомца: \n\n");
            int counter = 1;

            for (String itemId : itemIds) {
                Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);
                String itemInfo = counter + ")ITEM" + "\n" +
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
            return "✗ Ошибка при показе инвентаря питомца: " + e.getMessage();
        }
    }

    /**
     * Показ инвентаря питомца
     * @param params ID пользователя
     * @implNote
     * <p> Формат: /inventory PetID
     * <p> Пример: /inventory P4529
     * @return Инвентарь питомца
     */
    private String inventory(String params) {
        if (params.isBlank()) {
            return "Использование: /inventory PetID\n" +
                    "Пример: /inventory P4529";
        }

        String id = params.trim();

        if(id.isEmpty()) {
            return "Ошибка: ID питомца не может быть пустым";
        }

        try {
            List<String> itemIds = serviceComponent.getInventoryService().showItemsOfPet(id);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Инвентарь питомца: \n\n");
            int counter = 1;

            for (String itemId : itemIds) {
                Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);
                String itemInfo = counter + ")ITEM" + "\n" +
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
            return "✗ Ошибка при показе инвентаря питомца: " + e.getMessage();
        }
    }

    /**
     * Использование предмета пошагово
     * @param petId ID питомца
     * @param itemId ID предмета
     */
    private String useItemFromSession(String petId, String itemId) {
        try {
            List<String> items = serviceComponent.getInventoryService().showItemsOfPet(petId);
            if (!items.contains(itemId)) {
                return "У питомца нет такого предмета";
            }

            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);
            if (item == null) {
                return "Такого предмета не существует";
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
                    message = "Предмет использован! Голод теперь: " + newStatusValue + " единиц";
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
            return "✗ Ошибка при использовании предмета: " + e.getMessage();
        }
    }

    /**
     * Использование предмета
     * @param params { ID питомца | ID предмета }
     * @implNote
     * <p> Формат: /use_item PetID|ItemId
     * <p> Пример: /use_item P4529|I4789
     */
    private String useItem(String params) {
        if (params.isBlank()) {
            return "Использование: /use_item PetID|ItemId\n" +
                    "Пример: /use_item P4529|I4789";
        }

        String[] parts = params.split("\\|", 2);
        if (parts.length < 2) {
            return "Ошибка: неверный формат. Используйте символ | для разделения\n" +
                    "Формат: /use_item PetID|ItemId";
        }

        String petId = parts[0].trim();
        String itemId = parts[1].trim();

        if (petId.isEmpty() || itemId.isEmpty()) {
            return "Ошибка: ID питомца и ID предмета не могут быть пустыми";
        }

        try {
            List<String> items = serviceComponent.getInventoryService().showItemsOfPet(petId);
            if (!items.contains(itemId)) {
                return "У питомца нет такого предмета";
            }

            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);
            if (item == null) {
                return "Такого предмета не существует";
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
                    message = "Предмет использован! Голод теперь: " + newStatusValue + " единиц";
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
            return "✗ Ошибка при использовании предмета: " + e.getMessage();
        }
    }

    /**
     * Покупка предмета пошагово
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
            return "✗ Ошибка при покупке предмета: " + e.getMessage();
        }
    }

    /**
     * Покупка предмета
     * @param params { ID пользователя | ID предмета }
     * @implNote
     * <p> Формат: /buy_item UserID|ItemID
     * <p> Пример: /buy_item U7895|I4789
     */
    private String buyItem(String params) {
        if (params.isBlank()) {
            return "Использование: /buy_item UserID|ItemID\n" +
                    "Пример: /buy_item U7895|I4789";
        }

        String[] parts = params.split("\\|", 2);
        if (parts.length < 2) {
            return "Ошибка: неверный формат. Используйте символ | для разделения\n" +
                    "Формат: /buy_item UserID|ItemID";
        }

        String userId = parts[0].trim();
        String itemId = parts[1].trim();

        if (userId.isEmpty() || itemId.isEmpty()) {
            return "Ошибка: ID пользователя и ID предмета не могут быть пустыми";
        }

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
            return "✗ Ошибка при покупке предмета: " + e.getMessage();
        }
    }

    /**
     * Продажа предмета пошагово
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
            return "✗ Ошибка при продаже предмета: " + e.getMessage();
        }
    }

    /**
     * Продажа предмета
     * @param params { ID пользователя | ID предмета }
     * @implNote
     * <p> Формат: /sell_item UserID|ItemID
     * <p> Пример: /sell_item U7895|I4789
     */
    private String sellItem(String params) {
        if (params.isBlank()) {
            return "Использование: /sell_item UserID|ItemID\n" +
                    "Пример: /sell_item U7895|I4789";
        }

        String[] parts = params.split("\\|", 2);
        if (parts.length < 2) {
            return "Ошибка: неверный формат. Используйте символ | для разделения\n" +
                    "Формат: /sell_item UserID|ItemID";
        }

        String userId = parts[0].trim();
        String itemId = parts[1].trim();

        if (userId.isEmpty() || itemId.isEmpty()) {
            return "Ошибка: ID пользователя и ID предмета не могут быть пустыми";
        }

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
            return "✗ Ошибка при продаже предмета: " + e.getMessage();
        }
    }

    /**
     * Добавление предмета пошагово
     * @param id ID предмета
     * @param name Имя предмета
     * @param price Стоимость предмета
     * @param status Тип статуса
     * @param value Значение восстановления статуса
     */
    private String addItemFromSession(String id, String name, int price, WhatItemRestore status, int value) {
        try {
            Item item = new Item(id, name, value, status, price);
            repositoryComponent.getAllExistingItemsRepository().addItem(item);
            return "Предмет " + item.getItemName() +  " успешно добавлен!";
        } catch (Exception e) {
            return "✗ Ошибка при добавлении предмета: " + e.getMessage();
        }
    }

    /**
     * Добавление предмета
     * <p> Стоимость может варьироваться от 0 до 500
     * <p> Значение восст. статуса — от 0 до 100
     * @param params { ID предмета | Название предмета | Стоимость | Тип статуса | Значение восстановления статуса }
     * @implNote
     * <p> Формат: /add_item ItemID|ItemName|Price|StatusType|StatusValue
     * <p> Пример: /add_item I4789|Potato|15|HUNGER|10
     */
    private String addItem(String params) {
        if (params.isBlank()) {
            return "Использование: /add_item ItemID|ItemName|Price|StatusType|StatusValue\n" +
                    "Пример: /add_item I4789|Potato|15|HUNGER|10";
        }

        String[] parts = params.split("\\|", 5);
        if (parts.length < 5) {
            return "Ошибка: неверный формат. Используйте символ | для разделения\n" +
                    "Формат: /add_item ItemID|ItemName|Price|StatusType|StatusValue";
        }

        String id = parts[0].trim();
        String name = parts[1].trim();
        String priceStr = parts[2].trim();
        int price = Integer.parseInt(priceStr);
        String statusStr = parts[3].trim();
        WhatItemRestore status = WhatItemRestore.valueOf(statusStr);
        String valueStr = parts[4].trim();
        int value = Integer.parseInt(valueStr);

        if (id.isEmpty() || name.isEmpty() || priceStr.isEmpty() || statusStr.isEmpty() || valueStr.isEmpty()) {
            return "Ошибка: параметры не могут быть пустыми";
        }

        try {
            Item item = new Item(id, name, value, status, price);
            repositoryComponent.getAllExistingItemsRepository().addItem(item);
            return "Предмет " + item.getItemName() +  " успешно добавлен!";
        } catch (Exception e) {
            return "✗ Ошибка при добавлении предмета: " + e.getMessage();
        }
    }

    /**
     * Добавление предмета в магазин пошагово
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
            return "✗ Ошибка при добавлении предмета в магазин: " + e.getMessage();
        }
    }

    /**
     * Добавление предмета в магазин
     * @param params ID питомца
     * @implNote
     * <p> Формат: /add_shop ItemID
     * <p> Пример: /add_shop I4789
     */
    private String addItemToShop(String params) {
        if (params.isBlank()) {
            return "Использование: /add_shop ItemID\n" +
                    "Пример: /add_shop I4789";
        }

        String id = params.trim();

        if(id.isEmpty()) {
            return "Ошибка: ID предмета не может быть пустым";
        }

        try {
            List<String> catalog = serviceComponent.getShopService().getCurrentShopCatalog();
            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(id);

            if(catalog.contains(id)){
                return "Такой предмет уже есть в магазине";
            }

            serviceComponent.getShopService().addItemIdToCatalog(id);
            return "Предмет "+ item.getItemName() + " успешно добавлен в магазин!";
        } catch (Exception e) {
            return "✗ Ошибка при добавлении предмета в магазин: " + e.getMessage();
        }
    }

    /**
     * @return Приветственное сообщение
     */
    private String getWelcomeMessage() {
        return "Добро пожаловать! " +
                "\nСоздайте пользователя и питомца. Для этого введите /help для списка команд.";
    }

    /**
     * @return Справка по командам
     */
    private String getHelpMessage() {
        return """
                🐉 Доступные команды для управления драконом:
                
                
                /start - Приветствие
                /help - Справка
                /cancel - Отменить текущий диалог
                /games - Показать список доступных игр
                
                
                • Управление пользователем:
                /add_user - Добавить пользователя
                    /add_user UserID|UserName
                    Пример: /add_user U7895|Tim
                /wallet - Показать баланс пользователя
                    /wallet UserID
                    Пример: /wallet U7895
                
                
                • Управление питомцем:
                /add_pet - Добавить питомца
                    /add_pet UserID|PetID|PetName
                    Пример: /add_pet U7895|P4529|Tigra
                /pet_status - Посмотреть статус питомца
                    /pet_status PetID
                    Пример: /pet_status P4529
                /inventory - Показать инвентарь питомца
                    /inventory PetID
                    Пример: /inventory P4529
                /use_item - Использовать предмет
                    /use_item PetID|ItemId
                    Пример: /use_item P4529|I4789
                
                
                • Магазин и предметы:
                /all_items - Показать все предметы
                /add_item - Добавить предмет
                    /add_item ItemID|ItemName|Price|StatusType|StatusValue
                       Price = {1, ..., 500}
                       StatusType = { HUNGER | ENERGY | JOY }
                       StatusValue = {1, ..., 100}
                    Пример: /add_item I4789|Potato|15|HUNGER|10
                /shop - Показать каталог магазина
                /buy_item - Купить предмет
                    /buy_item UserID|ItemID
                    Пример: /buy_item U7895|I4789
                /sell_item - Продать предмет
                    /sell_item UserID|ItemID
                    Пример: /sell_item U7895|I4789
                /add_shop - Добавить предмет в магазин
                    /add_shop ItemID
                    Пример: /add_shop I4789
                
                
                ID пользователя должен начинаться с U (Пример: U7895)
                ID питомца должен начинаться с P (Пример: P4529)
                ID предмета должен начинаться с I (Пример: I4789)
                
                Почти у каждой команды есть быстрый и пошаговый режим:
                    • быстрый режим приведён в примерах;
                    • пошаговый режим запускается, если команде не переданы аргументы.
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
        StringBuilder stringBuilder = new StringBuilder();
        int counter = 1;
        stringBuilder.append("  Каталог магазина: \n\n");

        for (String itemId : catalog) {
            Item item = repositoryComponent.getAllExistingItemsRepository().getItemById(itemId);
            String itemInfo = counter + ")ITEM" + "\n" +
                    "  ID: " + item.getItemId() + "\n" +
                    "  Название: " + item.getItemName() + "\n" +
                    "  Цена: " + item.getItemPrice() + "\n" +
                    "  Тип: " + item.getItemType() + "\n" +
                    "  Восстанавливает: " + item.getItemRegenerationAmount() + "\n\n";
            stringBuilder.append(itemInfo);
            counter += 1;
        }

        return stringBuilder.toString();
    }

    /**
     * @return Все существующие предметы
     */
    private String allItems() {
        Set<Item> items = repositoryComponent.getAllExistingItemsRepository().getAllExistingItems();

        if(items.isEmpty()){
            return "Список предметов пуст";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Все существующие предметы: \n\n");

        int counter = 1;
        for (Item item : items) {
            String itemInfo = counter + ")ITEM" + "\n" +
                    "  ID: " + item.getItemId() + "\n" +
                    "  Название: " + item.getItemName() + "\n" +
                    "  Цена: " + item.getItemPrice() + "\n" +
                    "  Тип: " + item.getItemType() + "\n" +
                    "  Восстанавливает: " + item.getItemRegenerationAmount() + "\n\n";
            stringBuilder.append(itemInfo);
            counter += 1;
        }

        return stringBuilder.toString();
    }

}
