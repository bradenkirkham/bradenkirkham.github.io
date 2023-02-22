#include <iostream>
#include <vector>
#include "testGeneratorFunctions.h"
#include "exec.h"

int main(int argc, char ** argv) {
    srand(clock()); // seed rand with clock.

    if (argc == 2) { // in the case of two arguements (testing one msdscript)
        const char * const interp_argv[] = { argv[1], "--interp" };
        const char * const print_argv[] = { argv[1], "--print" };
        for (int i = 0; i < 100; i++) { // run tests 100 times
            std::string in = rand_expr_string(); // generate a random expr.
//        std::cout << "Trying " << in << "\n";
            ExecResult interp_result = exec_program(2, interp_argv, in); // test interp using random string
            ExecResult print_result = exec_program(2, print_argv, in); // test print using random string
            ExecResult interp_again_result = exec_program(2, interp_argv, print_result.out);// test interp using print of random string

            if (interp_again_result.out != interp_result.out) { // both interps should match if everything went right.
                std::cout << interp_result.out << "\n"; // print outs to check what went wrong.
                std::cout << print_result.out << "\n";
                std::cout << interp_again_result.out << "\n";

                throw std::runtime_error("different result for printed"); // error
            }
        }
    }

    if (argc == 3) { // comparing two msd scripts against eachother.
        const char *const arguements[] = {"--interp", "--print", "--pretty-print"}; // different commands to call
        std::string(*functions[])() = {rand_expr_string, multZero, randAdd, randMult, randWhiteSpace, randNum}; // functions to generate random.

        for (int i = 0; i < 3; ++i) { // triple stacked for loop for commands, functions, and testing each 100 times.
            for (int j = 0; j < 6; ++j) {
                for (int k = 0; k < 100; k++) {
                    const char *const interp1_argv[] = {argv[1], arguements[i]}; // call msdscript1 with arg at i
                    const char *const interp2_argv[] = {argv[2], arguements[i]}; // call msdscript2 with arg at i
                    std::string in = (*functions[j])(); // generate random string expr with function at j
//                    std::cout << "Test number# "<< k  << "Testing: " << in << "\n";
                    ExecResult interp1_result = exec_program(2, interp1_argv, in); // run script 1
                    ExecResult interp2_result = exec_program(2, interp2_argv, in); // run script 2


                    if (interp1_result.out != interp2_result.out) { // script 1 and 2 should match.
                        std::cout << "Tried " << arguements[i] << functions[j] << in << "\n"; // print statements if they didn't match.
                        std::cout << interp1_result.out << "\n";
                        std::cout << interp2_result.out << "\n";


                        throw std::runtime_error("different results");
                    }
                }
            }
        }
    }
    return 0;
}
