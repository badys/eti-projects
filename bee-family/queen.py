import logging

log = logging.getLogger("beelogger")

class QueenBee:

    def __init__(self, max_days, A, C, k_crit):
        self.A = A
        self.C = C
        self.b = [1] * max_days
        self.k_crit = k_crit # breeding efficiency slowly decreases after this day

    def layEggs(self, day, x13):
        # calculates how many eggs can be layed by queen bee
        avg_excessive_food = sum(x13[max(day-9, 0):day+1]) / 10
        if (avg_excessive_food <= 0):
            log.warning("no food left")
        eggs = (avg_excessive_food * self.A + self.C) * self.b[day]
        log.info("layEggs: queen bee layed %d eggs", eggs)
        if eggs > 2000 + self.C: 
            eggs = 2000 + self.C
        #assert eggs <= 2000 and eggs >= 0, eggs

        return eggs

