#NOTE: WATCHED THIS VIDEO TO HELP LEARN ALGORITHM
#https://www.youtube.com/watch?v=Thv3TfsZVpw

import sys, getopt
from constants import costFunction, printTable, printTable1



def main(argv):

	#TERMINAL SETUP STUFF
	string1 = ""
	string2 = ""
	optFlagsArr = [False, False]
	if (len(argv) != 2):
		print('sed.py [string1] [string2]')
		sys.exit(2)
	string1 = '#' + argv[0]
	string2 = '#' + argv[1]
	#####################
	s1Range = range(len(string1))
	s2Range = range(len(string2))
	table = [[(0,[]) for x in s2Range] for y in s1Range] 
	for i in s1Range:
		for j in s2Range: 
			l = []
			if i:
				l.append((table[i-1][j][0]+2, table[i-1][j][1] + [(i-1,j)]))
			if j:
				l.append((table[i][j-1][0]+2, table[i][j-1][1] + [(i,j-1)]))
			if i and j:
				l.append((table[i-1][j-1][0]+costFunction(string1[i],string2[j]), table[i-1][j-1][1] + [(i-1,j-1)]))
			if not l:
				continue
			minVal = min(l, key = lambda val: val[0])
			table[i][j] = minVal

	path = table[-1][-1][1] + [(-1,-1)] #this is where the final path is stored. also adding last square to path
	printTable(table, path, string1, string2)
	printTable1(table, path, string1, string2)

if __name__ == '__main__':
    main(sys.argv[1:])