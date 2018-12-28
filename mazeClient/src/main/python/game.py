import socket
import sys
import os
import maze


class Client:
    id = 0

    def __init__(self, server_ip, server_port, player_name):
        self.socket = socket.socket()
        self.server_ip = server_ip
        self.server_port = server_port
        self.player_name = player_name
        self.connect()
        self.player_id = self.receivePlayerId(self.player_name)

    def connect(self):
        self.socket.connect((self.server_ip, int(self.server_port)))
        print('after connect:', self.socket)
        print('connected to server!')

    def saveTable(self, table):
        table_string = "SAVE_TABLE_MAZE " + "table:"
        for line in table:
            for item in line:
                table_string += str(item) + ","
            table_string += "//"
        table_string += "END_MESSAGE\n"
        print(table_string)
        self.socket.send(table_string.encode())
        print("must be sent!!")

    def getSavedTable(self):
        message = "GET_TABLE_MAZE " + '\n'
        self.socket.send(message.encode())
        table_string = str(self.socket.recv(2048)[:-2])
        table_rows = table_string.split('//')
        n = len(table_rows)
        table = [[" # " for j in range(n)] for i in range(n)]
        for i in range(n):
            items = table_rows[i].split(",")
            m = len(items)
            for j in range(m):
                table[i][j] = items[j]
        return table

    def waitOtherPlayers(self):
        # todo: send my 'ready' status, read approval, when allows, start the game
        message = "READY " + '\n'
        self.socket.send(message.encode())
        print("waiting for other players...")
        status = str(self.socket.recv(2048)[:-1])

    def sendTimeToDefineWinner(self, time):
        message = "SEND_TIME " + "time:" + str(time) + '\n'
        self.socket.send(message.encode())
        print("waiting for others...")

    def showResults(self):
        message = "GET_RESULTS " + '\n'
        self.socket.send(message.encode())

        results = str(self.socket.recv(2048)[:-2])
        results_rows = results.split('//')

        # clearConsol()
        for result in results_rows:
            print(result)

    def sendKillServerMessage(self):
        message = "KILL_SERVER"
        self.socket.send(message.encode())

    def receivePlayerId(self, player_name):
        message = "CREATE_PLAYER_ID player_name:" + player_name  + '\n'
        self.socket.send(message.encode())
        answer = self.socket.recv(2048)
        print(answer)
        player_id = str(answer)[2:-3]
        print(player_id)
        return player_id


def main(server_ip, server_port, isHostRun, maze_height, player_name):
    print(server_port, server_ip, isHostRun, player_name)
    client = Client(server_ip, server_port, player_name)
    # client.connect()
    if isHostRun == str(True):
        table = maze.get_table(int(maze_height))
        client.saveTable(table)
    else:
        table = client.getSavedTable()
    client.waitOtherPlayers()
    # starts the game and retuns time required for user to get escaped
    time = maze.main(table)
    client.sendTimeToDefineWinner(time)
    client.showResults()
    input('type any key to exit the game')
    client.sendKillServerMessage()


if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5])

def clearConsol():
    if sys.platform == 'win32':
        os.system('cls')
    else:
        os.system('clear')