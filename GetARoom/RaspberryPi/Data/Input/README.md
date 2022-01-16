# `rooms.json`

This file contains room capacity values with "reasonable" proportions between each other (eyeballed the area of the rooms on the floor plans), although the absolute value may need adjustments.

At the moment of writing this, all the room capacity numbers are odd on purpose. This is to make bulk editing of the average capacity easier, by allowing editing of odd numbers into even numbers and vice-versa. It's only to prevent mistakes, by making sure that updated numbers aren't changed unintentionally.

For example, if the effect on occupancy should be greater for demonstration, then we can change all capacity values into half of the previous value. For instance, for the number `15` the result is `8`. Therefore, it's impossible to change this assigned `8` because all the other values that need to be changed are odd.

# `people.json`

(Nothing to note yet)