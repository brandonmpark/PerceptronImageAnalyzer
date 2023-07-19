# Two Hidden-Layer Perceptron in Java

This repository contains a Java implementation of a two hidden-layer perceptron that can conduct some image analysis (e.g. counting how many fingers are in a picture of a hand). All code was implemented from scratch, with the only external library used being a JSON parsing library.

# Usage Instructions
## External Libraries
This project is dependent on the `json-simple` Java library, which can be downloaded [here](https://code.google.com/archive/p/json-simple/downloads).
## Configuration
All configuration options can be set in a JSON file passed as a command line option when running the program. These can be placed in the `src/config/` directory. The program comes with a default configuration file and presets for basic boolean operators as well as image recognition. The default configuration options, located in the `src/defaults/` directory, should only be edited with caution. A list of configuration options can be found below.

## Testing/Training Sets
The first line of any training/testing sets file should contain the number of sets in the file. Each successive line should contain a single set, formatted as a list of inputs separated by spaces followed by a list of outputs separated by spaces.

## Configuration Options
### General Network Configuration
- *train* (boolean): true if training the network upon the set, false if testing the network upon the set
- *nodes* (int[]): the number of nodes in each layer of the network (nodes[0] corresponds to the input layer, nodes[1-2] correspond to the hidden layer, and nodes[3] corresponds to the output layer)
- *printDetailed* (boolean): true if the network should have more detailed output while training/testing, false otherwise
- *weightsPath* (String): a path to the text file containing the weights that the network should start with, if any
- *setsPath* (String): a path to the text file containing the training/testing sets for the network
### Training Configuration
- *useTrainingWeights* (boolean): true if the network should begin training with predetermined weights, false if beginning with random weights
- *lambda* (int): the learning rate for the network
- *maxIterations* (int): the maximum amount of training cycles the network should go through
- *errorThreshold* (double): the goal threshold error to be met through training
- *minRandom* (double): the lower bound of random generation for weights
- *maxRandom* (double): the upper bound of random generation for weights
- *saveWeights* (boolean): true if the weights should be saved after training, false otherwise
- *savedWeightsPath* (String): a path to the text file where the weights should be saved after training
- *autosaveInterval* (int): the number of iterations that must occur before the weights are automatically saved


