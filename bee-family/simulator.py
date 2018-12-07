#!/usr/local/bin/python3.6

import logging
from hive import Hive
import enviroment
from queen import QueenBee

log = logging.getLogger("beelogger")

# days
SIMUATION_TIME = 30

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

    c6 = 0.7

    average_flights_num = 10
    average_weight_efficiency = 0.1
    c1 = 0.5
    c2 = 0.3
    c3 = 1
    c8 = 1
    c4 = 1
    c5 = 1
    c6 = 1
    c7 = 1
    c9 = 1
    c10 = 1
    c11 = 1
    c12 = 1
    c13 = 1
    c14 = 1
    c15 = 1
    c16 = 1


    ########### start conditions
    hive.dy4[0] = 200
    hive.dy5[0] = 100
    hive.dy6[0] = 600
    hive.dy3[0] = hive.dy4[0] + hive.dy5[0] + hive.dy6[0]
    ###########

    for day in range(SIMUATION_TIME):
        
        if day > 0:
            queen.b[day] = 0 if hive.u2P[max(day-1, 0)] == 0 or hive.y1[day-1] >= 3*hive.y4[day-1] else 1
        hive.dy[day] = int(queen.layEggs(day, hive.x13))


        # do bees growth
        hive.updatePopulation(day)

        # do supply flights
        hive.u1[day] = c1 * average_flights_num * average_weight_efficiency * hive.y6Q[day]
        hive.u2[day] = c2 * average_flights_num * average_weight_efficiency * hive.y6V[day]

        # store supplies
        hive.u11[day] = hive.u11[max(day-1, 0)] + hive.u1[day] # - human factor
        hive.u12[day] = hive.u12[max(day-1, 0)] # - human factor
        hive.u2P[day] = hive.u2P[max(day-1, 0)] + hive.u2[day] # - human factor


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
        hive.x21[day] = c8 * hive.x11[day]
        hive.x22[day] = c9 * hive.x12[day]
        hive.x23[day] = c9 * hive.x13[day]
        hive.x24[day] = c9 * hive.x14[day]

        hive.u2P_1[day] = hive.u2P[day] - (hive.x21[day] + hive.x22[day] + hive.x23[day] + hive.x24[day])
        hive.u1_1[day] = hive.u11[day] - hive.u12[day] - ((hive.x11[day] + hive.x12[day] + hive.x13[day] + hive.x14[day]))
        
        # do not build new cluster TODO: implement this section
        hive.x15[day] = c10 * hive.u3[day]
        hive.x25[day] = c11 * hive.u3[day]

        hive.u11_2[day] = hive.u11[day] - (hive.x11[day] + hive.x12[day] + hive.x13[day] + hive.x14[day] + hive.x15[day])

        if hive.u11_2[day] >= 0:
            # enough liquid nectar to satisfy needs
            hive.u12d[day] = c12 * hive.u11_2[day]
            hive.x16[day] = c13 * hive.u12d[day]

            hive.u11[day] = hive.u11_2[day] - hive.u12d[day] - hive.x16[day]
            hive.u12[day] += hive.u12d[day]
        else:
            # need to liquify some solid nectar
            hive.u11[day] = 0
            hive.u12[day] += hive.u11_2[day]

        hive.u2P[day] -= (hive.x21[day] + hive.x22[day] + hive.x23[day] + hive.x24[day] + hive.x25[day])

        # resource used on scout flights
        hive.x10[day] = c3 * average_flights_num * average_weight_efficiency * \
            (hive.y6Q[day] * hive.y6V[day])


        print("DAY%d".center(80, "*") % day)
        print("🥚 x %d".center(18) % hive.y[day], end=' ')
        print("🐛 x %d".center(18) % hive.y1[day], end=' ')
        print("🐜 x %d".center(18) % hive.y2[day], end=' ')
        print("🐝 x %d".center(18) % hive.y3[day])
        print("nectar gathered %d, pollen gathered %d" % (hive.u1[day], hive.u2[day]))
        print("nectar to day %d, pollen to day %d" % (hive.u11[day], hive.u12[day]))
        print("demands: x11 %d, x12 %d, x13 %d, x14 %d" % \
            (hive.x11d[day], hive.x12d[day], hive.x13d[day], hive.x14d[day]))
        print("usage:   x11 %d, x12 %d, x13 %d, x14 %d" % \
            (hive.x11[day], hive.x12[day], hive.x13[day], hive.x14[day]))

        print("nectar above intake %f" % hive.u1_1[day])
        print("pollen above intake %f" % hive.u2P_1[day])
        print("".center(80, "*"))


        """ ---------------------------------------------------------------------------- """




    pass

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
