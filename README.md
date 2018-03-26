# Heartbeat-Algorithm
Assume that a process can communicate only with its neighbours, and that initially each process knows only its own links (i.e., which nodes are its neighbours) and the
total number of nodes in the network. Design a heartbeat algorithm that has each process repeatedly exchange information with its neighbours. When the
program terminates, every process should know the topology of the entire network. You will need to figure out what the processes should exchange and
how they can tell when to terminate (Based on Andrews, Exercise 9.12 on p.481).

In the distributed solution, all nodes must know about the state of all other nodes. In this method, a node will send its information to all its neighbors. The neighbors will receive the information
and sent all received information as well as their own information to their neighbors. Once all nodes know about the state of all other nodes the algorithm will be terminated.
I have implemented heartbeat algorithm with MPJ. Using MPI (message passing interface) in java programming language. Some requirement is needed for running MPI on each machine.
Below are some examples of running my codes. The number of nodes in my example is four.
