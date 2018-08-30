package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"path"
	"strings"
	"syscall"

	"github.com/google/logger"

	"./lib"
)

type (
	// Request the generic request.
	Request struct {
		Token    string          `json:"token"`
		EndPoint string          `json:"endpoint"`
		Method   string          `json:"method"`
		Payload  json.RawMessage `json:"payload"`
	}
	// RequestServerCreate the payload for a deletion of create.
	RequestServerCreate struct {
		Type   string                 `json:"type"`
		Custom map[string]interface{} `json:"custom"`
	}
	// RequestServerDelete the payload for deletion of servers
	RequestServerDelete struct {
		Name string `json:"name"`
	}
	// RequestServerExpire the payload for the expiration of servers.
	RequestServerExpire struct {
		Name string `json:"name"`
	}

	// Response to be returned once finished processing. With the exeception of "random" errors.
	Response struct {
		Code    int    `json:"code"`
		Message string `json:"message"`
	}
)

var log *logger.Logger

func main() {

	// Logger setup
	currDir, _ := os.Getwd()

	lf, err := os.OpenFile(path.Join(currDir, "log.log"), os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0660)
	if err != nil {
		logger.Fatalf("Failed to open log file %v", err)
	}
	defer lf.Close()

	log = logger.Init("main", true, true, lf)
	defer log.Close()

	// Pring startup + get key
	log.Info("\nAssimilationMC JSON Data-Proxy v2.1 server starting up...")
	if err := AquireMasterKey(); err != nil {
		log.Warning(err.Error())
	}

	// And serve hot and tasty
	http.HandleFunc("/", func(w http.ResponseWriter, req *http.Request) {

		log.Info("Incoming connection from ", req.RemoteAddr)

		if req.Method != "POST" {
			log.Errorf("Rejected request. Invalid method. (They said \"%v\") From `%v`", req.Method, req.RemoteAddr)
			http.Error(w, lib.InvalidRequest, 405)
			return
		}

		if err := req.ParseForm(); err != nil {
			log.Errorf("ParseForm() error %v", err)
			http.Error(w, lib.DataParseFail, 500)
			return
		}

		decoder := json.NewDecoder(req.Body)

		var parsedReq Request
		decodeErr := decoder.Decode(&parsedReq)

		if decodeErr != nil {
			log.Errorf("JSON decode error %v", decodeErr)
			http.Error(w, lib.DataParseFail, 500)
			return
		}

		success, serverIDErr := authRequest(&parsedReq)

		if !success {
			log.Warningf("Rejected request. Bad authentication (They said \"%v\") From `%v`", parsedReq.Token, req.RemoteAddr)
			http.Error(w, serverIDErr, 403)
			return
		}

		log.Infof("Identified %v as %v", req.RemoteAddr, serverIDErr)

		if parsedReq.EndPoint == "" {
			log.Warningf("Rejected request. No target. From `%v`", req.RemoteAddr)
			http.Error(w, lib.EndPoint, 400)
			return
		}

		var handlerErr = ""
		var handlerErrno = -1

		switch parsedReq.EndPoint {
		case "server":
			handlerErr, handlerErrno = HandleServersRequest(parsedReq)
			break
		case "ping":
			NotifyPingEvent(serverIDErr)
			fmt.Fprintf(w, string(buildResponse("Pong", 200)))
			return
		case "auth":
			handlerErr, handlerErrno = HandleAuthEndpoint(parsedReq)
			break
		// case "analytics":

		// handlerErr, handlerErrno =
		default:
			log.Warningf("Rejected request. Invalid EndPoint (They said \"%v\") From `%v`", parsedReq.EndPoint, req.RemoteAddr)
			http.Error(w, lib.EndPoint, 400)
			return
		}

		if handlerErr != "" {
			if strings.Contains(handlerErr, "errorId") {
				fmt.Fprintf(w, handlerErr)

			} else {
				fmt.Fprintf(w, string(buildResponse(handlerErr, handlerErrno)))
			}

		} else {
			buildResponse("OK", 200)
		}
		log.Infof("Connection closed for %v", req.RemoteAddr)
	})

	http.ListenAndServe(":6969", nil)
	log.Info("Server closed.")

	keepalive := make(chan os.Signal, 1)
	signal.Notify(keepalive, syscall.SIGINT, syscall.SIGTERM, os.Interrupt, os.Kill)
	<-keepalive
	// Kill all servers

}

func buildResponse(handlerErr string, handlerErrno int) []byte {
	data, _ := json.Marshal(&Response{Code: handlerErrno, Message: handlerErr})
	return data
}
