package com.example.coffee_shop.ProductModel;

public class CoffeeShopFactory {
    public static Object createItem(int id ,String name, double price) {
        switch(name) {
            case "Espresso":
                return new Espresso(id,name,price );
            case "Latte":
                return new Latte(id,name,price);
            case "IcedTea":
                return new IcedTea(id,name,price);
            case "Muffin":
                return new Muffin(id,name,price);
            case "Water":
                return new Water(id,name,price);
            case "Donuts":
                return new Donuts(id,name,price);
            default:
                throw new IllegalArgumentException("Invalid item name: " + name);
        }
    }
}
