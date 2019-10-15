from state import state
from forwardBackward import Forward, Backward, sumAlpha, sumBeta

def createSoftCountTables(states, Pi, alphabet, wordList):
	softCountTable = dict()
	initialCountTable = dict()
	for word in wordList:
		t = 1

		for letter in word:
			#create initial count table
			#fixed so that it updates with start of all word
			#(misunderstand this thing in last assignment and it updated
			#incorrectly)
			if (t == 1):
				row = createSoftCountRow(states, Pi, t, letter, word)
				if (initialCountTable.get(letter) == None):
					initialCountTable[letter] = row
				else:
					for key, value in initialCountTable.get(letter).items():
					    initialCountTable[letter][key] = initialCountTable.get(letter).get(key) + row.get(key)	

			#create soft count table
			else:  
				row = createSoftCountRow(states, Pi, t, letter, word)
				if (softCountTable.get(letter) == None):
					softCountTable[letter] = row
				else:
					for key, value in softCountTable.get(letter).items():
					    softCountTable[letter][key] = softCountTable.get(letter).get(key) + row.get(key)
			t+=1

	#making sure all letters have entries
	#this functionality fixes some problems in updateTables.py
	#where you need to iterate over the alphabet 
	noneEntry = {(0,0) : 0.0, (0,1) : 0.0, (1,0) : 0.0, (1,1) : 0.0}
	for letter in alphabet: 
		if (softCountTable.get(letter) == None):
			softCountTable[letter] = noneEntry
		if (initialCountTable.get(letter) == None):
			initialCountTable[letter] = noneEntry

	return (softCountTable, initialCountTable)

def createSoftCountRow(states, Pi, t, letter, word):
	Alpha = Forward(states,Pi, word)
	Beta = Backward(states, word)
	p_O = sumAlpha(Alpha, states)
	row = dict()
	for i in range(len(states)):
		for j in range(len(states)):
			alpha_i = Alpha[(i,t)]
			a_ij = states[i].transition.get(j)
			b_io = states[i].emission.get(letter)
			beta_j = Beta[(j,t)]
			rowEntry = 0.0
			#this is a problem that came up when I started iterating over corpus
			#and sometimes there was a zero probability of certain
			#letters appearing in certain places. Big 1 is 'q'
			try:
				rowEntry = (alpha_i * a_ij * b_io * beta_j)/p_O
			except ZeroDivisionError:
				rowEntry = 0.0
			row[(i,j)] = rowEntry
	counter = 0.0
	for key,value in row.items():
		counter += row.get(key)
	#if the row does not sum to zero or 1, 
	#then something went wrong, raise error
	if (counter != 0.0):	
		if (abs(counter - 1) > .00001):
			print ("Counter: %f" % (counter) )
			for key,value in row.items():
				print(key)
				print("value: %f" % (value))
			raise ValueError('ROW ENTRY DOES NOT SUM TO 1')
	return row 

def displayInitialCountTable(initialCountTable):
	print("\nINITIAL COUNT TABLE (SO FAR)")
	total = 0
	for key_0,value_0 in initialCountTable.items():
		sum_val = 0.0
		for key_1,value_1 in value_0.items():
			sum_val += value_1
			print(key_0, end = ' ')
			print(key_1, end = ' val: ')
			print(value_1, end = ' ')
			print("")
		print("sum of %s : %f \n" % (key_0, sum_val))
		total += sum_val
	print("total sum: %d" % (total))

def displaySoftCountTable(softCountTable):
	print("\nSOFT COUNT TABLE (SO FAR)")
	for key_0,value_0 in softCountTable.items():
		sum_val = 0.0
		for key_1,value_1 in value_0.items():
			sum_val += value_1
			print(key_0, end = ' ')
			print(key_1, end = ' val: ')
			print(value_1, end = ' ')
			print("")
		print("sum of %s : %f \n" % (key_0, sum_val))
