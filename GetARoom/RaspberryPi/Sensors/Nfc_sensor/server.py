#!/usr/bin/env python
# encoding: utf-8
import json
import datetime
from flask import Flask, request
from os import environ

app = Flask(__name__)

# Room where sensor is applied
room = environ.get("ROOM_ID", "4.2.08")

# set of people inside
usersInside = []

@app.route('/', methods=['POST'])
def update_record():
    try:
        name = request.values.get('name')
        email = request.values.get('email')

        # See if user is already inside or not
        if email in usersInside:
            usersInside.remove(email)
            entered = "false"
        else:
            usersInside.append(email)
            entered = "true"
        
        # Create event body
        event = "0{\"room\":" + f"\"{room}\",\"user\": \"{name}\",\"email\": \"{email}\",\"entered\": {entered},\"time\": \"{datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\"" + "}"
        
        # Write logs
        f = open("Data/Output/logs.txt", "a")
        f.write(event + "\n")
        f.close()
        
        return '200'

    except:
        return '500'

app.run(host='0.0.0.0')