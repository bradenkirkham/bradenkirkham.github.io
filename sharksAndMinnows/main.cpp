//
//  main.cpp
//  SharksAndMinnows
//
//  Created by Andrew Kellett and Mr. Braden Kirkham on 9/20/21.
//

#include <SFML/Graphics.hpp>
#include "userClass.hpp"
#include "sharkClass.hpp"
#include "Textures.hpp"
#include "gameFunctions.hpp"
#include <math.h>
#include <vector>
#include <iostream>

using namespace std;


//We tried to put this function in its own file to keep things neat, but it stopped working and two TAs couldn't figure it out either. So we placed it here above main().
void GameOver(sf::RenderWindow &window, bool &gameRunning, int &score, sf::Sprite backgroundSprite){
    sf::Font font;
    if (!font.loadFromFile("Arial Unicode.ttf")){
        cout << "ERROR! font not loaded!";
    }
    sf::Text text;                                      
    string final_score = to_string(score);
    text.setFont(font);
    text.setString("GAMEOVER!\n Score: " + final_score );
    text.setCharacterSize(50);
    text.setFillColor(sf::Color::Red);
    text.setPosition(1200, 375);

    gameRunning = false;
    window.clear();
    window.draw(backgroundSprite);
    window.draw(text);
    window.display();
}

int main()
{
    int frame_rate = 144;
    // Create the main program window.
    sf::RenderWindow window(sf::VideoMode(2700, 1350), "Deep Blue Sea");
    window.setFramerateLimit(frame_rate);
    
    // Creates fish, vector of sharks.
    Textures tex;
    user fish(tex);
    vector<shark> sharkVec;
    bool gameRunning = true;
    
    int score = 0;
    
    
    sf::Sprite backgroundSprite;
    backgroundSprite.setTexture(tex.backgroundTex);
    
    // Run the program as long as the main window is open.
    while (window.isOpen()){
        
        
        // Check all the window's events that were triggered since the last iteration of the loop
        sf::Event event;
        
        while (window.pollEvent(event) )
        {
            // "close requested" event: we close the window
            if (event.type == sf::Event::Closed) {
                window.close();
            }
            
        }
   
        if (gameRunning == false) {                 //if game not running, skip the below code in loop.
            window.setMouseCursorVisible(true);     //We want to stop updating/moving the screen.
            continue;
        }
        // clear the window with black color
        
        window.clear();
        window.draw(backgroundSprite);
        window.setMouseCursorVisible(false);
        
        //      "minnow" follows mouse cursor.
        sf::Vector2i localPosition = sf::Mouse::getPosition(window);
        fish.userSprite.setPosition( localPosition.x, localPosition.y );
        
        score += 1;
        
        if ((score % 144 == 1)){
            shark testshark(tex);
            sharkVec.push_back(testshark);
        }
        
        for( int i = 0 ; i < sharkVec.size(); i++){     //goes through shark vector.
            sharkVec[i].position_update(window, localPosition.x, localPosition.y);     //update/move shark positions.
            fish.tagged(fish, sharkVec[i]);         //verify if tagged
        }
        
        window.draw( fish.userSprite );
        
        // end the current frame
        window.display();
        
        if(fish.isTagged == true){
            GameOver(window, gameRunning, score, backgroundSprite);//passes in information for endgame window.
        }
    }
    
    return 0;
}
