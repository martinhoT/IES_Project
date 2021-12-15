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

const char* fname = "../Data/Output/logs.txt";

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
    data.open(fname, std::ios::in);
    // I don't think I need to say this, but don't clear the contents of or remove the file
    // that is being read, the program doesn't crash apparently but it won't properly read
    // it because the cursor position will not go back
    if (data.is_open()) {

        client = mosquitto_new(client_name, true, nullptr);
        const int status = mosquitto_connect(client, broker_host, broker_port, connection_keepalive);
        signal(SIGINT, signal_handler);

        if (status == MOSQ_ERR_SUCCESS) {
            int mid;

            std::streampos pos = 0;
            stat(fname, &data_info);
            long prev_time = 0;
            while (true) {
                // Check file timestamp
                std::cout << "Checking old timestamp " << prev_time << " with new timestamp " << data_info.st_mtim.tv_sec << std::endl;
                if (prev_time < data_info.st_mtim.tv_sec) {
                    // Clear the error state (such as the EOF bit), so that the file may be read again
                    data.clear();
                    while (data.good()) {
                        // It's apparently easier to get a line from the file to a string rather than a char*
                        std::string msg_str;
                        getline(data, msg_str);
                        int ln = msg_str.length();
                        if (!ln) continue;
                        char* msg = new char[ln+1];
                        memcpy(msg, msg_str.c_str(), ln+1);

                        mosquitto_publish(client, &mid, topic, ln, msg, qos, true);
                        std::cout << "Sent message: " << msg << std::endl;
                    }
                    // Make the current timestamp outdated, and wait for the next one
                    prev_time = data_info.st_mtim.tv_sec;
                    std::cout << "Old timestamp is now " << prev_time << std::endl;
                }
                else {
                    std::cout << "I'll sleep...";
                    fflush(stdout);
                    sleep(5);
                    std::cout << " rise and shine!" << std::endl;
                }
                // Keep updating the current timestamp
                stat(fname, &data_info);
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
    else {
        std::cout << "Oops, the file could not be opened??" << std::endl;
    }

    
    return 0;
}
