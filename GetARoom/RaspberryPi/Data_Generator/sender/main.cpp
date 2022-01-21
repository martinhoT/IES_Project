#include <mosquittopp.h>
#include <iostream>
#include <csignal>
#include <algorithm>
#include <iterator>
#include <cstring>
#include <fstream>
#include <sstream>
#include <sys/stat.h>
#include <unistd.h>

/* Message type IDs
0: event
1: status
*/

std::string client_name = "melga";

std::string broker_host = "localhost";
// It's usually 1883
const int broker_port = 1883;
// The number of seconds after which the broker should send a PING message to the client if no other messages have been exchanged in that time
const int connection_keepalive = 30;
// The number of seconds that the sensor will wait between the log file checks
unsigned int sleep_time = 5;

std::fstream data;

/* Quality of Service
0: send once (no ACK)
1: send at least once (with ACK)
2: send once (4 step handshake)
*/
const int qos = 1;

// The mosquitto client. Should be global so that the signal handler can properly terminate it
struct mosquitto* client;

std::string fname = "../Data/Output/logs.txt";

void signal_handler(int signum) {
    data.close();
    std::cout << "Closing mosquitto..." << std::endl;
    mosquitto_disconnect(client);
    mosquitto_destroy(client);

    exit(signum);
}

// TODO: not secure, maybe use TLS later?
// This is a mosquitto instance that obtains data and sends it to the broker
int main(int argc, char** argv) {
    // Argument parsing
    for (int i = 1; i < argc; i+=2) {
        if (!strcmp(argv[i], "-h") || !strcmp(argv[i], "--help")) {
            printf("\
Mosquitto client that sends data from an append-only file.\n\
\n\
Usage: ./mosquitto [options]\n\
Options:\n\
    - -h / --help       Show this help\n\
    - -b / --broker     Specify the address of the MQTT broker\n\
    - -n / --name       Specify the name of this client\n\
    - -f / --file       Specify the location of the log file\n\
    - -s / --sleep      Specify the amount of seconds that the sensor will sleep between the log file checks\n\
");
            return 0;
        }
        else if (!strcmp(argv[i], "-b") || !strcmp(argv[i], "--broker")) {
            if (i+1 == argc) {
                std::cerr << "Option '-b'/'--broker' doesn't have a value!" << std::endl;
                return 1;
            }
            broker_host = argv[i+1];
        }
        else if (!strcmp(argv[i], "-n") || !strcmp(argv[i], "--name")) {
            if (i+1 == argc) {
                std::cerr << "Option '-n'/'--name' doesn't have a value!" << std::endl;
                return 1;
            }
            client_name = argv[i+1];
        }
        else if (!strcmp(argv[i], "-f") || !strcmp(argv[i], "--file")) {
            if (i+1 == argc) {
                std::cerr << "Option '-f'/'--file' doesn't have a value!" << std::endl;
                return 1;
            }
            fname = argv[i+1];
        }
        else if (!strcmp(argv[i], "-s") || !strcmp(argv[i], "--sleep")) {
            if (i+1 == argc) {
                std::cerr << "Option '-s'/'--sleep' doesn't have a value!" << std::endl;
                return 1;
            }
            // Accept only the first digit
            sleep_time = strtol(argv[i+1], &argv[i+1]+1, 10);
        }
        else {
            std::cerr << "Options are in an incorrect format! Unknown option '" << argv[i] << "'" << std::endl;
            return 1;
        }
    }
    
    struct stat data_info;
    data.open(fname, std::ios::in);
    // I don't think I need to say this, but don't clear the contents of or remove the file
    // that is being read, the program doesn't crash apparently but it won't properly read
    // it because the cursor position will not go back
    if (data.is_open()) {

        client = mosquitto_new(client_name.c_str(), true, nullptr);
        const int status = mosquitto_connect(client, broker_host.c_str(), broker_port, connection_keepalive);
        signal(SIGINT, signal_handler);

        if (status == MOSQ_ERR_SUCCESS) {
            int mid;

            std::streampos pos = 0;
            stat(fname.c_str(), &data_info);
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

                        // Split the message so that its room can be obtained (to be sent to the respective topic)
                        strtok(msg, "\"");
                        // Obtain the room number (includes department, floor, room)
                        int idx = 0;
                        for (int i = 0; i < 2; i++) strtok(nullptr, "\"");
                        char* room_all = strtok(nullptr, "\"");

                        char* dep = strtok(room_all, ".");
                        char* floor = strtok(nullptr, ".");
                        char* room = strtok(nullptr, ".");

                        std::stringstream topic_stream;
                        // Character index in the message where the content excluding the type definition starts
                        // (since the type doesn't have to be sent, the topic already indicates the type explicitly)
                        char type = *msg;
                        switch (type)
                        {
                            case '0':
                                topic_stream << "event/" << dep << "/" << floor << "/" << room;
                                break;
                            case '1':
                                topic_stream << "status/" << dep << "/" << floor << "/" << room;
                                break;

                            default:
                                std::cerr << "Unknown message type '" << type << "' from data stream" << std::endl;
                                continue;
                        }

                        delete[] msg;

                        mosquitto_publish(client, &mid, topic_stream.str().c_str(), ln-1, msg_str.c_str()+1, qos, true);
                        std::cout << "Sent message: " << msg_str.c_str()+1 << " (type " << type << ", topic '" << topic_stream.str() << "')" << std::endl;
                    }
                    // Make the current timestamp outdated, and wait for the next one
                    prev_time = data_info.st_mtim.tv_sec;
                    std::cout << "Old timestamp is now " << prev_time << std::endl;
                }
                else {
                    std::cout << "I'll sleep...";
                    fflush(stdout);
                    sleep(sleep_time);
                    std::cout << " rise and shine!" << std::endl;
                }
                // Keep updating the current timestamp
                stat(fname.c_str(), &data_info);

                mosquitto_loop(client, -1, 1);
            }

        }
        else if (status == MOSQ_ERR_INVAL) {
            std::cout << "ERROR: Invalid connection parameters!" << std::endl;
            raise(SIGINT);
        }
        else {
            std::cout << "ERROR: A system call returned an error: " << mosquitto_strerror(status) << std::endl;
            raise(SIGINT);
        }

    }
    else {
        std::cout << "Oops, the file could not be opened!" << std::endl;
    }
    
    return 0;
}
