# Blackjack Reactive REST API
This project is a Java-based API for managing a Blackjack game. 
It leverages Spring Boot to provide a reactive application that interacts with both MongoDB and MySQL databases. 
The API allows creating games, managing players' actions, and accessing game details and player rankings.

## Technologies Used
* __Java 21__
* __Spring Boot with Spring WebFlux__ for reactive programming
* __MongoDB__ for game persistence
* __MySQL__ for player management
* __JUnit__ and __Mockito__ for testing
* __OpenAPI/Swagger__ for API documentation

## Installation and Setup

### Prerequisites

Java 21
Maven 4.0 or higher
MySQL and MongoDB installed and running on default ports

### Getting Started
**1. Clone the repository**
  Clone this repository on your computer and install all depencencies using: `mvn clean install`

**2. Configute the databases**
  * MySQL
    1. Set up MySQL user credentials in `application.properties`.
    2. The default port is 3306, change it if needed in `application.properties`.
    3. Run the MySQL server and create the database `blackjack`.
   
  * MongoDB
    * Ensure MongoDB service is running. No initial setup required.

  **3. Run the application**
  
      mvn spring-boot:run
    
  The API will be accessible at `http://localhost:8080`.

## API Endpoints

### Game Management
  * **Create Game**
      * **POST** `/game/new`
      * **Description:** Creates a new game.
      * **Request Body (JSON):**
        ```
        [
          {
            "name": "Player1",
            "bet": 240
          },
          {
            "name": "Player2",
            "bet": 100
          }
        ]
        ```
        
 * **Get Game Details**
     * **GET** `/game/{id}`
     * **Description:** Retrieves details of a specified game.
     * **Path Parameter:** (String) `id` --> Unique game identifier.
       
 * **Play**
     * **POST** `/game/{id}/play`
     * **Description:** Submits a player's action in an existing game. The player can also increment their bet.
     * **Path Parameter:** (String) `id` --> Unique game identifier.
     * **Request Body (JSON):**
        ```
        {
          "action": "HIT",
          "higherBet": 0
        }
        ```
        
 * **Delete Game**
     * **DELETE** `/game/{id}/delete`
     * **Description:** Removes an existing game from the database.
     * **Path Parameter:** (String) `id` --> Unique game identifier.

### Player Management

* **Update Player Name**
    * **PUT** `/player/{playerId}`
    * **Description:** Updates the name of a player.
    * **Path Parameter:** (String) `playerId` --> Unique player identifier.
    * **Request Body (JSON):**
      ```
      {
        "name": "newName"
      }
      ```
* **Player Ranking**
    * **GET** `/ranking`
    * **Description:** Fetches the ranking of players based on their performance.

### Documentation
While the application is running, access the Swagger UI for detailed API documentation and to test the endpoints directly at:
`http://localhost:8080/swagger-ui.html`

