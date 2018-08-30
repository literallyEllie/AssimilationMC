package lib

import (
	"bufio"
	"io"
	"io/ioutil"
	"log"
	"math/rand"
	"os"
	"os/exec"
	"strings"
	"time"
)

// EditFileLine Set a file line to a specified string.
func EditFileLine(path string, line int, newLine string) error {
	input, err := ioutil.ReadFile(path)
	lines := strings.Split(string(input), "\n")
	lines[line] = newLine
	output := strings.Join(lines, "\n")
	err = ioutil.WriteFile(path, []byte(output), 0644)
	return err
}

// FindLine find a line in a file by query, return -1 if not exist in file.
func FindLine(path string, query string) int {
	f, err := os.Open(path)
	if err != nil {
		return 0
	}
	defer f.Close()

	scanner := bufio.NewScanner(f)

	line := 1
	for scanner.Scan() {
		if strings.Contains(scanner.Text(), query) {
			return line
		}

		line++
	}

	return -1
}

// CopyFile Method to copy a file to another location
func CopyFile(source string, dest string) (err error) {
	sourcefile, err := os.Open(source)
	if err != nil {
		return err
	}

	defer sourcefile.Close()

	destfile, err := os.Create(dest)
	if err != nil {
		return err
	}

	defer destfile.Close()

	_, err = io.Copy(destfile, sourcefile)
	if err == nil {
		sourceinfo, err := os.Stat(source)
		if err != nil {
			err = os.Chmod(dest, sourceinfo.Mode())
		}

	}

	return
}

// CopyDir Method to copy a direction to another location
func CopyDir(source string, dest string) (err error) {

	// get properties of source dir
	sourceinfo, err := os.Stat(source)
	if err != nil {
		return err
	}

	// create dest dir

	err = os.MkdirAll(dest, sourceinfo.Mode())
	if err != nil {
		return err
	}

	directory, _ := os.Open(source)

	objects, err := directory.Readdir(-1)

	for _, obj := range objects {

		sourcefilepointer := source + "/" + obj.Name()

		destinationfilepointer := dest + "/" + obj.Name()

		if obj.IsDir() {
			// create sub-directories - recursively
			err = CopyDir(sourcefilepointer, destinationfilepointer)
			if err != nil {
				log.Println(err)
			}
		} else {
			// perform copy
			err = CopyFile(sourcefilepointer, destinationfilepointer)
			if err != nil {
				log.Println(err)
			}
		}

	}
	return
}

// IntArrayContains check if an array contains a value
func IntArrayContains(x int, list []int) bool {
	for _, b := range list {
		if b == x {
			return true
		}
	}
	return false
}

// RemElemFromIntArray remove an element from an array.
func RemElemFromIntArray(s []int, thingToRemove int) []int {
	i := GetIndexOf(s, thingToRemove)
	if i == -1 {
		return s
	}

	s[len(s)-1], s[i] = s[i], s[len(s)-1]
	return s[:len(s)-1]
}

// GetIndexOf gets an index of variable, -1 if not in array
func GetIndexOf(s []int, i int) int {

	for index, x := range s {
		if x == i {
			return index
		}
	}

	return -1
}

// RemStringFromArray remove a string from a string slice.
func RemStringFromArray(s []string, r string) []string {
	for i, v := range s {
		if v == r {
			return append(s[:i], s[i+1:]...)
		}
	}
	return s
}

// ExecuteSpecial Executes a special command in console
func ExecuteSpecial(handler func(*exec.Cmd), command string, args ...string) error {
	cmd := exec.Command(command, args...)
	if handler != nil {
		handler(cmd)
	}
	_, err := cmd.CombinedOutput()
	if err != nil {
		log.Printf("Error executing command %s with arguments %v: %s\n", command, args, err.Error())
	}
	// log.Printf("Execute output (%d): %s\n", len(out), strings.TrimSpace(string(out)))
	return err
}

var letterRunes = []rune("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012345679")

// RandomString makes a random string with n as the length
func RandomString(n int) string {
	rand.Seed(time.Now().UTC().UnixNano())
	b := make([]rune, n)
	for i := range b {
		b[i] = letterRunes[rand.Intn(len(letterRunes))]
	}
	return string(b)
}
