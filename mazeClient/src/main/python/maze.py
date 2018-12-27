import os
import random
import time, sys


class Game:

    EMPTY = ' - '
    WALL = ' # '
    WAY = '   '
    FINISH = 'END'
    PASSED = ' . '
    CURRENT = ' @ '

    def __init__(self, n):
        self.n = int(n)
        self.visibletable = [[self.WALL if i == 0 or i == self.n - 1 or j == 0 or j == self.n - 1 else self.WAY for j in range(self.n)] for i in range(self.n)]

    def getkey(self):
        import sys
        if sys.platform[:3] == 'win':
            import msvcrt
            key = msvcrt.getch()
            return key
        elif sys.platform[:3] == 'lin':
            import termios, sys, os

            TERMIOS = termios

            fd = sys.stdin.fileno()
            old = termios.tcgetattr(fd)
            new = termios.tcgetattr(fd)
            new[3] = new[3] & ~TERMIOS.ICANON & ~TERMIOS.ECHO
            new[6][TERMIOS.VMIN] = 1
            new[6][TERMIOS.VTIME] = 0
            termios.tcsetattr(fd, TERMIOS.TCSANOW, new)
            c = None
            try:
                c = os.read(fd, 1)
            finally:
                termios.tcsetattr(fd, TERMIOS.TCSAFLUSH, old)
            return c

    def clearConsol(self):
        if sys.platform == 'win32':
            os.system('cls')
        else:
            os.system('clear')

    def breakWallAndGo(self, x, y, nextx, nexty, table):
        if x > nextx:
            table[x - 1][y] = self.WAY
        if x < nextx:
            table[x + 1][y] = self.WAY
        if y > nexty:
            table[x][y - 1] = self.WAY
        if y < nexty:
            table[x][y + 1] = self.WAY

        table[nextx][nexty] = self.WAY

    def getStartCoords(self):
        StartX = random.randrange(1, self.n, 2)
        StartY = random.randrange(1, self.n, 2)
        # print('COORDINATES: ', StartX, StartY)
        return StartX, StartY

    def fullLabirint(self, table, stack):
        while stack:
            x, y = stack.pop()
            go = True
            while go:
                x, y, go = self.nextStep(x, y, table, stack, go)

    def generateLabirint(self, table):
        x, y = self.getStartCoords()
        stack = []

        table[x][y] = self.WAY
        go = True
        while go:
            x, y, go = self.nextStep(x, y, table, stack, go)

        table[x][y] = self.FINISH
        print(stack)
        return stack

    def nextStep(self, x, y, table, stack, go):
        neighbours = self.getFreeNeighbours(x, y, table, shift=2)
        # print('neighbours: ', neighbours)
        if len(neighbours) > 1:
            stack.append([x, y])
        elif len(neighbours) == 0:
            go = False
            # print('GO FALSE')
            return x, y, go
        coords = random.choice(neighbours)
        nextx = coords[0]
        nexty = coords[1]

        self.breakWallAndGo(x, y, nextx, nexty, table)

        return nextx, nexty, go

    def getFreeNeighbours(self, x, y, table, shift=1):
        result = []
        if x + shift < self.n:
            if table[x + shift][y] == self.EMPTY:
                result.append([x + shift, y])
        if x - shift > 0:
            if table[x - shift][y] == self.EMPTY:
                result.append([x - shift, y])
        if y + shift < self.n:
            if table[x][y + shift] == self.EMPTY:
                result.append([x, y + shift])
        if y - shift > 0:
            if table[x][y - shift] == self.EMPTY:
                result.append([x, y - shift])

        return result

    def printField(self, x, y, table):

        if 0 < x - 1 < self.n and 0 < y - 1 < self.n:
            if self.visibletable[x - 1][y - 1] != table[x - 1][y - 1]:
                self.visibletable[x - 1][y - 1] = table[x - 1][y - 1]
        if 0 < x - 1 < self.n and 0 < y < self.n:
            if self.visibletable[x - 1][y] != table[x - 1][y]:
                self.visibletable[x - 1][y] = table[x - 1][y]
        if 0 < x - 1 < self.n and 0 < y + 1 < self.n:
            if self.visibletable[x - 1][y + 1] != table[x - 1][y + 1]:
                self.visibletable[x - 1][y + 1] = table[x - 1][y + 1]
        if 0 < x < self.n and 0 < y - 1 < self.n:
            if self.visibletable[x][y - 1] != table[x][y - 1]:
                self.visibletable[x][y - 1] = table[x][y - 1]
        if 0 < x < self.n and 0 < y < self.n:
            if self.visibletable[x][y] != table[x][y]:
                self.visibletable[x][y] = table[x][y]

        if 0 < x < self.n and 0 < y + 1 < self.n:
            if self.visibletable[x][y + 1] != table[x][y + 1]:
                self.visibletable[x][y + 1] = table[x][y + 1]

        if 0 < x + 1 < self.n and 0 < y - 1 < self.n:
            if self.visibletable[x + 1][y - 1] != table[x + 1][y - 1]:
                self.visibletable[x + 1][y - 1] = table[x + 1][y - 1]

        if 0 < x + 1 < self.n and 0 < y < self.n:

            if self.visibletable[x + 1][y] != table[x + 1][y]:
                self.visibletable[x + 1][y] = table[x + 1][y]
        if 0 < x + 1 < self.n and 0 < y + 1 < self.n:
            if self.visibletable[x + 1][y + 1] != table[x + 1][y + 1]:
                self.visibletable[x + 1][y + 1] = table[x + 1][y + 1]

        for line in self.visibletable:
            print(''.join(str(item) for item in line))

        print('\n')

    def findend(self, x, y, table):
        table[x][y] = self.CURRENT
        self.clearConsol()
        self.printField(x, y, table)
        while table[x][y] != self.FINISH:

            step = self.getkey()

            if step == b'\xe6' or step == b'w':

                if self.checkIfPossible(x - 1, y, table):
                    table[x][y] = self.PASSED
                    x -= 1
                    if table[x][y] != self.FINISH:
                        table[x][y] = self.CURRENT
                        self.clearConsol()
                        self.printField(x, y, table)
                    else:
                        continue



            elif step == b'\xa2' or step == b'd':

                if self.checkIfPossible(x, y + 1, table):
                    table[x][y] = self.PASSED
                    y += 1
                    if table[x][y] != self.FINISH:
                        table[x][y] = self.CURRENT
                        self.clearConsol()
                        self.printField(x, y, table)
                    else:
                        continue




            elif step == b'\xe4' or step == b'a':
                if self.checkIfPossible(x, y - 1, table):
                    table[x][y] = self.PASSED
                    y -= 1
                    if table[x][y] != self.FINISH:
                        table[x][y] = self.CURRENT
                        self.clearConsol()
                        self.printField(x, y, table)
                    else:
                        continue


            elif step == b'\xeb' or step == b's':

                if self.checkIfPossible(x + 1, y, table):
                    table[x][y] = self.PASSED
                    x += 1
                    if table[x][y] != self.FINISH:
                        table[x][y] = self.CURRENT
                        self.clearConsol()
                        self.printField(x, y, table)
                    else:
                        continue

        print('YOU ESCAPRED MAZE')

    def checkIfPossible(self, x, y, table):
        if 0 < x < self.n and 0 < y < self.n:
            if table[x][y] !=self.WALL:
                return True
        return False

    def startGame(self, table):
        x, y = self.getStartCoords()
        self.findend(x, y, table)

    def generateTable(self):
        table = [[self.WALL if j % 2 == 0 or i % 2 == 0 else self.EMPTY for j in range(self.n)] for i in range(self.n)]
        stack = self.generateLabirint(table)
        print(' labirint\n')
        self.fullLabirint(table, stack)
        return table


def main(table):
    game = Game(len(table))
    start = time.time()
    game.startGame(table)
    end = time.time()
    print(end - start)
    return end - start


def get_table(maze_height):
    game = Game(maze_height)
    return game.generateTable()

