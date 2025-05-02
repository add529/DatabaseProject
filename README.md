# **DATABASE PROJECT**

## Tasks
- Write manual
- Write tutorial
- Make Flyer
- Make Presentation
- Develop GUI more
- Add data to tables

---

## How to Make Changes

1. **Clone the repository** (only needed once):
   ```bash
   git clone https://github.com/<your-username>/<repo-name>.git
   cd <repo-name>
   ```

2. **Make your changes and commit them**:
   ```bash
   git add .
   git commit -m "Describe your changes"
   git push origin main
   ```


**How to connect to Database**

For this project to run as intended, your computer must be connected to the Towson University VPN.

Instructions to connect can be found at this link. You will need your Towson University credentials to download and login.

[Towson Tech Help](https://techhelp.towson.edu/TDClient/1879/Portal/KB/ArticleDet?ID=140698)

[https://vpnc.towson.edu](https://vpnc.towson.edu)

Once connected, the project connection to the MySQL database will authenticate properly.


## How to Run

This project was built on javac version 21.0.4
The project was created in VS Code using SWING and MySQL.

There are two methods to run the project. It is our recommendation that this project be run from VS Code.

1. **Running From VS Code**:
    To run from VS Code, load the project into a VS window, navigate to /src/MainApp.java file, and click play button located near the top-right of window.

    From there a SWING window should shortly appear where you then be able to explore the Database Management Software.

    If you are using a default version of VS Code, make sure to have extensions "Java" and "Extensions for Java" installed.

2. **Running .class files**:
    As mentioned previously, this project was created with javac version 21.0.4. Therefore the correct version of java needs to be installed for this project to be properly compiled.

    You can use these links to install the correct version of Java needed to run.

    [Adoptium](https://adoptium.net/)

    [Oracle](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)

    Once the correct version of Java is installed, run the project from a command prompt/ or powershell.

    Navigate to the bin folder located in the Database Project directory.

    ```bash
    cd \User\<Your Windows Username>\DatabaseProject\bin
    ```

    To run the Project:
    ```bash

    java MainApp
    ```

This project can also be found on GitHub at:

    (https://github.com/add529/DatabaseProject)