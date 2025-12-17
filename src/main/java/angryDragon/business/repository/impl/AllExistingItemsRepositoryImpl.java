package angryDragon.business.repository.impl;

import angryDragon.business.domain.item.Item;
import angryDragon.business.repository.AllExistingItemsRepository;

import java.util.HashSet;
import java.util.Set;

public class AllExistingItemsRepositoryImpl implements AllExistingItemsRepository {
    private final Set<Item> allItems = new HashSet<>();

    @Override
    public void addItem(Item item){
        allItems.add(item);
    }

    @Override
    public Item getItemById(String itemId){
        try{
            for (Item item : allItems){
                if (item.getItemId().equals(itemId)){
                    return item;
                }
            }
        } catch(Exception e){
            System.out.println("Ошибка в AllExistingItems.getItemById: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Set<Item> getAllExistingItems(){
        return allItems;
    }

}
