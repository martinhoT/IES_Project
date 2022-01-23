#!/bin/bash

# Cleanup when the script exits (normally through SIGINT / CTRL+C)
graceful_shutdown() {
    echo "Graceful shutdown (SIGINT)"
    cleanup
}
cleanup() {
    echo "Cleaning up!"
    if [ -n "$pids" ]
    then
        echo "Killing processes with PIDs ${pids[@]}"
        for pid in "${pids[@]}"
        do
            kill $pid
        done
        unset pids
    fi
    exit
}
trap graceful_shutdown SIGINT
trap cleanup EXIT

# If the log_file path doesn't exist, create it
if [ ! -e "./Data/Output/logs.txt" ]
then
    if [ ! -d "./Data/Output" ]
    then
        mkdir -p ./Data/Output
    fi
    touch ./Data/Output/logs.txt
fi

> "./Data/Output/logs.txt"

# Camera sensor
cd Camera_sensor
python3 -u run.py --prototxt mobilenet_ssd/MobileNetSSD_deploy.prototxt --model mobilenet_ssd/MobileNetSSD_deploy.caffemodel >> "../Data/Output/logs.txt" &
pids+=($!)

# NFC sensor
cd ..
python3 Nfc_sensor/server.py &
pids+=($!)

# Sender
./sender/sender "$@" --file "Data/Output/logs.txt" --broker "34.140.235.127"
