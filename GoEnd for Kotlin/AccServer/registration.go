package main

import (
	"database/sql"
	"fmt"
	"net/http"

	_ "github.com/go-sql-driver/mysql"
	"golang.org/x/crypto/bcrypt"
)

var db *sql.DB
var err error

func signupPage(res http.ResponseWriter, req *http.Request) {
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

func loginPage(res http.ResponseWriter, req *http.Request) {
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

func main() {
	db, err = sql.Open("mysql", "root@tcp(127.0.0.1:3306)/test")
	if err != nil {
		panic(err.Error())
	}
	defer db.Close()

	err = db.Ping()
	if err != nil {
		panic(err.Error())
	}

	http.HandleFunc("/signup", signupPage)
	http.HandleFunc("/login", loginPage)
	http.ListenAndServe(":8080", nil)
}
