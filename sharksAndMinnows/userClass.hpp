//
//  userClass.hpp
//  SharksAndMinnows
//
//  Created by Andrew Kellett on 9/20/21.
//

#pragma once
#include <SFML/Graphics.hpp>
#include <stdio.h>
#include "Textures.hpp"
#include "sharkClass.hpp"

class user{
public:
    //member variables
    sf::Sprite userSprite;
    bool isTagged;
   
    
    //functions
    
    user(const Textures & texture);
    //Checks if shark and player sprites "collide", if so, flip isIt bool, starting end of game.
    void tagged (user & player , const shark & enemy);
};


