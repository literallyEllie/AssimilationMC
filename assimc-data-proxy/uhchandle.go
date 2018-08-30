package main

import (
	"encoding/json"
	"io/ioutil"
	"os"
	"os/exec"
	"path"
	"runtime"
	"strconv"
	"strings"
	"time"

	"./lib"
)

const (
	uhcMapStore = mapsDataStore + "/UHC_MAPS"
)

var activeUHCServers = []int{}
var mappedRAM = map[int]int{}
var ramAllocate = map[string]int{
	"TEAMED_CLASSIC":    5,
	"TEAMED_SCATTER":    5,
	"TEAMED_DEATHMATCH": 3,

	"SINGLES_CLASSIC":    4,
	"SINGLES_DEATHMATCH": 2,
}

func createUHCServer(mapSubType string, mapName string, settings map[string]interface{}) (string, int) {

	// Ram check
	neededRAM := ramAllocate[mapSubType]
	if ramUsage+neededRAM > maxRAM {
		return lib.TooManyServers, 503
	}

	// Assign
	serverID := 1
	if lib.IntArrayContains(serverID, activeUHCServers) {
		for lib.IntArrayContains(serverID, activeUHCServers) {
			serverID++
		}
	}

	// Deprecated
	// if serverID > activeCustomServerThreshold {
	// return lib.TooManyServers, 503
	// }

	fullName := "UHC-" + strconv.Itoa(serverID)

	// Get server directory
	serverDirectory := path.Join(globServerDirectory, fullName)

	// If server direct
	if _, err := os.Stat(serverDirectory); os.IsNotExist(err) {
		return lib.ServerNotFound, 404
	}

	// Copy over required map.
	msg, code := copyMaps(path.Join(serverDirectory, mapName), mapSubType, mapName)
	if msg != "" {
		log.Warningf("Rejected request: %v", msg)
		return msg, code
	}

	// Copy over plugin
	lib.CopyDir(path.Join(pluginDataStore, "uhc"), path.Join(serverDirectory, "plugins"))

	if runtime.GOOS == "windows" {

		// Will only be used for debug so i dont give a shit really. so done
		fileName := "\"D:/Ellie/Test Servers/AssimilationMC/NEW Network/SERVERS/UHC-1/run.bat\""
		escaped, err := strconv.Unquote(fileName)

		out := exec.Command("cmd", "/C", "start", escaped, escaped)
		err = out.Start()
		if err != nil {
			// log.Printf("%v", err)
			return lib.ActionFailure, 500
		}

		// log.Printf("Process started for server %v", serverID)

	} else {

		// 2.0
		// err := lib.ExecuteSpecial(func(cmd *exec.Cmd) {
		// cmd.Dir = serverDirectory
		// }, path.Join(serverDirectory, "start.sh"))

		// if err != nil {
		// log.Errorf("Failed to run server bash script for UHC-%v :: %v", serverID, err.Error())
		// return lib.ActionFailure, 500
		// }

		// 2.1
		err := lib.ExecuteSpecial(func(cmd *exec.Cmd) {
			cmd.Dir = serverDirectory
		}, "tmux", "new-session", "-s", fullName, "-d", "java -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:MaxGCPauseMillis=45 -XX:TargetSurvivorRatio=90 -XX:G1NewSizePercent=50 -XX:G1MaxNewSizePercent=80 -XX:InitiatingHeapOccupancyPercent=10 -XX:G1MixedGCLiveThresholdPercent=50 -XX:+AggressiveOpts -Xmx"+strconv.Itoa(neededRAM)+"G -jar PaperSpigot-1.8.8-R0.1-SNAPSHOT-latest.jar nogui")

		if err != nil {
			log.Errorf("Failed to start server %v :: %v", fullName, err.Error())
			return lib.ActionFailure, 500
		}

		if err != nil {
			log.Errorf("Failed to start server %v :: %v", fullName, err.Error())
			return lib.ActionFailure, 500
		}

	}

	// Seralize custom settings
	settingsJSON, _ := json.Marshal(settings)

	// Set startup params
	err := ioutil.WriteFile(path.Join(serverDirectory, "STARTUP_PARAMS"), []byte("subType="+mapSubType+"\nmap="+mapName+"\ncustom="+string(settingsJSON)), 0644)
	if err != nil {
		log.Errorf("Failed to create startup arguements for UHC-%v :: %v", serverID, err.Error())
		return lib.ActionFailure, 500
	}

	// Set active
	startingServers = append(startingServers, fullName)
	ramUsage += neededRAM
	mappedRAM[serverID] = neededRAM

	log.Infof("Server UHC-%v pending activation - 15 second timeout", serverID)
	// Timeout task
	go func() {
		<-time.NewTimer(time.Second * 15).C

		// Check if there was a response gotten
		arrIndex := -1
		for index, s := range startingServers {
			if s == fullName {
				arrIndex = index
			}
		}
		if arrIndex == -1 {
			return
		}

		// Cleanup mess
		log.Infof("Server %v failed to respond in 15 seconds, problem? Cleaning up", serverID)
		startingServers = lib.RemStringFromArray(startingServers, fullName)
		cleanServer(fullName)
		ramUsage -= neededRAM
		delete(mappedRAM, serverID)
	}()

	return fullName, 200
}

