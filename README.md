# Background
The Aladdin Product Service team is building out our engineering presence in Gurgoan, and we are seeking exceptional candidates to fill several rolls on our team. We are looking for engineers who have a high degree of technical aptitude, who are creative problem solvers, and who, most of all, are passionate. We have created this test to let you show us how excited you are about the kinds of technical challenges we face, and to give you an opportunity to show-case your technical skill and creative problem solving.  

A few things to bear in mind if you take on this test:
   1. **We do not expect that you have familiarity with all of the tools and technologies used in this test (Java, Kafka, Protocol Buffers, etc.).**  Part of the test is to see how you tackle an unfamiliar problem with an unfamiliar toolchain.  Completing the test will require a combination of technical know-how, the ability to find and leverage useful resources and documentation (expect to use Google a lot!), and a great deal of creativity and the determination to follow through. We believe these are the primary attributes that will make for a successful team member!  
   2. **Completing the test will take a lot of work.**  As a result, it gives us much of the information we need to know about you as a candidate.  Successfully completing the test will put you in an excellent position to move forward with the process. Subsequent interviews will focus largely on the process and approach taken during the test, as well as the design and result of your solution.  
   3. **There isn’t a single right answer, and aren’t many structured requirements.**  We are very much interested in how you approach and solve this problem.  Our team needs engineers who can synthesize business problems into slick technical solutions that create business value without prescriptive oversight.  This test will help us understand the kind of decisions you make and the kind of work you do, when given an open ended problem  with intentionally few requirements.
   4. But, there are a few requirements.   The few requirements outlined in more detail below are hard requirements.   Doing these things exactly as specified are the only hard requirements.
    * Read Protocol Buffers from Kafka, though we give you the Protocol Buffer definitions and generated Java files  
    * Read server data over HTTP as JSON via a REST API are hard requirements  

A final note on academic honesty, as clearly this is not a supervised test. It's certainly fine to ask people you know questions, indeed that's part of any engineer's day to day, but we cannot overemphasize that we expect you to be able to discuss any and all aspects of your solution, and if it is not your own work then that will become apparent very quickly in the interview process.  

Enough preamble, let's jump into the detail...

# Crazy Cricket
Welcome to crazy cricket, the greatest Cricket Video game on earth! You have been recruited to help our Analytics Team team make the game more engaging for users, driving growth in the user base! In conjuction with the product manager for the game we have decided that an REST API that allows us to publish league tables will be the catalyst for an explosion of interest in the game!

## High Level
The goal here is to create a REST API that serves aggregated metrics created from a data source. In this case the data source will be a Kafka instance where the topics are protocol buffer objects that represent game results. Your task is to provide a REST API that returns correct aggregated tables of the data that comes off of the Kafka topics. If you don't know Kafka, or protocol buffers, that shouldn't be a problem as you really don't need to know much about either to execute the project, though it will necessitate a few additional hours of reading to get familiar with those tools.

