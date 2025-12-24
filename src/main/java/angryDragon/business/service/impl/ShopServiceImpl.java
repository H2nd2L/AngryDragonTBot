package angryDragon.business.service.impl;

import angryDragon.business.repository.AllExistingItemsRepository;
import angryDragon.business.repository.WalletsRepository;
import angryDragon.business.service.ShopService;

import java.util.HashSet;
import java.util.Set;


public class ShopServiceImpl implements ShopService {
    private final Set<String> currentShopCatalog = new HashSet<>();
    private final WalletsRepository walletsRepository;

    public ShopServiceImpl(WalletsRepository walletsRepository) {
        this.walletsRepository = walletsRepository;
    }

    @Override
    public Set<String> getCurrentShopCatalog(){
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
}
