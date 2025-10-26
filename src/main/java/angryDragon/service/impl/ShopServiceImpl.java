package angryDragon.service.impl;

import angryDragon.domain.item.Item;
import angryDragon.repository.AllExistingItemsRepository;
import angryDragon.repository.WalletsRepository;
import angryDragon.service.ShopService;

import java.util.ArrayList;
import java.util.List;

public class ShopServiceImpl implements ShopService {
    private final List<Long> currentShopCatalog = new ArrayList<>();
    private final AllExistingItemsRepository allExistingItemsRepository;
    private final WalletsRepository walletsRepository;

    public ShopServiceImpl(AllExistingItemsRepository allExistingItemsRepository, WalletsRepository walletsRepository) {
        this.allExistingItemsRepository = allExistingItemsRepository;
        this.walletsRepository = walletsRepository;
    }

    @Override
    public List<Long> getCurrentShopCatalog(){
        return currentShopCatalog;
    }

    @Override
    public void addItemIdToCatalog(long itemId){
        currentShopCatalog.add(itemId);
    }

    @Override
    public String buyItem(long itemId, String userId){
        Item currentItem = allExistingItemsRepository.getItemById(itemId); // сделать проверку на наличие товара в магазине

        if (currentItem == null){
            return "This item does not exist.";
        }

        if(!currentShopCatalog.contains(currentItem.getItemId())){
            return "You can not buy this item.";
        }

        int itemPrice = currentItem.getItemPrice();
        int userCash = walletsRepository.getUserCashValue(userId);

        if (userCash == -1){
            return "This user does not exist.";
        }

        if (userCash - itemPrice < 0){
            return "Not enough money.";
        }

        walletsRepository.setUserCashValue(userId, (userCash-itemPrice));
        return "Transaction complete.";
    }

    @Override
    public String sellItem(long itemId, String userId){
        Item currentItem = allExistingItemsRepository.getItemById(itemId);

        if (currentItem == null){
            return "This item does not exist.";
        }

        int itemPrice = currentItem.getItemPrice();
        int userCash = walletsRepository.getUserCashValue(userId);

        if (userCash == -1){
            return "This user does not exist.";
        }

        walletsRepository.setUserCashValue(userId, (userCash+itemPrice));
        return "Transaction complete.";
    }


}
