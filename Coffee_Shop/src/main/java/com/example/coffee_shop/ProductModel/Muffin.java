package com.example.coffee_shop.ProductModel;

public class Muffin implements ProductModel {

    private int id ;
    private  String description ;
    private  double price ;



    public Muffin(int id , String name,double price )
    {
        this.id = id;
        this.description = name;
        this.price = price ;

    }
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getID() {
        return id;
    }



    @Override
    public double getCost() {
        return price;
    }
}

