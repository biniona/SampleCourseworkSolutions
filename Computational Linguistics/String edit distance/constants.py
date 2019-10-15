import string

alphabet = list(string.ascii_lowercase)
vowels = ['a','e','i','o','u']
consonants = (list(set(alphabet) - set(vowels))) 

#######################
#FUCTION MODELS THIS BEHAVIOR
#identity 0
#vowel-to-vowel 0.5
#consonant-to-consonant 0.6
#consonant-to-vowel 3
#insertion 2
#deletion 2
######################

def costFunction(char1, char2):
	if (char1 == char2):
		return 0.0
	elif (char1 in vowels and char2 in vowels):
		#print("c1 : %s c2: %s" % (char1, char2))
		return 0.5
	elif (char1 in consonants and char2 in consonants):
		#print("c1 : %s c2: %s" % (char1, char2))
		return 0.6
	elif (char1 == '#' or char2 == '#'):
		print("c1 : %s c2: %s" % (char1, char2))
		return 2.0
	else:
		return 3.0
	

def sumList(l, e):
	s = 0
	for val in l[:e]:
		s += val
	return s

def printTable(table, path, s1, s2):
	maxVals = [0 for i in range(len(s1))]
	spacer = "    "
	for (i,j) in path:
		if i == None:
			continue
		maxVals[i] = max(maxVals[i], len(str(int(table[i][j][0])))+2)
	header = spacer + " "
	counter = 0
	for letter in s1:
		header += letter + (maxVals[counter] * " ")
		counter+=1
	print(header)
	oldJ = -1 
	line = ""
	whiteSpace = spacer
	for (i,j) in path:
		if (oldJ != j):
			if (j != 0):
				print(line)
				whiteSpace = (len(line) - 1) * " " 
			line = s2[j]
			line += whiteSpace
		line += "%.1f " % (table[i][j][0])
		oldJ = j
	print(line)
	print("\n")

def printTable1(table, path, s1, s2):
	spacer = "    "
	header = spacer + " "
	counter = 0
	for letter in s1:
		header += letter + spacer
	print(header)
	oldJ = -1 
	line = ""
	whiteSpace = spacer
	for (i,j) in path:
		if (oldJ != j):
			if (j != 0):
				print(line)
				whiteSpace = (len(line) - 1) * " " 
			line = s2[j]
			line += whiteSpace
		line += "%s:%s  " % (s1[i], s2[j])
		oldJ = j
	print(line)
	print("\n")


