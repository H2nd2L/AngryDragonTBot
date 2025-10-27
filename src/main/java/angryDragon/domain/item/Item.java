package angryDragon.domain.item;

public class Item {
    private final long itemId;
    private final String itemName;
    private final int itemRegenerationAmount;
    private final WhatItemRestore whatItemRestore;
    private final int itemPrice;

    public Item(long itemId, String itemName, int itemValue, WhatItemRestore whatItemRestore, int itemPrice){
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemRegenerationAmount = itemValue;
        this.whatItemRestore = whatItemRestore;
        this.itemPrice = itemPrice;
    }

    public long getItemId() {
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
