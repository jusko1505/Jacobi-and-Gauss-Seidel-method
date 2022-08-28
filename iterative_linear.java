import java.util.*;
import java.lang.*;
import java.io.*;

public class iterative_linear{
    public static void main(String[] args) throws Exception{
        Scanner sc = new Scanner(System.in);
        iterative_linear il = new iterative_linear();
        float[][] matrix = il.getUserEquation(sc);
        il.print_matrix(matrix);
        float[] guess = il.getUserGuess(matrix, sc);
        float error = il.getUserError(sc);
        il.jacobiOrGauss(matrix, guess, error, sc);
        sc.close();
        
    }
    public float[][] getUserEquation(Scanner sc) throws Exception{
        int user_choice = -1;
        System.out.println("Please enter the number of equations:");
        int matrix_size = sc.nextInt();
        sc.nextLine();
        float[][] matrix = new float[matrix_size][matrix_size+1];
        System.out.println("Enter 1 to enter through console, enter 2 for file input.");
        user_choice = sc.nextInt();
        sc.nextLine();
        if (user_choice ==1){
            matrix = console_input(matrix, sc);
        }
        else if(user_choice == 2){
            matrix = file_input(matrix, sc);
        }
        return matrix;
    }
    public float getUserError(Scanner sc){
        System.out.println("What is your error value?");
        String strerror = sc.nextLine();
        float error = Float.parseFloat(strerror);
        System.out.println("error is: " + error);
        return error;
        
    }
    public float[] getUserGuess(float[][] matrix, Scanner sc) throws Exception{
        float[] guess = new float[matrix.length];
        String str_flt;
        System.out.print("Please enter initial guesses with spaces in between: ");
        str_flt = sc.nextLine();
        String[] str_flt_arr = str_flt.split(" ");
        for (int i =0; i<str_flt_arr.length; i++){
            guess[i] = Float.parseFloat(str_flt_arr[i]);
        }
        return guess;
    }
    public double calculateError(float[] previousIteration, float[] currentIteration){
        double numerator = 0;
        double denominator = 0;
        double error;
        for (int i = 0; i<previousIteration.length; i++){
            //build numerator
            double difference = currentIteration[i]-previousIteration[i];
            numerator += Math.pow(difference, 2);
        }
        numerator = Math.sqrt(numerator);
        for(int j = 0; j< currentIteration.length; j++){
            //build denominator
            denominator += Math.pow(currentIteration[j], 2);
        }
        denominator = Math.sqrt(denominator);
        error = numerator/denominator;
        return error;
    }
    public void jacobiOrGauss(float[][] matrix, float[] guess, float error, Scanner sc){
        int method_choice = -1;
        System.out.println("Enter 1 to use Jacobi, enter 2 for Gauss-Seidel");
        method_choice = sc.nextInt();
        sc.nextLine();
        if(method_choice ==1){
            new_jacobi(matrix, guess, error);
        }
        else if(method_choice == 2){
            new_gauss_seidel(matrix, guess, error);
        }
    }
    
    public float[][] console_input(float[][] matrix, Scanner sc) throws Exception{
        for(int i = 0; i<matrix.length; i++){
            System.out.println("Please enter equation " + i + " with spaces in between numbers");
            String user_equation = sc.nextLine();
            //System.out.println("user_equation is: "+user_equation);
            String [] split_equation = user_equation.split(" ");
            for(int j= 0;j<split_equation.length;j++){
                float parsed_float = Float.parseFloat(split_equation[j]);
                //System.out.println("parsed_float is: "+ parsed_float);
                matrix[i][j] = parsed_float;
            }
        }
        return matrix;
    }
    public void print_matrix(float[][] matrix){
        for (int i =0; i<matrix.length; i++){
            for (int j = 0; j < matrix.length+1; j++){
                System.out.print(matrix[i][j] +" ");
            }
            System.out.println();
        }
    }
    public float[][] file_input(float[][] matrix, Scanner sc) throws Exception{
        System.out.println("Please enter the name of your .txt file. You may need to press enter multiple times.");
        String file_path = sc.nextLine();
        sc.nextLine();
        File file = new File(file_path);
        Scanner fileScanner = new Scanner(file);

        for(int i = 0; i<matrix.length; i++){
            String scannedString = fileScanner.nextLine();
            String[] split_file = scannedString.split(" ");
            for(int j = 0; j< split_file.length; j++){
                float user_number = Float.parseFloat(split_file[j]);
                matrix[i][j] = user_number;
            }
            sc.nextLine();
        }
        fileScanner.close();
        return matrix;
    }
    public float[][] reverse_b_vector(float[][] matrix){
        for(int i = 0; i<matrix.length;i++){
            matrix[i][matrix.length] = matrix[i][matrix.length]*-1;
        }
        return matrix;
    }
    public void print_solutions(float[] array){
         System.out.print("[");
        for (float x: array){
            System.out.print(x + " ");
        }
        System.out.print("]");
    }

