//
//  Textures.hpp
//  sharksandminnows
//
//  Created by Braden Kirkham on 9/22/21.
//

#pragma once
#include <SFML/Graphics.hpp>
#include <stdio.h>
#include <iostream>



class Textures{
public:
    sf::Texture userTex; //creating variables for the different textures used.
    sf::Texture sharkTex;
    sf::Texture backgroundTex;
    
    Textures(){
        //Loads/checks to make sure files loaded.
        if(!backgroundTex.loadFromFile("backgroundINIT.jpg")||!sharkTex.loadFromFile("shark2.png")||userTex.loadFromFile("fish1.png")) // if statements loads/assigns textures and returns error upon failure.
        {
            std::cout << "Load failed" << std::endl;
            system("pause");
        }
    }
};


