package com.orain.mealmemory;

//One Meal with an attached rating
class Review {
    private String mealName;
    private int rating;

    Review(String n, int r) {
        mealName = n;
        if(r <= 10 && r >= 1) {
            rating = r;
        }
        else{
            rating = 1;
        }
    }

    int compareTo0(Review otherReview){
        return this.mealName.compareToIgnoreCase(otherReview.getMealName());
    }
    int compareTo1(Review otherReview){
        return -1 * this.mealName.compareToIgnoreCase(otherReview.getMealName());
    }
    int compareTo2(Review otherReview){
        return this.rating > otherReview.rating ? -1 : (this.rating < otherReview.rating) ? 1 : compareTo0(otherReview);
    }
    int compareTo3(Review otherReview){
        return this.rating > otherReview.rating ? 1 : (this.rating < otherReview.rating) ? -1 : compareTo0(otherReview);
    }

    public String toString(){
        return (mealName + " " + Integer.toString(rating));
    }
    String getMealName(){
        return mealName;
    }
    int getRating(){
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
