#!/usr/local/bin/python3.6

from logger import getBeeLogger
import random as rand
import matplotlib
import matplotlib.pyplot as plt
from hive import Hive
import enviroment
from queen import QueenBee

log = getBeeLogger()

# days
SIMUATION_TIME = 200

MAX_FAMINE_PERIOD = 5

def simulate(data_set):

    assert isinstance(data_set, dict)

    env = None
    hive = None
    queen = None

    if not data_set.get("enviroment"):
        raise AttributeError("Data set has to include \"enviroment\" section!")
    else:
        env = enviroment.Enviroment(
            data_set.get("enviroment").get("nectar"),
            data_set.get("enviroment").get("pollen"),
            data_set.get("bee").get("flight_distance"),
            data_set.get("enviroment").get("temperature")
        )

    #print(str(env))

    queen = QueenBee(SIMUATION_TIME, rand.randint(400, 800)/100, rand.randint(120,130), rand.randint(150, 230))
    hive = Hive(SIMUATION_TIME, [0, 310, 207, None, 100, 100, 200])

    out_population = None
    out_honey = None

    average_flights_num = 10
    average_gather_to_weight_ratio = 0.1

    # gathering efficiency
    c1 = 3 # nectar
    c2 = 3 # pollen

    # scout flights loss ratio
    c3 = 0.15

    # nectar demands
    c4 = .2
    c5 = .285
    c6 = .09
    c7 = .14

    # pollen demands (relative to nectar demands)
    c8 = .2
    c9 = .18

    c10 = .08
    c11 = .006
    c12 = 0.5
    c13 = 0.2

    c14 = 0.11
    c15 = 0.3

    # c16 = 1

    c17 = 1
    c18 = 1
     
    E = 100000

    ########### initial resources
    hive.u11[0] = 4000 #rand.randint(2000, 10000)
    hive.u12[0] = 3000 #rand.randint(2000, 10000)
    hive.u2P[0] = 3000 #rand.randint(2000, 10000)
    ###########


    ###
    famine = None

    ###

    for day in range(SIMUATION_TIME):
        log.info("DAY %d" % day)
        
        if day > 0:
            if hive.u2P[day-1] == 0 or hive.y1[day-1] >= 3*hive.y4[day-1]:
                if hive.y1[day-1] >= 3*hive.y4[day-1]:
                    log.warning("too many young bees")
                queen.b[day] = 0
            elif day > queen.k_crit:
                queen.b[day] = max(1 - (1/E) * sum(hive.dy[:day]), 0)
            else:
                queen.b[day] = 1

        hive.dy[day] = int(queen.layEggs(day, hive.x13))


        # do bees growth
        hive.updatePopulation(day)

        log.info("🥚 x %d, 🐛 x %d, 🐜 x %d, 🐝 x %d (%d, %d, %d)" % (hive.y[day], hive.y1[day], hive.y2[day], hive.y3[day], hive.y4[day], hive.y5[day], hive.y6Q[day] + hive.y6V[day]))


        # do supply flights
        hive.u1[day] = c1 * average_flights_num * average_gather_to_weight_ratio * hive.y6Q[day]
        hive.u2[day] = c2 * average_flights_num * average_gather_to_weight_ratio * hive.y6V[day]

        log.debug("harvested: u1: %4.4f, u2: %4.4f" % (hive.u1[day], hive.u2[day]))

        # store supplies
        hive.u11[day] = hive.u11[max(day-1, 0)] + hive.u1[day] # - human factor
        hive.u12[day] = hive.u12[max(day-1, 0)] # - human factor
        hive.u2P[day] = hive.u2P[max(day-1, 0)] + hive.u2[day] # - human factor

        # resource used on scout flights
        hive.x10[day] = c3 * average_flights_num * average_gather_to_weight_ratio * (hive.y6Q[day] + hive.y6V[day])
        hive.u11[day], _ = distribute_resources(hive.u11[day], hive.x10[day])
  
        # cannot have negative resources
        assert hive.u11[day] >= 0
        assert hive.u12[day] >= 0
        assert hive.u2P[day] >= 0

        log.debug("resources: u11: %4.4f, u12: %4.4f, u2P: %4.4f" % (hive.u11[day], hive.u12[day], hive.u2P[day]))


        """ ----------------------- calculate resource demand -------------------------- """
        

        n = 4
        # calculate the building related stuff
        if day > 10+n:
            hive.u3[day] = 0.3 * (c15 + (c14/n) * sum(hive.x13[day-(9+n):day-9])) * hive.y5[day]
        log.debug("u3 = %4.4f" % hive.u3[day])

        ## RESOURCE DEMAND:
        # keep vital functions
        hive.x11d[day] = c4 * hive.y3[day]
        # proper larva growth
        hive.x12d[day] = c5 * hive.y1[day]
        # excess intake for feeding and larva care
        hive.x13d[day] = c6 * hive.y4[day] if (hive.y1[day] + hive.y[day]) > 0 else 0
        # excess intake for cluster building and wax synthesis
        hive.x14d[day] = c7 * hive.y5[day] if (hive.u3[day]) > 0 else 0

        log.debug("demands: x11: %4.4f, x12: %4.4f, x13: %4.4f, x14: %4.4f," % (hive.x11d[day], hive.x12d[day], hive.x13d[day], hive.x14d[day]))


        ## CALCULATE ACTUAL NECTAR INTAKE (priority based):
        if hive.u11[day] >= (hive.x11d[day] + hive.x12d[day] + hive.x13d[day] + hive.x14d[day]):
            # all demands can be satisfied
            log.info("all demands can be satisfied")
            hive.x11[day] = hive.x11d[day]
            hive.x12[day] = hive.x12d[day]
            hive.x13[day] = hive.x13d[day]
            hive.x14[day] = hive.x14d[day]
            
        elif (hive.x11d[day] + hive.x12d[day]) <= hive.u11[day]:
            # basic survival needs can be fully satisfied
            log.warn("basic survival needs can be fully satisfied")
            hive.x11[day] = hive.x11d[day]
            hive.x12[day] = hive.x12d[day]

            # other needs are diminished 
            hive.x13[day] = (hive.x13d[day] / (hive.x13d[day] + hive.x14d[day])) * \
                (hive.u11[day] - (hive.x11d[day] + hive.x12d[day]))
            hive.x14[day] = hive.u11[day] - (hive.x11d[day] + hive.x12d[day] + hive.x13[day])
            
        elif hive.u11[day] < (hive.x11d[day] + hive.x12d[day]):
            # basic survival needs cannot be fully satisfied
            log.warn("basic survival needs cannot be fully satisfied")
            hive.x11[day] = hive.x11d[day]
            hive.x12[day] = hive.x12d[day]
            hive.x13[day] = 0
            hive.x14[day] = 0
        else:
            log.error("this should never occur")
            assert False

        

        ## POLLEN INTAKE
        hive.u2P[day], hive.x21[day] = distribute_resources(hive.u2P[day], c8 * hive.x11[day])
        hive.u2P[day], hive.x22[day] = distribute_resources(hive.u2P[day], c9 * hive.x12[day])
        hive.u2P[day], hive.x23[day] = distribute_resources(hive.u2P[day], c9 * hive.x13[day])
        hive.u2P[day], hive.x24[day] = distribute_resources(hive.u2P[day], c9 * hive.x14[day])


        # what's left after supply division
        hive.u2P_1[day] = hive.u2P[day]

        hive.u1_1[day] = hive.u11[day] + hive.u12[day] - (hive.x11[day] + hive.x12[day] + hive.x13[day] + hive.x14[day])
        
        if hive.u2P_1[day] <= 0 or hive.u1_1[day] <= 0:
            log.warning("u2P_1 == %4.4f, u1_1 == %4.4f" % (hive.u2P_1[day], hive.u1_1[day]))
            if famine is None:
                famine = {'start_day': day, 'end_day': None}
            elif day - famine.get('start_day') > MAX_FAMINE_PERIOD:
                log.warning(" ☠️ ")
                out_population = 0
                break
        elif famine is not None:
            famine = None
        
        

        
        # calculate resources spent on building
        hive.x15[day] = c10 * hive.u3[day]
        hive.x25[day] = c11 * hive.u3[day]

        log.debug("usage: x11: %4.4f, x12: %4.4f, x13: %4.4f, x14: %4.4f, x15: %4.4f" % (hive.x11[day], hive.x12[day], hive.x13[day], hive.x14[day], hive.x15[day]))
        log.debug("usage: x21: %4.4f, x22: %4.4f, x23: %4.4f, x24: %4.4f, x25: %4.4f" % (hive.x21[day], hive.x22[day], hive.x23[day], hive.x24[day], hive.x25[day]))



        #hive.u2P[day], hive.x25[day] = distribute_resources(hive.u2P[day], hive.x15[day])
        hive.u2P[day], hive.x25[day] = distribute_resources(hive.u2P[day], hive.x25[day])

        hive.u11_2[day] = hive.u11[day] - (hive.x11[day] + hive.x12[day] + hive.x13[day] + hive.x14[day] + hive.x15[day])
        log.debug("liquid nectar after usage %4.4f" % hive.u11_2[day])
        if hive.u11_2[day] >= 0:
            # enough liquid nectar to satisfy needs
            hive.u12d[day] = c12 * hive.u11_2[day]
            hive.x16[day] = c13 * hive.u12d[day]

            assert c12*(1 + c13) < 1

            hive.u11[day] = hive.u11_2[day] - hive.u12d[day] - hive.x16[day]
            hive.u12[day] += hive.u12d[day]
        else:
            # need to liquify some solid nectar
            hive.u11[day] = 0
            hive.u12[day], _ = distribute_resources(hive.u12[day], -hive.u11_2[day])
            # hive.u12[day] += hive.u11_2[day]


        out_population = hive.sy[day] = c17 * hive.y[day] + hive.y1[day] + hive.y2[day]
        out_honey = hive.su[day] = c18 * (2 * hive.u11[day] + hive.u12[day] + hive.u2P[day])


        log.warning("SY = %d, SU = %d" % (hive.sy[day], hive.su[day]))
        

        """ ---------------------------------------------------------------------------- """

    log.info("queen params: A=%f, C=%d" % (queen.A, queen.C))
    fig, ax = plt.subplots(6, 2, sharex=True)
    ax[0][0].plot(hive.y3)
    ax[1][0].plot(hive.dy)
    ax[2][0].plot([sum(x) for x in zip(hive.u11, hive.u12)])
    ax[3][0].plot(hive.u2P)
    ax[0][1].plot(hive.y)
    ax[1][1].plot(hive.y1)
    ax[2][1].plot(hive.y2)
    ax[3][1].plot(hive.y4)
    ax[4][1].plot(hive.y5)
    ax[5][1].plot([sum(x) for x in zip(hive.y6Q, hive.y6V)])
    #plt.show()

    

    data = {'i_queenA' : queen.A, 'i_queenC' : queen.C, 'i_queenKcrit' : queen.k_crit, "o_population" : out_population, "o_honey" : out_honey}
    
    return data
    
    


def distribute_resources(resource, demand):
    assert demand >= 0, "Error: Negative demands!"
    assert resource >= 0, "Error: Negative resources!"
    usage = 0
    if demand > resource:
        usage = resource
        resource = 0
    else:
        usage = demand
        resource -= usage
    return resource, usage

if __name__ == "__main__":
    
    
    stream = open('data/document.yaml', 'w')
    from yaml import dump
    for i in range(1000):
        data = simulate({
            "enviroment" : {
                "nectar": 100,
                "pollen": 150,
                "temperature": 24,
            },
            "bee" : {
                "flight_distance" : 3000,
            }
        })
        dump(data, stream)
