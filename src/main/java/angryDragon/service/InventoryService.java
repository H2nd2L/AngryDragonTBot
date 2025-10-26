package angryDragon.service;

import java.util.List;

public interface InventoryService {
    // добавить предметы в инвентарь
    void addItemsToPet(String petId, List<String> addedItemsId);

    // показать ID предметов из инвентаря определённого питомца
    List<String> showItemsOfPet(String petId);

    // убрать вещи из инвентаря
    boolean takeAwaySomeItemsOfPet(String petId, List<String> takenItemsId);
}
