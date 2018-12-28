import logging

logger = logging.getLogger("beelogger")

def getBeeLogger():
    logger = logging.getLogger("beelogger")
    handler = logging.StreamHandler()
    formatter = logging.Formatter(
            '%(levelname)-8s %(message)s')
    handler.setFormatter(formatter)
    logger.addHandler(handler)
    logger.setLevel(logging.DEBUG)
    return logger