all: compile run

compile:
	javac Modules/Auction.java

run:
	java -cp .:./postgresql-42.6.0.jar Modules.Auction bnam changethis

.PHONY: all compile run