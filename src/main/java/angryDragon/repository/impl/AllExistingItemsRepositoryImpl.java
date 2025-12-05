package angryDragon.repository.impl;

import angryDragon.domain.item.Item;
import angryDragon.repository.AllExistingItemsRepository;

import java.util.HashSet;
import java.util.List;
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
                if (item.getItemId() == itemId){
                    return item;
                }
            }
        } catch(Exception e){
            System.out.println("Ошибка в AllExistingItems.getItemById: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List <Item> getItemsByIds(List<String> ids){
        try {
            return allItems.stream() .filter(item -> ids.contains(item.getItemId())) .toList();
        } catch (Exception e){
            System.out.println("Ошибка в AllExistingItems.getItemsByIds: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Set<Item> getAllExistingItems(){
        return allItems;
    }

}
