# eti-projects
Repozytorium zawiarające projekty wykonywane na 2. semestrze Informatyki drugiego stopnia.

### Inteligentne Wyszukiwanie Informacji & Metody analizy danych wielkoskalowych
Modelowanie rodziny pszczelej na podstawie artykułu https://www.dropbox.com/home/mgr_sem_2/PSZCZOŁY?preview=Model_rodziny_pszczelej.pdf
1. Symulator w Java/C++/C#
2. Sieć wytrenowana w Python + Keras

### Rzeczywistość wirtualna
Maze Runner - gra w unity polegająca na wyjściu z labiryntu wykorzystująca HTC VIVE

### Elementy Bioinformatyki
"Kalkulator" drzew ukorzenionych i nieukorzenionych. Drzewa wczytywane są z pliku tekstowego (wykorzystany parser formatu NEWICK).
Operacje:
1. Konwersja reprezentacji "rodzina zgodnych klastrów" (ukorzenione) lub "rodzina zgodnych rozbić" (nieukorzenione) na graf z testem poprawności danych tj. "czy podana rodzina była zgodna?"
2. Wyznaczanie odległości topologicznej metryką Robinsona-Fouldsa między parą drzew
3. Drzewa konsensusu (o podanym poziomie procentowym) dla zadanego zbioru drzew oraz jej wspólne rozszerzanie (jeśli takie istnieje).
4. Obcięcie podanego drzewa do drzewa filogenetycznego do zadanego podzbioru liści

Ew. dodatki dotyczące drzew nieukorzenionych
5. Przeprowadzenie operacji NNI (Nearest neighbor interchange) i TBR (Tree bisection and reconnection) dla drzew nieukorzenionych binarnych w podanych przez użytkownika punktach.