"""
    Reikalingas python 3.+
    Reikalingas numpy

"""

import numpy as np


# absolute matrix element sum
def matrixElementSum(matrix):
    ret = 0
    for elem in matrix:
        ret += np.abs(elem)
    return ret


# sigmoid function
def nonlin(x, deriv=False):
    if deriv:
        return x * (1 - x)
    else:
        return 1 / (1 + np.exp(-x))


# modified sigmoid
def nonlin2(x, deriv=False):
    if deriv:
        return (2 * np.exp(x)) / ((np.exp(x) + 1) * (np.exp(x) + 1))
    else:
        return 2 / (1 + np.exp(-x)) - 1


class SingleLayerNetwork:
    """
        @alpha = step multiplier
        @weightCount = dimension
        @inputDataset
        @inputDataset
        @function activation function, with optional boolean parameter for getting derivative
        @errorMarginTerminate terminate function if error margin is too small (returns boolean value)

    """
    def __init__(self, alpha: float, weightCount: int, inputDataset, outputDataset, function, useRandom=True, errorMarginTerminate=None):
        np.random.seed(1)
        self.weightCount = weightCount
        self.inputDataset = inputDataset
        self.outputDataset = outputDataset
        if useRandom:
            self.synapse = 2 * np.random.random((weightCount, 1)) - 1  # sugeneruojamos atsistiktines reiksmes pagal svoriu kieki
        else:
            self.synapse = np.zeros((weightCount, 1))
        self.layerAfterTraining = self.inputDataset
        self.function = function
        self.alpha = alpha
        self.errorMarginTerminate = errorMarginTerminate

    def iteration(self, count=1):
        layer1 = self.layerAfterTraining
        for i in range(0, count):
            layer0 = self.inputDataset
            layer1 = self.function(np.dot(layer0, self.synapse))  # vykdoma aktyvacijos funkcija
            error = self.outputDataset - layer1  # kiek suklydom (paklaida)
            # pasirinktinas paklaidos patikrinimas
            if self.errorMarginTerminate is not None:
                if self.errorMarginTerminate(error):
                    break

            # print(error)
            # for e in range(0,error.__len__()):
            #     num = (layer1[e]-self.outputDataset[e])**2 * error[e]
            #     error[e] = (num) /2
            # print(error)
            delta = error * self.function(layer1, True)  # pokytis gradiento link
            self.synapse += self.alpha * np.dot(layer0.T, delta)  # pagal paklaida daromas poslinkis svoriuose

        self.layerAfterTraining = layer1  # issaugojamas rezultatas


def action(network: SingleLayerNetwork, terminateFunction):
    print("Input:")
    print(network.inputDataset)
    print("Desired output:")
    print(network.outputDataset)
    print("Starting weights:")
    print(network.synapse.T)
    iteration = 0
    while not terminateFunction(network.layerAfterTraining.T[0]):
        network.iteration()
        iteration += 1
    print("Iterations:" + str(iteration))
    print("Output after training:")
    print(network.layerAfterTraining.T)
    print("Weights:")
    print(network.synapse.T)


def demo1():
    def terminate(m):
        return m[0] < 0 and m[1] < 0 and m[2] < 0 < m[3]

    print("First")
    inputDataset = np.array(
        [
            [0, 0, -1],
            [0, 1, -1],
            [1, 0, -1],
            [1, 1, -1]
        ]
    )
    outputDataset = np.array(
        [
            [-1, -1, -1, 1]
        ]
    ).T

    network = SingleLayerNetwork(1, 3, inputDataset, outputDataset, nonlin2, useRandom=False)
    action(network, terminate)


def demo2():
    def terminate(m):
        return m[0] < 0 and m[1] > 0 and m[2] < 0 and m[3] > 0 and m[4] < 0 and m[5] > 0 and m[6] > 0 and m[7] > 0

    print("Second")
    inputDataset = np.array(
        [
            [0, 0, 0, -1],
            [0, 0, 1, -1],
            [0, 1, 0, -1],
            [0, 1, 1, -1],
            [1, 0, 0, -1],
            [1, 0, 1, -1],
            [1, 1, 0, -1],
            [1, 1, 1, -1]
        ]
    )
    outputDataset = np.array(
        [
            [-1, 1, -1, 1, -1, 1, 1, 1]
        ]
    ).T
    network = SingleLayerNetwork(1, 4, inputDataset, outputDataset, nonlin2, useRandom=True)
    action(network, terminate)


if __name__ == "__main__":
    print("Start")
    choice = int(input("Pick option[1,2]: "))
    if choice == 1:
        demo1()
    elif choice == 2:
        demo2()
    else:
        print("Wrong selection")
    print("End")
