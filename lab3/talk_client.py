'''This talk client is a client that is able to a server through a TCP/IP connection
and send and receive data from the server.  All data that it recives from the server is 
automatically printed out, and all data that is entered into the command line is sent to 
the server with the users name attached.  It also handles command line arguments and any errors
it recives.

Run with: python3 talk_client.py -s <server-ip> -p <server-port> -n <name> (-v if you want verbose)

Authors: Quentin Barnes and Ty Vredeveld
Date: Oct 26, 2019

Citations:
1) https://medium.com/vaidikkapoor/understanding-non-blocking-i-o-with-python-part-1-ec31a2e2db9b
'''

#Import stmts
import select
from socket import*
import sys
import argparse

#Parse the cmd line arguments
parser = argparse.ArgumentParser(description="A prattle client")

parser.add_argument("-n", "--name", dest="name", default=gethostname(), help="name to be prepended in messages (default: machine name)")
parser.add_argument("-s", "--server", dest="server", default="127.0.0.1",
                    help="server hostname or IP address (default: 127.0.0.1)")
parser.add_argument("-p", "--port", dest="port", type=int, default=12345,
                    help="TCP port the server is listening on (default 12345)")
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose",
                    help="turn verbose output on")
args = parser.parse_args()

#Use the cmd line arguments
isVerbose = args.verbose

nameToPrint = args.name + ' says: '

#Connect to the server through sockets and using the IP and Port provided
s = socket(AF_INET, SOCK_STREAM)
s.connect((args.server, args.port))
if isVerbose:
    print('Client connected to server ' + HOST + ' on port ' + str(PORT), sys.stdout)

#Send the the server that the user is connected
s.sendall((nameToPrint + 'connected').encode())

#Main loop that the program will sit in until it is exited
while True:
    #Check to see if the socket still has a peer name and if not the server is closed.  It then exits nicely instad of throwing an error
    try:
        if s.getpeername() is None:
            print("Error Quitting from server crash, its about to be ugly\n", sys.stdout)
    except OSError:
        if isVerbose:
            print('Detected that the client lost its connection to the server \nand lost the peer name of it, so terminating the program', sys.stdout)
        print("Server Closed")
        break
    
    #Trys the rest of the code so if there is an error, it can still exit nicely
    try:
        #This line was taken from citation 1 because i had a hard time understanding what select was doing
        #Use it as a example that i modified
        #When stdin or s is updated, it is put on the reedable array
        readable, writeable, exceptional = select.select([sys.stdin, s], [], [])
        
        #For everything in the readable array see if its the socket or the stdin
        for read in readable:
            if read == s:
                #if its the socket, receive and print the data
                data = read.recv(1024)
                if isVerbose:
                    print('Got message: Server: ' + data.decode(), file=sys.stdout)
                if not data:
                    break
                else:
                    print(data.decode(), file=sys.stdout)
            else:
                #If its stdin, read the line and send it to the server with the name attached
                enteredMsg = sys.stdin.readline()
                if isVerbose:
                    print('Sending message: Server: ' + enteredMsg, file=sys.stdout)
                enteredMsg = nameToPrint + enteredMsg
                s.sendall(enteredMsg.strip().encode())
                
    except Exception as e:
        #If it encounters an exception print it out if verbose and exit
        if not isVerbose:
            print('Encountered an error and will now exit the program.\nTurn on verbose (-v) to see more.', file=sys.stdout)
        else:
            print('Encountered error: ' + e + '.  This error will now terminate the program.', file=sys.stdout)
        break