//
// Created by Braden Kirkham on 2/14/22.
//

#include <string>
#include "testGeneratorFunctions.h"

static std::string randString() { //returns a random string that will most likely be x or y and occasionally be any other letter of the alphabet.
    int percent = rand();
    if (percent < 6) {
        return "x";
    } else if (percent > 3 && percent < 7) {
        return "y";
    } else {
        std::string randString;
        randString += (rand() % 26) + 'a';

        return randString;
    }
}

std::string rand_expr_string(){ // generates a random expr in string format
    int randNum = rand()% 100; // gives a random number between 1 and 100
    if (randNum < 60) { // 50 % of the time
        int newRandNum = rand() % 10; // get a number between 1 and 10
        if (newRandNum < 6) { // 50 percen t of the time
            return std::to_string(rand()); // return a string
        }
        else if (newRandNum > 5){ //50 percent of the time
            return " " + std::to_string(rand()) + " "; // return a string with spaces around it.
        }
    }
    else if(randNum > 60 && randNum < 80){ // 20 percent of the time
        return rand_expr_string() + "+" + rand_expr_string(); // generate a random expression added to a random string
    }
    else if(randNum > 80 && randNum < 99){ // 20 percent of the time
        return rand_expr_string() + "*" + rand_expr_string(); // random expression multed with random string.
    }
    else{
        std::string testString = randString(); //otherwise construct a let.
        return "_let " + testString + " = " + rand_expr_string() + " _in " + std::to_string(rand()) + testString;
    }
}

std::string multZero(){ // return a string multiplying by zero
    std::string randStr = std::to_string(rand());
    return randStr + " * 0";
}

std::string randAdd(){ // generate a random add of two random numbers
    return std::to_string(rand()) + " + " + std::to_string(rand());
}

std::string randMult(){ // same as above with mult
    return std::to_string(rand()) + " * " + std::to_string(rand());
}

std::string randWhiteSpace(){ //return numbers with random white space.
    int randNum = rand();
    if (randNum % 2) {
        return " " + std::to_string(rand()) + " + " + std::to_string(rand());
    }
    else{
        return std::to_string(rand()) + " + " + std::to_string(rand()) + " ";
    }
}

std::string randNum(){ // generate a random number in string format.
    return std::to_string(rand());
}