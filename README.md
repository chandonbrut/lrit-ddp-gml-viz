akka-ship-simulator
===================

Introduction
------------
This is a very brief Ship Simulator written in Scala, using Akka and Play.
The platform is controlled using REST interfaces: mostly GET requests and one JSON POST
request (containing simulation parameters) to start the simulation.


Building and Running
--------------------
To build the platform I am using sbt, but I'm pretty sure you can build it using activator
or play.

To build the distribution version using sbt, you should run:

		$ sbt universal:packageBin

Then you get the **target/universal/ship-simulator-1.0.zip** and unzip it where you see fit.

To run it, just run the **<path>/bin/ship-simulator** file.

You can also change where Play listens for connections, using Play's properties:

		<path>/bin/ship-simulator -Dhttp.port=8080
		<path>/bin/ship-simulator -Dhttp.address=127.0.0.1


Simulation Parameters
---------------------

The simulation configuration is pretty straight-forward and is done using a JSON POST request
to *http://localhost:9000/configure*

I use [httpie](https://github.com/jkbrzt/httpie) for that.

		$ cat simulation.json
		{
			"simFrontEndBaseUrl": "http://127.0.0.1:8080/simulator/sim",
			"wktArea": "POLYGON((30 10, 40 40, 20 40, 10 20, 30 10))",
			"imoFirstDigit": 1,
			"tickUnit": 2,
			"numberOfShips": 1000
		}

		$ http POST http://localhost:9000/configure < simulation.json
		HTTP/1.1 200 OK
		Content-Length: 202
		Content-Type: application/json; charset=utf-8
		Date: Tue, 09 Feb 2016 19:18:33 GMT

		{
		    "configuration": {
		        "imoFirstDigit": 1,
		        "numberOfShips": 1000,
		        "simFrontEndBaseUrl": "http://127.0.0.1:8080/simulator/sim",
		        "tickUnit": 2,
		        "wktArea": "POLYGON((30 10, 40 40, 20 40, 10 20, 30 10))"
		    },
		    "status": "ok"
		}

* The **simFrontEndBaseUrl** parameter specifies an URL so that the *ForwarderActor* can report
back simulated data. I also use a REST service for that, and the URL should be formatted as below:

		http://127.0.0.1:8080/simulator/sim/report/**id**,**latitude**,**longitude**,**timestamp**

* The **numberOfShips** parameter defines how many ships will be created.

* The **imoFirstDigit** parameter is kinda of domain specific (LRIT Ships) and is used to build
the ship identification number. The simulated LRIT ship ids (imo number) are built as below:

			for (i = 0; i < numberOfShips; i++) {
				id = (imoFirstDigit * 1000000) + i
			}

The **tickUnit** defines how long does a tick last.
	* 0 means a millisecond
	* 1 means a second
	* 2 means a minute
	* 3 means an hour
	* 4 means a day
	* anything else means a minute.

The **wktArea** parameter is a String defining the area in which the ships will be places.
This string follows the [WKT](https://en.wikipedia.org/wiki/Well-known_text)
format.

Requests
--------
To poll data or request the ship to change its reporting rate, you just need to do a GET
request to the simulator.

* Poll: $ http GET http://localhost:9000/request/**id**/poll

* Change Rate: $ http GET http://localhost:9000/request/**id**/**numberOfTicks**

The **numberOfTicks** parameter defines how many ticks are needed for a ship to report back
its position. Keep in mind that the **id** parameter can be a wildcard.

You can poll all ships using a GET request to the following URL:

http://localhost:9000/request/**\***/poll

You can also change the reporting rate for all ships using a wildcard.

Further Info
------------
This simulator is in a pretty early stage but can be easily extended. You might wanna check
the Protocol object to get a better understanding of the parameters and you might wanna check
the ForwarderActor to adapt it to your needs.