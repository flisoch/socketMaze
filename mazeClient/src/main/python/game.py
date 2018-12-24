import socket
import sys

import maze


class Client:

    def __init__(self, server_ip, server_port):
        self.socket = socket.socket()
        self.server_ip = server_ip
        self.server_port = server_port

    def connect(self):
        self.socket.connect((self.server_ip, int(self.server_port)))
        print('after connect:', self.socket)
        print('connected to server!')

    def saveTable(self, table):
        table_string = "SAVE_TABLE_MAZE port:" + self.server_port + ";table:"
        for line in table:
            for item in line:
                table_string += str(item) + ","
            table_string += "//"
        table_string += "END_MESSAGE"
        print(table_string)
        self.socket.send(table_string + '\n')
        print("must be sent!!")

    def getSavedTable(self):
        message = "GET_TABLE_MAZE port:" + self.server_port + '\n'
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


def main(server_ip, server_port, isHostRun, maze_height):
    print(server_port, server_ip, isHostRun)
    client = Client(server_ip, server_port)
    client.connect()
    if isHostRun == str(True):
        table = maze.get_table(maze_height)
        client.saveTable(table)
    else:
        table = client.getSavedTable()

    # for line in table:
    #     print(''.join(str(item) for item in line))

    maze.main(table)


if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4])
