import os
import random
import time, sys

n = 13
EMPTY = ' - '
WALL = ' # '
WAY = '   '
FINISH = 'END'
PASSED = ' . '
CURRENT = ' @ '
visibletable = [[WALL if i == 0 or i == n - 1 or j == 0 or j == n - 1 else WAY for j in range(n)] for i in range(n)]


class Game:

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
            table[x - 1][y] = WAY
        if x < nextx:
            table[x + 1][y] = WAY
        if y > nexty:
            table[x][y - 1] = WAY
        if y < nexty:
            table[x][y + 1] = WAY

        table[nextx][nexty] = WAY

    def getStartCoords(self):
        StartX = random.randrange(1, n, 2)
        StartY = random.randrange(1, n, 2)
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

        table[x][y] = WAY
        go = True
        while go:
            x, y, go = self.nextStep(x, y, table, stack, go)

        table[x][y] = FINISH
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
        if x + shift < n:
            if table[x + shift][y] == EMPTY:
                result.append([x + shift, y])
        if x - shift > 0:
            if table[x - shift][y] == EMPTY:
                result.append([x - shift, y])
        if y + shift < n:
            if table[x][y + shift] == EMPTY:
                result.append([x, y + shift])
        if y - shift > 0:
            if table[x][y - shift] == EMPTY:
                result.append([x, y - shift])

        return result

    def printField(self, x, y, table):
        global visibletable

        if 0 < x - 1 < n and 0 < y - 1 < n:
            if visibletable[x - 1][y - 1] != table[x - 1][y - 1]:
                visibletable[x - 1][y - 1] = table[x - 1][y - 1]
        if 0 < x - 1 < n and 0 < y < n:
            if visibletable[x - 1][y] != table[x - 1][y]:
                visibletable[x - 1][y] = table[x - 1][y]
        if 0 < x - 1 < n and 0 < y + 1 < n:
            if visibletable[x - 1][y + 1] != table[x - 1][y + 1]:
                visibletable[x - 1][y + 1] = table[x - 1][y + 1]
        if 0 < x < n and 0 < y - 1 < n:
            if visibletable[x][y - 1] != table[x][y - 1]:
                visibletable[x][y - 1] = table[x][y - 1]
        if 0 < x < n and 0 < y < n:
            if visibletable[x][y] != table[x][y]:
                visibletable[x][y] = table[x][y]

        if 0 < x < n and 0 < y + 1 < n:
            if visibletable[x][y + 1] != table[x][y + 1]:
                visibletable[x][y + 1] = table[x][y + 1]

        if 0 < x + 1 < n and 0 < y - 1 < n:
            if visibletable[x + 1][y - 1] != table[x + 1][y - 1]:
                visibletable[x + 1][y - 1] = table[x + 1][y - 1]

        if 0 < x + 1 < n and 0 < y < n:

            if visibletable[x + 1][y] != table[x + 1][y]:
                visibletable[x + 1][y] = table[x + 1][y]
        if 0 < x + 1 < n and 0 < y + 1 < n:
            if visibletable[x + 1][y + 1] != table[x + 1][y + 1]:
                visibletable[x + 1][y + 1] = table[x + 1][y + 1]

        for line in visibletable:
            print(''.join(str(item) for item in line))

        print('\n')

    def findend(self, x, y, table):
        table[x][y] = CURRENT
        self.clearConsol()
        self.printField(x, y, table)
        while table[x][y] != FINISH:

            step = self.getkey()

            if step == b'\xe6' or step == b'w':

                if self.checkIfPossible(x - 1, y, table):
                    table[x][y] = PASSED
                    x -= 1
                    if table[x][y] != FINISH:
                        table[x][y] = CURRENT
                        self.clearConsol()
                        self.printField(x, y, table)
                    else:
                        continue



            elif step == b'\xa2' or step == b'd':

                if self.checkIfPossible(x, y + 1, table):
                    table[x][y] = PASSED
                    y += 1
                    if table[x][y] != FINISH:
                        table[x][y] = CURRENT
                        self.clearConsol()
                        self.printField(x, y, table)
                    else:
                        continue




            elif step == b'\xe4' or step == b'a':
                if self.checkIfPossible(x, y - 1, table):
                    table[x][y] = PASSED
                    y -= 1
                    if table[x][y] != FINISH:
                        table[x][y] = CURRENT
                        self.clearConsol()
                        self.printField(x, y, table)
                    else:
                        continue


            elif step == b'\xeb' or step == b's':

                if self.checkIfPossible(x + 1, y, table):
                    table[x][y] = PASSED
                    x += 1
                    if table[x][y] != FINISH:
                        table[x][y] = CURRENT
                        self.clearConsol()
                        self.printField(x, y, table)
                    else:
                        continue

        print('YOU ESCAPRED MAZE')

    def checkIfPossible(self, x, y, table):
        if 0 < x < n and 0 < y < n:
            if table[x][y] != WALL:
                return True
        return False

    def startGame(self, table):
        x, y = self.getStartCoords()
        self.findend(x, y, table)

    def generateTable(self):
        table = [[WALL if j % 2 == 0 or i % 2 == 0 else EMPTY for j in range(n)] for i in range(n)]
        stack = self.generateLabirint(table)
        print(' labirint\n')
        self.fullLabirint(table, stack)
        return table


def main(table):
    game = Game()
    game.startGame(table)


def get_table(maze_height):
    global n
    n = maze_height
    game = Game()
    return game.generateTable()

