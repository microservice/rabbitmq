# _RabbitMQ_ Open Microservice

> This service allows communication to a RabbitMQ server

[![Open Microservice Specification Version](https://img.shields.io/badge/Open%20Microservice-1.0-477bf3.svg)](https://openmicroservices.org)
[![Open Microservices Spectrum Chat](https://withspectrum.github.io/badge/badge.svg)](https://spectrum.chat/open-microservices)
[![Open Microservices Code of Conduct](https://img.shields.io/badge/Contributor%20Covenant-v1.4%20adopted-ff69b4.svg)](https://github.com/oms-services/.github/blob/master/CODE_OF_CONDUCT.md)
[![Open Microservices Commitzen](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](http://makeapullrequest.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

## Introduction

This project is an example implementation of the [Open Microservice Specification](https://openmicroservices.org), a standard
originally created at [Storyscript](https://storyscript.io) for building highly-portable "microservices" that expose the
events, actions, and APIs inside containerized software.

## Getting Started

The `oms` command-line interface allows you to interact with Open Microservices. If you're interested in creating an Open
Microservice the CLI also helps validate, test, and debug your `oms.yml` implementation!

See the [oms-cli](https://github.com/microservices/oms) project to learn more!

### Installation

```
npm install -g @microservices/oms
```

## Usage

### Open Microservices CLI Usage

Once you have the [oms-cli](https://github.com/microservices/oms) installed, you can run any of the following commands from
within this project's root directory:

#### Actions

##### exchange

##### Action Arguments

| Argument Name | Type     | Required | Default | Description                                                          |
| :------------ | :------- | :------- | :------ | :------------------------------------------------------------------- |
| name          | `string` | `true`   | None    | The name of the exchange to subscribe to                             |
| type          | `string` | `true`   | None    | The type of this exchange (eg: direct, topic)                        |
| routing_key   | `string` | `true`   | None    | The routing key to use                                               |
| consumer_tag  | `string` | `true`   | None    | The consumer tag                                                     |
| RABBITMQ_URI  | `string` | `true`   | None    | The RabbitMQ connection URI (eg: amqp://username:password@host:port) |

```shell
oms subscribe exchange \
    -a name='*****' \
    -a type='*****' \
    -a routing_key='*****' \
    -a consumer_tag='*****' \
    -e RABBITMQ_URI=$RABBITMQ_URI
```

##### basic_publish

##### Action Arguments

| Argument Name    | Type     | Required | Default | Description                                                          |
| :--------------- | :------- | :------- | :------ | :------------------------------------------------------------------- |
| exchange         | `string` | `true`   | None    | No description provided.                                             |
| routing_key      | `string` | `true`   | None    | No description provided.                                             |
| app_id           | `string` | `false`  | None    | No description provided.                                             |
| cluster_id       | `string` | `false`  | None    | No description provided.                                             |
| content_encoding | `string` | `false`  | None    | No description provided.                                             |
| content_type     | `string` | `false`  | None    | No description provided.                                             |
| expiration       | `string` | `false`  | None    | No description provided.                                             |
| message_id       | `string` | `false`  | None    | No description provided.                                             |
| type             | `string` | `false`  | None    | No description provided.                                             |
| reply_to         | `string` | `false`  | None    | No description provided.                                             |
| headers          | `map`    | `false`  | None    | No description provided.                                             |
| content          | `string` | `true`   | None    | No description provided.                                             |
| RABBITMQ_URI     | `string` | `true`   | None    | The RabbitMQ connection URI (eg: amqp://username:password@host:port) |

```shell
oms run basic_publish \
    -a exchange='*****' \
    -a routing_key='*****' \
    -a app_id='*****' \
    -a cluster_id='*****' \
    -a content_encoding='*****' \
    -a content_type='*****' \
    -a expiration='*****' \
    -a message_id='*****' \
    -a type='*****' \
    -a reply_to='*****' \
    -a headers='*****' \
    -a content='*****' \
    -e RABBITMQ_URI=$RABBITMQ_URI
```

## Contributing

All suggestions in how to improve the specification and this guide are very welcome. Feel free share your thoughts in the
Issue tracker, or even better, fork the repository to implement your own ideas and submit a pull request.

[![Edit rabbitmq on CodeSandbox](https://codesandbox.io/static/img/play-codesandbox.svg)](https://codesandbox.io/s/github/oms-services/rabbitmq)

This project is guided by [Contributor Covenant](https://github.com/oms-services/.github/blob/master/CODE_OF_CONDUCT.md).
Please read out full [Contribution Guidelines](https://github.com/oms-services/.github/blob/master/CONTRIBUTING.md).

## Additional Resources

- [Install the CLI](https://github.com/microservices/oms) - The OMS CLI helps developers create, test, validate, and build
  microservices.
- [Example OMS Services](https://github.com/oms-services) - Examples of OMS-compliant services written in a variety of
  languages.
- [Example Language Implementations](https://github.com/microservices) - Find tooling & language implementations in Node,
  Python, Scala, Java, Clojure.
- [Storyscript Hub](https://hub.storyscript.io) - A public registry of OMS services.
- [Community Chat](https://spectrum.chat/open-microservices) - Have ideas? Questions? Join us on Spectrum.
