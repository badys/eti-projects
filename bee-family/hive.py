# mama czerwi ~2000 jaj dziennie
# pszczółka unosi do 10% własnej masy
# pszczółki latają do 3km za jedzonkiem
# wartosci stalych dobierac na oko

import logging
log = logging.getLogger("beelogger")

class Hive:

    EMBRIO_PERIOD = 3
    LARVA_PERIOD = 6
    BIGGER_LARVA_PERIOD = 12
    YOUNG_BEE_PERIOD = 10
    ADULT_BEE_PERIOD = 10
    SCOUT_BEE_PERIOD = 15



    def __init__(self, max_days, initial_values):
        self.initial_values = initial_values
        self.y = [0] * max_days
        self.y1 = [0] * max_days
        self.y2 = [0] * max_days
        self.y3 = [0] * max_days
        self.y4 = [0] * max_days
        self.y5 = [0] * max_days
        self.y6Q = [0] * max_days
        self.y6V = [0] * max_days
        self.dy = [0] * max_days
        self.dy1 = [0] * max_days
        self.dy2 = [0] * max_days
        self.dy3 = [0] * max_days
        self.dy4 = [0] * max_days
        self.dy5 = [0] * max_days
        self.dy6 = [0] * max_days
        self.x = [0] * max_days
        self.x1 = [0] * max_days
        self.x2 = [0] * max_days
        self.x3 = [0] * max_days
        self.x4 = [0] * max_days
        self.x5 = [0] * max_days
        self.x6 = [0] * max_days
        self.x7 = [0] * max_days
        self.x8 = [0] * max_days
        self.x9 = [0] * max_days
        self.x10 = [0] * max_days
        self.x11 = [0] * max_days
        self.x12 = [0] * max_days
        self.x13 = [0] * max_days
        self.x14 = [0] * max_days
        self.x11d = [0] * max_days
        self.x12d = [0] * max_days
        self.x13d = [0] * max_days
        self.x14d = [0] * max_days
        self.x15 = [0] * max_days
        self.x25 = [0] * max_days
        self.u1 = [0] * max_days
        self.u2 = [0] * max_days
        self.u11 = [0] * max_days # stored liquid honey
        self.u12 = [0] * max_days # stored solid honey
        self.u2P = [0] * max_days # stored pollen
        self.u3 = [0] * max_days 
        self.x21 = [0] * max_days
        self.x22 = [0] * max_days
        self.x23 = [0] * max_days
        self.x24 = [0] * max_days
        self.u2P_1 = [0] * max_days
        self.u1_1 = [0] * max_days
        self.u3 = [0] * max_days
        self.u12d = [0] * max_days
        self.u11_2 = [0] * max_days
        self.x16 = [0] * max_days
        self.sy = [0] * max_days
        self.su = [0] * max_days
        # self.x14 = [0] * max_days
        # self.x13 = [0] * max_days
        # self.x14 = [0] * max_days

    def updatePopulation(self, day):
        assert isinstance(day, int) 

        if day == 0:
            # starting conditions
            self.dy[day] = self.initial_values[0]
            self.dy1[day] = self.initial_values[1]
            self.dy2[day] = self.initial_values[2]
            self.dy3[day] = self.initial_values[4] + self.initial_values[5] + self.initial_values[6]
            self.dy4[day] = self.initial_values[4]
            self.dy5[day] = self.initial_values[5]
            self.dy6[day] = self.initial_values[6]
        else:
            self.dy1[day] = self.dy[day-self.EMBRIO_PERIOD]
            self.dy2[day] = self.dy1[day-self.LARVA_PERIOD]
            self.dy3[day] = self.dy2[day-self.BIGGER_LARVA_PERIOD]
            self.dy4[day] = self.dy3[day]
            self.dy5[day] = self.dy4[day-self.YOUNG_BEE_PERIOD]
            self.dy6[day] = self.dy5[day-self.ADULT_BEE_PERIOD]

        log.debug("growth: dy=%d, dy1=%d, dy2=%d, dy3=%d, dy4=%d, dy5=%d, dy6=%d" % (self.dy[day], self.dy1[day], self.dy2[day], self.dy3[day], self.dy4[day], self.dy5[day], self.dy6[day]))
        
        self.y[day]  = sum(self.dy[max(day-2, 0):day+1])
        self.y1[day] = sum(self.dy1[max(day-5, 0):day+1])
        self.y2[day] = sum(self.dy2[max(day-11, 0):day+1])
        self.y4[day] = sum(self.dy4[max(day-9, 0):day+1])
        self.y5[day] = sum(self.dy5[max(day-9, 0):day+1])
        self.y6V[day] = int(sum(self.dy6[max(day-14, 0):day+1]) * 0.2)
        self.y6Q[day] = sum(self.dy6[max(day-14, 0):day+1]) - self.y6V[day]
        self.y3[day] = self.y4[day] + self.y5[day] + self.y6Q[day] + self.y6V[day]

        #log.debug("y = sum({})".format(self.dy[max(day-2, 0):day+1]))
        #log.debug("y1 = sum({})".format(self.dy1[max(day-5, 0):day+1]))
        #log.debug("y2 = sum({})".format(self.dy2[max(day-11, 0):day+1]))
        #log.debug("y3 = sum({})".format(self.dy3[max(day-34, 0):day+1]))
        #log.debug("y4 = sum({})".format(self.dy4[max(day-9, 0):day+1]))
        #log.debug("y5 = sum({})".format(self.dy5[max(day-9, 0):day+1]))
        #log.debug("y6 = sum({})".format(self.dy6[max(day-14, 0):day+1]))
        
        # make sure bee count across fractions is correct
        assert self.y3[day] == (self.y4[day] + self.y5[day] + self.y6Q[day] + self.y6V[day]), "%d != %d + %d + %d + %d" % (self.y3[day], self.y4[day], self.y5[day], self.y6Q[day], self.y6V[day])
       
