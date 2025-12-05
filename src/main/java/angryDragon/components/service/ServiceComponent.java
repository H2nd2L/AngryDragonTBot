package angryDragon.components.service;

import angryDragon.components.repository.RepositoryComponent;
import angryDragon.service.InventoryService;
import angryDragon.service.ShopService;
import angryDragon.service.impl.InventoryServiceImpl;
import angryDragon.service.impl.ShopServiceImpl;


public class ServiceComponent {
    private final InventoryService inventoryService;
    private final ShopService shopService;

    public ServiceComponent(RepositoryComponent repositoryComponent){
        this.inventoryService = new InventoryServiceImpl(); // нужен ли здесь inventory
        this.shopService = new ShopServiceImpl(
                repositoryComponent.getAllExistingItemsRepository(),
                repositoryComponent.getWalletsRepository()
        );
    }

    public ShopService getShopService(){
        return shopService;
    }
    public InventoryService getInventoryService(){
        return inventoryService;
    }
}