    public void new_jacobi(float[][] matrix, float[] guess, float error){
        double calcError = 99999;
        float[] previous_solution = guess;
        int length = previous_solution.length;
        float[] solution = new float[length];
        int iterations = 0;
        int total_iterations = 1;
        matrix = isolateX(matrix);
        while(Double.compare(error, calcError)<0){
            if(total_iterations==51){
                break;
            }
            solution = jacobi_logic(matrix, previous_solution);
            calcError = calculateError(previous_solution, solution);
            previous_solution = solution;
            total_iterations++;
            iterations++;
            print_solutions(solution);
            System.out.print("^"+iterations);
            System.out.println(" error: "+calcError);
        }
    }

    public void new_gauss_seidel(float[][] matrix, float[] guess, float error){
        double calcError = 99999;
        float[] previous_solution = guess;
        int length = previous_solution.length;
        float[] solution = new float[length];
        int iterations = 0;
        int total_iterations = 1;
        matrix = isolateX(matrix);

        while(Double.compare(error, calcError)<0){
            if(total_iterations==51){
                break;
            }
            float []prevSolCopy = previous_solution.clone();
            solution = gauss_logic(matrix, prevSolCopy);
            calcError = calculateError(previous_solution, solution);
            previous_solution = solution;  
            total_iterations++;
            iterations++;
            print_solutions(solution);
            System.out.print("^"+iterations);
            System.out.println(" error: "+calcError);
        }
        
    }

    public float[][] isolateX(float[][] matrix){
        int equation_to_isolate_x = 0;
        for(int i = 0; i < matrix.length; i++){
            float divisor = matrix[i][i];
            for (int j = 0; j < matrix.length+1; j++){
                matrix[i][j] = matrix[i][j]/divisor;
                if(j != equation_to_isolate_x){
                    matrix[i][j] = matrix[i][j]*-1;
                }
            }
            equation_to_isolate_x++;
        }
        matrix = reverse_b_vector(matrix);
        return matrix;
    }

    //takes in prebious iteration and spits out next iterations
    public float[] jacobi_logic(float[][] matrix, float[] previous_iteration){
        float current_solution;
        float[] solution = new float[matrix.length];
        for(int m = 0; m < matrix.length; m++){
            current_solution = 0;
            for(int n = 0; n<matrix.length+1;n++){
                if(m==n){
                    continue;
                }
                if(n==matrix.length){
                    current_solution+=matrix[m][n];
                    continue;
                }
                float multiply = previous_iteration[n];
                current_solution+=matrix[m][n]*multiply;
            }
            solution[m] = current_solution;
        }
        return solution;
    }
    
    public float[] gauss_logic(float[][] matrix, float[] previous_iteration){
        float current_solution;
        for(int m = 0; m < matrix.length; m++){
            current_solution = 0;
            for(int n = 0; n<matrix.length+1;n++){
                if(m==n){
                    continue;
                }
                if(n==matrix.length){
                    current_solution+=matrix[m][n];
                    continue;
                }
                float multiply = previous_iteration[n];
                current_solution+=matrix[m][n]*multiply;
               
            }
            previous_iteration[m] = current_solution;
        }
        return previous_iteration;
    }
}