# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:

The example implementation anchored the position on the top left empty corner. My program used the first tile
of the hexagon for its position. The example also used a position class to handle x and y coordinates.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer:

Hexagons are rooms and tessellating hexagons is analogous to placing rooms in project 3. 

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:

I would start with creating methods for placing a single room and any helper methods needed in order to do so.
Next, I would work on placing multiple rooms making sure to not overlap any or attempt to place them out of bounds.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer:

Hallways have a width of 1 or 2. They are similar in that they have random positions and have random lengths.
They both must have walls and floors that are visually distinct.
