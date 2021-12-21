#!/usr/bin/env bash

# Cleanup when the script exits (normally through SIGINT / CTRL+C)
graceful_shutdown() {
    echo "Graceful shutdown (SIGINT)"
    cleanup
}
cleanup() {
    echo "Cleaning up!"
    if [ -n "$pid" ]
    then
        echo "Killing process with PID $pid"
        kill $pid
        unset pid
    fi
    exit
}
trap graceful_shutdown SIGINT
trap cleanup EXIT

broker=localhost
if [ -n "$1" ]
then
    broker=$1
fi

log_file="./Data/Output/logs.txt"

# Clear the file's contents, or else the sensor will send all the content inside at once
> ${log_file}

# Data Generator
python3 ./Data_Generator/PSMG.py >> ${log_file} &
pid=($!)

# Sensor
./sensor/sensor --broker "${broker}" --name melga --file ${log_file}
