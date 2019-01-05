#!/usr/local/bin/python3.6

from keras.models import Sequential, Model
from keras.layers import Activation, Dropout, Flatten, Dense, Input
import yaml
import numpy as np
import click
import simulator

@click.command()
@click.argument("infile", type=click.File('r'))
@click.argument("modelname", type=str)
def train(infile, modelname):

    model = Sequential()
    model.add(Dense(64, activation='tanh', input_dim=3))
    model.add(Dense(1, activation='sigmoid'))
    model.compile(optimizer='rmsprop',
                loss='binary_crossentropy',
                metrics=['accuracy'])
    model.summary()

    data = yaml.load_all(infile)
    x_train = np.zeros((10000, 3))
    y_train = np.zeros((10000, 1))
    i = 0
    for d in data:
        x_train[i][0] = d.get("i_queenA") / simulator.QUEEN_A_MAX
        x_train[i][1] = d.get("i_queenC") / simulator.QUEEN_C_MAX
        x_train[i][2] = d.get("i_queenKcrit") / simulator.QUEEN_CRIT_MAX
        y_train[i][0] = d.get("mark")
        i+=1
    x_train = np.resize(x_train, (i, 3))
    y_train = np.resize(y_train, (i, 1))
    model.fit(x_train, y_train, epochs=15, batch_size=32)

    loss_and_metrics = model.evaluate(x_train, y_train, batch_size=128)
    print(loss_and_metrics)
    score = model.predict(x_train, verbose=1)

    # from mpl_toolkits import mplot3d
    # import matplotlib.pyplot as plt
    # fig = plt.figure()
    # ax = plt.axes(projection='3d')
    # ax.scatter(x_train[:,0], x_train[:,1], y_train)
    # ax.scatter(x_train[:,0], x_train[:,1], score, cmap='Greens');
    # plt.show()

    # serialize model to JSON
    model_json = model.to_json()
    with open("{modelname}.json".format(modelname=modelname), "w") as json_file:
        json_file.write(model_json)
    # serialize weights to HDF5
    model.save_weights("{modelname}.h5".format(modelname=modelname))
    print("Saved model to disk")


if __name__ == '__main__':
    train()