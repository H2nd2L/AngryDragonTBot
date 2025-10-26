package angryDragon.repository;

import angryDragon.domain.item.Item;

import java.util.List;

public interface AllExistingItemsRepository {
    void addItem(Item item);

    Item getItemById(long itemId);

    List<Item> getItemsByIds(List<Long> idsOfItems);

    List<Item> getInventory();
}
