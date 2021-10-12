package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	_ "io/ioutil"
	"net/http"
	"os"
	"os/exec"
	"path/filepath"
)

var sizesSlice []int

func uploadVideoFile(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Video Upload Endpoint Hit")

	// Parse our multipart form, 10 << 20 specifies a maximum
	// upload of 10 MB files.
	r.ParseMultipartForm(10 << 20)
	// FormFile returns the first file for the given key `myFile`
	// it also returns the FileHeader so we can get the Filename,
	// the Header and the size of the file
	file, handler, err := r.FormFile("myFile")
	if err != nil {
		fmt.Println("Error Retrieving the File")
		fmt.Println(err)
		return
	}
	defer file.Close()
	fmt.Printf("Uploaded File: %+v\n", handler.Filename)
	fmt.Printf("File Size: %+v\n", handler.Size)
	fmt.Printf("MIME Header: %+v\n", handler.Header)

	// Create a temporary file within our temp-images directory that follows
	// a particular naming pattern
	tempFile, err := ioutil.TempFile("temp-videos", "*"+handler.Filename)
	if err != nil {
		fmt.Print("33")
		fmt.Println(err)
	}
	createdFileName := tempFile.Name()
	defer tempFile.Close()

	fileBytes, err := ioutil.ReadAll(file)
	if err != nil {
		fmt.Println(err)
	}
	// write this byte array to our temporary file
	tempFile.Write(fileBytes)
	// return that we have successfully uploaded our file!

	writeToFile(returnCurPath(), createdFileName)
	a := append(sizesSlice, int(handler.Size))
	_ = a

	fmt.Fprintf(w, "Successfully Uploaded File\n")
}

func endedUpload(w http.ResponseWriter, r *http.Request) {
	fmt.Println("File Ended Endpoint Hit")

	//Get final file size
	resultFileSize := 0
	for _, v := range sizesSlice {
		resultFileSize += v
	}

	fmt.Printf("Final File Size is %d\n", resultFileSize) //Print final file size

	currentPath := returnCurPath()                                                                                                       //Get current path
	finalCommand := fmt.Sprintf("ffmpeg -f concat -safe 0 -i %s\\parsedVideoNames.txt -c copy %s\\output.mp4", currentPath, currentPath) //Format final command

	fmt.Printf("Final Command is %s\n", finalCommand)

	//Execute final format
	cmd := exec.Command("cmd", "/C", finalCommand)
	stdout, err := cmd.Output()

	if err != nil {
		fmt.Println(err.Error())
		return
	}
	fmt.Println(string(stdout))

	fmt.Fprintf(w, "Successfully Uploaded File\n")
}

type Client struct {
	Name string
}

func uploadImageFile(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Image Upload Endpoint Hit")
	r.ParseMultipartForm(10 << 20)

	var client Client

	jsonDataToBeParsed := r.MultipartForm.Value["data"][0]

	err := json.Unmarshal([]byte(jsonDataToBeParsed), &client)
	if err != nil {
		//http.Error(w, err.Error(), http.StatusBadRequest)
		fmt.Println(err)
		return
	}

	file := r.PostFormValue("myFile")

	fmt.Printf("Uploader Name: %+v\n", client.Name)
	fmt.Printf("File Size: %+v\n", cap([]byte(file)))

	if len(file) > 5 {
		tempFile, err := ioutil.TempFile("temp-images", "*.jpeg") //+handler.Filename)
		if err != nil {
			fmt.Println(err)
		}
		defer tempFile.Close()

		if err != nil {
			fmt.Println(err)
		}

		tempFile.Write([]byte(file))

		fmt.Fprintf(w, "Successfully Uploaded Photo\n")
	} else {
		fmt.Println("File Not Found.")
	}
}

func setupRoutes() {
	http.HandleFunc("/uploadVideo", uploadVideoFile)
	http.HandleFunc("/uploadImage", uploadImageFile)
	http.HandleFunc("/end", endedUpload)
	http.ListenAndServe(":8080", nil)
}

func main() {
	fmt.Println("Started Listening.")
	setupRoutes()
}

func returnCurPath() string {
	ex, err := os.Executable()
	if err != nil {
		panic(err)
	}
	exPath := filepath.Dir(ex)
	return exPath
}

func writeToFile(currentDirectory string, data string) {
	dataWithNewLine := fmt.Sprintf("file '%s/%s'\n", currentDirectory, data)

	lastData, err := ioutil.ReadFile("parsedVideoNames.txt")
	bytesData := []byte(dataWithNewLine)
	err = ioutil.WriteFile("parsedVideoNames.txt", append(lastData, bytesData...), 0)

	if err != nil {
		fmt.Print(err)
		return
	}

	fmt.Println("Wrote to file First")
}
