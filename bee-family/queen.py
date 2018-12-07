import logging

log = logging.getLogger("beelogger")

class QueenBee:

    def __init__(self, max_days):
        self.A = 0.15
        self.C = 100
        self.b = [1] * max_days # needs calculation 1 for now

    def layEggs(self, day, x13):
        # calculates how many eggs can be layed by queen bee
        excessive_food = sum(x13[max(day-9, 0):day+1])
        # print("sum_x13 ", x13[max(day-9, 0):day+1])
        eggs = ((excessive_food * self.A / 10) + self.C) * self.b[day]
        log.info("layEggs: queen bee layed %d eggs", eggs)
        if eggs > 2000 or eggs < 0:
            log.error("layEggs: needs tweaking!")
            # assert False
        return eggs