package angryDragon.service;

import java.util.List;

public interface ShopService {
    List<String> getCurrentShopCatalog();

    void addItemIdToCatalog(String itemId);

    String buyItem(String itemId, String userId);

    String sellItem(String itemId, String userId);
}
