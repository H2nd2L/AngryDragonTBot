package angryDragon.business.service;

import angryDragon.business.domain.item.Item;

import java.util.List;
import java.util.Set;

public interface ShopService {
    Set<String> getCurrentShopCatalog();

    void addItemIdToCatalog(String itemId);

    void buyItem(String userId, int itemPrice, int userCash);

    void sellItem(String userId, int itemPrice, int userCash);

}
