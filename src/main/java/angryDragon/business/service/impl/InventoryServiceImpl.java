package angryDragon.business.service.impl;

import angryDragon.business.service.InventoryService;

import java.util.*;


public class InventoryServiceImpl implements InventoryService {
    Map<String, List <String>> itemsOfPet = new HashMap<>();

    @Override
    public void addItemToPet(String petId, String addedItemId){
        try{
            itemsOfPet.computeIfAbsent(petId, k -> new ArrayList<>())
                    .add(addedItemId);
        } catch (Exception e){
            System.out.println("Ошибка в InventoryService.addItemsToPet: " + e.getMessage());
        }
    }

    @Override
    public List<String> showItemsOfPet(String petId){
        try{
            return itemsOfPet.getOrDefault(petId, new ArrayList<>());
        } catch(Exception e){
            System.out.println("Ошибка в InventoryService.getItemsOfPet: " + e.getMessage());
        }

        return List.of();
    }

    @Override
    public void removeItemById(String petId, String takenItemId){
        try{
            List<String> itemsId = itemsOfPet.get(petId);
            itemsId.remove(takenItemId);
        } catch(Exception e){
            System.out.println("Ошибка в InventoryService.takeAwaySomeItemsOfPet: " + e.getMessage());
        }
    }
}
