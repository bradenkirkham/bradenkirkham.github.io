//
//  userClass.cpp
//  SharksAndMinnows
//
//  Created by Andrew Kellett on 9/20/21.
//

#include "userClass.hpp"
#include "sharkClass.hpp"
#include <iostream>
using namespace std;

//constructor: sets texture and initializes values.
user::user(const Textures & texture){
    userSprite.setTexture(texture.userTex);
    isTagged = false;
}

void user::tagged (user & player , const shark & enemy){
    if ( userSprite.getGlobalBounds().intersects(enemy.shark_sprite.getGlobalBounds())) {//detects collision
        userSprite.setColor(sf::Color::Red);
        isTagged = true; // This boolean is used in other functions to determine when the game is over.
    }
}

