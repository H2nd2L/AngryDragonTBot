package angryDragon.service;

import java.util.List;

public interface ShopService {
    List<Long> getCurrentShopCatalog();

    void addItemIdToCatalog(long itemId);

    String buyItem(long itemId, String userId);

    String sellItem(long itemId, String userId);
}
