package angryDragon.business.repository;

import angryDragon.business.domain.item.Item;

import java.util.Set;

public interface AllExistingItemsRepository {

    void addItem(Item item);

    Item getItemById(String itemId);

    Set<Item> getAllExistingItems();
}