// OnUHCServerPing called when a UHC ping after being activated
func OnUHCServerPing(serverID string) {
	if !strings.Contains(serverID, "UHC") {
		return
	}

	arrIndex := -1
	for index, s := range startingServers {
		if s == serverID {
			arrIndex = index
		}
	}

	if arrIndex == -1 {
		log.Warningf("Recieved a ping from %v but they are pending activation.", serverID)
		return
	}

	serverIntID, err := strconv.Atoi(strings.Split(serverID, "-")[1])
	if err != nil {
		log.Errorf("Failed to get UHC ID from server ID %v", serverID)
	}

	startingServers = lib.RemStringFromArray(startingServers, serverID)
	activeUHCServers = append(activeUHCServers, serverIntID)
	activeServers = append(activeServers, serverID)

	log.Infof("Server %v has been set to active.", serverID)
}

func copyMaps(to string, serverType string, name string) (string, int) {
	if _, err := os.Stat(uhcMapStore + "/" + serverType); os.IsNotExist(err) {
		return lib.InvalidGameType, 404
	}

	if _, err := os.Stat(uhcMapStore + "/" + serverType + "/" + name); os.IsNotExist(err) {
		return lib.MapNotFound, 404
	}

	lib.CopyDir(uhcMapStore+"/"+serverType+"/"+name, to)
	return "", 200
}

func delayedExpireUHCServer(serverID string) (string, int) {
	serverIntID, err := strconv.Atoi(strings.Split(serverID, "-")[1])
	if err != nil {
		return lib.BadPayload, 400
	}

	if _, err := os.Stat(globServerDirectory + "/" + serverID); os.IsNotExist(err) {
		return lib.ServerNotFound, 404
	}

	timer := time.NewTimer(time.Second * 2)
	go func() {
		<-timer.C
		expireUHCServer(serverIntID)
	}()

	return "Expiring", 200
}

func expireUHCServer(serverID int) {
	// Deactivate
	activeUHCServers = lib.RemElemFromIntArray(activeUHCServers, serverID)
	cleanServer(path.Join(globServerDirectory, "UHC-"+strconv.Itoa(serverID)))
	// Free up RAM
	ramUsage -= mappedRAM[serverID]
	delete(mappedRAM, serverID)
	// Double check to kill off process
	exec.Command("tmux", "kill-session", "-t", "UHC"+strconv.Itoa(serverID)).Run()

	// Tell the good news
	log.Infof("Server UHC-%v expired", serverID)
}

func cleanServer(serverDirectory string) string {

	// Get map
	b, err := ioutil.ReadFile(path.Join(serverDirectory, "STARTUP_PARAMS"))
	if err == nil {
		mapName := strings.Split((strings.Split(string(b), "\n")[1]), "=")[1]
		os.RemoveAll(path.Join(serverDirectory, mapName))
	}

	// Delete stale data
	os.Remove(path.Join(serverDirectory, "usercache.json"))
	os.Remove(path.Join(serverDirectory, "ops.json"))
	os.Remove(path.Join(serverDirectory, "whitelist.json"))
	os.Remove(path.Join(serverDirectory, "STARTUP_PARAMS"))
	os.RemoveAll(path.Join(serverDirectory, "maps"))
	os.RemoveAll(path.Join(serverDirectory, "world", "playerdata"))
	os.RemoveAll(path.Join(serverDirectory, "world", "stats"))
	os.RemoveAll(path.Join(serverDirectory, "plugins"))

	return "ok"
}
