package angryDragon.repository;

import angryDragon.domain.item.Item;

import java.util.List;
import java.util.Set;

public interface AllExistingItemsRepository {
    void addItem(Item item);

    Item getItemById(long itemId);

    List<Item> getItemsByIds(List<Long> ids);

    Set<Item> getAllExistingItems();
}