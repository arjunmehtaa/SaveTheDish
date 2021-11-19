# SaveTheDish

SaveTheDish is a clean and minimalist Android app that allows users to view recipes that they can cook using the ingredients they have at home.

<p float="left">
  <img src="images/1.jpg" width="33%" />
  <img src="images/2.jpg" width="33%" />
  <img src="images/3.jpg" width="33%" />
  <img src="images/4.jpg" width="33%" />
  <img src="images/5.jpg" width="33%" />
  <img src="images/6.jng" width="33%" />
</p>

## Architecture

SaveTheDish features a simple single-activity architecture with plans of implementing fragments in the future to handle increased complexity.

<p float="left">
  <img src="images/7.png" />
</p>

## Database

An SQLite Database is used to store, read and edit dishes/recipes locally.


## User Interface

* The first CardView provides an EditText for the user to input a comma-separated list of the ingredients they have.
* It also incorporates Buttons to execute the Search and Add a new dish.
* _dishesYouCanCook_ RecyclerView displays the dishes the user can cook with their current ingredients.
* _allDishes_ RecyclerView shows all the dishes added by the user in the past along with Edit and Delete buttons.
