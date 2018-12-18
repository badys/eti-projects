#!/usr/local/bin/python3.6

import logging
from hive import Hive
import enviroment
from queen import QueenBee

log = logging.getLogger("beelogger")

# days
SIMUATION_TIME = 100

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

    queen = QueenBee(SIMUATION_TIME)
    hive = Hive(SIMUATION_TIME)

    log.setLevel(logging.DEBUG)

    average_flights_num = 10
    average_gather_to_weight_ratio = 0.1

    # gathering efficiency
    c1 = 1 # nectar
    c2 = 1 # pollen

    # scout flights loss ratio
    c3 = 0.001

    # nectar demands
    c4 = .1
    c5 = .1
    c6 = .1
    c7 = .1

    # pollen demands (relative to nectar demands)
    c8 = .1
    c9 = .1

    c10 = 1
    c11 = 1
    c12 = 0.5
    c13 = 0.2
    c14 = 1
    c15 = 1
    c16 = 1

    E = 100000

    ########### start conditions
    hive.dy4[0] = 200
    hive.dy5[0] = 100
    hive.dy6[0] = 600
    hive.dy3[0] = hive.dy4[0] + hive.dy5[0] + hive.dy6[0]

    hive.u11[0] = 1000
    hive.u12[0] = 2000
    hive.u2P[0] = 2000
    ###########

    for day in range(SIMUATION_TIME):
        
        if day > 0:
            queen.b[day] = 0 if hive.u2P[max(day-1, 0)] == 0 or hive.y1[day-1] >= 3*hive.y4[day-1] else 1

            if day > queen.k_crit:
                queen.b[day] = max(1 - (1/E) * sum(hive.dy[:day]), 0)

        hive.dy[day] = int(queen.layEggs(day, hive.x13))


        # do bees growth
        hive.updatePopulation(day)

        # do supply flights
        hive.u1[day] = c1 * average_flights_num * average_gather_to_weight_ratio * hive.y6Q[day]
        hive.u2[day] = c2 * average_flights_num * average_gather_to_weight_ratio * hive.y6V[day]

        # store supplies
        hive.u11[day] = hive.u11[max(day-1, 0)] + hive.u1[day] # - human factor
        hive.u12[day] = hive.u12[max(day-1, 0)] # - human factor
        hive.u2P[day] = hive.u2P[max(day-1, 0)] + hive.u2[day] # - human factor

        # cannot have negative resources
        assert hive.u11[day] >= 0
        assert hive.u12[day] >= 0
        assert hive.u2P[day] >= 0


        """ ----------------------- calculate resource demand -------------------------- """
        
        ## RESOURCE DEMAND:
        # keep vital functions
        hive.x11d[day] = c4 * hive.y3[day]
        # proper larva growth
        hive.x12d[day] = c5 * hive.y1[day]
        # excess intake for feeding and larva care
        hive.x13d[day] = c6 * hive.y4[day] if (hive.y1[day] + hive.y[day]) > 0 else 0
        # excess intake for cluster building and wax synthesis
        hive.x14d[day] = c7 * hive.y5[day] if (hive.u3[day]) > 0 else 0


        # resource used on scout flights
        hive.x10[day] = c3 * average_flights_num * average_gather_to_weight_ratio * \
            (hive.y6Q[day] * hive.y6V[day])
        hive.u11[day] -= hive.x10[day]

        ## CALCULATE ACTUAL NECTAR INTAKE (priority based):
        if hive.u11[day] >= (hive.x11d[day] + hive.x12d[day] + hive.x13d[day] + hive.x14d[day]):
            # all demands can be satisfied
            print("all demands can be satisfied")
            hive.x11[day] = hive.x11d[day]
            hive.x12[day] = hive.x12d[day]
            hive.x13[day] = hive.x13d[day]
            hive.x14[day] = hive.x14d[day]
            
        elif (hive.x11d[day] + hive.x12d[day]) <= hive.u11[day]:
            # basic survival needs can be fully satisfied
            print("basic survival needs can be fully satisfied")
            hive.x11[day] = hive.x11d[day]
            hive.x12[day] = hive.x12d[day]

            # other needs are diminished 
            hive.x13[day] = (hive.x13d[day] / (hive.x13d[day] + hive.x14d[day])) * \
                (hive.u11[day] - (hive.x11d[day] + hive.x12d[day]))
            hive.x14[day] = hive.u11[day] - (hive.x11d[day] + hive.x12d[day] + hive.x13[day])
            
        elif hive.u11[day] < (hive.x11d[day] + hive.x12d[day]):
            # basic survival needs cannot be fully satisfied
            print("basic survival needs cannot be fully satisfied")
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
        
        # do not build new cluster TODO: implement this section
        hive.x15[day] = c10 * hive.u3[day]
        hive.x25[day] = c11 * hive.u3[day]

        hive.u11_2[day] = hive.u11[day] - (hive.x11[day] + hive.x12[day] + hive.x13[day] + hive.x14[day] + hive.x15[day])

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
            hive.u12[day] += hive.u11_2[day]

        hive.u2P[day], hive.x25[day] = distribute_resources(hive.u2P[day], hive.x25[day])


        # calculate the building related stuff
        hive.u3[day] = 0 # TODO
        # sy, sv



        print("DAY%d".center(80, "*") % day)
        print("🥚 x %d".center(18) % hive.y[day], end=' ')
        print("🐛 x %d".center(18) % hive.y1[day], end=' ')
        print("🐜 x %d".center(18) % hive.y2[day], end=' ')
        print("🐝 x %d".center(18) % hive.y3[day])
        print("COLLECTED: nectar %f, pollen %f" % (hive.u1[day], hive.u2[day]))
        print("STORED: nectar liquid %f, nectar solid %f, pollen %f" % (hive.u11[day], hive.u12[day], hive.u2P[day]))
        print("demands: x11 %f, x12 %f, x13 %f, x14 %f" % \
            (hive.x11d[day], hive.x12d[day], hive.x13d[day], hive.x14d[day]))
        print("usage:   x10 %f, x11 %f, x12 %f, x13 %f, x14 %f, x15 %f, x16 %f" % \
            (hive.x10[day], hive.x11[day], hive.x12[day], hive.x13[day], hive.x14[day], hive.x15[day], hive.x16[day]))
        print("usage:                   x21 %f, x22 %f, x23 %f, x24 %f, x25 %f" % \
            (hive.x21[day], hive.x22[day], hive.x23[day], hive.x24[day], hive.x25[day]))

        #print("nectar above intake %f" % hive.u1_1[day])
        #print("pollen above intake %f" % hive.u2P_1[day])
        print("".center(80, "*"))
        # input()

        """ ---------------------------------------------------------------------------- """




    pass


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
    simulate({
        "enviroment" : {
            "nectar": 100,
            "pollen": 150,
            "temperature": 24,
        },
        "bee" : {
            "flight_distance" : 3000,
        }
    })
