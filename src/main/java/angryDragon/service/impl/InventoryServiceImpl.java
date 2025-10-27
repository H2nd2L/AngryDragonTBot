package angryDragon.service.impl;

import angryDragon.service.InventoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InventoryServiceImpl implements InventoryService {
    //HashMap<PetId, List[ItemId]>
    HashMap<String, List <String>> itemsOfPet = new HashMap<>();

    @Override
    public void addItemsToPet(String petId, List<String> addedItemsId){
        try{
            //itemsOfPet.putIfAbsent();
            itemsOfPet.merge(petId, addedItemsId,
                    (oldList, newList) -> {oldList.addAll(newList);
                    return oldList;
            });
        } catch (Exception e){
            System.out.println("Ошибка в InventoryService.addItemsToPet: " + e.getMessage());
        }
    }

    // показать ID предметов из инвентаря определённого питомца
    @Override
    public List<String> showItemsOfPet(String petId){
        try{
            return itemsOfPet.getOrDefault(petId, new ArrayList<>());
        } catch(Exception e){
            System.out.println("Ошибка в InventoryService.getItemsOfPet: " + e.getMessage());
        }

        return List.of();
    }

    // убрать вещи из инвентаря
    @Override
    public boolean takeAwaySomeItemsOfPet(String petId, List<String> takenItemsId){
        try{
            List<String> itemsId = itemsOfPet.get(petId);
            int currentSize = itemsId.size();
            itemsId.removeIf(takenItemsId::contains);
            return currentSize != itemsId.size();
        } catch(Exception e){
            System.out.println("Ошибка в InventoryService.takeAwaySomeItemsOfPet: " + e.getMessage());
        }

        return false;
    }
}

