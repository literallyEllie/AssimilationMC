package lib

import "encoding/json"

type errorData struct {
	ID      string `json:"errorId"`
	Message string `json:"message"`
}

func encode(data errorData) string {
	j, _ := json.Marshal(data)
	return string(j)
}

// Generic
var PermissionDenied = encode(errorData{"PermissionDenied", "You cannot access this endpoint"})
var CannotParse = encode(errorData{"CannotParseRequest", "Failed to parse request"})
var InvalidRequest = encode(errorData{"InvalidMethod", "Invalid method verb"})
var DataParseFail = encode(errorData{"DataParseFail", "Failed to parse the provided data"})
var BadAuth = encode(errorData{"BadAuthentication", "Bad authentication code"})
var EndPoint = encode(errorData{"BadEndPoint", "No or invalid endpoint specified"})
var EndPointMethod = encode(errorData{"BadEndPointMethod", "Unrecognized endpoint method"})
var BadPayload = encode(errorData{"BadPayload", "Bad or non-existant payload sent"})
var ActionFailure = encode(errorData{"ActionFailure", "Failed to perfrom specified action"})

// Servers
var TooManyServers = encode(errorData{"TooManyActiveServers", "Too many active servers, try again later"})
var ServerNotFound = encode(errorData{"ServerNotFound", "Server with ID has no active directory"})

var InvalidGameType = encode(errorData{"InvalidGameType", "Game type specified does not exist."})
var MapNotFound = encode(errorData{"MapNotFound", "Map specified was invalid."})

// Auth
var TokenExists = encode(errorData{"TokenExists", "Could not create token as it already exists."})
var TokenNoExist = encode(errorData{"TokenNoExists", "A token assigned to that server could not be found"})
