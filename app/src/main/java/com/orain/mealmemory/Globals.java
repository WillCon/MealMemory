package com.orain.mealmemory;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by William on 12/20/2015.
 */
class Review{
    private String mealName;
    private int rating;

    public Review(String n, int r) {
        mealName = n;
        if(r <= 10 && r >= 1) {
            rating = r;
        }
        else{
            rating = 1;
        }
    }

    public String toString(){
        return (mealName + " " + Integer.toString(rating));
    }
    public String getMealName(){
        return mealName;
    }
    public int getRating(){
        return rating;
    }

    public void setMealName(String n){
        mealName = n;
    }
    public void setRating(int r){
        if(r <= 10 && r >= 1) {
            rating = r;
        }
        else{
            rating = 1;
        }
    }
}

class Restaurant{
    private String name;
    private ArrayList<Review> reviews =  new ArrayList<Review>();

    public Restaurant(String n){
        name = n;
    }

    private int findReview(String n){
        for(int i = 0; i < reviews.size(); i++){
            if(reviews.get(i).getMealName().equals(n)){
                return i;
            }
        }
        return -1;
    }

    public boolean addReview(String n, int r) {
        if (findReview(n) != -1) {
            return false;
        }
        reviews.add(new Review(n, r));
        return true;
    }
    public boolean removeReview(String n){
        final int temp = findReview(n);
        if(temp != -1){
            reviews.remove(temp);
            return true;
        }
        return false;
    }

    public String toString(){
        return name;
    }
    public String getName(){
        return name;
    }

    public ArrayList<Review> getReviews(){return reviews;}

    public void setName(String n){
        name = n;
    }

}

public class Globals{
    private static Globals instance;
    private ArrayList<Restaurant> restaurants = null;

    private Globals(){
        //Log.i("STATE OF GLOBALS", "RUNNING INIT");
        restaurants = new ArrayList<Restaurant>();
        //restaurants.add(new Restaurant("Burger King"));
        //Log.i("STATE OF GLOBALS", "BURGER KING TEST - " + restaurants.get(0).getName());
    }

    private int findRestaurant(String n){
        //Log.i("FIND RESTAURANT", Integer.toString(restaurants.size()));
        for(int i = 0; i < restaurants.size(); i++){
            //Log.i("FIND RESTAURANT", restaurants.get(i).getName() + " = " + n);
            if(restaurants.get(i).getName().equals(n)){
                return i;
            }
        }
        return -1;
    }

    public boolean addRestaurant(String n) {
        if (findRestaurant(n) != -1) {
            return false;
        }
        int i = 0;
        for(; i < restaurants.size(); i++) {
            if(restaurants.get(i).getName().compareTo(n) > 0) {
                restaurants.add(i, new Restaurant(n));
                return true;
            }
        }
        restaurants.add(i, new Restaurant(n));
        return true;
    }
    public boolean removeRestaurant(String n){
        final int temp = findRestaurant(n);
        if(temp != -1){
            restaurants.remove(temp);
            return true;
        }
        return false;
    }

    public boolean addReview(int rest, String n, int r) {
        return restaurants.get(rest).addReview(n, r);
    }
    public boolean removeReview(int rest, String n){
        return restaurants.get(rest).removeReview(n);
    }

    public ArrayList<Restaurant> getRestaurants() {
       return restaurants;
    }
    public ArrayList<Review> getReviews(int r){
        return restaurants.get(r).getReviews();
    }

    public static synchronized Globals getInstance(){
        if(instance == null){
            instance = new Globals();
        }
        return instance;
    }
}
