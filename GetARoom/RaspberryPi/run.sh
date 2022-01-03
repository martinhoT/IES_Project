#!/usr/bin/env bash

# The script's arguments are all passed to the sender program

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

log_file="./Data/Output/logs.txt"

# If the log_file path doesn't exist, create it
if [ ! -e "./Data/Output/logs.txt" ]
then
    if [ ! -d "./Data/Output" ]
    then
        if [ ! -d "./Data" ]
        then
            mkdir ./Data
        fi
        mkdir ./Data/Output
    fi
    touch ./Data/Output/logs.txt
fi

# Clear the file's contents, or else the sensor will send all the content inside at once
> ${log_file}

# Data Generator
python3 ./Data_Generator/PSMG.py >> ${log_file} &
pid=($!)

# Sender
./sender/sender "$@" --file "$log_file"
