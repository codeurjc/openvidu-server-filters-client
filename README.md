# openvidu-server-filters-client

Simple Spring Boot application to consume OpenVidu Server WebSocket RPC protocol to manage filters.

## Running the application

1. Run OpenVidu Server

```bash
docker run -p 4443:4443 --rm openvidu/openvidu-server-kms:2.11.0
```

2. Run the application (change `OPENVIDU_URL` and `OPENVIDU_SECRET` to the proper values of your OpenVidu Server process)

```bash
git clone https://github.com/codeurjc/openvidu-server-filters-client.git
cd openvidu-server-filters-client
mvn -DOPENVIDU_URL=https://localhost:4443/ -DOPENVIDU_SECRET=MY_SECRET exec:java
```

## Managing OpenVidu filters through the application

This SpringBoot applications offers a very simple REST API endpoint to call the 5 filters-related methods offered by OpenVidu Server (see [OpenVidu Server RPC protocol](https://openvidu.io/docs/developing/rpc/)).

By default it listens on port `8080` with no security at all, and the 5 REST operations are:

> **NOTE**: file `postman.json` in the root path of this repository contains all 5 REST operations properly configured to be used with [Postman client](https://www.getpostman.com/) on localhost. Just import this json file inside Postman app and you will be ready to test all 5 REST methods on a running OpenVidu Session.

---

### POST /apply-filter

**Header**: `Content-Type: application/json`

**Body example**

```json
{
	"sessionId": "TestSession",
	"streamId": "exyoujh3l7wlhrru_CAMERA_SVTZP",
	"type": "GStreamerFilter",
    "options": "{\"command\": \"videobalance saturation=0.0\"}"
}
```

**Response**

- 200: operation success
- 400: some body parameter wrong
- 500: error in OpenVidu Server. See the attached message for further information

---

### POST /remove-filter

**Header**: `Content-Type: application/json`

**Body example**

```json
{
	"sessionId": "TestSession",
	"streamId": "exyoujh3l7wlhrru_CAMERA_SVTZP"
}
```

**Response**

- 200: operation success
- 400: some body parameter wrong
- 500: error in OpenVidu Server. See the attached message for further information

---

### POST /exec-filter-method

**Header**: `Content-Type: application/json`

**Body example**

```json
{
	"sessionId": "TestSession",
	"streamId": "exyoujh3l7wlhrru_CAMERA_SVTZP",
	"method": "setElementProperty",
	"params": "{\"propertyName\":\"saturation\",\"propertyValue\":\"1.0\"}"
}
```

**Response**

- 200: operation success
- 400: some body parameter wrong
- 500: error in OpenVidu Server. See the attached message for further information

---

### POST /add-filter-event-listener

**Header**: `Content-Type: application/json`

**Body example**

```json
{
	"sessionId": "TestSession",
	"streamId": "exyoujh3l7wlhrru_CAMERA_SVTZP",
	"eventType": "CodeFound"
}
```

**Response**

- 200: operation success
- 400: some body parameter wrong
- 500: error in OpenVidu Server. See the attached message for further information

---

### POST /remove-filter-event-listener

**Header**: `Content-Type: application/json`

**Body example**

```json
{
	"sessionId": "TestSession",
	"streamId": "exyoujh3l7wlhrru_CAMERA_SVTZP",
	"eventType": "CodeFound"
}
```

**Response**

- 200: operation success
- 400: some body parameter wrong
- 500: error in OpenVidu Server. See the attached message for further information