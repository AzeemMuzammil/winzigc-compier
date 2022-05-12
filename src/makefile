JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	./winzigc.java \
	./Tokenizer.java \
	./TokenData.java \
	./Token.java \
	./Parser.java \
	./TreeNode.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class