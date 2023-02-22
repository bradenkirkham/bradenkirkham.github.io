#define CATCH_CONFIG_RUNNER
#include "cmdline.hpp"
#include "Expr.hpp"
#include "catch.hpp"
#include <iostream>
#include <string>
#include "parse.h"
#include "val.h"
#include "Env.h"
#include "Step.h"

void handleArg(int argc, char* argv[]){
    if(argc > 1){ // if there are additional arguements
        bool testSeen = false; // track if test as been used before.

        for (int i = 1; i < argc; i++){ // cycle through all the arguements.
            if(strcmp(argv[i], "--help") == 0){ // if help print the following.
                std::cout << "current commands are:\n--help\n--test\n--step\n--interp\n--print\n--pretty-print";
                exit(0);
            }

            if(strcmp(argv[i], "--test") == 0 && !testSeen){ // if test, and it hasn't been seen

                if(Catch::Session().run() != 0){ // run the tests and check the return
                    exit(1); // exit if there is a problem
                }

                std::cout << "tests passed\n"; // otherwise print test past
                testSeen = true; // mark test has been seen
                continue; // continue with the arguements.
            }

            if(strcmp(argv[i], "--test") == 0 && testSeen){ // if it has been seen then err.
                std::cerr << "Error: test command used more than once.\n";
                exit(1);
            }

            if (strcmp(argv[i], "--step") == 0){ // parse the input, interp it and print it out as a string.
                PTR(Expr) userInput = parse(std::cin);
                std::cout <<  Step::interp_by_steps(userInput)->to_string() << "\n";
                exit(0);
//                PTR(Expr) userInput = parse(std::cin);
//                std::cout << userInput->interp() << "\n";
//                exit(0);
            }

            if (strcmp(argv[i], "--interp") == 0){ // parse the input, interp it and print it out as a string.
                std::string userInput = (parse(std::cin)->interp(Env::empty))->to_string();
                std::cout << userInput << "\n";
                exit(0);
//                PTR(Expr) userInput = parse(std::cin);
//                std::cout << userInput->interp() << "\n";
//                exit(0);
            }

            if (strcmp(argv[i], "--print") == 0){ // parse user input and convert to string, then print.
                std::string userInput = (parse(std::cin))->to_string();
//                PTR(Expr) userInput = parse(std::cin);
//                std::string printInput = userInput->to_string();
                std::cout << userInput;
//                userInput->print(std::cout);
                std::cout << "\n";
                exit(0);
            }

            if (strcmp(argv[i], "--pretty-print") == 0){ // same as above but using pretty print.
                PTR(Expr) userInput = parse(std::cin);
                std::string printInput = userInput->to_string_pretty();
                std::cout << printInput;
//                userInput->pretty_print(std::cout);
                std::cout << "\n";
                exit(0);
            }

            else{ // error if no command matches.
                std::cerr << "error: invalid command. try help command to see list of valid commands.";
                exit(1);
            }
        }
    }
}