package main

import (
	"encoding/json"
	"io/ioutil"
	"os"
	"path"

	"./lib"
)

// RequestAuth the request called when token management is needed
type RequestAuth struct {
	ServerID string `json:"serverid"`
}

var masterKey string

// AquireMasterKey loads the master key into memory for easy access.
func AquireMasterKey() error {
	currDir, _ := os.Getwd()

	txt, err := ioutil.ReadFile(path.Join(currDir, "MASTER_KEY"))
	if err != nil {
		return err
	}

	masterKey = string(txt)

	return nil
}

func authRequest(req *Request) (bool, string) {
	token := req.Token
	if token == "" {
		return false, lib.BadAuth
	}

	info, err := tokenServer(token)
	if err != nil {
		log.Errorf("Failed to get server of token %v", err.Error())
		return false, lib.ActionFailure
	}

	if info == "bad" {
		return false, lib.BadAuth
	}

	return true, info
}

func tokenServer(token string) (string, error) {

	if token == masterKey {
		return "Amibigious Master", nil
	}

	currDir, _ := os.Getwd()

	files, err := ioutil.ReadDir(path.Join(currDir, "tokens"))
	if err != nil {
		return "Failed to read tokens directory", err
	}

	for _, f := range files {
		if f.Name() == token {
			txt, _ := ioutil.ReadFile(path.Join(currDir, "tokens", f.Name()))

			return string(txt), nil
		}
	}

	return "bad", nil
}

func makeToken(serverID string) string {

	if serverID == "" {
		return lib.BadPayload
	}

	currDir, _ := os.Getwd()

	files, err := ioutil.ReadDir(path.Join(currDir, "tokens"))
	if err != nil {
		log.Fatalf("Error reading directory tokens %v", err)
		return lib.ActionFailure
	}

	for _, f := range files {
		txt, _ := ioutil.ReadFile(path.Join(currDir, "tokens", f.Name()))
		if string(txt) == serverID {
			return lib.TokenExists
		}
	}

	newToken := lib.RandomString(25)
	ioutil.WriteFile(path.Join(currDir, "tokens", newToken), []byte(serverID), 0644)
	return ""
}

func delToken(serverID string) string {
	if serverID == "" {
		return lib.BadPayload
	}

	currDir, _ := os.Getwd()

	files, err := ioutil.ReadDir(path.Join(currDir, "tokens"))
	if err != nil {
		log.Fatalf("Error reading directory tokens %v", err)
		return lib.ActionFailure
	}

	var toDel string

	for _, f := range files {
		txt, _ := ioutil.ReadFile(path.Join(currDir, "tokens", f.Name()))
		if string(txt) == serverID {
			toDel = f.Name()
			break
		}
	}

	if toDel == "" {
		return lib.TokenNoExist
	}

	os.Remove(path.Join(currDir, "tokens", toDel))
	log.Warningf("Deleted server token for %v", serverID)

	return ""
}

// HandleAuthEndpoint handles a request to the auth endpoint
func HandleAuthEndpoint(req Request) (string, int) {

	if req.Token != masterKey {
		return lib.PermissionDenied, 401
	}

	if req.Method == "MAKE" {
		var data RequestAuth
		if err := json.Unmarshal([]byte(req.Payload), &data); err != nil {
			log.Errorf("(AUTH-MAKE) Failed to parse payload: %v (%v)", err, req.Payload)
			return lib.CannotParse, 400
		}

		err := makeToken(data.ServerID)
		if err != "" {
			log.Errorf("Error when making token for %v: %v", data.ServerID, err)
			return err, 500
		}

		log.Warningf("Token for server %v created", data.ServerID)

		return "", 200
	}

	if req.Method == "KILL" {
		var data RequestAuth
		if err := json.Unmarshal([]byte(req.Payload), &data); err != nil {
			log.Errorf("(AUTH-KILL) Failed to parse payload: %v (%v)", err, req.Payload)
			return lib.CannotParse, 400
		}

		err := delToken(data.ServerID)
		if err != "" {
			log.Errorf("Error when deleing key for %v: %v", data.ServerID, err)
			return err, 500
		}

		log.Warningf("Token for server %v deleted", data.ServerID)
		return "", 200
	}

	return lib.EndPointMethod, 502
}
