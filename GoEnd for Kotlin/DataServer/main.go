package main

import (
	"bytes"
	"database/sql"
	"encoding/json"
	"fmt"
	"image"
	"io"
	"io/ioutil"
	_ "io/ioutil"
	"log"
	"net/http"
	"os"

	//"os/exec"
	"path/filepath"

	"image/jpeg"
	_ "image/png"

	_ "github.com/go-sql-driver/mysql"

	"github.com/nfnt/resize"
	"golang.org/x/crypto/bcrypt"
)

var sizesSlice []int

func uploadVideoFile(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Video Upload Endpoint Hit")

	var err error
	if _, err := os.Stat("temp-videos/" + client.Name); os.IsNotExist(err) {
		errr := os.Mkdir("temp-videos/"+client.Name, 0755)
		if errr != nil {
			fmt.Println(err)
		}
	}
	if err != nil {
		fmt.Println(err)
	}

	r.ParseMultipartForm(10 << 20)
	var client Client

	jsonDataToBeParsed := r.Form.Get("data")
	var buf bytes.Buffer

	err = json.Unmarshal([]byte(jsonDataToBeParsed), &client)
	if err != nil {
		//http.Error(w, err.Error(), http.StatusBadRequest)
		fmt.Println(err)
		return
	}

	file, handler, err := r.FormFile("video")
	if err != nil {
		panic(err)
	}
	defer file.Close()

	io.Copy(&buf, file)

	fmt.Printf("Uploaded File: %+v\n", handler.Filename)
	fmt.Printf("File Size: %+v\n", handler.Size)
	fmt.Printf("MIME Header: %+v\n", handler.Header)

	tempFile, err := ioutil.TempFile("temp-videos\\\\"+client.Name+"\\\\", "*.tlv")
	if err != nil {
		fmt.Println(err)
	}

	defer tempFile.Close()

	/*fileBytes, err := ioutil.ReadAll(file)
	if err != nil {
		fmt.Println(err)
	}*/

	tempFile.Write(buf.Bytes()) //fileBytes)

	addToLib(client.Name, tempFile.Name(), "temp-videos////video_bitmap.jpg", "video")

	a := append(sizesSlice, int(handler.Size))
	_ = a

	fmt.Fprintf(w, "Successfully Uploaded File\n")
}

/*func endedUpload(w http.ResponseWriter, r *http.Request) {
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
}*/

var db *sql.DB

type Client struct {
	Name    string
	Frame   string
	VideoId string
}

var client Client

func uploadImageFile(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Image Upload Endpoint Hit")
	r.ParseMultipartForm(10 << 20)
	var client Client

	//fmt.Println(r.FormFile("image"))

	jsonDataToBeParsed := r.Form.Get("data")
	var buf bytes.Buffer

	err := json.Unmarshal([]byte(jsonDataToBeParsed), &client)
	if err != nil {
		//http.Error(w, err.Error(), http.StatusBadRequest)
		fmt.Println(err)
		return
	}

	file, _, err := r.FormFile("image")
	if err != nil {
		panic(err)
	}
	defer file.Close()

	io.Copy(&buf, file)

	fmt.Printf("Uploader Name: %+v\n", client.Name)
	fmt.Printf("File Size: %+v\n", cap(buf.Bytes()))

	if len(buf.Bytes()) > 5 {

		if _, err := os.Stat("temp-images/" + client.Name); os.IsNotExist(err) {
			err = os.Mkdir("temp-images/"+client.Name, 0755)
			if err != nil {
				fmt.Println(err)
			}
		}
		if err != nil {
			fmt.Println(err)
		}

		tempFile, err := ioutil.TempFile("temp-images\\\\"+client.Name+"\\\\", "*.tli") //+handler.Filename)
		if err != nil {
			fmt.Println(err)
		}
		defer tempFile.Close()

		tempFile.Write(buf.Bytes())

		orig_image, _, err := image.Decode(&buf)

		if err != nil {
			fmt.Println(err)
			return
		}

		newImage := resize.Resize(64, 64, orig_image, resize.Lanczos3)

		bitmapImage, _ := os.Create("temp-images\\\\Thumbnails\\\\" + tempFile.Name()[13+len(client.Name)+4:len(tempFile.Name())-4] + ".tlb")
		defer bitmapImage.Close()

		jpeg.Encode(bitmapImage, newImage, &jpeg.Options{75})

		fmt.Fprintf(w, "Successfully Uploaded Photo\n")
		fmt.Println(tempFile.Name())
		addToLib(client.Name, tempFile.Name(), bitmapImage.Name(), "image")
		buf.Reset()

		return

	} else {
		fmt.Println("File Not Found.")
	}
}

type Gallery struct {
	Images []ImageInGallery
	Videos []VideoInGallery
}

type ImageInGallery struct {
	Username       string
	Filename       string
	BitmapFilename string
	Filetype       string
}

type VideoInGallery struct {
	Username       string
	Filename       string
	BitmapFilename string
	Filetype       string
}

func getGallery(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Get Gallery Endpoint Hit")
	r.ParseMultipartForm(10 << 20)
	var client Client

	jsonDataToBeParsed := r.Form.Get("data")

	err := json.Unmarshal([]byte(jsonDataToBeParsed), &client)
	if err != nil {
		//http.Error(w, err.Error(), http.StatusBadRequest)
		fmt.Println(err)
		return
	}

	fmt.Printf(client.Name)
	iiii := getImages(client.Name)
	vvvv := getVideos(client.Name)
	gallery := Gallery{iiii, vvvv}

	b, err := json.Marshal(gallery)
	if err != nil {
		fmt.Println(err)
	}
	w.Write(b)
	fmt.Println(string(b[:]))
}

