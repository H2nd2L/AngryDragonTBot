package angryDragon.business.handlers.tgApiHandler;

import angryDragon.business.domain.item.Item;
import angryDragon.business.domain.pet.Pet;
import angryDragon.components.repository.RepositoryComponent;
import angryDragon.components.service.ServiceComponent;

import java.util.List;
import java.util.Set;

public class ShopFunctions {
    private final RepositoryComponent repositoryComponent;
    private final ServiceComponent serviceComponent;

    public ShopFunctions(RepositoryComponent repositoryComponent, ServiceComponent serviceComponent){
        this.repositoryComponent = repositoryComponent;
        this.serviceComponent = serviceComponent;
    }

    /**
     * Покупка предмета
     * @param userId ID пользователя
     * @param itemId ID предмета
     */
    String buyItemFromSession(String userId, String itemId) {
        try {
            Set<String> catalog = serviceComponent.getShopService().getCurrentShopCatalog();
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
    String sellItemFromSession(String userId, String itemId) {
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
     * @return Каталог магазина
     */
    String shop() {
        Set<String> catalog = serviceComponent.getShopService().getCurrentShopCatalog();

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
}
