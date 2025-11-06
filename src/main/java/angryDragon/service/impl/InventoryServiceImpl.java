package angryDragon.service.impl;

import angryDragon.service.InventoryService;

import java.util.*;


public class InventoryServiceImpl implements InventoryService {
    //HashMap<PetId, List[ItemId]>
    Map<String, List <String>> itemsOfPet = new HashMap<>();

    @Override
    public void addItemsToPet(String petId, List<String> itemsIdsToAdd){
        try{
            itemsOfPet.computeIfAbsent(petId, k -> new ArrayList<>())
                    .addAll(itemsIdsToAdd);
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
    public boolean removeItemsByIds(String petId, List<String> takenItemsId){
        try{
            List<String> itemsId = itemsOfPet.get(petId);
            if (itemsId == null || itemsId.isEmpty()) {
                return false;
            }

            return itemsId.removeAll(takenItemsId);
        } catch(Exception e){
            System.out.println("Ошибка в InventoryService.takeAwaySomeItemsOfPet: " + e.getMessage());
        }

        return false;
    }
}
