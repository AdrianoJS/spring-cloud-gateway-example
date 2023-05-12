# Spring-cloud-gateway-example

This is an example project created for [Embriq](https://embriq.no) by [Adriano Stewart](https://github.com/adrianojs).
The main point is to show how simple it is to set up an API gateway using spring-cloud-gateway.

Different stages of the setup can be found in the different branches.
Each branch is meant to represent a final state for the API gateway.
All the branches combined show that you can choose how simple or complex of a gateway you want.

The main branch is used as the base as it represents two individual applications that need to be hidden behind an API gateway.

Read https://medium.com/@adrianostewart/creating-an-api-gateway-ba8447d82b5 for an explanation of what happens when and wny.

## Requirements
* Java 17
* Maven 3.8.6+

## Building the project
Simplu run `mvn clean install`

## Running the project
Each module must be started individually.
This can either be done by your IDE, e.g. IntelliJ,
or executing `mvn spring-boot:run` in the root directory of the application you want to start