func setupRoutes() {
	http.HandleFunc("/uploadVideo", uploadVideoFile)
	http.HandleFunc("/uploadImage", uploadImageFile)
	//	http.HandleFunc("/endVideo", endedUpload)
	http.HandleFunc("/getGallery", getGallery)
	http.HandleFunc("/signup", signUpUser)
	http.HandleFunc("/login", loginUser)
	http.ListenAndServe(":8080", nil)
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

func main() {
	fmt.Println("Started Listening.")

	InitializeDB()

	setupRoutes()
}

func InitializeDB() {
	fmt.Println("Main Started.")

	var err error
	db, err = sql.Open("mysql", "root@tcp(127.0.0.1:3306)/test")
	if err != nil {
		log.Fatal(err)
	} else {
		fmt.Println("Database initialized.")
	}
	//defer db.Close()

	// addToLib(db, "Dedniz", "olduu", "image")
	// getLib(db, "Deniz")
}

func addToLib(_username string, _filename string, _bitmap_filename string, _filetype string) {
	fmt.Println(_filename)
	_, err := db.Exec(fmt.Sprintf("INSERT INTO filedata VALUES ('%s', '%s', '%s', '%s')", _username, _filename, _bitmap_filename, _filetype))
	if err != nil {
		log.Fatal(err)
	}
}

func getImages(_username string) []ImageInGallery {
	var (
		Username        string
		Filename        string
		bitmap_filename string
		Type            string
	)

	rows, err := db.Query("select Username, Filename, bitmap_filename, Type from filedata where Username = ? AND Type = 'image'", _username)
	if err != nil {
		log.Fatal(err)
	}
	defer rows.Close()

	var images []ImageInGallery

	for rows.Next() {
		err := rows.Scan(&Username, &Filename, &bitmap_filename, &Type)
		if err != nil {
			log.Fatal(err)
		}

		iig := ImageInGallery{_username, Filename, bitmap_filename, "image"}
		images = append(images, iig)
		// log.Println(Username, Filename, Type)
	}
	err = rows.Err()
	if err != nil {
		log.Fatal(err)
	}
	return images
}

func getVideos(_username string) []VideoInGallery {
	var (
		Username        string
		Filename        string
		bitmap_filename string
		Type            string
	)

	rows, err := db.Query("select Username, Filename, bitmap_filename, Type from filedata where Username = ? AND type = 'video'", _username)
	if err != nil {
		log.Fatal(err)
	}
	defer rows.Close()

	var videos []VideoInGallery

	for rows.Next() {
		err := rows.Scan(&Username, &Filename, &bitmap_filename, &Type)
		if err != nil {
			log.Fatal(err)
		}

		vig := VideoInGallery{_username, Filename, bitmap_filename, "video"}
		videos = append(videos, vig)
		// log.Println(Username, Filename, Type)
	}
	err = rows.Err()
	if err != nil {
		log.Fatal(err)
	}
	return videos
}

func signUpUser(res http.ResponseWriter, req *http.Request) {
	if req.Method != "POST" {
		http.ServeFile(res, req, "signup.html")
		return
	}

	fmt.Println(req)
	username := req.FormValue("username")
	password := req.FormValue("password")

	var user string

	err := db.QueryRow("SELECT username FROM users WHERE username=?", username).Scan(&user)

	switch {
	case err == sql.ErrNoRows:
		hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
		_ = hashedPassword

		if err != nil {
			//http.Error(res, "Server error, unable to create your account.", 500)
			fmt.Printf("Server error, unable to create your account.")
			return
		}

		_, err = db.Exec("INSERT INTO users(username, password) VALUES(?, ?)", username, password) //hashedPassword)
		if err != nil {
			//http.Error(res, "Server error, unable to create your account.", 500)
			fmt.Printf("Server error, unable to create your account.")
			return
		}

		fmt.Printf("success")
		res.Write([]byte("success"))
		return
	case err != nil:
		//http.Error(res, "Server error, unable to create your account.", 500)
		fmt.Printf("Server error, unable to create your account.")
		return
	default:
		fmt.Printf("Returned default")
		return
		//http.Redirect(res, req, "/", 301)
	}
}

func loginUser(res http.ResponseWriter, req *http.Request) {
	if req.Method != "POST" {
		http.ServeFile(res, req, "login.html")
		return
	}

	username := req.FormValue("username")
	password := req.FormValue("password")

	var databaseUsername string
	var databasePassword string

	err := db.QueryRow("SELECT username, password FROM users WHERE username=?", username).Scan(&databaseUsername, &databasePassword)

	if err != nil {
		//http.Redirect(res, req, "/login", 301)
		res.Write([]byte("db fail"))
		return
	}

	/*err = bcrypt.CompareHashAndPassword([]byte(databasePassword), []byte(password))
	if err != nil {
		//http.Redirect(res, req, "/login", 301)
		res.Write([]byte("wrong password"))
		return
	}*/
	if databasePassword == password {
		res.Write([]byte("success"))
	} else {
		res.Write([]byte("wrong password"))
	}

}
