#include <mosquittopp.h>
#include <iostream>
#include <csignal>
#include <algorithm>
#include <iterator>

const char* client_name = "fetcher";

const char* broker_host = "localhost";
// It's usually 1883
const int broker_port = 1883;
// The number of seconds after which the broker should send a PING message to the client if no other messages have been exchanged in that time
const int connection_keepalive = 60;

const char* topic = "mosquitto/test";
/* Quality of Service
0: send once (no ACK)
1: send at least once (with ACK)
2: send once (4 step handshake)
*/
const int qos = 1;

std::string msg = "No messages... :(";

void msg_received(struct mosquitto* mosq, void* obj, const struct mosquitto_message* message) {
    msg.copy((char*) message->payload, message->payloadlen);
}

// This is the broker's mosquitto instance that fetches the data sent by the sensor mosquittoes
int main() {
    struct mosquitto* client = mosquitto_new(client_name, true, nullptr);
    mosquitto_message_callback_set(client, msg_received);

    const int status = mosquitto_connect(client, broker_host, broker_port, connection_keepalive);
    if (status == MOSQ_ERR_SUCCESS) {
        int mid;

        const char* msg = "Bom dia!";
        mosquitto_publish(client, &mid, topic, 9, msg, qos, true);

        mosquitto_subscribe(client, &mid, topic, qos);

        std::cout << msg << std::endl;

        mosquitto_disconnect(client);
    }
    else if (status == MOSQ_ERR_INVAL) {
        std::cout << "ERROR: Invalid connection parameters!" << std::endl;
    }
    else {
        std::cout << "ERROR: A system call returned an error:" << mosquitto_strerror(status) << std::endl;
    }

    mosquitto_destroy(client);

    return 0;
}