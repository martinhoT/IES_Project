#include <mosquittopp.h>
#include <iostream>
#include <csignal>
#include <algorithm>
#include <iterator>
#include <cstring>
#include <fstream>
#include <sys/stat.h>
#include <unistd.h>

const char* client_name = "melga";

const char* broker_host = "localhost";
// It's usually 1883
const int broker_port = 1883;
// The number of seconds after which the broker should send a PING message to the client if no other messages have been exchanged in that time
const int connection_keepalive = 30;

const char* topic = "mosquitto/test";
/* Quality of Service
0: send once (no ACK)
1: send at least once (with ACK)
2: send once (4 step handshake)
*/
const int qos = 1;

// The mosquitto client. Should be global so that the signal handler can properly terminate it
struct mosquitto* client;

void signal_handler(int signum) {
    std::cout << "Closing mosquitto..." << std::endl;
    mosquitto_disconnect(client);
    mosquitto_destroy(client);

    exit(signum);
}

// TODO: not secure, maybe use TLS later?
// This is a mosquitto instance that obtains data and sends it to the broker
int main(int argc, char** argv) {
    std::fstream data;
    struct stat data_info;
    data.open("../Data/Output/logs.txt", std::ios::in);
    if (data.is_open()) {

        client = mosquitto_new(client_name, true, nullptr);
        const int status = mosquitto_connect(client, broker_host, broker_port, connection_keepalive);
        signal(SIGINT, signal_handler);

        if (status == MOSQ_ERR_SUCCESS) {
            int mid;

            std::streampos pos = 0;
            while (true) {
                // Check file timestamp
                if (1) {
                    data.seekg(pos);
                    
                    char* msg;
                    int chars_read, read_size=10;
                    data.getline(msg, -1);                

                    mosquitto_publish(client, &mid, topic, strlen(msg), msg, qos, false);
                    pos = data.tellg();
                }
                else {
                    sleep(5);
                }
            }

        }
        else if (status == MOSQ_ERR_INVAL) {
            std::cout << "ERROR: Invalid connection parameters!" << std::endl;
            raise(SIGINT);
        }
        else {
            std::cout << "ERROR: A system call returned an error:" << mosquitto_strerror(status) << std::endl;
            raise(SIGINT);
        }

    }

    
    return 0;
}
