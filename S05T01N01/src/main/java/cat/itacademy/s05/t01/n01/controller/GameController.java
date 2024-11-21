package cat.itacademy.s05.t01.n01.controller;

public class GameController {
    /*Endpoints per al joc:
    Crear partida:

    Mètode: POST

    Descripció: Crea una nova partida de Blackjack.

    Endpoint: /Game/new

    Cos de la sol·licitud (body): Nou nom del jugador.

    Paràmetres d'entrada: Cap

    Resposta exitosa: Codi 201 Created amb informació sobre la partida creada.


    Obtenir detalls de partida:

    Mètode: GET

    Descripció: Obté els detalls d'una partida específica de Blackjack.

    Endpoint: /Game/{id}

    Paràmetres d'entrada: Identificador únic de la partida (id)

    Resposta exitosa: Codi 200 OK amb informació detallada sobre la partida.


    Realitzar jugada:

    Mètode: POST

    Descripció: Realitza una jugada en una partida de Blackjack existent.

            Endpoint: /Game/{id}/play

    Paràmetres d'entrada: Identificador únic de la partida (id), dades de la jugada (per exemple, tipus de jugada i quantitat apostada).

    Resposta exitosa: Codi 200 OK amb el resultat de la jugada i l'estat actual de la partida.


    Eliminar partida:

    Mètode: DELETE

    Descripció: Elimina una partida de Blackjack existent.

    Endpoint: /Game/{id}/delete

    Paràmetres d'entrada: Identificador únic de la partida (id).

    Resposta exitosa: Codi 204 No Content si la partida s'elimina correctament.*/

}
