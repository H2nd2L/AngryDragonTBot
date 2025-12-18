package angryDragon.business.service;

import java.util.List;

public interface InventoryService {
    void addItemToPet(String petId, String addedItemId);

    List<String> showItemsOfPet(String petId);

    void removeItemById(String petId, String takenItemId);
}
