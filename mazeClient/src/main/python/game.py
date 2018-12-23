import socket
import sys

import maze


class Client:
    # server_ip = None
    # server_port = None

    def __init__(self, server_ip, server_port):
        self.socket = socket.socket()
        self.server_ip = server_ip
        self.server_port = server_port

    def listen_server(self):
        while True:
            data = self.socket.recv(1024)
            parts = data.decode('UTF-8').split('\n')
            print('author : ' + parts[0] + '\n' + parts[1] + '\n' + parts[2])
            print('enter message text')

    def connect(self):
        self.socket.connect((self.server_ip, int(self.server_port)))
        print('after connect:', self.socket)
        print('connected to server!')



def main(server_ip, server_port):
    print(server_port, server_ip)
    client = Client(server_ip, server_port)
    client.connect()
    maze.main()


if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2])
