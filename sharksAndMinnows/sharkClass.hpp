//
//  sharkClass.hpp
//  SharksAndMinnows
//
//  Created by Andrew Kellett on 9/20/21.
//
#pragma once
#include <SFML/Graphics.hpp>
#include <stdio.h>
#include "Textures.hpp"

class shark{
public:
    //member variables
    sf::Sprite shark_sprite;
    long xpos;
    long ypos;
    float sharkSpeed;
    
    //functions
    shark(const Textures & texture);
    void position_update (sf::RenderWindow & window, const int & userX, const int & userY);     //'moves' shark per user position.
};

