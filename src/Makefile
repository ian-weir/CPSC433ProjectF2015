JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Slot.java \
	SetBased.java \
	Preference.java \
	Pair.java \
	Output.java \
	OrTreeNode.java \
	OrTree.java \
	Main.java \
	Lab.java \
	FileParser.java \
	Fact.java \
	Eval.java \
	Course.java \
	Constr.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	 $(RM) *.class
