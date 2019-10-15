class state:
	def __init__(self, emission, transition, number):
		self.transition = transition
		self.emission = emission
		self.number = number
	def setTransition(self, transition):
		self.transition = transition
	def setEmission(self, emission):
		self.emission = emission
	def transition(self):
		return self.transition
	def emission(self):
		return self.emission