## Setup and submission requirements  
1. Linux (or Windows with Cygwin): you will need to be able to execute .sh scripts to run Kafka and the project locally. I recommend doing this on a VM using Virtual Box, though as long as you can run Kafka you are all set!
2. a distribution of Kafka: download the distribution [here](http://kafka.apache.org/downloads.html), and unpack it
3. sbt, or Simple Build Tool (honestly, it's not remotely simple): in order to run the tests and get access to the Java protocol buffer defintions, you need to download and compile the project. Typesafe provide binary distributions of sbt [here](http://www.scala-sbt.org/0.13/docs/Setup.html), and it is very easy to install (Windows, Mac, Linux).
4. Clone this repo: `git clone... <your dir>; cd <your dir>; sbt`, which should clone the project into `<your dir>`, and fire up sbt, then use `run` to run the main classes, providing the requird arguments. For example, below will run the tests if your Kafka broker is running at localhost:9092, and your REST API at localhost:8080:
```
oscar > cd crazy-cricket
oscar > sbt
> run --kafka-broker localhost:9092
[info] Compiling 1 Scala source to /Users/Oscar/Work/crazy-cricket/target/scala-2.11/classes...
[warn] Multiple main classes detected.  Run 'show discoveredMainClasses' to see the list

Multiple main classes detected, select one to run:

 [1] com.bfm.acs.crazycricket.ResultsTester
 [2] com.bfm.acs.crazycricket.SampleDataProducer
 [3] com.bfm.acs.crazycricket.DummyServer
> 1
...
```
5. Create a project of your own with a solution: there is only one requirement, that the project contians a directory called bin, and a shell script `bin/run.sh <kafka host:port>` which starts your REST API and any other components (databases, Kafka consumers, etc), and connects to the Kafka broker specified as an argument.

## Task
There are two requirements in the implementation:  
   1. The solution must read Protobuf messages off of a Kafka instance, and store the results in a persistent data store.  
   2. The end results must be served via HTTP get requests, and return results as JSON. The format of the required JSON is specified below.  

There are **no** other requirements! You can use whatever technology/language you like! At a high level, you will need three pieces:  
   1. A processor for reading the Protobuf messages off of the Kafka topics and persisting them in some form, which is itself an interesting design decision you will have to make  
   2. A datastore of some kind that will hold the the data you have read  
   3. A server that will respond to REST requests as follows:  
    * Current leaderboad:  
    `GET <server address>/api/leaderboard`  
    * Date range leaderboard:  
    `GET <server address>/api/leaderboard?start=yyyyMMdd&end=yyyyMMdd`  
    * Current country leaderboard:  
    `GET <server address>/api/national_leaderboard`  
    * Date range country leaderboard:  
    `GET <server address>/api/national_leaderboard?start=yyyyMMdd&end=yyyyMMdd`  

Also JSON should be formatted as `{[name_1,value_1],...,[name_k,value_k]}`

There is a class in the repo called `com.bfm.acs.crazycricket.SampleDataProducer`. Once you have a Kafka broker up and running you can run something like this:  
```shell
oscar > cd crazy-cricket
oscar/crazy-cricket > sbt build
oscar/crazy-cricket > java -cp CrazyCricket-ASSEMBLY.jar com.bfm.acs.crazycricket.SampleDataProducer --kafka-broker <host:port>
```
This will populate your Kafka topics with a few sample messages that you might want to use for testing.

## Submission
Your submission should either be a Github repository on your personal Github page, or an archive that you submit to your HR contact. A few requirements:  
   1. There **must** me an executable in bin, called `run.sh`, i.e. `bin/run.sh`. You can put whatever build/run steps you like in there, but that script must fire up all your services (REST API, Kafka consumer, database, depending on what pieces you use to attack the problem)
   2. This shell script must take as an argument the Kafka broker location
   3. The script should print out the location at which the REST API is accpeting HTTP requests so I can hook the tests into your REST service

## Gotchas  
There are a few pretty easy mistakes make and things that you can get confused by:  
   1. Beware starting up Kafka, it requires a Zookeeper instance. Make sure you read the "Quickstart" section of the docs carefully, as it can be a little tricky.
   2. When you've populated a few messages in Kafka, if you want to start a program and read them off, you need to explicitly configure Kafka to read from the beginning of the topic. By default the consumer reads only new messages. I lost a few hours writing this test on that one...  

## Grading Rubric
First and foremost, this is a design test. Given a set of constraints on input and output we are interested in the details of your implementation when you make all the decisions yourself. More specifically we will be looking at:  
   1. Unit tests: we would like to see unit tests  
   2. Coding style: clear and concise code is always preferred  
   3. Design decisions: this essentially a design test, in the sense that it is really asking you to design a system for   capturing and presenting data via a specified interface. We are most interested not in which technologies you chose, but in **why** you chose them, and if the way that you used them demonstrated a clear understanding of that technology.

## Bonus  
There are some bonus points available for the following:    
   1. Catching cheaters: how could our backend ensure hackers can't artificially inflate scores?  
   2. Duplicate games: how many games can someone play at once?!  
These kinds of questions come up regularly in data engineering, so it would be great to see some thoughtful solutions.  

## Final Words
Good luck, and please feel free to post questions in the "Issues" section! We are very excited to see what you come up with!

