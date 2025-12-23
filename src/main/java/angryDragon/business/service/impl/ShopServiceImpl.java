package angryDragon.business.service.impl;

import angryDragon.business.domain.item.Item;
import angryDragon.business.repository.AllExistingItemsRepository;
import angryDragon.business.repository.WalletsRepository;
import angryDragon.business.service.ShopService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ShopServiceImpl implements ShopService {
    private final List<String> currentShopCatalog = new ArrayList<>();
    private final AllExistingItemsRepository allExistingItemsRepository;
    private final WalletsRepository walletsRepository;

    public ShopServiceImpl(AllExistingItemsRepository allExistingItemsRepository, WalletsRepository walletsRepository) {
        this.allExistingItemsRepository = allExistingItemsRepository;
        this.walletsRepository = walletsRepository;
    }

    @Override
    public List<String> getCurrentShopCatalog(){
        return currentShopCatalog;
    }

    @Override
    public void addItemIdToCatalog(String itemId){
        currentShopCatalog.add(itemId);
    }

    @Override
    public void buyItem(String userId, int itemPrice, int userCash){
        walletsRepository.setUserCashValue(userId, (userCash-itemPrice));
    }

    @Override
    public void sellItem(String userId, int itemPrice, int userCash){
        walletsRepository.setUserCashValue(userId, (userCash+itemPrice));
    }

    @Override
    public void packOfItems(Set<Item> items) {
        for (Item item : items) {
            currentShopCatalog.add(item.getItemId());
        }
    }
}