package angryDragon.business.handlers.tgApiHandler;

import angryDragon.business.domain.item.Item;
import angryDragon.business.domain.item.WhatItemRestore;
import angryDragon.components.repository.RepositoryComponent;
import angryDragon.components.service.ServiceComponent;

import java.util.List;
import java.util.Set;

public class AdminFunctions {
    private final RepositoryComponent repositoryComponent;
    private final ServiceComponent serviceComponent;

    public AdminFunctions(RepositoryComponent repositoryComponent, ServiceComponent serviceComponent){
        this.repositoryComponent = repositoryComponent;
        this.serviceComponent = serviceComponent;
    }

    /**
     * Добавление предмета
     * @param id ID предмета
     * @param name Имя предмета
     * @param price Стоимость предмета
     * @param status Тип статуса
     * @param value Значение восстановления статуса
     */
    String addItemFromSession(String id, String name, int value, WhatItemRestore status, int price) {
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
    String addItemToShopFromSession(String id) {
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
     * @return Все существующие предметы
     */
    String _allItems() {
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
