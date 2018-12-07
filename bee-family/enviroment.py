PI = 3.14159265359

class Enviroment:

    def __init__(self, nectar_per_ha, pollen_per_ha, radious_m, temperature):
        # field of interest, radious is bee's maximum flight distance
        field_ha = PI * radious_m * radious_m / 10000

        # nectar and pollen amounts given in kg
        self.nectar = nectar_per_ha * field_ha
        self.pollen = pollen_per_ha * field_ha

        self.temperature = temperature

    def __str__(self):
        return "Enviroment: nectar left = {nectar}, pollen left = {pollen}, current temperature = {temp}" .format(
                nectar=self.nectar, pollen=self.pollen, temp=self.temperature)

    def __repr__(self):
        return super().__repr__()