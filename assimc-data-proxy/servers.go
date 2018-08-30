package main

import (
	"encoding/json"
	"os"
	"strconv"
	"strings"
	"time"

	"./lib"
)

const (
	// globServerDirectory = "D:\\Ellie\\Test Servers\\AssimilationMC\\NEW Network\\SERVERS"
	globServerDirectory         = "/assimilationmc/server"
	serverLibs                  = globServerDirectory + "/lib"
	mapsDataStore               = serverLibs + "/maps"
	pluginDataStore             = serverLibs + "/plugin"
	activeCustomServerThreshold = 6
)

var activeServers = []string{}
var startingServers = []string{}

// maxRAM the max ram that will be allowed to be used.
var maxRAM = 25
var ramUsage = 0

// HandleServersRequest the method called when the endpoint servers is called.
func HandleServersRequest(req Request) (string, int) {

	switch req.Method {
	case "CREATE_SERVER":
		var data RequestServerCreate
		if err := json.Unmarshal([]byte(req.Payload), &data); err != nil {
			log.Errorf("(SERVER-CREATE) Failed to parse payload: %v (%v)", err, req.Payload)
			return lib.CannotParse, 400
		}
		return createServer(data)
	case "DELETE_SERVER":
		var data RequestServerDelete
		if err := json.Unmarshal([]byte(req.Payload), &data); err != nil {
			log.Errorf("(SERVER-DELETE) Failed to parse payload: %v (%v)", err, req.Payload)
			return lib.CannotParse, 400
		}
		return delayedServerDelete(data)
	case "EXPIRE_SERVER":
		var data RequestServerExpire
		if err := json.Unmarshal([]byte(req.Payload), &data); err != nil {
			log.Errorf("(SERVER-EXPIRE) Failed to parse payload: %v (%v)", err, req.Payload)
			return lib.CannotParse, 400
		}
		expireServer(data.Name)
		return "Expired.", 200
	case "USAGE":
		return strconv.Itoa(ramUsage), 200
	}
	return lib.EndPointMethod, 502
}

// NotifyPingEvent called when a MC server pings the server.
func NotifyPingEvent(serverID string) {

	arrIndex := -1
	for index, id := range startingServers {
		if id == serverID {
			arrIndex = index
		}
	}

	if arrIndex == -1 {
		return
	}

	// Call listeners
	OnUHCServerPing(serverID)

}

func createServer(req RequestServerCreate) (string, int) {
	if len(activeServers)+len(startingServers) >= activeCustomServerThreshold {
		return lib.TooManyServers, 503
	}

	// Example UHC type string: uhc/TEST_SINGLES/STM
	if strings.Contains(req.Type, "uhc/") {
		serverParam := strings.Split(req.Type, "/")
		return createUHCServer(serverParam[1], serverParam[2], req.Custom)
	}

	// TODO server creation logic based on the type.

	return "", 501
}

func delayedServerDelete(req RequestServerDelete) (string, int) {
	serverID := req.Name

	if _, err := os.Stat(globServerDirectory + "/" + serverID); os.IsNotExist(err) {
		return lib.ServerNotFound, 404
	}

	timer := time.NewTimer(time.Second * 2)
	go func() {
		<-timer.C
		deleteServer(serverID)
	}()

	return "", 200
}

func deleteServer(serverID string) {
	log.Infof("Deleting server %v", serverID)
	os.RemoveAll(globServerDirectory + "/" + serverID)
	expireServer(serverID)
	log.Infof("Deleted server by ID %v", serverID)
}

func expireServer(serverID string) (string, int) {
	activeServers = lib.RemStringFromArray(activeServers, serverID)
	startingServers = lib.RemStringFromArray(startingServers, serverID)

	if strings.Contains(serverID, "UHC") {
		return delayedExpireUHCServer(serverID)
	}

	return "", 200
}

func getActiveServers() (string, int) {
	jsonV, _ := json.Marshal(activeServers)
	return string(jsonV), 200
}
