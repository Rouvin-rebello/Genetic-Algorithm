package Y3_FallSem.COSC3P71;

/*
@author     : Rouvin Rebello
@Email      : rr20jk@brocku.ca
@course     : COSC 3P71
@assignment : #2
@student ID : 7098080
@since      : October 27th, 2023
*/


import java.io.*;
import java.util.Random;
import java.util.Scanner;

//This class is to implement the genetic algorithm for assignment 2
public class Genetic_algorithm {
    int population_size;
    int chromosome_length;
    double crossoverRate;
    double mutationRate;
    int numOfGenerations;
    int seed;
    String folder = "D2-OPX_CR_90_MR_15";
    int crossoverChoice;
    int fileChoice;
    String filePath;

    Random random;
    String[] initial_population;      // Array of randomly generated chromosomes
    double[] population_fitness;      // Array of fitness values
    double[] spare_fitness;           // Array of fitness values
    double[] averageOfEachGen;
    String[] parents;                 // Array that stores suitable parents
    String[] UOCchildren;             // Array to store the results of the UO crossover
    String[] unmutatedChildren;
    String[] MutUOCchild;
    String[] bestOfEachGen;
    double[] bestOfEachGenFit;
    String content;
    String elite1;
    String elite2;

    // constructor to run all functions for a specified number of generations
    public Genetic_algorithm() {
        Scanner scanner = new Scanner(System.in);

        boolean Continue1 = true;
        while (Continue1) {
            System.out.println("\n1. Data file 1 ");
            System.out.println("2. Data file 2 ");
            System.out.print("Enter your choice           : ");
            fileChoice = scanner.nextInt();
            System.out.println("\n");

            if (fileChoice == 1 || fileChoice == 2) {
                Continue1 = false;
            } else {
                System.out.println("\n!!!!!!!! Enter 1 or 2 and retry !!!!!!!!");
            }
        }

        System.out.print("Enter the population size      : ");
        population_size = scanner.nextInt();

        System.out.print("Enter the chromosome length    : ");
        chromosome_length = scanner.nextInt();

        System.out.print("Enter the crossover rate       : ");
        crossoverRate = scanner.nextInt();

        System.out.print("Enter the mutation rate        : ");
        mutationRate = scanner.nextInt();

        System.out.print("Enter the number of generations: ");
        numOfGenerations = scanner.nextInt();

        System.out.print("Enter the seed                 : ");
        seed = scanner.nextInt();

        boolean Continue = true;
        while (Continue) {
            System.out.println("\n1. Uniform Crossover ");
            System.out.println("2. One Point Crossover ");
            System.out.print("Enter your crossover choice    : ");
            crossoverChoice = scanner.nextInt();

            if (crossoverChoice == 1 || crossoverChoice == 2){
                Continue = false;
            } else{
                System.out.println("\n!!!!!!!! Enter 1 or 2 and retry !!!!!!!!");
            }
        }


        folder = "D2-OPX_CR_90_MR_15";

        random = new Random(seed);
        initial_population = new String[population_size];      // Array of randomly generated chromosomes
        population_fitness = new double[population_size];      // Array of fitness values
        spare_fitness = new double[population_size];           // Array of fitness values
        averageOfEachGen = new double[numOfGenerations];       // Array to store averages
        parents = new String[population_size];                 // Array that stores suitable parents
        UOCchildren = new String[population_size];             // Array to store the results of the UO crossover
        unmutatedChildren = new String[population_size];       // unmutated children
        MutUOCchild = new String[population_size];             // mutated children
        bestOfEachGen = new String[numOfGenerations];          // Array to store best of each generation
        bestOfEachGenFit = new double[numOfGenerations];       // best individuals fitness
        content = "";                                          // data from text file
        elite1 = "";                                           // elite individual
        elite2 = "";                                           // elite individual


        System.out.println("\nInitial population");
        readData();
        population_initilizer();
        fitness(initial_population, population_fitness);

        for (int j = 0; j < numOfGenerations; j++) {
            averageOfEachGen[j] = averageOfGen(population_fitness);
            int smallestIndex;
            int secondSmallestIndex;

            if (population_fitness[0] < population_fitness[1]) {
                smallestIndex = 0;
                secondSmallestIndex = 1;
            } else {
                smallestIndex = 1;
                secondSmallestIndex = 0;
            }

            for (int i = 2; i < population_fitness.length; i++) {
                if (population_fitness[i] < population_fitness[smallestIndex]) {
                    secondSmallestIndex = smallestIndex;
                    smallestIndex = i;
                } else if (population_fitness[i] < population_fitness[secondSmallestIndex] && population_fitness[i] != population_fitness[smallestIndex]) {
                    secondSmallestIndex = i;
                }
            }

            elite1 = initial_population[smallestIndex];
            elite2 = initial_population[secondSmallestIndex];

            //System.out.println("\nElites");
            //System.out.println(elite1);
            //System.out.println(elite2);
            bestOfEachGen[j] = elite1;

            parents = tournament_selection(initial_population);
            //System.out.println("\nParents");
            //fitness(parents, spare_fitness);

            if (crossoverChoice == 1){
                unmutatedChildren = uniform_crossover(parents, UOCchildren);
            }else if (crossoverChoice == 2){
                unmutatedChildren = onePointCrossover(parents, UOCchildren);
            }

            //System.out.println("\nUniform Crossover:");
            //fitness(unmutatedChildren, spare_fitness);

            initial_population = mutate(unmutatedChildren, MutUOCchild);
            initial_population[0] = elite1;
            initial_population[1] = elite2;
            //System.out.println("\nMutated Children");
            fitness(initial_population, population_fitness);
        }

        System.out.println("\nBest individual of all generations: " + bestOfEachGen[numOfGenerations - 1] + " : " + Evaluation.fitness(bestOfEachGen[numOfGenerations - 1],  content) + "\n");
        System.out.println("Decrypted Text:\n" + Evaluation.decrypt(bestOfEachGen[numOfGenerations - 1], content) + "\n");
        System.out.println("Average of each generation:");
        print_array(averageOfEachGen);
        System.out.println("\nBest of each generation:");
        print_array(bestOfEachGen);
        fitness(bestOfEachGen, bestOfEachGenFit);
        //writeToFile("One Point Crossover", "Data1", elite1, Evaluation.decrypt(bestOfEachGen[numOfGenerations - 1], content), averageOfEachGen, bestOfEachGenFit);
    }


