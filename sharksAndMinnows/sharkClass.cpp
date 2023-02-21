//
//  sharkClass.cpp
//  SharksAndMinnows
//
//  Created by Andrew Kellett on 9/20/21.
//

#include "sharkClass.hpp"
#include "Textures.hpp"
#include <iostream>
using namespace std;

shark::shark(const Textures & texture){
   
    shark_sprite.setTexture(texture.sharkTex);
    //Effectively 'sets' xpos and ypos.
    shark_sprite.setPosition(rand() % 2700, rand() %1350); //randomly sets the position of sharks within the window.
    shark_sprite.setOrigin(50, 50);
    sharkSpeed = 1;
}

void shark::position_update (sf::RenderWindow & window, const int & userX, const int & userY){
    xpos = shark_sprite.getPosition().x;
    ypos = shark_sprite.getPosition().y;        //gets shark position.
    if(xpos > userX){
        xpos -= sharkSpeed;
    }
    
    if(xpos < userX){
        xpos += sharkSpeed;                     //compares and updates/moves shark position per user's position.
    }
    
    if(ypos > userY){
        ypos -= sharkSpeed;
    }
    
    if(ypos < userY){
        ypos += sharkSpeed;
    }
    
    sharkSpeed +=0.002;
    
    shark_sprite.setPosition( xpos, ypos);
    window.draw(shark_sprite);                  //draws shark at updated position.

}
