package com.orain.mealmemory;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by William on 12/20/2015.
 */

//One Meal with an attached rating
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

//One Restaurant with attached list of rated meals
class Restaurant{
    private String name; //Name of the Restaurant
    private ArrayList<Review> reviews =  new ArrayList<Review>(); //List of all individual meal reviews at that restaurant

    public Restaurant(String n){
        name = n;
    }

    //Returns index of review matching name n, no matching review returns -1
    private int findReview(String n){
        for(int i = 0; i < reviews.size(); i++){
            if(reviews.get(i).getMealName().equals(n)){
                return i;
            }
        }
        return -1;
    }

    //Adds a new review of name n and rating r, returns false if that meal already exists
    public boolean addReview(String n, int r) {
        if (findReview(n) != -1) {
            return false;
        }
        reviews.add(new Review(n, r));
        return true;
    }

    //Removes review of name n, returns true if it removes something, otherwise returns false
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
    public void setName(String n){
        name = n;
    }

    //Get the list of all reviewed meals
    public ArrayList<Review> getReviews(){return reviews;}

}

//Nested list of Restaurants with their list of reviewed meals.
public class Globals{
    private static Globals instance; //Singleton reference
    private ArrayList<Restaurant> restaurants = null; //List of all restaurants in the app

    private Globals(){
        //Log.i("STATE OF GLOBALS", "RUNNING INIT");
        restaurants = new ArrayList<Restaurant>();
        //restaurants.add(new Restaurant("Burger King"));
        //Log.i("STATE OF GLOBALS", "BURGER KING TEST - " + restaurants.get(0).getName());
    }

    //Returns index of restaurant of name n, returns -1 if no matching restaurant
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

    //Adds restaurant of name n, returns false and does nothing if restaurant with that name exists already
    public boolean addRestaurant(String n) {
        if (findRestaurant(n) != -1) { //TODO: Could probably remove this and handle while searching for correct placement (see below loop)
            return false;
        }
        int i = 0;
        //Restaurants are added in alphabetical order
        for(; i < restaurants.size(); i++) {
            if(restaurants.get(i).getName().compareTo(n) > 0) {
                restaurants.add(i, new Restaurant(n));
                return true;
            }
        }
        restaurants.add(i, new Restaurant(n)); //Handles inserting into empty list (restaurants.size() == 0)
        return true;
    }

    //Removes restaurant of name n, returns true if removed and false if not found
    public boolean removeRestaurant(String n){
        final int temp = findRestaurant(n);
        if(temp != -1){
            restaurants.remove(temp);
            return true;
        }
        return false;
    }

    //TODO: Are these necessary? ReviewActivity should handle only its own restaurant, possibly does not need to touch Globals object at all
    public boolean addReview(int rest, String n, int r) {
        return restaurants.get(rest).addReview(n, r);
    }
    public boolean removeReview(int rest, String n){
        return restaurants.get(rest).removeReview(n);
    }

    //Get reference to restaurants list
    public ArrayList<Restaurant> getRestaurants() {
       return restaurants;
    }
    //Get reference to reviews list, TODO: possibly deprecated
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
