#!/usr/local/bin/python3.6

from keras.models import Sequential, Model, model_from_json
from keras.layers import Activation, Dropout, Flatten, Dense, Input
import yaml
import numpy as np
import click
import simulator

@click.command()
@click.argument("infile", type=click.File('r'))
@click.argument("modelname", type=str)
def test(infile, modelname):
    # load json and create model
    json_file = open('{modelname}.json'.format(modelname=modelname), 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    loaded_model = model_from_json(loaded_model_json)
    # load weights into new model
    loaded_model.load_weights("{modelname}.h5".format(modelname=modelname))
    print("Loaded model from disk")
    loaded_model.compile(loss='binary_crossentropy', optimizer='rmsprop', metrics=['accuracy'])
    
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
    
    score = loaded_model.evaluate(x_train, y_train, verbose=1)
    print("%s: %.2f%%" % (loaded_model.metrics_names[1], score[1]*100))
    print("%s: %.2f%%" % (loaded_model.metrics_names[0], score[0]*100))


if __name__ == '__main__':
    test()