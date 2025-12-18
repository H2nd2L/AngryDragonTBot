package angryDragon.business.domain.item;

public class Item {
    private final String itemId;
    private final String itemName;
    private final int itemRegenerationAmount;
    private final WhatItemRestore whatItemRestore;
    private final int itemPrice;

    public Item(String itemId, String itemName, int itemValue, WhatItemRestore whatItemRestore, int itemPrice){
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemRegenerationAmount = itemValue;
        this.whatItemRestore = whatItemRestore;
        this.itemPrice = itemPrice;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemRegenerationAmount() {
        return itemRegenerationAmount;
    }

    public WhatItemRestore getItemType() {
        return whatItemRestore;
    }

    public int getItemPrice() {
        return itemPrice;
    }
}
