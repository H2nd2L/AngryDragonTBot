package angryDragon.service;

import java.util.List;

public interface InventoryService {
    void addItemsToPet(String petId, List<String> addedItemsId);

    List<String> showItemsOfPet(String petId);

    boolean takeAwaySomeItemsOfPet(String petId, List<String> takenItemsId);
}