    // creates an array of randomly generated chromosomes
    public void population_initilizer() {
        String characters = "abcdefghijklmnopqrstuvwxyz-";
        for (int j = 0; j < population_size; j++) {
            String chromosome = "";
            //Random random = new Random();
            for (int i = 0; i < chromosome_length; i++) {
                chromosome = chromosome + characters.charAt(random.nextInt(characters.length()));
            }
            initial_population[j] = chromosome;
        }
    }

    public void readData(){
        if (fileChoice == 1){
            filePath = "C:\\Users\\Rouvin\\IdeaProjects\\JavaProjects\\src\\Y3_FallSem\\COSC3P71\\Data1.txt";
        }else if (fileChoice == 2){
            filePath = "C:\\Users\\Rouvin\\IdeaProjects\\JavaProjects\\src\\Y3_FallSem\\COSC3P71\\Data2.txt";
        }

        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                content = content + line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // evaluates the fitness of each chromosome using Evaluation.java
    public void fitness(String[] array, double[] arrayOut){
        // reading file Data1.txt

        // measures the fitness of each chromosome
        for (int i = 0; i < array.length; i++) {
            double fitness = Evaluation.fitness(array[i], content);
            arrayOut[i] = fitness;
        }
        // prints out the initial population and their corresponding fitness values
        for (int i = 0; i < arrayOut.length; i++) {
            //System.out.println(array[i] + " : " + arrayOut[i]);
        }
    }

    // implements tournament selection (k=2) for the initial population for a specified number of generations
    public String[] tournament_selection(String[] array){
        for (int i = 0; i < array.length; i++) {
            //Random random = new Random(); // selecting random indices
            int index1 = random.nextInt(array.length);
            int index2 = random.nextInt(array.length);

            if (Evaluation.fitness(array[index1],content) < Evaluation.fitness(array[index2],content)) {
                //System.out.println("---------" + Evaluation.fitness(array[index1], content));
                //System.out.println("---------" + Evaluation.fitness(array[index2], content));
                parents[i] = array[index1];
            } else {
                parents[i] = array[index2];
            }
        }

        return parents;
    }


    public String[] uniform_crossover(String[] array, String[] arrayOut){
        double NumCrossover = crossoverRate/100;
        for (int j = 0; j < array.length; j = j + 2) {
            //Random random = new Random();
            double value = random.nextDouble();

            if (value < NumCrossover){
                String child1 = "";
                String child2 = "";
                String parent1 = array[j];
                String parent2 = array[j+1];

                // generating a 2 bit mask
                StringBuilder mask = new StringBuilder();
                for (int i = 0; i < parent1.length(); i++) {
                    int bit = random.nextInt(2);
                    mask.append(bit);
                }

                // switching characters between parents using the mask
                for (int i = 0; i < parent1.length(); i++) {
                    if (mask.charAt(i) == '0') {
                        child1 = child1 + parent2.charAt(i);
                        child2 = child2 + parent1.charAt(i);
                    } else {
                        child1 = child1 + parent1.charAt(i);
                        child2 = child2 + parent2.charAt(i);
                    }
                }
                arrayOut[j] = child1;
                arrayOut[j+1] = child2;
            }else{
                arrayOut[j] = array[j];
                arrayOut[j+1] = array[j+1];
            }
        }
        //print_array(UOCchildren);
        return arrayOut;
    }

    public String[] onePointCrossover(String[] array, String[] arrayOut){
        double NumCrossover = crossoverRate/100;
        //Random random = new Random();

        for (int j = 0; j < array.length; j = j + 2) {
            double value = random.nextDouble();

            if (value < NumCrossover){
                String child1 = "";
                String child2 = "";
                //Random random = new Random();                                          // selecting random indices
                int index1 = random.nextInt(array.length);
                int index2 = random.nextInt(array.length);
                String parent1 = array[index1];
                String parent2 = array[index2];

                // generating a random crossover point
                int crossoverPoint = random.nextInt(parent1.length());

                for (int i = 0; i < parent1.length(); i++) {
                    if (i < crossoverPoint) {
                        child1 = child1 + parent1.charAt(i);
                        child2 = child2 + parent2.charAt(i);
                    } else {
                        child1 = child1 + parent2.charAt(i);
                        child2 = child2 + parent1.charAt(i);
                    }
                }
                arrayOut[j] = child1;
                arrayOut[j+1] = child2;
            }else{
                arrayOut[j] = array[j];
                arrayOut[j+1] = array[j+1];}

        }
        return arrayOut;
        //print_array(OPCchildren);
    }


    public String[] mutate(String[] unmutatedArray, String[] mutatedArray){
        double NumMutate = mutationRate/100;
        for (int i = 0; i < unmutatedArray.length; i++) {
            //Random random = new Random();
            double value = random.nextDouble();

            if (value < NumMutate){
                int index1 = random.nextInt((chromosome_length/2 + 1));
                int index2 = random.nextInt((chromosome_length - chromosome_length/2) + 1) + chromosome_length/2;
                String substring = unmutatedArray[i].substring(index1, index2);

                char[] charArray = substring.toCharArray();
                int left = 0;
                int right = charArray.length - 1;
                while (left < right) {
                    char temp = charArray[left];
                    charArray[left] = charArray[right];
                    charArray[right] = temp;
                    left++;
                    right--;
                }
                // Reversed sub string
                String reversedSubString = new String(charArray);
                // Storing the mutated child in the specified array
                mutatedArray[i] = unmutatedArray[i].substring(0, index1) + reversedSubString + unmutatedArray[i].substring(index2);
            }
            else{
                mutatedArray[i] = unmutatedArray[i];
            }
        }
        //print_array(mutatedArray);
        return mutatedArray;
    }

    // prints a specified array
    public void print_array(String[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }
    }

    public void print_array(double[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }

        //for (int i = 0; i < array.length; i++) {
          //  Array_op = array[i] + ", ";
        //}
        //System.out.println(Array_op);
    }

    public double averageOfGen(double[] array){
        double sum = 0.0;
        for (double number : array) {
            sum += number;
        }
        return sum / array.length;
    }

    public void writeToFile(String Crossovertype, String file, String elite, String Decrypted, double[] AvgArray, double[] BestArray){


        String fileName = "C:\\Users\\Rouvin\\IdeaProjects\\JavaProjects\\src\\Y3_FallSem\\COSC3P71\\Tests\\" + folder + "\\D2_Seed_" + seed;
        String content = "Population Size      : " + population_size + "\nChromosome Length    : " + chromosome_length + "\nCrossover Rate       : " + crossoverRate +
                "\nMutation Rate        : " + mutationRate + "\nNumber of Generations: " + numOfGenerations + "\nSeed                 : " + seed +
                "\nCrossover Type       : " + Crossovertype + "\nEncrypted file       : " + file + "\n\nBest key             : " + elite +
                "\n\nDecrypted text: " + Decrypted + "\n\nAverage generational fitness: \n";

        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);

            for (int i = 0; i < AvgArray.length; i++) {
                bufferedWriter.write(AvgArray[i] + ", ");
            }

            bufferedWriter.write("\n\nBest generational fitness:\n");

            for (int i = 0; i < BestArray.length; i++) {
                bufferedWriter.write(BestArray[i] + ", ");
            }

            bufferedWriter.close();
        } catch (IOException e) {
        }
    }

    public static void main(String[] args){
        new Genetic_algorithm();
    }
}
