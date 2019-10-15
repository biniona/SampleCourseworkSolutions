from state import state
import math

def logRatio(state0, state1):
	logRatio = []
	for key, value in state0.emission.items():
		if (state1.emission.get(key) == 0):
			val = 0
		else: 
			val = math.log(state0.emission.get(key)/state1.emission.get(key), 2)
		logRatio.append([key,val])
	logRatio.sort(key = lambda ind: ind[1])
	return logRatio

def displayLogRatio(logRatio):
	endString = ""
	print("")
	for pair in logRatio:
		if (pair[1] == 0):
			endString = " ---- ZERO ----"
		elif (pair[1] > 0):
			endString = " - state0"
		else: 
			endString = " - state1"
		print("Letter: %s Log Ratio : %f%s" % (pair[0], pair[1], endString))
		zeroString = ""