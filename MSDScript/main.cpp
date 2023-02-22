#include <iostream>
#include "cmdline.hpp"
#include "catch.hpp"
#include "Expr.hpp"
#include "parse.h"

int main(int argc, char* argv[]) {
    handleArg(argc, argv);

//    PTR(Expr) userInput = parse_num(std::cin);
//    std::cout << userInput->to_string();


//    while (true){
//        PTR(Expr) userInput = parse_expr(std::cin);
//
//        userInput->pretty_print(std::cout);
//        std::cout << "\n";
//
//        skip_whitespace(std::cin);
//        if (std::cin.eof()){
//            break;
//        }
//    }

    return 0;
}
