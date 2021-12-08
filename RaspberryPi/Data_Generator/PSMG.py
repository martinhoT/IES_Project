import json
import time
import datetime
import random
import numpy as np
from MarkovChain import MarkovChain

class Person():
    def __init__(self, name, email):
        self.name = name
        self.email = email
        self.location = None
        self.tMatrix = None
        self.action = None

    def generateTMatrix(self, k):
        '''Generate transition matrix for given k states (places)'''

        # Add a random drift term.  We can guarantee that the diagonal terms will be larger by specifying a `high` parameter that is < 1.
        # How much larger depends on that term.  Here, it is 0.25.
        result = np.identity(k) + np.random.uniform(low=0., high=.01, size=(k, k))

        # Lastly, divide by row-wise sum to normalize to 1.
        self.tMatrix = result / result.sum(axis=1, keepdims=1)

    def move(self, places):
        # Person isnt in a room
        if self.location == None:
            self.location = random.choice(places)
            self.action = "enter"
            event = "{" + f"\"user\": {self.name},\"email\": {self.email},\"room\": {self.location},\"entered\": true,\"time\": {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}" + "}"
            return event

        if self.action == "enter":
            mChain = MarkovChain(self.tMatrix , places)
            place = mChain.next_state(self.location)
            if place != self.location:
                self.action = "exit"
                event = "{" + f"\"user\": {self.name},\"email\": {self.email},\"room\": {self.location},\"entered\": false,\"time\": {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}" + "}"
                self.location = place
                return event
            else:
                return
        
        if self.action == "exit":
            self.action = "enter"
            event = "{" + f"\"user\": {self.name},\"email\": {self.email},\"room\": {self.location},\"entered\": true,\"time\": {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}" + "}"
            return event
        
    def __str__(self):
        return f"Name: {self.name}, email: {self.email}"

def main():
    peoplePath = "Data/Input/people.json"
    depsPath = "Data/Input/deps"
    peopleLimit = None


    # Get people data
    with open(peoplePath,"r") as jsonF:
        people = json.load(jsonF)
        if peopleLimit:
            people = people[:peopleLimit]
    
    # Get Department rooms
    with open(depsPath) as file:
        lines = file.readlines()
        depRooms = [line.rstrip() for line in lines]

    # Create person objects
    people = [Person(p["name"], p["mail"]) for p in people]

    # Create tmatrixs
    for p in people:
        p.generateTMatrix(len(depRooms))

    # Start generator
    try:
        while True:
            event = random.choice(people).move(depRooms)
            if event:
                print(event)
            time.sleep(random.randrange(3, 8))
    except KeyboardInterrupt:
        pass

    
if __name__=="__main__":
    main()