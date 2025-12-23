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
    private final BasicFunctions basicFunctions;
    private final UserFunctions userFunctions;
    private final PetFunctions petFunctions;
    private final ShopFunctions shopFunctions;
    private final AdminFunctions adminFunctions;

    public TgApiHandler() {
        this.repositoryComponent = new RepositoryComponent();
        this.serviceComponent = new ServiceComponent(repositoryComponent);
        this.userSessions =new HashMap<>();
        this.basicFunctions = new BasicFunctions();
        this.userFunctions = new UserFunctions(repositoryComponent);
        this.petFunctions = new PetFunctions(repositoryComponent, serviceComponent);
        this.shopFunctions = new ShopFunctions(repositoryComponent, serviceComponent);
        this.adminFunctions = new AdminFunctions(repositoryComponent, serviceComponent);

        statusChange();
        repositoryComponent.getAllExistingItemsRepository().packOfItems();
        serviceComponent.getShopService().packOfItems(repositoryComponent.getAllExistingItemsRepository().getAllExistingItems());
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
            case "/start" -> basicFunctions.getWelcomeMessage();
            case "/help" -> basicFunctions.getHelpMessage();
            case "/games" -> basicFunctions.games();
            case "/shop" -> shopFunctions.shop();
            case "/add_user" -> {
                session.setState(UserSession.SessionState.ADD_USER_WAITING_NAME);
                yield "Добавление пользователя\n\nВведите имя пользователя (Tim):";
            }
            case "/wallet" -> userFunctions.walletFromSession(chatId);
            case "/add_pet" -> {
                session.setState(UserSession.SessionState.ADD_PET_WAITING_PET_NAME);
                yield "Добавление питомца\n\nВведите имя питомца (Tigra):";
            }
            case "/pet_status" -> petFunctions.petStatusFromSession(chatId);
            case "/inventory" -> petFunctions.inventoryFromSession(chatId);
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
            case "/_all_items" -> adminFunctions._allItems();
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
                String result = userFunctions.addUserFromSession(id, input);
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
                String result = petFunctions.addPetFromSession(userId, petId, input);
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
                String result = petFunctions.useItemFromSession(petId, input);
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
                String result = shopFunctions.buyItemFromSession(userId, input);
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
                String result = shopFunctions.sellItemFromSession(userId, input);
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
                String result = adminFunctions.addItemFromSession(id, name, value, status, price);
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

                String result = adminFunctions.addItemToShopFromSession(input);
                session.reset();
                yield result;
            }

            default -> {
                session.reset();
                yield "Произошла ошибка. Попробуйте ещё раз";
            }
        };
    }

}
