from state import state

#this file just contains output helper functions

Initialization = '''\n-----------------------
-   Initialization    -
-----------------------'''

Output = '''\n-----------------------
-   Output    -
-----------------------'''

creatingState = '''Creating State %d
Transitions
    To State 0 %f
    To State 1 %f '''
piString = '''Pi:
State    0    %f
State    1    %f'''


#this is the function that does the print initialization output if
#the verbose flag is specified
def printInitialization(state0, state1, Pi, initialization = True):
	if (initialization == True):
		print(Initialization)
	else : 
		print(Output)
	print(creatingState % (0, state0.transition.get(0),state0.transition.get(1)))
	print("Emission Probabilities")
	for key, value in state0.emission.items():
		print("    Letter   %s %f" % (key, value))
	print(creatingState % (1, state1.transition.get(0),state1.transition.get(1)))
	print("Emission Probabilities")
	for key, value in state1.emission.items():
		print("    Letter   %s %f" % (key, value))
	print("\n-----------------------")
	print(piString % (Pi.get(0),Pi.get(1)))


def displayStates(states):
	count = 0
	print("\n\n##################################")
	for state in states:
		print("\n\n-----------------------")
		print("DISPLAYING STATE %d" % (count))
		print("TRANSITION: ", end ="")
		print(state.transition)
		print("EMISSION: ", end ="")
		print(state.emission)
		print("NUMBER: %d " % (state.number))
		print("-----------------------\n\n")
		count+=1
	print("##################################\n\n")
