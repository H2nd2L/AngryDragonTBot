package angryDragon.business.handlers.tgApiHandler;

import angryDragon.business.domain.item.Item;
import angryDragon.business.domain.pet.Pet;
import angryDragon.business.domain.status.Status;
import angryDragon.components.repository.RepositoryComponent;
import angryDragon.components.service.ServiceComponent;

import java.time.LocalDate;
import java.util.List;

public class PetFunctions {
    private final RepositoryComponent repositoryComponent;
    private final ServiceComponent serviceComponent;

    public PetFunctions(RepositoryComponent repositoryComponent, ServiceComponent serviceComponent){
        this.repositoryComponent = repositoryComponent;
        this.serviceComponent = serviceComponent;
    }

    /**
     * Добавление питомца
     * @param userId ID пользователя
     * @param petId ID питомца
     * @param name Имя питомца
     */
    String addPetFromSession(String userId, String petId, String name) {
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
    String petStatusFromSession(long chatId) {
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
    String inventoryFromSession(long chatId) {
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
    String useItemFromSession(String petId, String itemId) {
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
}
