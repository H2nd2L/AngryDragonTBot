package angryDragon.business.addition;

import angryDragon.business.domain.item.Item;
import angryDragon.business.repository.impl.AllExistingItemsRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

import static angryDragon.business.domain.item.WhatItemRestore.ENERGY;
import static angryDragon.business.domain.item.WhatItemRestore.HUNGER;
import static angryDragon.business.domain.item.WhatItemRestore.JOY;


public class PackOfItems {

    public PackOfItems(){
        List<Item> items = new ArrayList<>();

        Item potato = new Item("I13853", "Potato", 7, HUNGER, 10);
        items.add(potato);
        Item chips = new Item("I13575", "Chips Bay's", 20, HUNGER, 30);
        items.add(chips);
        Item apple = new Item("I11124", "Apple", 12, HUNGER, 18);
        items.add(apple);
        Item bread = new Item("I10701", "Bread", 4, HUNGER, 7);
        items.add(bread);
        Item steak = new Item("I12004", "Steak", 35, HUNGER, 40);
        items.add(steak);
        Item chicken = new Item("I10465", "Roasted Chicken", 28, HUNGER, 35);
        items.add(chicken);
        Item fish = new Item("I16767", "Grilled Fish", 24, HUNGER, 29);
        items.add(fish);
        Item banana = new Item("I10207", "Banana", 12, HUNGER, 18);
        items.add(banana);
        Item carrot = new Item("I10078", "Carrot", 8, HUNGER, 13);
        items.add(carrot);
        Item orange = new Item("I19009", "Orange", 14, HUNGER, 21);
        items.add(orange);

        Item energyDrink1 = new Item("I81029", "Energy drink Wonster", 25, ENERGY, 30);
        items.add(energyDrink1);
        Item energyDrink2 = new Item("I45703", "Energy drink BlueBull", 50, ENERGY, 45);
        items.add(energyDrink2);
        Item matcha = new Item("I10014", "Matcha", 35, ENERGY, 38);
        items.add(matcha);
        Item coffee = new Item("I10618", "coffee", 30, ENERGY, 34);
        items.add(coffee);
        Item chocolateBar = new Item("I10015", "Chocolate Bar Nikers", 14, ENERGY, 19);
        items.add(chocolateBar);
        Item nuts = new Item("I10023", "Mixed Nuts", 18, ENERGY, 24);
        items.add(nuts);
        Item proteinBar = new Item("I10030", "Protein Bar", 40, ENERGY, 41);
        items.add(proteinBar);

        Item marmelade = new Item("I48396", "Marmalade", 20, JOY, 25);
        items.add(marmelade);
        Item candy = new Item("I10016", "Candy", 10, JOY, 14);
        items.add(candy);
        Item iceCream = new Item("I10017", "Ice Cream Nilka", 35, JOY, 38);
        items.add(iceCream);
        Item cakeSlice = new Item("I10018", "Cake Slice", 28, JOY, 32);
        items.add(cakeSlice);
        Item lollipop = new Item("I10025", "Lollipop", 10, JOY, 2);
        items.add(lollipop);
        Item brownie = new Item("I10026", "Brownie", 25, JOY, 29);
        items.add(brownie);
        Item pieSlice = new Item("I10027", "Apple Pie Slice", 22, JOY, 28);
        items.add(pieSlice);

        for (Item item : items) {

        }
    }

}
