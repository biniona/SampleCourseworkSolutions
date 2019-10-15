from state import state

#contains: updateTransitionProbabilites, updateEmissionProbabilites, updatePiProbabilities

#TODO: ROBUST TESTING (IT WORKS, BUT NOT VERY ROBUST)

def updateTransitionProbabilites(softCountTable, alphabet, states):
	numStates = range(len(states))
	for state_0 in numStates:
		transitionDict = dict()
		for state_1 in numStates:
			numerator = 0.0
			denominator = 0.0
			for letter in alphabet:
				numerator += softCountTable.get(letter).get((state_0, state_1))
				for state_i in numStates:
					denominator += softCountTable.get(letter).get((state_0, state_i))
			transitionDict[state_1] = numerator/denominator
		states[state_0].setTransition(transitionDict)
	return states

def updateEmissionProbabilites(softCountTable, alphabet, states):
	numStates = range(len(states))
	for state_0 in numStates:
		emissionDict = dict()
		for letter in alphabet:
			numerator = 0.0 
			denominator = 0.0
			for state_i_1 in numStates:
				numerator += softCountTable.get(letter).get((state_0, state_i_1))
				for letter_i in alphabet:
					denominator += softCountTable.get(letter_i).get((state_0, state_i_1))
			emissionDict[letter] = numerator/denominator
		states[state_0].setEmission(emissionDict)
	return states

def updatePiProbabilities(initialCountTable, alphabet, states, corpusLength):
	numStates = range(len(states))
	piDict = dict()
	for state_0 in numStates:
		numerator = 0.0
		for state_i in numStates:
			for letter_i in alphabet:
				numerator += initialCountTable.get(letter_i).get((state_0, state_i))
		piDict[state_0] = numerator * (1/float(corpusLength))
	return piDict

