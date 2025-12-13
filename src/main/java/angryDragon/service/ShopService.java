package angryDragon.service;

import java.util.List;

public interface ShopService {
    List<String> getCurrentShopCatalog();

    void addItemIdToCatalog(String itemId);

    void buyItem(String userId, int itemPrice, int userCash);

    void sellItem(String userId, int itemPrice, int userCash);
}
