import sys, getopt
import random
import math
from output import printInitialization, displayStates
from forwardBackward import Forward, Backward, sumAlpha, sumBeta
from state import state
from softCount import createSoftCountTables, displaySoftCountTable, displayInitialCountTable
from updateTables import updateTransitionProbabilites, updateEmissionProbabilites, updatePiProbabilities
from sort import logRatio, displayLogRatio


def main(argv):

	#HMM main function Organization
	#1 - terminal setup
	#2 - HMM Initialization
	#3 - iterative learning
	#4 - final output

	#IMPORTANT NOTE: THE ANSWER TO QUESTION 4
	#ON THE HW HANDOUT (DETERMINE STARTING VALUES OF A)
	#IS IN README.TXT. 

	#######################################
	#1 - terminal setup
	global verboseFlag
	verboseFlag = False
	optFlagsArr = [False]
	fileName = ''
	numIterations = 50

	try:
 		opts, args = getopt.getopt(argv,"f:vi:")
	except getopt.GetoptError:
 		print("anagrams.py [-f filename]")
 		sys.exit(2)
	for opt, arg in opts:
		if opt == '-f':
			optFlagsArr[0] = True
			fileName = arg
		if opt == '-v':
			verboseFlag = True
		if opt == '-i':
			try: 
				numIterations = int(arg)
			except ValueError:
				print('-i must take int as argument')
				sys.exit(2)
	if False in optFlagsArr:
		print('HMM.py [-f filename]')
		sys.exit(2)
	
	###############################################
	#2 - HMM Initialization
	numStates = 2
	#get the alphabet and the word list from the corpus
	alphabet , wordList = readFileAndCreateAlphabet(fileName)
	transitions = createUniformDistribution(numStates)
	state0 = state(createRandomDistribution(alphabet), transitions, 0)
	state1 = state(createRandomDistribution(alphabet), transitions, 1)
	states = [state0, state1]
	Pi = createRandomDistribution([0,1])
	
	if verboseFlag:
		printInitialization(state0,state1,Pi)

	#######################################
	#3 - iterative learning
	iterCounter = 0
	totalPlog = 0.0
	oldTotalPlog = 0.0
	#this variable checks how many times 
	#the totalPlog and oldPlog have been
	#super close
	sameCounter = 0.0
	for i in range(numIterations):
		#expectation
		softCountTable , initialCountTable = createSoftCountTables(states, Pi, alphabet, wordList)
		if verboseFlag:
			displayInitialCountTable(initialCountTable)
			displaySoftCountTable(softCountTable)
		#maximization
		states = updateTransitionProbabilites(softCountTable, alphabet, states)
		states = updateEmissionProbabilites(softCountTable, alphabet, states)
		Pi = updatePiProbabilities(initialCountTable, alphabet, states, len(wordList))
		
		#calculate total plog for stop condition
		totalPlog = 0.0
		for word in wordList:
			Alpha = Forward(states, Pi, word, verboseFlag)
			sumA = sumAlpha(Alpha, states)
			if (sumA == 0):
				continue
			totalPlog += plog(sumA)
		#stop condition
		if((oldTotalPlog - totalPlog < 0.00001) and iterCounter > 10):
			#has it total plog not decreased twice?
			if (sameCounter > 1):
				print("\r", end = "")
				printString = "--- Iteration %d / %d ---                " % (iterCounter, numIterations)
				print(printString)
				print("LOCAL OPTIMUM DETECTED")
				break
			sameCounter += 1

		oldTotalPlog = totalPlog

		iterCounter+=1
		#this is all just fancy printing to show the
		#user the progress of program. I just did this because I've wanted
		#to learn how to do this for a while. 
		print("\r", end = "")
		printString = "--- Iteration %d / %d ---                " % (iterCounter, numIterations)
		print(printString, end = "")
	#print("\r", end = "%s" % (" " * len(printString)*2))
	############################################

	#4 - final output

	printInitialization(states[0],states[1],Pi, False)
	sortedLogRation = logRatio(state0,state1)
	displayLogRatio(sortedLogRation)


def plog(prob):
	plog = -1 * math.log(float(prob),float(2))
	return(plog)

#FILE IO CODE BASED ON EXAMPLE: https://stackabuse.com/read-a-file-line-by-line-in-python/
def readFileAndCreateAlphabet(filepath):
	alphabet = []
	wordList = []
	with open(filepath) as fp:  
		line = fp.readline()
		while line:
			line = line.strip().lower()
			line = line + "#"
			charList = list(line)
			if line not in wordList:
				wordList.append(line)
			for char in line:
				if char not in alphabet:
					alphabet.append(char)
			line = fp.readline()
	return (alphabet,wordList)

#CREATE A LIST THAT EVENLY DIVIDES PROB SPACE
def createUniformDistribution(n):
	probability = (1.0/float(n))
	uniformDict = dict()
	for x in range(n):
		uniformDict[x] = probability
	return uniformDict

#CREATE A LIST THAT RANDOMLY DIVIDES PROB SPACE
def createRandomDistribution(alphabet):
	alphabetLength = len(alphabet)
	randomList = []
	distributionDict = dict()
	total = 0
	for x in range(alphabetLength):
		randNum = random.randint(0,10000)
		total += randNum
		randomList.append(randNum)
	for i in range(alphabetLength):
		distributionDict[alphabet[i]] = (float(randomList[i])/float(total))
	return distributionDict


if __name__ == '__main__':
    main(sys.argv[1:])
