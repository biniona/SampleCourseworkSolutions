from state import state


#BASED OFF OF EXAMPLE CODE IN HANDOUT, BUILT OUT SO 
#THAT IT WORKS WITH THE WAY I DEFINED STATES 
def Forward(States,Pi,thisword, verboseFlag = False):
	Alpha= dict()
	for s in range(len(States)):
		Alpha[(s,1)] = Pi[s]
	for t in range(1,len(thisword)+1):
		for to_state_ind in range(len(States)):
			Alpha[(to_state_ind,t+1)] = 0
			for from_state_ind in range(len(States)):
				try:
					Alpha[(to_state_ind,t+1)] += Alpha[(from_state_ind,t)] * \
					    States[from_state_ind].emission.get(thisword[t-1]) * \
					    States[from_state_ind].transition.get(States[to_state_ind].number)
				except:
					print("BAD LETTER: %s" % (thisword[t-1]))
					raise NameError('NONE ERROR')
	return Alpha

#BASED OFF OF EXAMPLE CODE IN HANDOUT, BUILT OUT SO 
#THAT IT WORKS WITH THE WAY I DEFINED STATES 
def Backward(States, thisword, verboseFlag = False):
	Beta = dict()
	last = len(thisword)
	for s in range(len(States)):
		Beta[(s, last)] = 1
	for t in range(len(thisword),0,-1):
		for from_state_ind in range(len(States)):
			Beta[(from_state_ind,t-1)] = 0
			for to_state_ind in range(len(States)):
				Beta[(from_state_ind,t-1)] += Beta[(to_state_ind,t)] * \
				         States[from_state_ind].emission.get(thisword[t-1]) * \
				         States[from_state_ind].transition.get(States[to_state_ind].number)
	return Beta

#function to sum the output of forward 
#function to get string probability
def sumAlpha(Alpha, States):
	wordLen = (len(Alpha)/len(States))
	sumAlpha = 0
	for i in range(len(States)):
		sumAlpha += Alpha[(i, wordLen)]
	return sumAlpha

#function to sum the output of backward function
#to get string probability 
def sumBeta(Beta, Pi, States):
	sumBeta = 0
	for i in range(len(States)):
		sumBeta += (Beta[(i, 0)] * Pi.get(i))
	return sumBeta