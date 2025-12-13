package angryDragon.repository;

import angryDragon.domain.item.Item;

import java.util.List;
import java.util.Set;

public interface AllExistingItemsRepository {

    void addItem(Item item);

    Item getItemById(String itemId);

    Set<Item> getAllExistingItems();
}