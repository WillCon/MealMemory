package com.orain.mealmemory;

/**
 * Created by William on 5/13/2017.
 */

//One Meal with an attached rating
public class Review {
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

    public int compareTo0(Review otherReview){
        return this.mealName.compareToIgnoreCase(otherReview.getMealName());
    }
    public int compareTo1(Review otherReview){
        return -1 * this.mealName.compareToIgnoreCase(otherReview.getMealName());
    }
    public int compareTo2(Review otherReview){
        return this.rating > otherReview.rating ? -1 : (this.rating < otherReview.rating) ? 1 : compareTo0(otherReview);
    }
    public int compareTo3(Review otherReview){
        return this.rating > otherReview.rating ? 1 : (this.rating < otherReview.rating) ? -1 : compareTo0(otherReview);
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
