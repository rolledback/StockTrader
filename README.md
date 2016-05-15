StockTrader
==========
This project is being created as a learning exercise in the fields of autonomous multi-agent systems and genetic learning algorithms. The general idea of the project is to run a simplified simulation of a stock market, which agents can then buy and sell stocks on. The project is currently very much a WIP and all components are likely to change greatly between now and completion.

A high level roadmap can be formed by looking at the issues page for the repository. That being said, the current plan is to get the the main components completely finished before writing any AIs. Large tasks left to be completed include object serialization, scenario definitions, and documentation. Work is currently focused on integrating Gradle into the project.

Build & Execution
=================
To run the project, make sure that you have Gradle installed on your machine. Then, simply run `gradle build` at the root of the project to build the project. You may then use the `run.ps1` script to execute the project. Flags for said script are as follows.

* `server`: Starts the server and an example human client. Each will open in a new window.
* `console`: Only has a purpose when used along with the `server` flag. Adding this flag will also cause the console client to be started in a new window. It will automatically connect to the server.
* `stock`: Runs code found in the main of `Stock.java`.