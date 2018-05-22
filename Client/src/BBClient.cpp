#include <stdlib.h>
#include "../include/connectionHandler.h"
#include <boost/thread.hpp>


static bool flag = true;

class taskWriteToServer {
private:
    ConnectionHandler *_connectionHandler;
public:
    taskWriteToServer(ConnectionHandler *connectionHandler) : _connectionHandler(connectionHandler) {}

    void operator()() {
        while (flag&&!std::cin.eof()) {
           try {
                const short bufsize = 1024;
                char buf[bufsize];
                std::cin.getline(buf, bufsize);
                std::string line(buf);
              
                if (!_connectionHandler->sendLine(line)) {
                    //std::cout <<"Disconnected. Exiting...\n" << std::endl;
                    break;
                }
            }catch (boost::thread_interrupted const &){
                break;
           }
        }
    }
};


class taskReadFromServer {
private:
    ConnectionHandler *_connectionHandler;
public:
    taskReadFromServer(ConnectionHandler *connectionHandler) : _connectionHandler(connectionHandler) {}

    void operator()() {
        while (flag) {
            std::string answer;
            if (!_connectionHandler->getLine(answer)) {
                //std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            int len = answer.length();
            answer.resize(len - 1);
            std::cout << answer << std::endl;
            if (answer == "ACK signout succeeded") {
                //std::cout << "Exiting...\n" << std::endl;
                flag = false;
                break;
            }
        }
        
    }
};


int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    taskWriteToServer writer(&connectionHandler);
    taskReadFromServer reader(&connectionHandler);
    boost::thread threadWrite(writer);
    boost::thread threadRead(reader);
    threadRead.join();


    return 0;
}

