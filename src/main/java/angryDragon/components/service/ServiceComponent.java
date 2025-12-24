package angryDragon.components.service;

import angryDragon.components.repository.RepositoryComponent;
import angryDragon.business.service.InventoryService;
import angryDragon.business.service.ShopService;
import angryDragon.business.service.impl.InventoryServiceImpl;
import angryDragon.business.service.impl.ShopServiceImpl;


public class ServiceComponent {
    private final InventoryService inventoryService;
    private final ShopService shopService;

    public ServiceComponent(RepositoryComponent repositoryComponent){
        this.inventoryService = new InventoryServiceImpl();
        this.shopService = new ShopServiceImpl(
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
