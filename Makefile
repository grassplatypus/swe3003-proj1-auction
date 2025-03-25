all: compile run

compile:
	javac Auction.java

run:
	java -cp .:./postgresql-42.6.0.jar Auction bnam changethis

.PHONY: all compile run