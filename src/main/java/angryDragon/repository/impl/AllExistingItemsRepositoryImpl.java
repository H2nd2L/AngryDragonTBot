package angryDragon.repository.impl;

import angryDragon.domain.item.Item;
import angryDragon.repository.AllExistingItemsRepository;

import java.util.ArrayList;
import java.util.List;

public class AllExistingItemsRepositoryImpl implements AllExistingItemsRepository {
    private final List<Item> allItems = new ArrayList<>();

    @Override
    public void addItem(Item item){
        allItems.add(item);
    }

    @Override
    public Item getItemById(long itemId){
        try{
            for (Item item : allItems){
                if (item.getItemId() == itemId){
                    return item;
                }
            }
        } catch(Exception e){ // написать в будущем разные кетчи
            System.out.println("Ошибка в AllExistingItems.getItemById: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List <Item> getItemsByIds(List<Long> itemsIds){
        try {
            List <Item> returningItems = new ArrayList<>();
            for (long itemId : itemsIds){
                for (Item item : allItems){
                    if (item.getItemId() == itemId){
                        returningItems.add(item);
                        break;
                    }
                }
            }
            return returningItems;
        } catch (Exception e){ // написать в будущем разные кетчи
            System.out.println("Ошибка в AllExistingItems.getItemsByIds: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Item> getInventory(){
        return allItems;
    }

}
